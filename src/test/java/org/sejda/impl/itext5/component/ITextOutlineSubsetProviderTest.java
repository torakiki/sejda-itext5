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
package org.sejda.impl.itext5.component;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Test;
import org.sejda.impl.itext5.util.ITextUtils;
import org.sejda.model.exception.TaskException;

import com.itextpdf.text.pdf.PdfReader;

/**
 * 
 * @author Andrea Vacondio
 * 
 */
public class ITextOutlineSubsetProviderTest {
    private PdfReader reader;

    @After
    public void tearDown() {
        ITextUtils.nullSafeClosePdfReader(reader);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailingConstructor() {
        new ITextOutlineSubsetProvider(null);
    }

    @Test
    public void getOutlineUntillPage() throws IOException, TaskException {
        InputStream inputStream = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("pdf/test_outline.pdf");
            reader = new PdfReader(inputStream);
            ITextOutlineSubsetProvider victim = new ITextOutlineSubsetProvider(reader);
            victim.startPage(2);
            Collection<HashMap<String, Object>> retList = victim.getOutlineUntillPage(2);
            assertNotNull(retList);
            assertFalse(retList.isEmpty());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    @Test
    public void getOutlineUntillPageEmptyBookmarks() throws TaskException, IOException {
        InputStream inputStream = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("pdf/test_no_outline.pdf");
            reader = spy(new PdfReader(inputStream));
            ITextOutlineSubsetProvider victim = new ITextOutlineSubsetProvider(reader);
            verify(reader).getNumberOfPages();
            victim.startPage(0);
            Collection<HashMap<String, Object>> retList = victim.getOutlineUntillPage(1);
            assertNotNull(retList);
            assertTrue(retList.isEmpty());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Test(expected = TaskException.class)
    public void getOutlineUntillPageNoStart() throws TaskException, IOException {
        InputStream inputStream = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("pdf/test_no_outline.pdf");
            reader = spy(new PdfReader(inputStream));
            ITextOutlineSubsetProvider victim = new ITextOutlineSubsetProvider(reader);
            verify(reader).getNumberOfPages();
            victim.getOutlineUntillPage(1);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Test(expected = TaskException.class)
    public void getOutlineUntillPageStartGTEnd() throws TaskException, IOException {
        InputStream inputStream = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream("pdf/test_no_outline.pdf");
            reader = spy(new PdfReader(inputStream));
            ITextOutlineSubsetProvider victim = new ITextOutlineSubsetProvider(reader);
            verify(reader).getNumberOfPages();
            victim.startPage(5);
            victim.getOutlineUntillPage(1);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
