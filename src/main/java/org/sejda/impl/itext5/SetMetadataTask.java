/*
 * Created on 12/gen/2014
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

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.impl.itext5.util.ITextUtils.nullSafeClosePdfReader;

import java.io.File;

import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.itext5.component.DefaultPdfSourceOpener;
import org.sejda.impl.itext5.component.PdfStamperHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SetMetadataParameters;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.pdf.PdfReader;

/**
 * Task setting metadata on an input {@link PdfSource}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SetMetadataTask extends BaseTask<SetMetadataParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SetMetadataTask.class);

    private PdfReader reader = null;
    private PdfStamperHandler stamperHandler = null;
    private SingleOutputWriter outputWriter;
    private PdfSourceOpener<PdfReader> sourceOpener;

    public void before(SetMetadataParameters parameters) {
        sourceOpener = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.isOverwrite());
    }

    public void execute(SetMetadataParameters parameters) throws TaskException {
        notifyEvent(getNotifiableTaskMetadata()).progressUndetermined();

        PdfSource<?> source = parameters.getSource();
        LOG.debug("Opening {} ", source);
        reader = source.open(sourceOpener);

        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {} ", tmpFile);
        stamperHandler = new PdfStamperHandler(reader, tmpFile, parameters.getVersion());
        stamperHandler.setCompression(parameters.isCompress());

        LOG.debug("Setting metadata on temporary document.");
        stamperHandler.setMetadata(parameters.entrySet());

        nullSafeCloseQuietly(stamperHandler);
        nullSafeClosePdfReader(reader);

        outputWriter.setOutput(file(tmpFile).name(parameters.getOutputName()));
        parameters.getOutput().accept(outputWriter);

        LOG.debug("Metadata set on {}", parameters.getOutput());

    }

    public void after() {
        nullSafeClosePdfReader(reader);
        nullSafeCloseQuietly(stamperHandler);
    }

}
