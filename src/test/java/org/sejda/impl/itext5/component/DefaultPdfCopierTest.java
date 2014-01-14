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
package org.sejda.impl.itext5.component;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sejda.impl.itext5.util.ITextUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.pdf.PdfVersion;

import com.itextpdf.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 * 
 */
public class DefaultPdfCopierTest {

    private File outFile;

    @Before
    public void setUp() throws IOException {
        outFile = File.createTempFile("sejdaTest", ".pdf");
        outFile.deleteOnExit();
    }

    @After
    public void tearDown() {
        outFile.delete();
    }

    @Test
    public void testCount() throws IOException, TaskException {
        PdfReader reader = null;
        InputStream inputStream = null;
        DefaultPdfCopier victim = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("pdf/test_file.pdf");
            reader = new PdfReader(inputStream);
            reader.selectPages("2-3");
            victim = new DefaultPdfCopier(reader, outFile, PdfVersion.VERSION_1_5);
            victim.open();
            victim.addAllPages(reader);
            assertEquals(2, victim.getNumberOfCopiedPages());
            victim.addBlankPage(reader);
            assertEquals(3, victim.getNumberOfCopiedPages());
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(victim);
            ITextUtils.nullSafeClosePdfReader(reader);
        }
    }

}
