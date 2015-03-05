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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sejda.model.exception.TaskException;
import org.sejda.model.outline.OutlineSubsetProvider;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;

/**
 * iText implementation of a bookmarks subset provider.
 * 
 * @author Andrea Vacondio
 * 
 */
public class ITextOutlineSubsetProvider implements OutlineSubsetProvider<List<HashMap<String, Object>>> {
    private int totalNumberOfPages;
    private List<HashMap<String, Object>> bookmarks;
    private int startPage = -1;

    public ITextOutlineSubsetProvider(PdfReader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("Unable to retrieve bookmarks from a null reader.");
        }

        this.totalNumberOfPages = reader.getNumberOfPages();
        this.bookmarks = getBookmarksOrEmpty(reader);
    }

    private List<HashMap<String, Object>> getBookmarksOrEmpty(PdfReader reader) {
        List<HashMap<String, Object>> documentBookmarks = SimpleBookmark.getBookmark(reader);
        if (documentBookmarks != null) {
            return Collections.unmodifiableList(documentBookmarks);
        }
        return Collections.emptyList();
    }

    public void startPage(int startPage) {
        this.startPage = startPage;
    }

    public List<HashMap<String, Object>> getOutlineUntillPage(int endPage) throws TaskException {
        return getOutlineUntillPageWithOffset(endPage, 0);
    }

    public List<HashMap<String, Object>> getOutlineWithOffset(int offset) {
        List<HashMap<String, Object>> books = getDeepCopyBookmarks(bookmarks);
        if (offset != 0) {
            SimpleBookmark.shiftPageNumbers(books, offset, null);
        }
        return books;
    }

    public List<HashMap<String, Object>> getOutlineUntillPageWithOffset(int endPage, int offset)
            throws TaskException {
        if (startPage < 0 || startPage > endPage) {
            throw new TaskException(
                    "Unable to process document bookmarks: start page is negative or higher then end page.");
        }
        if (bookmarks.isEmpty()) {
            return Collections.emptyList();
        }
        List<HashMap<String, Object>> books = getDeepCopyBookmarks(bookmarks);
        if (endPage < totalNumberOfPages) {
            SimpleBookmark.eliminatePages(books, new int[] { endPage + 1, totalNumberOfPages });
        }
        if (startPage > 1) {
            SimpleBookmark.eliminatePages(books, new int[] { 1, startPage - 1 });
            SimpleBookmark.shiftPageNumbers(books, -(startPage - 1), null);
        }
        if (offset != 0) {
            SimpleBookmark.shiftPageNumbers(books, offset, null);
        }
        return books;
    }

    @SuppressWarnings("unchecked")
    private List<HashMap<String, Object>> getDeepCopyBookmarks(List<?> inputBook) {
        List<HashMap<String, Object>> retVal = new ArrayList<HashMap<String, Object>>();
        for (Map<String, Object> item : (List<Map<String, Object>>) inputBook) {
            retVal.add(getCopyMap(item));
        }
        return retVal;
    }

    private HashMap<String, Object> getCopyMap(Map<String, Object> map) {
        HashMap<String, Object> retVal = new HashMap<String, Object>();
        if (map != null) {
            for (Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof List) {
                    retVal.put(entry.getKey(), getDeepCopyBookmarks((List<?>) entry.getValue()));
                } else {
                    retVal.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return retVal;
    }
}
