/*
 * Created on 17/gen/2014
 * Copyright 2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of sejda-itext5.
 *
 * sejda-itext5 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sejda-itext5 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with sejda-itext5.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.itext5;

import static org.sejda.impl.itext5.util.ITextUtils.nullSafeClosePdfReader;

import org.sejda.impl.itext5.component.DefaultPdfSourceOpener;
import org.sejda.impl.itext5.component.split.PagesPdfSplitter;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.AbstractSplitByPageParameters;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.pdf.PdfReader;

/**
 * Task splitting an input pdf document on a set of pages defined in the input parameter object.
 * 
 * @author Andrea Vacondio
 * @param <T>
 *            the type of the parameters.
 */
public class SplitByPageNumbersTask<T extends AbstractSplitByPageParameters> extends BaseTask<T> {

    private static final Logger LOG = LoggerFactory.getLogger(SplitByPageNumbersTask.class);

    private PdfReader reader = null;
    private PdfSourceOpener<PdfReader> sourceOpener;
    private PagesPdfSplitter<T> splitter;

    public void before(T parameters) {
        sourceOpener = new DefaultPdfSourceOpener();
    }

    public void execute(T parameters) throws TaskException {
        LOG.debug("Opening {} ", parameters.getSource());
        reader = parameters.getSource().open(sourceOpener);

        splitter = new PagesPdfSplitter<T>(reader, parameters);
        LOG.debug("Starting split by page numbers for {} ", parameters);
        splitter.split(getNotifiableTaskMetadata());

        LOG.debug("Input documents splitted and written to {}", parameters.getOutput());
    }

    public void after() {
        nullSafeClosePdfReader(reader);
        splitter = null;
    }

}
