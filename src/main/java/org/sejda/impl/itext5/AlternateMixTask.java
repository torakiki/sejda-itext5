/*
 * Created on 14/gen/2014
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

import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.impl.itext5.util.ITextUtils.nullSafeClosePdfReader;

import java.io.File;

import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.itext5.component.DefaultPdfCopier;
import org.sejda.impl.itext5.component.DefaultPdfSourceOpener;
import org.sejda.impl.itext5.component.PdfCopier;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfMixInput.PdfMixInputProcessStatus;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.AlternateMixParameters;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.pdf.PdfReader;

/**
 * iText implementation for the alternate mix task
 * 
 * @author Andrea Vacondio
 * 
 */
public class AlternateMixTask extends BaseTask<AlternateMixParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(AlternateMixTask.class);

    private PdfReader firstReader = null;
    private PdfReader secondReader = null;
    private PdfCopier copier = null;
    private SingleOutputWriter outputWriter;
    private PdfSourceOpener<PdfReader> sourceOpener;

    public void before(AlternateMixParameters parameters) {
        sourceOpener = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.isOverwrite());
    }

    public void execute(AlternateMixParameters parameters) throws TaskException {
        LOG.debug("Opening first input {} ", parameters.getFirstInput().getSource());
        firstReader = parameters.getFirstInput().getSource().open(sourceOpener);
        LOG.debug("Opening second input {} ", parameters.getSecondInput().getSource());
        secondReader = parameters.getSecondInput().getSource().open(sourceOpener);

        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {} ", tmpFile);
        copier = new DefaultPdfCopier(firstReader, tmpFile, parameters.getVersion());
        copier.setCompression(parameters.isCompress());
        copier.open();

        PdfMixInputProcessStatus firstDocStatus = parameters.getFirstInput().newProcessingStatus(
                firstReader.getNumberOfPages());
        PdfMixInputProcessStatus secondDocStatus = parameters.getSecondInput().newProcessingStatus(
                secondReader.getNumberOfPages());

        int currentStep = 0;
        int totalSteps = firstReader.getNumberOfPages() + secondReader.getNumberOfPages();
        while (firstDocStatus.hasNextPage() || secondDocStatus.hasNextPage()) {
            for (int i = 0; i < parameters.getFirstInput().getStep() && firstDocStatus.hasNextPage(); i++) {
                copier.addPage(firstReader, firstDocStatus.nextPage());
                notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(totalSteps);
            }
            for (int i = 0; i < parameters.getSecondInput().getStep() && secondDocStatus.hasNextPage(); i++) {
                copier.addPage(secondReader, secondDocStatus.nextPage());
                notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(totalSteps);
            }
        }
        copier.freeReader(firstReader);
        copier.freeReader(secondReader);
        closeResources();
        outputWriter.setOutput(file(tmpFile).name(parameters.getOutputName()));
        parameters.getOutput().accept(outputWriter);

        LOG.debug("Alternate mix with step first document {} and step second document {} completed.", parameters
                .getFirstInput().getStep(), parameters.getSecondInput().getStep());
    }

    public void after() {
        closeResources();
    }

    private void closeResources() {
        nullSafeCloseQuietly(copier);
        nullSafeClosePdfReader(firstReader);
        nullSafeClosePdfReader(secondReader);
    }
}
