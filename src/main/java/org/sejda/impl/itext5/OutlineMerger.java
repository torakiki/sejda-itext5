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
package org.sejda.impl.itext5;

import static org.apache.commons.io.FilenameUtils.removeExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.sejda.impl.itext5.component.ITextOutlineSubsetProvider;
import org.sejda.impl.itext5.component.ITextOutlineUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.outline.OutlinePolicy;
import org.sejda.model.outline.OutlineSubsetProvider;
import org.sejda.model.pdf.page.PageRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfString;

/**
 * Helper class for the {@link MergeTask} creating the new document outline based on the selected {@link OutlinePolicy}
 * 
 * @author Andrea Vacondio
 * 
 */
class OutlineMerger {
    private static final Logger LOG = LoggerFactory.getLogger(OutlineMerger.class);

    private OutlinePolicy policy;
    private List<HashMap<String, Object>> outline = new ArrayList<HashMap<String, Object>>();

    OutlineMerger(OutlinePolicy policy) {
        this.policy = policy;
    }

    void updateOutline(PdfReader reader, PdfMergeInput input, int offset) throws TaskException {
        switch (policy) {
        case ONE_ENTRY_EACH_DOC:
            updateOneEntryPerDoc(input, offset);
            break;
        case RETAIN:
            updateRetainingOutline(reader, input, offset);
            break;
        default:
            LOG.debug("Discarding outline");
        }
    }

    private void updateOneEntryPerDoc(PdfMergeInput input, int offset) {
        String name = input.getSource().getName();
        if (StringUtils.isNotBlank(name)) {
            LOG.debug("Adding outline entry for {}", name);
            HashMap<String, Object> current = new HashMap<String, Object>();
            current.put(ITextOutlineUtils.TITLE_KEY, new PdfString(removeExtension(name)).toUnicodeString());
            current.put(ITextOutlineUtils.PAGE_KEY, Integer.toString(offset + 1));
            current.put(ITextOutlineUtils.ACTION_KEY, ITextOutlineUtils.GOTO_VALUE);
            outline.add(current);
        } else {
            LOG.warn("Outline entry not created, unable to find its name.");
        }
    }

    public List<HashMap<String, Object>> getOutline() {
        return outline;
    }

    private void updateRetainingOutline(PdfReader reader, PdfMergeInput input, int offset) throws TaskException {
        LOG.debug("Retaining outline");
        OutlineSubsetProvider<List<HashMap<String, Object>>> outlineProvider = new ITextOutlineSubsetProvider(reader);
        if (input.isAllPages()) {
            LOG.trace("Adding complete outline");
            outline.addAll(outlineProvider.getOutlineWithOffset(offset));
        } else {
            for (PageRange range : input.getPageSelection()) {
                outlineProvider.startPage(range.getStart());
                LOG.trace("Adding outline for {}", range);
                outline.addAll(outlineProvider.getOutlineUntillPageWithOffset(range.getEnd(), offset));
            }
        }
    }
}
