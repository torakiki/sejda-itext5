/*
 * Created on 15/gen/2014
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
import static org.sejda.impl.itext5.util.ViewerPreferencesUtils.getPageMode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.sejda.core.support.io.OutputWriters;
import org.sejda.core.support.io.SingleOutputWriter;
import org.sejda.impl.itext5.component.DefaultPdfSourceOpener;
import org.sejda.impl.itext5.component.PdfStamperHandler;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.parameter.SetPagesTransitionParameters;
import org.sejda.model.pdf.transition.PdfPageTransition;
import org.sejda.model.pdf.viewerpreference.PdfPageMode;
import org.sejda.model.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.pdf.PdfReader;
/**
 * Task that applies pages transitions to an input document.
 * 
 * @author Andrea Vacondio
 * 
 */
public class SetPagesTransitionTask extends BaseTask<SetPagesTransitionParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(SetPagesTransitionTask.class);

    private PdfReader reader = null;
    private PdfStamperHandler stamperHandler = null;
    private SingleOutputWriter outputWriter;
    private PdfSourceOpener<PdfReader> sourceOpener;

    public void before(SetPagesTransitionParameters parameters) {
        sourceOpener = new DefaultPdfSourceOpener();
        outputWriter = OutputWriters.newSingleOutputWriter(parameters.isOverwrite());
    }

    public void execute(SetPagesTransitionParameters parameters) throws TaskException {
        PdfSource<?> source = parameters.getSource();
        LOG.debug("Opening {} ", source);
        reader = source.open(sourceOpener);

        File tmpFile = createTemporaryPdfBuffer();
        LOG.debug("Created output temporary buffer {} ", tmpFile);
        stamperHandler = new PdfStamperHandler(reader, tmpFile, parameters.getVersion());
        stamperHandler.setCompression(parameters.isCompress(), reader);

        if (parameters.isFullScreen()) {
            LOG.debug("Setting fullscreen mode");
            stamperHandler.setViewerPreferences(getPageMode(PdfPageMode.FULLSCREEN));
        }

        Map<Integer, PdfPageTransition> transitions = getTransitionsMap(parameters, reader.getNumberOfPages());
        LOG.debug("Applying {} transitions", transitions.size());
        int currentStep = 0;
        for (Entry<Integer, PdfPageTransition> entry : transitions.entrySet()) {
            LOG.trace("Applying transition {} to page {}", entry.getValue(), entry.getKey());
            stamperHandler.setTransition(entry.getKey(), entry.getValue());
            notifyEvent(getNotifiableTaskMetadata()).stepsCompleted(++currentStep).outOf(transitions.size());
        }

        nullSafeCloseQuietly(stamperHandler);
        nullSafeClosePdfReader(reader);

        outputWriter.setOutput(file(tmpFile).name(parameters.getOutputName()));
        parameters.getOutput().accept(outputWriter);

        LOG.debug("Transitions set on {}", parameters.getOutput());
    }

    public void after() {
        nullSafeCloseQuietly(stamperHandler);
        nullSafeClosePdfReader(reader);
    }

    /**
     * @param parameters
     * @param totalPages
     * @return a map containing all the transitions to apply considering the default transition if there is one.
     */
    private Map<Integer, PdfPageTransition> getTransitionsMap(SetPagesTransitionParameters parameters, int totalPages) {
        Map<Integer, PdfPageTransition> map = new HashMap<Integer, PdfPageTransition>();
        if (parameters.getDefaultTransition() != null) {
            for (int i = 1; i <= totalPages; i++) {
                map.put(i, parameters.getDefaultTransition());
            }
        }
        map.putAll(parameters.getTransitions());
        return map;
    }
}
