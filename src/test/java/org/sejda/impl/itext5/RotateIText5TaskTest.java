/*
 * Created on 11/gen/2014
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.sejda.core.Sejda;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.service.DefaultTaskExecutionService;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.StreamTaskOutput;
import org.sejda.model.parameter.RotateParameters;
import org.sejda.model.pdf.PdfMetadataKey;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.rotation.PageRotation;
import org.sejda.model.rotation.Rotation;
import org.sejda.model.rotation.RotationType;
import org.sejda.model.task.Task;

import com.itextpdf.text.pdf.PdfReader;

/**
 * Rotate task test for the itext implementation
 * 
 * @author Andrea Vacondio
 * 
 */
public class RotateIText5TaskTest {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();

    private SejdaContext context = mock(DefaultSejdaContext.class);
    private RotateParameters parameters = new RotateParameters(PageRotation.createMultiplePagesRotation(
            Rotation.DEGREES_180, RotationType.ALL_PAGES));
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private Task<RotateParameters> victimTask = new RotateTask();

    @Before
    public void setUp() throws SecurityException, NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {
        Field field = victim.getClass().getDeclaredField("context");
        field.setAccessible(true);
        field.set(victim, context);
        parameters.setCompress(true);
        parameters.setOutputPrefix("test_prefix_");
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setOverwrite(true);
        parameters.setOutput(new StreamTaskOutput(out));
    }

    private void setUpParameters() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
        parameters.addSource(source);
    }

    private void setUpParametersEncrypted() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_with_modify_perm.pdf");
        PdfStreamSource source = PdfStreamSource.newInstanceWithPassword(stream, "test_file.pdf", "test");
        parameters.addSource(source);
        parameters.setOverwrite(true);
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        setUpParameters();
        doExecute();
    }

    @Test
    public void testExecuteEncrypted() throws TaskException, IOException {
        setUpParametersEncrypted();
        doExecute();
    }

    private void doExecute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultStream("test_prefix_test_file.pdf");
        assertCreator(reader);
        assertEquals(PdfVersion.VERSION_1_6.getVersionAsCharacter(), reader.getPdfVersion());
        assertEquals(4, reader.getNumberOfPages());
        assertEquals(180, reader.getPageRotation(2));
        reader.close();
    }

    protected PdfReader getReaderFromResultStream(String expectedFileName) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());
        ZipInputStream zip = new ZipInputStream(input);
        ZipEntry entry = zip.getNextEntry();
        if (StringUtils.isNotBlank(expectedFileName)) {
            assertEquals(expectedFileName, entry.getName());
        }
        PdfReader reader = new PdfReader(zip);
        reader.removeUnusedObjects();
        reader.consolidateNamedDestinations();
        return reader;
    }

    protected void assertCreator(PdfReader reader) {
        HashMap<String, String> meta = reader.getInfo();
        assertEquals(Sejda.CREATOR, meta.get(PdfMetadataKey.CREATOR.getKey()));
    }
}
