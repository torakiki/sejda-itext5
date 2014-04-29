/*
 * Created on 14/gen/2014
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
import static org.sejda.impl.itext5.util.ITextUtils.nullSafeClosePdfReader;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.service.DefaultTaskExecutionService;
import org.sejda.impl.TestUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.parameter.ExtractPagesParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.page.PageRange;
import org.sejda.model.pdf.page.PredefinedSetOfPages;
import org.sejda.model.task.Task;

import com.itextpdf.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 * 
 */
public class ExtractPagesIText5TaskTest extends BaseTaskTest {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();
    private SejdaContext context = mock(DefaultSejdaContext.class);
    private ExtractPagesParameters parameters;
    private Task<ExtractPagesParameters> victimTask = new ExtractPagesTask();

    @Before
    public void setUp() throws IOException {
        TestUtils.setProperty(victim, "context", context);

        parameters = new ExtractPagesParameters();
        parameters.setOverwrite(true);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setOutput(getOutputFile());
    }

    private void setUpParametersOddPages() throws IOException {
        parameters = new ExtractPagesParameters(PredefinedSetOfPages.ODD_PAGES);
        parameters.setOverwrite(true);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setSource(getSource());
        parameters.setOutput(getOutputFile());
    }

    private void setUpParametersPageRangesPages() {
        parameters.addPageRange(new PageRange(1, 1));
        parameters.addPageRange(new PageRange(3));
        parameters.setSource(getSource());
    }

    private void setUpParametersPageRangesMediumFile() {
        PageRange firstRange = new PageRange(2, 3);
        PageRange secondRange = new PageRange(5, 7);
        PageRange thirdRange = new PageRange(12, 18);
        PageRange fourthRange = new PageRange(20, 26);
        Set<PageRange> ranges = new HashSet<PageRange>();
        ranges.add(firstRange);
        ranges.add(secondRange);
        ranges.add(thirdRange);
        ranges.add(fourthRange);
        parameters.addAllPageRanges(ranges);
        parameters.setSource(getMediumSource());
    }

    @Test
    public void testExecuteExtractOddPages() throws TaskException, IOException {
        setUpParametersOddPages();
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        PdfReader reader = null;
        try {
            reader = getReaderFromResultFile();
            assertCreator(reader);
            assertEquals(PdfVersion.VERSION_1_6.getVersionAsCharacter(), reader.getPdfVersion());
            assertEquals(2, reader.getNumberOfPages());
        } finally {
            nullSafeClosePdfReader(reader);
        }
    }

    @Test
    public void testExecuteExtractRanges() throws TaskException, IOException {
        setUpParametersPageRangesPages();
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        PdfReader reader = null;
        try {
            reader = getReaderFromResultFile();
            assertCreator(reader);
            assertEquals(PdfVersion.VERSION_1_6.getVersionAsCharacter(), reader.getPdfVersion());
            assertEquals(3, reader.getNumberOfPages());
        } finally {
            nullSafeClosePdfReader(reader);
        }
    }

    @Test
    public void testExecuteExtractRangesMedium() throws TaskException, IOException {
        setUpParametersPageRangesMediumFile();
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        PdfReader reader = null;
        try {
            reader = getReaderFromResultFile();
            assertCreator(reader);
            assertEquals(PdfVersion.VERSION_1_6.getVersionAsCharacter(), reader.getPdfVersion());
            assertEquals(19, reader.getNumberOfPages());
        } finally {
            nullSafeClosePdfReader(reader);
        }
    }
}
