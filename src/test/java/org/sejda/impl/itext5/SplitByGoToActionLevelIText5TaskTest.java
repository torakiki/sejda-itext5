/*
 * Created on 17/gen/2014
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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.notification.context.ThreadLocalNotificationContext;
import org.sejda.core.service.DefaultTaskExecutionService;
import org.sejda.impl.TestUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.notification.EventListener;
import org.sejda.model.notification.event.TaskExecutionFailedEvent;
import org.sejda.model.parameter.SplitByGoToActionLevelParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;

/**
 * @author Andrea Vacondio
 *
 */
public class SplitByGoToActionLevelIText5TaskTest extends BaseTaskTest {

    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();
    private SejdaContext context = mock(DefaultSejdaContext.class);
    private Task<SplitByGoToActionLevelParameters> victimTask = new SplitByGoToActionLevelTask();

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
    }

    private SplitByGoToActionLevelParameters setUpParameters(int level, String regEx) {
        SplitByGoToActionLevelParameters parameters = new SplitByGoToActionLevelParameters(level);
        parameters.setMatchingTitleRegEx(regEx);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setSource(getOutlineSource());
        parameters.setOverwrite(true);
        parameters.setOutput(getOutput());
        return parameters;
    }

    @Test
    public void testExecuteLevel3() throws TaskException, IOException {
        SplitByGoToActionLevelParameters parameters = setUpParameters(3, null);
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        assertOutputContainsDocuments(2);
    }

    @Test
    public void testExecuteLevel2() throws TaskException, IOException {
        SplitByGoToActionLevelParameters parameters = setUpParameters(2, null);
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        assertOutputContainsDocuments(3);
    }

    @Test
    public void testExecuteLevel2MatchingregEx() throws TaskException, IOException {
        SplitByGoToActionLevelParameters parameters = setUpParameters(2, ".+(page)+.+");
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        assertOutputContainsDocuments(2);
    }

    @Test
    public void testExecuteLevel4() throws TaskException {
        SplitByGoToActionLevelParameters parameters = setUpParameters(4, null);
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        TestListenerFailed failListener = new TestListenerFailed();
        ThreadLocalNotificationContext.getContext().addListener(failListener);
        victim.execute(parameters);
        assertTrue(failListener.isFailed());
    }

    private class TestListenerFailed implements EventListener<TaskExecutionFailedEvent> {

        private boolean failed = false;

        public void onEvent(TaskExecutionFailedEvent event) {
            failed = true;
        }

        public boolean isFailed() {
            return failed;
        }
    }
}
