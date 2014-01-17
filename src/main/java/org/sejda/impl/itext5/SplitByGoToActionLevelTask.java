/*
 * Created on 17/gen/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of sejda-itext5.
 *
 * sejda-itext5 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sejda-itext5 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with sejda-itext5.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.itext5;

import static org.sejda.impl.itext5.util.ITextUtils.nullSafeClosePdfReader;

import org.sejda.impl.itext5.component.DefaultPdfSourceOpener;
import org.sejda.impl.itext5.component.ITextOutlineHandler;
import org.sejda.impl.itext5.component.split.GoToPageDestinationsPdfSplitter;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.outline.OutlineGoToPageDestinations;
import org.sejda.model.parameter.SplitByGoToActionLevelParameters;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.pdf.PdfReader;

/**
 * Task splitting an input pdf document on a set of pages given by a GoTo Action level defined in the input parameter.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SplitByGoToActionLevelTask extends BaseTask<SplitByGoToActionLevelParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SplitByGoToActionLevelTask.class);

    private PdfReader reader = null;
    private PdfSourceOpener<PdfReader> sourceOpener;
    private GoToPageDestinationsPdfSplitter splitter;

    public void before(SplitByGoToActionLevelParameters parameters) {
        sourceOpener = new DefaultPdfSourceOpener();
    }

    public void execute(SplitByGoToActionLevelParameters parameters) throws TaskException {
        LOG.debug("Opening {} ", parameters.getSource());
        reader = parameters.getSource().open(sourceOpener);

        LOG.debug("Retrieving outline information for level {}", parameters.getLevelToSplitAt());
        OutlineGoToPageDestinations goToPagesDestination = new ITextOutlineHandler(reader,
                parameters.getMatchingTitleRegEx())
                .getGoToPageDestinationForActionLevel(parameters.getLevelToSplitAt());
        splitter = new GoToPageDestinationsPdfSplitter(reader, parameters, goToPagesDestination);
        LOG.debug("Starting split by GoTo Action level for {} ", parameters);
        splitter.split(getNotifiableTaskMetadata());

        LOG.debug("Input documents splitted and written to {}", parameters.getOutput());
    }

    public void after() {
        nullSafeClosePdfReader(reader);
        splitter = null;
    }
}
