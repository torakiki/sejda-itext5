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

import static org.apache.commons.lang3.StringUtils.join;
import static org.sejda.common.ComponentsUtility.nullSafeCloseQuietly;
import static org.sejda.core.notification.dsl.ApplicationEventsNotifier.notifyEvent;
import static org.sejda.core.support.io.IOUtils.createTemporaryPdfBuffer;
import static org.sejda.core.support.io.model.FileOutput.file;
import static org.sejda.impl.itext5.util.ITextUtils.nullSafeClosePdfReader;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.itext5.component.DefaultPdfCopier;
import org.sejda.impl.itext5.component.DefaultPdfSourceOpener;
import org.sejda.impl.itext5.component.PdfCopier;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.pdf.PdfReader;

/**
 * iText implementation for a merge task that merges a collection of input pdf documents.
 * 
 * @author Andrea Vacondio
 * 
 */
public class MergeTask extends BaseTask<MergeParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(MergeTask.class);

    private SingleOutputWriter outputWriter;
    private PdfSourceOpener<PdfReader> sourceOpener;
    private OutlineMerger outlineMerger;
    private PdfCopier copier = null;
    private Set<PdfReader> readers;
    private int totalSteps;

    public void before(MergeParameters parameters) {
        totalSteps = parameters.getInputList().size();
        sourceOpener = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.isOverwrite());
        outlineMerger = new OutlineMerger(parameters.getOutlinePolicy());
        readers = new HashSet<PdfReader>();
    }

    public void execute(MergeParameters parameters) throws TaskException {
        int currentStep = 0;
        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {} ", tmpFile);

        for (PdfMergeInput input : parameters.getInputList()) {
            LOG.debug("Opening input {} ", input.getSource());
            PdfReader reader = input.getSource().open(sourceOpener);
            readers.add(reader);

            if (copier == null) {
                createCopier(parameters, tmpFile, reader);
            }
            outlineMerger.updateOutline(reader, input, copier.getNumberOfCopiedPages());

            if (!input.isAllPages()) {
                String selection = join(input.getPageSelection(), ',');
                LOG.debug("Setting pages selection");
                reader.selectPages(selection);
                LOG.trace("Pages selection set to {}", selection);
            }

            copier.addAllPages(reader);

            if (parameters.isBlankPageIfOdd()) {
                LOG.debug("Adding blank page if required");
                copier.addBlankPageIfOdd(reader);
            }
            copier.freeReader(reader);

            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(totalSteps);
        }
        copier.setOutline(outlineMerger.getOutline());
        closeResources();
        outputWriter.setOutput(file(tmpFile).name(parameters.getOutputName()));
        parameters.getOutput().accept(outputWriter);
        LOG.debug("Input documents merged correctly and written to {}", parameters.getOutput());
    }

    private void createCopier(MergeParameters parameters, File tmpFile, PdfReader reader) throws TaskException {
        copier = new DefaultPdfCopier(reader, tmpFile, parameters.getVersion());
        copier.setCompression(parameters.isCompress());
        if (parameters.isCopyFormFields()) {
            copier.mergeFields();
            LOG.debug("Fields merging enabled");
        }
        copier.open();
    }

    public void after() {
        closeResources();
    }

    private void closeResources() {
        nullSafeCloseQuietly(copier);
        for (PdfReader reader : readers) {
            nullSafeClosePdfReader(reader);
        }
        this.outlineMerger = null;
    }
}
