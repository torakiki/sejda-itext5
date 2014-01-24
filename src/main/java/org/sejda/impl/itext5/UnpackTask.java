/*
 * Created on 16/gen/2014
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

import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.impl.itext5.util.ITextUtils.nullSafeClosePdfReader;

import org.sejda.impl.itext5.component.DefaultPdfSourceOpener;
import org.sejda.impl.itext5.component.PdfUnpacker;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.UnpackParameters;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.pdf.PdfReader;
/**
 * iText implementation of a task that unpacks files attached to a collection of input documents.
 * 
 * @author Andrea Vacondio
 * 
 */
public class UnpackTask extends BaseTask<UnpackParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(UnpackTask.class);

    private PdfReader reader = null;
    private PdfUnpacker unpacker;
    private PdfSourceOpener<PdfReader> sourceOpener;
    private int totalSteps;

    public void before(UnpackParameters parameters) {
        unpacker = new PdfUnpacker(parameters.isOverwrite());
        sourceOpener = new DefaultPdfSourceOpener();
        totalSteps = parameters.getSourceList().size();
    }

    public void execute(UnpackParameters parameters) throws TaskException {
        int currentStep = 0;

        for (PdfSource<?> source : parameters.getSourceList()) {
            LOG.debug("Opening {} ", source);
            reader = source.open(sourceOpener);

            unpacker.unpack(reader);

            nullSafeClosePdfReader(reader);
            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(totalSteps);
        }

        unpacker.write(parameters.getOutput());
        LOG.debug("Attachments unpacked and written to {}", parameters.getOutput());
    }

    public void after() {
        nullSafeClosePdfReader(reader);
    }

}
