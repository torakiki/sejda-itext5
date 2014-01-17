/*
 * Created on 16/gen/2014
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sejda.core.context.DefaultSejdaContext;
import org.sejda.core.context.SejdaContext;
import org.sejda.core.service.DefaultTaskExecutionService;
import org.sejda.impl.TestUtils;
import org.sejda.impl.itext5.util.ITextUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.outline.OutlinePolicy;
import org.sejda.model.parameter.MergeParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.task.Task;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;

/**
 * @author Andrea Vacondio
 * 
 */
public class MergeIText5TaskTest extends BaseTaskTest {
    private DefaultTaskExecutionService victim = new DefaultTaskExecutionService();
    private SejdaContext context = mock(DefaultSejdaContext.class);
    private MergeParameters parameters;
    private Task<MergeParameters> victimTask = new MergeTask();

    @Before
    public void setUp() throws IOException {
        TestUtils.setProperty(victim, "context", context);
        parameters = new MergeParameters();
        parameters.setOverwrite(true);
        parameters.setCompress(true);
        parameters.setVersion(PdfVersion.VERSION_1_6);
        parameters.setOutput(getOutputFile());
    }

    private void inputWithOutline() {
        parameters.addInput(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/large_outline.pdf"), "first_test_file.pdf")));
        parameters.addInput(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/large_test.pdf"), "large_test.pdf")));
    }

    private void inputWithEncrypted() {
        parameters.addInput(new PdfMergeInput(PdfStreamSource.newInstanceWithPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/enc_with_modify_perm.pdf"), "enc_with_modify_perm.pdf", "test")));
        parameters.addInput(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/large_test.pdf"), "large_test.pdf")));
    }

    private void input() {
        parameters.addInput(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/test_no_outline.pdf"), "first_test_file.pdf")));
        parameters.addInput(new PdfMergeInput(PdfStreamSource.newInstanceNoPassword(getClass().getClassLoader()
                .getResourceAsStream("pdf/attachments.pdf"), "second_test.pdf")));
    }

    @Test
    public void executeMergeAllWithOutlineRetainingOutline() throws TaskException, IOException {
        inputWithOutline();
        parameters.setOutlinePolicy(OutlinePolicy.RETAIN);
        doExecuteMergeAll(true, 311);
    }

    @Test
    public void executeMergeAllWithEncryptedRetainingOutline() throws TaskException, IOException {
        inputWithEncrypted();
        doExecuteMergeAll(true, 310);
    }

    @Test
    public void executeMergeAllRetainingOutline() throws TaskException, IOException {
        input();
        doExecuteMergeAll(false, 4);
    }

    @Test
    public void executeMergeAllWithOutlineDiscardingOutline() throws TaskException, IOException {
        inputWithOutline();
        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        doExecuteMergeAll(false, 311);
    }

    @Test
    public void executeMergeAllDiscardingOutline() throws TaskException, IOException {
        input();
        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        doExecuteMergeAll(false, 4);
    }

    @Test
    public void executeMergeAllWithEncryptedDiscardingOutline() throws TaskException, IOException {
        inputWithEncrypted();
        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        doExecuteMergeAll(false, 310);
    }

    @Test
    public void executeMergeAllWithOutlineOnePerDoc() throws TaskException, IOException {
        inputWithOutline();
        parameters.setOutlinePolicy(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        doExecuteMergeAll(true, 311);
    }

    @Test
    public void executeMergeAllOnePerDoc() throws TaskException, IOException {
        input();
        parameters.setOutlinePolicy(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        doExecuteMergeAll(true, 4);
    }

    @Test
    public void executeMergeAllWithEncryptedOnePerDoc() throws TaskException, IOException {
        inputWithEncrypted();
        parameters.setOutlinePolicy(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        doExecuteMergeAll(true, 310);
    }

    @Test
    @Ignore
    // TODO investigate merge fields
    public void executeMergeAllWithEncryptedOnePerDocCopyFields() throws TaskException, IOException {
        inputWithEncrypted();
        TestUtils.setProperty(parameters, "copyFormFields", Boolean.TRUE);
        parameters.setOutlinePolicy(OutlinePolicy.ONE_ENTRY_EACH_DOC);
        doExecuteMergeAll(true, 310);
    }

    @Test
    @Ignore
    // TODO investigate merge fields
    public void executeMergeAllWithOutlineDiscardingOutlineCopyFields() throws TaskException, IOException {
        inputWithOutline();
        TestUtils.setProperty(parameters, "copyFormFields", Boolean.TRUE);
        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        doExecuteMergeAll(false, 311);
    }

    @Test
    @Ignore
    // TODO investigate merge fields
    public void executeMergeAllDiscardingOutlineCopyFields() throws TaskException, IOException {
        input();
        TestUtils.setProperty(parameters, "copyFormFields", Boolean.TRUE);
        parameters.setOutlinePolicy(OutlinePolicy.DISCARD);
        doExecuteMergeAll(false, 4);
    }

    void doExecuteMergeAll(boolean hasBookmarks, int pages) throws TaskException, IOException {
        when(context.getTask(parameters)).thenReturn((Task) victimTask);
        victim.execute(parameters);
        PdfReader reader = null;
        try {
            reader = getReaderFromResultFile();
            assertCreator(reader);
            assertEquals(PdfVersion.VERSION_1_6.getVersionAsCharacter(), reader.getPdfVersion());
            assertEquals(pages, reader.getNumberOfPages());
            if (hasBookmarks) {
                assertNotNull(SimpleBookmark.getBookmark(reader));
            } else {
                assertNull(SimpleBookmark.getBookmark(reader));
            }
        } finally {
            ITextUtils.nullSafeClosePdfReader(reader);
        }
    }
}
