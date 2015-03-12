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
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.sejda.impl.itext5.component.ITextOutlineUtils.KIDS_KEY;
import static org.sejda.impl.itext5.component.ITextOutlineUtils.getMaxBookmarkLevel;
import static org.sejda.impl.itext5.component.ITextOutlineUtils.getPageNumber;
import static org.sejda.impl.itext5.component.ITextOutlineUtils.isGoToAction;
import static org.sejda.impl.itext5.component.ITextOutlineUtils.nullSafeGetTitle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sejda.model.outline.OutlineLevelsHandler;
import org.sejda.model.outline.OutlinePageDestinations;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;

/**
 * iText implementation of an {@link OutlineLevelsHandler}.
 * 
 * @author Andrea Vacondio
 * 
 */
public class ITextOutlineLevelsHandler implements OutlineLevelsHandler {

    private Pattern titleMatchingPattern = Pattern.compile(".+");
    private List<HashMap<String, Object>> bookmarks;

    public ITextOutlineLevelsHandler(PdfReader reader, String matchingTitleRegEx) {
        reader.consolidateNamedDestinations();
        this.bookmarks = SimpleBookmark.getBookmark(reader);
        if (isNotBlank(matchingTitleRegEx)) {
            titleMatchingPattern = Pattern.compile(matchingTitleRegEx);
        }
    }

    public int getMaxOutlineDepth() {
        return getMaxBookmarkLevel(bookmarks, 0);
    }

    public OutlinePageDestinations getPageDestinationsForLevel(int goToActionLevel) {
        OutlinePageDestinations destinations = new OutlinePageDestinations();
        addPageIfBookmarkLevel(bookmarks, 1, destinations, goToActionLevel);
        return destinations;
    }

    @SuppressWarnings("unchecked")
    private void addPageIfBookmarkLevel(List<HashMap<String, Object>> bookmarks, int currentLevel,
            OutlinePageDestinations destinations, int levelToAdd) {
        if (bookmarks != null) {
            for (Map<String, Object> bookmark : bookmarks) {
                if (currentLevel <= levelToAdd && isGoToAction(bookmark)) {
                    if (isLevelToBeAdded(currentLevel, levelToAdd)) {
                        addPageIfValid(destinations, bookmark);
                    } else {
                        addPageIfBookmarkLevel((List<HashMap<String, Object>>) bookmark.get(KIDS_KEY),
                                currentLevel + 1,
                                destinations, levelToAdd);
                    }
                }
            }
        }
    }

    private boolean isLevelToBeAdded(int currentLevel, int levelToAdd) {
        return currentLevel == levelToAdd;
    }

    private void addPageIfValid(OutlinePageDestinations destinations, Map<String, Object> bookmark) {
        int page = getPageNumber(bookmark);
        String title = nullSafeGetTitle(bookmark);
        Matcher matcher = titleMatchingPattern.matcher(title);
        if (page != -1 && matcher.matches()) {
            destinations.addPage(page, title);
        }
    }

}
