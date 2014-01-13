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

import org.junit.Before;
import org.junit.Test;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.service.DefaultTaskExecutionService;
import org.sejda.impl.TestUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.parameter.ViewerPreferencesParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.viewerpreference.PdfBooleanPreference;
import org.sejda.model.pdf.viewerpreference.PdfDirection;
import org.sejda.model.pdf.viewerpreference.PdfDuplex;
import org.sejda.model.pdf.viewerpreference.PdfNonFullScreenPageMode;
import org.sejda.model.pdf.viewerpreference.PdfPageLayout;
import org.sejda.model.pdf.viewerpreference.PdfPageMode;
import org.sejda.model.pdf.viewerpreference.PdfPrintScaling;
import org.sejda.model.task.Task;

import com.itextpdf.text.pdf.PdfBoolean;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.internal.PdfViewerPreferencesImp;

/**
 * @author Andrea Vacondio
 *
 */
public class ViewerPreferencesIText5TaskTest extends BaseTaskTest {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();
    private SejdaContext context = mock(DefaultSejdaContext.class);
    private ViewerPreferencesParameters parameters = new ViewerPreferencesParameters();
    private Task<ViewerPreferencesParameters> victimTask = new ViewerPreferencesTask();

    @Before
    public void setUp() {
        TestUtils.setProperty(victim, "context", context);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_7);
        parameters.setDirection(PdfDirection.LEFT_TO_RIGHT);
        parameters.setDuplex(PdfDuplex.SIMPLEX);
        parameters.setNfsMode(PdfNonFullScreenPageMode.USE_THUMNS);
        parameters.setPageLayout(PdfPageLayout.ONE_COLUMN);
        parameters.setPageMode(PdfPageMode.USE_THUMBS);
        parameters.setPrintScaling(PdfPrintScaling.APP_DEFAULT);
        parameters.addEnabledPreference(PdfBooleanPreference.CENTER_WINDOW);
        parameters.addEnabledPreference(PdfBooleanPreference.HIDE_MENUBAR);
        parameters.setOutput(getOutput());
    }

    @Test
    public void testExecute() throws TaskException, IOException {
        parameters.addSource(getSource());
        doExecute();
    }

    @Test
    public void testExecuteEncrypted() throws TaskException, IOException {
        parameters.addSource(getEncryptedSource());
        doExecute();
    }

    private void doExecute() throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        PdfReader reader = getReaderFromResultStream("test_file.pdf");
        assertCreator(reader);
        assertEquals(PdfVersion.VERSION_1_7.getVersionAsCharacter(), reader.getPdfVersion());
        PdfDictionary catalog = PdfViewerPreferencesImp.getViewerPreferences(reader.getCatalog())
                .getViewerPreferences();
        assertEquals(PdfName.SIMPLEX, catalog.getAsName(PdfName.DUPLEX));
        assertEquals(PdfName.L2R, catalog.getAsName(PdfName.DIRECTION));
        assertEquals(PdfName.APPDEFAULT, catalog.getAsName(PdfName.PRINTSCALING));
        assertEquals(PdfName.USETHUMBS, catalog.getAsName(PdfName.NONFULLSCREENPAGEMODE));
        assertEquals(PdfBoolean.PDFTRUE, catalog.getAsBoolean(PdfName.CENTERWINDOW));
        assertEquals(PdfBoolean.PDFTRUE, catalog.getAsBoolean(PdfName.HIDEMENUBAR));
        assertEquals(PdfBoolean.PDFFALSE, catalog.getAsBoolean(PdfName.HIDETOOLBAR));
        reader.close();
    }
}
