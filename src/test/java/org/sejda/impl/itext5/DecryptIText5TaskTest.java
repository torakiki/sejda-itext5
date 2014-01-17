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

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.service.DefaultTaskExecutionService;
import org.sejda.impl.TestUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.parameter.DecryptParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;

import com.itextpdf.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 *
 */
public class DecryptIText5TaskTest extends BaseTaskTest {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();
    private SejdaContext context = mock(DefaultSejdaContext.class);
    private DecryptParameters parameters = new DecryptParameters();
    private Task<DecryptParameters> victimTask = new DecryptTask();

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setOverwrite(true);
        parameters.addSource(getEncryptedSource());
        parameters.setOutput(getOutput());
    }

    @Test
    public void testExecuteEncrypted() throws TaskException, IOException {
        parameters.addSource(getEncryptedSource());
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultStream("test_file.pdf");
        assertCreator(reader);
        assertEquals(PdfVersion.VERSION_1_6.getVersionAsCharacter(), reader.getPdfVersion());
        // TODO
        // assertFalse(reader.isEncrypted());
        reader.close();
    }

}
