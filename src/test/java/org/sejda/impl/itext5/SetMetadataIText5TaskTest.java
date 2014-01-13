/*
 * Created on 13/gen/2014
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
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.service.DefaultTaskExecutionService;
import org.sejda.impl.TestUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.parameter.SetMetadataParameters;
import org.sejda.model.pdf.PdfMetadataKey;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;

import com.itextpdf.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 *
 */
public class SetMetadataIText5TaskTest extends BaseTaskTest {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();
    private SejdaContext context = mock(DefaultSejdaContext.class);
    private SetMetadataParameters parameters = new SetMetadataParameters();
    private Task<SetMetadataParameters> victimTask = new SetMetadataTask();

    @Before
    public void setUp() throws IOException {
        TestUtils.setProperty(victim, "context", context);
        parameters.setCompress(true);
        parameters.setOutputName("outName.pdf");
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.put(PdfMetadataKey.AUTHOR, "test_author");
        parameters.put(PdfMetadataKey.KEYWORDS, "test_keywords");
        parameters.put(PdfMetadataKey.SUBJECT, "test_subject");
        parameters.put(PdfMetadataKey.TITLE, "test_title");
        parameters.setOverwrite(true);
        parameters.setOutput(getOutputFile());
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        parameters.setSource(getSource());
        doExecute();
    }

    @Test
    public void testExecuteEncrypted() throws TaskException, IOException {
        parameters.setSource(getEncryptedSource());
        doExecute();
    }

    private void doExecute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultFile();
        assertCreator(reader);
        assertEquals(PdfVersion.VERSION_1_6.getVersionAsCharacter(), reader.getPdfVersion());
        Map<String, String> meta = reader.getInfo();
        assertEquals("test_author", meta.get(PdfMetadataKey.AUTHOR.getKey()));
        assertEquals("test_keywords", meta.get(PdfMetadataKey.KEYWORDS.getKey()));
        assertEquals("test_subject", meta.get(PdfMetadataKey.SUBJECT.getKey()));
        assertEquals("test_title", meta.get(PdfMetadataKey.TITLE.getKey()));
        reader.close();
    }
}
