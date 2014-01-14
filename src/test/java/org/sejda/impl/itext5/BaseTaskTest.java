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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.sejda.core.Sejda;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.output.FileTaskOutput;
import org.sejda.model.output.StreamTaskOutput;
import org.sejda.model.pdf.PdfMetadataKey;

import com.itextpdf.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 */
@Ignore
class BaseTaskTest {
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private File outFile;

    void assertCreator(PdfReader reader) {
        HashMap<String, String> meta = reader.getInfo();
        assertEquals(Sejda.CREATOR, meta.get(PdfMetadataKey.CREATOR.getKey()));
    }

    PdfStreamSource getSource() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
        return PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
    }

    PdfStreamSource getMediumSource() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/medium_test.pdf");
        return PdfStreamSource.newInstanceNoPassword(stream, "test_file.pdf");
    }

    PdfStreamSource getEncryptedSource() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf/enc_with_modify_perm.pdf");
        return PdfStreamSource.newInstanceWithPassword(stream, "test_file.pdf", "test");
    }

    StreamTaskOutput getOutput() {
        return new StreamTaskOutput(out);
    }

    FileTaskOutput getOutputFile() throws IOException {
        outFile = File.createTempFile("SejdaTest", ".pdf");
        outFile.deleteOnExit();
        return new FileTaskOutput(outFile);
    }

    PdfReader getReaderFromResultFile() throws IOException {
        PdfReader reader = new PdfReader(new FileInputStream(outFile));
        reader.removeUnusedObjects();
        reader.consolidateNamedDestinations();
        return reader;
    }

    PdfReader getReaderFromResultStream(String expectedFileName) throws IOException {
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
}
