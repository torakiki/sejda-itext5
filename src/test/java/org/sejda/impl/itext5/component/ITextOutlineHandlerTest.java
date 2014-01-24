/*
 * Created on 17/gen/2014
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
package org.sejda.impl.itext5.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.sejda.model.outline.OutlineHandler;

import com.itextpdf.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 * 
 */
public class ITextOutlineHandlerTest {
    @Test
    public void testPositiveGetGoToBookmarkMaxDepth() throws IOException {
        PdfReader reader = null;
        InputStream inputStream = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf");
            reader = new PdfReader(inputStream);
            OutlineHandler victim = new ITextOutlineHandler(reader, null);
            assertEquals(3, victim.getMaxGoToActionDepth());
        } finally {
            IOUtils.closeQuietly(inputStream);
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Test
    public void testNegativeGetGoToBookmarkMaxDepth() throws IOException {
        PdfReader reader = null;
        InputStream inputStream = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("pdf/test_no_outline.pdf");
            reader = new PdfReader(inputStream);
            OutlineHandler victim = new ITextOutlineHandler(reader, null);
            assertEquals(0, victim.getMaxGoToActionDepth());
        } finally {
            IOUtils.closeQuietly(inputStream);
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Test
    public void testGetPageNumbersAtGoToActionLevel() throws IOException {
        PdfReader reader = null;
        InputStream inputStream = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf");
            reader = new PdfReader(inputStream);
            OutlineHandler victim = new ITextOutlineHandler(reader, null);
            assertTrue(victim.getGoToPageDestinationForActionLevel(4).isEmpty());
            assertEquals(2, victim.getGoToPageDestinationForActionLevel(2).size());
            assertEquals(1, victim.getGoToPageDestinationForActionLevel(3).size());
        } finally {
            IOUtils.closeQuietly(inputStream);
            if (reader != null) {
                reader.close();
            }
        }
    }
}
