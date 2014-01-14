/*
 * Created on 14/gen/2014
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.service.DefaultTaskExecutionService;
import org.sejda.impl.TestUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfMixInput;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.parameter.AlternateMixParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;

import com.itextpdf.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 * 
 */
public class AlternateMixIText5TaskTest extends BaseTaskTest {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();
    private SejdaContext context = mock(DefaultSejdaContext.class);
    private AlternateMixParameters parameters;
    private Task<AlternateMixParameters> victimTask = new AlternateMixTask();

    @Before
    public void setUp() throws SecurityException, IllegalArgumentException, IOException {
        TestUtils.setProperty(victim, "context", context);
        InputStream firstStream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource firstSource = PdfStreamSource.newInstanceNoPassword(firstStream, "first_test_file.pdf");
        PdfMixInput firstInput = new PdfMixInput(firstSource);
        InputStream secondStream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource secondSource = PdfStreamSource.newInstanceNoPassword(secondStream, "first_test_file.pdf");
        PdfMixInput secondInput = new PdfMixInput(secondSource, true, 3);
        parameters = new AlternateMixParameters(firstInput, secondInput, "outName.pdf");
        parameters.setOverwrite(true);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setOutput(getOutputFile());
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultFile();
        assertCreator(reader);
        assertEquals(PdfVersion.VERSION_1_6.getVersionAsCharacter(), reader.getPdfVersion());
        assertEquals(8, reader.getNumberOfPages());
        reader.close();
    }
}
