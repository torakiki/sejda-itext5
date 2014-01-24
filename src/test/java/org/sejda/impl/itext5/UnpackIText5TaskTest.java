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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.service.DefaultTaskExecutionService;
import org.sejda.impl.TestUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.StreamTaskOutput;
import org.sejda.model.parameter.UnpackParameters;
import org.sejda.model.task.Task;

/**
 * @author Andrea Vacondio
 *
 */
public class UnpackIText5TaskTest extends BaseTaskTest {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();
    private SejdaContext context = mock(DefaultSejdaContext.class);
    private UnpackParameters parameters;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private Task<UnpackParameters> victimTask = new UnpackTask();

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
        parameters = new UnpackParameters(new StreamTaskOutput(out));
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/attachments.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "attachments.pdf");
        parameters.addSource(source);
        parameters.setOverwrite(true);
    }

    @Test
    public void testExecuteStream() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
        ZipInputStream zip = new ZipInputStream(input);
        int counter = 0;
        while (zip.getNextEntry() != null) {
            counter++;
        }
        assertEquals(1, counter);
    }
}
