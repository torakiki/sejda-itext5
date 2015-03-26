/*
 * Created on 11/gen/2014
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.service.DefaultTaskExecutionService;
import org.sejda.impl.TestUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.parameter.RotateParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PredefinedSetOfPages;
import org.sejda.model.rotation.Rotation;
import org.sejda.model.task.Task;

import com.itextpdf.text.pdf.PdfReader;

/**
 * Rotate task test for the itext implementation
 * 
 * @author Andrea Vacondio
 * 
 */
public class RotateIText5TaskTest extends BaseTaskTest {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();
    private SejdaContext context = mock(DefaultSejdaContext.class);
    private RotateParameters parameters;
    private Task<RotateParameters> victimTask = new RotateTask();

    @Before
    public void setUp() throws SecurityException, IllegalArgumentException {
        TestUtils.setProperty(victim, "context", context);
    }

    private void setUpParameters() {
        parameters.setCompress(true);
        parameters.setOutputPrefix("test_prefix_");
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setOverwrite(true);
        parameters.setOutput(getOutput());
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        parameters = new RotateParameters(Rotation.DEGREES_180, PredefinedSetOfPages.ALL_PAGES);
        setUpParameters();
        parameters.addSource(getSource());
        doExecute();

        PdfReader reader = getReaderFromResultStream("test_prefix_test_file.pdf");
        assertCreator(reader);
        assertEquals(PdfVersion.VERSION_1_6.getVersionAsCharacter(), reader.getPdfVersion());
        assertEquals(4, reader.getNumberOfPages());
        assertEquals(180, reader.getPageRotation(2));
        reader.close();
    }

    @Test
    public void testExecuteEncrypted() throws TaskException, IOException {
        parameters = new RotateParameters(Rotation.DEGREES_180, PredefinedSetOfPages.ALL_PAGES);
        setUpParameters();
        parameters.addSource(getEncryptedSource());
        doExecute();

        PdfReader reader = getReaderFromResultStream("test_prefix_test_file.pdf");
        assertCreator(reader);
        assertEquals(PdfVersion.VERSION_1_6.getVersionAsCharacter(), reader.getPdfVersion());
        assertEquals(4, reader.getNumberOfPages());
        assertEquals(180, reader.getPageRotation(2));
        reader.close();
    }

    @Test
    public void testRotateSpecificPages() throws TaskException, IOException {
        parameters = new RotateParameters(Rotation.DEGREES_90);
        setUpParameters();
        parameters.addPageRange(new PageRange(2, 4));
        parameters.addSource(getSource());
        doExecute();

        PdfReader reader = getReaderFromResultStream("test_prefix_test_file.pdf");
        assertEquals(90, reader.getPageRotation(3));
        reader.close();
    }

    private void doExecute() throws TaskException {
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);

    }
}
