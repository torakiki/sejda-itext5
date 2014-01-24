/*
 * Created on 15/gen/2014
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
import org.sejda.model.parameter.EncryptParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.encryption.PdfAccessPermission;
import org.sejda.model.pdf.encryption.PdfEncryption;
import org.sejda.model.task.Task;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * @author Andrea Vacondio
 *
 */
public class EncryptIText5TaskTest extends BaseTaskTest {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();
    private SejdaContext context = mock(DefaultSejdaContext.class);
    private EncryptParameters parameters;
    private Task<EncryptParameters> victimTask = new EncryptTask();

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
        parameters = new EncryptParameters(PdfEncryption.AES_ENC_128);
        parameters.setCompress(true);
        parameters.setOutputPrefix("test_prefix_");
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setOwnerPassword("test");
        parameters.addPermission(PdfAccessPermission.COPY_AND_EXTRACT);
        parameters.setOutput(getOutput());
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        parameters.addSource(getSource());
        doExecute();
    }

    private void doExecute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultStream("test_prefix_test_file.pdf");
        assertCreator(reader);
        assertEquals(PdfVersion.VERSION_1_6.getVersionAsCharacter(), reader.getPdfVersion());
        assertTrue(reader.isEncrypted());
        assertTrue((reader.getPermissions() & PdfWriter.ALLOW_COPY) == PdfWriter.ALLOW_COPY);
        assertFalse((reader.getPermissions() & PdfWriter.ALLOW_ASSEMBLY) == PdfWriter.ALLOW_ASSEMBLY);
        reader.close();
    }
}
