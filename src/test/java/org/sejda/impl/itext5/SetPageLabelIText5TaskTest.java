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
import org.sejda.model.parameter.SetPagesLabelParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.label.PdfLabelNumberingStyle;
import org.sejda.model.pdf.label.PdfPageLabel;
import org.sejda.model.task.Task;

import com.itextpdf.text.pdf.PdfPageLabels;
import com.itextpdf.text.pdf.PdfPageLabels.PdfPageLabelFormat;
import com.itextpdf.text.pdf.PdfReader;

/**
 * @author Andrea Vacondio
 *
 */
public class SetPageLabelIText5TaskTest extends BaseTaskTest {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();
    private SejdaContext context = mock(DefaultSejdaContext.class);
    private SetPagesLabelParameters parameters = new SetPagesLabelParameters();
    private Task<SetPagesLabelParameters> victimTask = new SetPagesLabelTask();

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setOverwrite(true);
        parameters.putLabel(1, PdfPageLabel.newInstanceWithoutLabel(PdfLabelNumberingStyle.LOWERCASE_ROMANS, 1));
        parameters.putLabel(3, PdfPageLabel.newInstanceWithLabel("Test", PdfLabelNumberingStyle.ARABIC, 1));
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
        parameters.setOutput(getOutputFile());
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultFile();
        assertCreator(reader);
        assertEquals(PdfVersion.VERSION_1_6.getVersionAsCharacter(), reader.getPdfVersion());
        PdfPageLabelFormat[] formats = PdfPageLabels.getPageLabelFormats(reader);
        assertEquals(1, formats[0].logicalPage);
        assertEquals(1, formats[1].logicalPage);
        assertEquals(1, formats[0].physicalPage);
        assertEquals(3, formats[1].physicalPage);
        assertEquals(PdfPageLabels.LOWERCASE_ROMAN_NUMERALS, formats[0].numberStyle);
        assertEquals(PdfPageLabels.DECIMAL_ARABIC_NUMERALS, formats[1].numberStyle);
        assertEquals("Test", formats[1].prefix);
        reader.close();
    }
}
