/*
 * Created on 11/gen/2014
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
package org.sejda.impl.itext5.util;

import com.itextpdf.text.pdf.PdfReader;

/**
 * Provides utility methods to handle iText related components.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class ITextUtils {

    private ITextUtils() {
        // on purpose
    }

    /**
     * Null safe close of the input {@link PdfReader}
     * 
     * @param pdfReader
     */
    public static void nullSafeClosePdfReader(PdfReader pdfReader) {
        if (pdfReader != null) {
            pdfReader.close();
        }
    }
}
