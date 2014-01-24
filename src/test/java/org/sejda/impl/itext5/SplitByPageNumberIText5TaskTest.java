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
package org.sejda.impl.itext5;

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
import org.sejda.model.parameter.SplitByPagesParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;

/**
 * @author Andrea Vacondio
 *
 */
public class SplitByPageNumberIText5TaskTest extends BaseTaskTest {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();
    private SejdaContext context = mock(DefaultSejdaContext.class);
    private SplitByPagesParameters parameters = new SplitByPagesParameters();
    private Task<SplitByPagesParameters> victimTask = new SplitByPageNumbersTask<SplitByPagesParameters>();

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setSource(getSource());
        parameters.setOverwrite(true);
        parameters.setOutput(getOutput());
    }

    @Test
    public void testExecuteBurst() throws TaskException, IOException {
        parameters.addPage(1);
        parameters.addPage(2);
        parameters.addPage(3);
        parameters.addPage(4);
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        assertOutputContainsDocuments(4);
    }

    @Test
    public void testExecuteEven() throws TaskException, IOException {
        parameters.addPage(2);
        parameters.addPage(4);
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        assertOutputContainsDocuments(3);
    }

    @Test
    public void testExecuteOdd() throws TaskException, IOException {
        parameters.addPage(1);
        parameters.addPage(3);
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        assertOutputContainsDocuments(2);
    }
}
