/*
 * Created on 11/gen/2014
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
package org.sejda.impl.itext5.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.sejda.model.pdf.transition.PdfPageTransitionStyle;

import com.itextpdf.text.pdf.PdfTransition;

/**
 * Utility class used to deal with transitions mapping.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class TransitionUtils {

    private static final Map<PdfPageTransitionStyle, Integer> TRANSITIONS_STYLES;
    static {
        Map<PdfPageTransitionStyle, Integer> transitionsStyles = new HashMap<PdfPageTransitionStyle, Integer>();
        transitionsStyles.put(PdfPageTransitionStyle.BLINDS_HORIZONTAL, PdfTransition.BLINDH);
        transitionsStyles.put(PdfPageTransitionStyle.BLINDS_VERTICAL, PdfTransition.BLINDV);
        transitionsStyles.put(PdfPageTransitionStyle.SPLIT_HORIZONTAL_INWARD, PdfTransition.SPLITHIN);
        transitionsStyles.put(PdfPageTransitionStyle.SPLIT_HORIZONTAL_OUTWARD, PdfTransition.SPLITHOUT);
        transitionsStyles.put(PdfPageTransitionStyle.SPLIT_VERTICAL_INWARD, PdfTransition.SPLITVIN);
        transitionsStyles.put(PdfPageTransitionStyle.SPLIT_VERTICAL_OUTWARD, PdfTransition.SPLITVOUT);
        transitionsStyles.put(PdfPageTransitionStyle.BOX_INWARD, PdfTransition.INBOX);
        transitionsStyles.put(PdfPageTransitionStyle.BOX_OUTWARD, PdfTransition.OUTBOX);
        transitionsStyles.put(PdfPageTransitionStyle.WIPE_BOTTOM_TO_TOP, PdfTransition.BTWIPE);
        transitionsStyles.put(PdfPageTransitionStyle.WIPE_LEFT_TO_RIGHT, PdfTransition.LRWIPE);
        transitionsStyles.put(PdfPageTransitionStyle.WIPE_RIGHT_TO_LEFT, PdfTransition.RLWIPE);
        transitionsStyles.put(PdfPageTransitionStyle.WIPE_TOP_TO_BOTTOM, PdfTransition.TBWIPE);
        transitionsStyles.put(PdfPageTransitionStyle.DISSOLVE, PdfTransition.DISSOLVE);
        transitionsStyles.put(PdfPageTransitionStyle.GLITTER_DIAGONAL, PdfTransition.DGLITTER);
        transitionsStyles.put(PdfPageTransitionStyle.GLITTER_LEFT_TO_RIGHT, PdfTransition.LRGLITTER);
        transitionsStyles.put(PdfPageTransitionStyle.GLITTER_TOP_TO_BOTTOM, PdfTransition.TBGLITTER);
        TRANSITIONS_STYLES = Collections.unmodifiableMap(transitionsStyles);
    }

    private TransitionUtils() {
        // utility
    }

    /**
     * Mapping between Sejda transition style enum and iText constants.<br>
     * Not all the possible transition styles are available in iText so this method can return null if a mapping is not found.
     * 
     * @param transition
     * @return the iText constant or null of no constant is found.
     */
    public static Integer getTransition(PdfPageTransitionStyle transition) {
        return TRANSITIONS_STYLES.get(transition);
    }
}
