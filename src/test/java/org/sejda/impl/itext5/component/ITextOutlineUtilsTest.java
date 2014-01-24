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

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.sejda.impl.itext5.component.ITextOutlineUtils;

/**
 * @author Andrea Vacondio
 * 
 */
public class ITextOutlineUtilsTest {
    private Map<String, Object> outline;

    @Before
    public void setUp() {
        outline = new HashMap<String, Object>();
    }

    @Test
    public void testGetPageNumberNotExisting() {
        assertEquals(-1, ITextOutlineUtils.getPageNumber(outline));
    }

    @Test
    public void testGetPageNumber() {
        outline.put(ITextOutlineUtils.PAGE_KEY, "10 0 R");
        assertEquals(10, ITextOutlineUtils.getPageNumber(outline));
    }

    @Test
    public void testGetPageNumberWrongString() {
        outline.put(ITextOutlineUtils.PAGE_KEY, "Chuck Norris");
        assertEquals(-1, ITextOutlineUtils.getPageNumber(outline));
    }

    @Test
    public void testGetTitle() {
        outline.put(ITextOutlineUtils.TITLE_KEY, "Chuck");
        assertEquals("Chuck", ITextOutlineUtils.nullSafeGetTitle(outline));
    }

    @Test
    public void testNullOutline() {
        assertEquals("", ITextOutlineUtils.nullSafeGetTitle(null));
    }

    @Test
    public void testNullTitle() {
        outline.put(ITextOutlineUtils.TITLE_KEY, null);
        assertEquals("", ITextOutlineUtils.nullSafeGetTitle(null));
    }
}
