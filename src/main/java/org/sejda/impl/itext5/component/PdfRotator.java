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
package org.sejda.impl.itext5.component;

import static org.sejda.model.rotation.Rotation.getRotation;

import org.sejda.model.rotation.PageRotation;
import org.sejda.model.rotation.RotationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;

/**
 * Handles rotations on a given PdfReader
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PdfRotator implements OngoingRotation {

    private static final Logger LOG = LoggerFactory.getLogger(PdfRotator.class);

    private PdfReader reader;
    private PageRotation rotation;

    private PdfRotator(PageRotation rotation) {
        this.rotation = rotation;
    }

    /**
     * DSL entry point to apply a rotation
     * <p>
     * <code>applyRotation(rotation).to(reader);</code>
     * </p>
     * 
     * @param rotation
     * @return the ongoing apply rotation exposing methods to set the reader you want to apply the rotation to.
     */
    public static OngoingRotation applyRotation(PageRotation rotation) {
        return new PdfRotator(rotation);
    }

    public void to(PdfReader reader) {
        this.reader = reader;
        executeRotation();
    }

    /**
     * Apply the rotation to the dictionary of the given {@link PdfReader}
     */
    private void executeRotation() {
        RotationType type = rotation.getRotationType();
        LOG.debug("Applying rotation of {} to pages {}", rotation.getRotation().getDegrees(), type);
        if (RotationType.SINGLE_PAGE.equals(type)) {
            apply(rotation.getPageNumber());
        } else {
            for (int j = 1; j <= reader.getNumberOfPages(); j++) {
                apply(j);
            }
        }
    }

    /**
     * apply the rotation to the given page if necessary
     * 
     * @param pageNmber
     */
    private void apply(int pageNmber) {
        if (rotation.accept(pageNmber)) {
            int pageRotation = reader.getPageRotation(pageNmber);
            PdfDictionary dictionary = reader.getPageN(pageNmber);
            dictionary.put(PdfName.ROTATE,
                    new PdfNumber(rotation.getRotation().addRotation(getRotation(pageRotation))
                            .getDegrees()));
        }
    }
}
