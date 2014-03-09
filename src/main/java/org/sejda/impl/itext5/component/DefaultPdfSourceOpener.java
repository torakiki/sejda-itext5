/*
 * Created on 10/gen/2014
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

import java.io.IOException;

import org.sejda.model.exception.TaskIOException;
import org.sejda.model.exception.TaskWrongPasswordException;
import org.sejda.model.input.PdfFileSource;
import org.sejda.model.input.PdfSourceOpener;
import org.sejda.model.input.PdfStreamSource;
import org.sejda.model.input.PdfURLSource;

import com.itextpdf.text.exceptions.BadPasswordException;
import com.itextpdf.text.io.RandomAccessSource;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;

/**
 * Default implementation for a PdfSourceOpener returning a {@link PdfReader}
 * 
 * @author Andrea Vacondio
 * 
 */
public class DefaultPdfSourceOpener implements PdfSourceOpener<PdfReader> {
    public static final String UNETHICAL_PROPERTY_NAME = "org.sejda.impl.itext.unethicalread";
    private static final RandomAccessSourceFactory FACTORY = new RandomAccessSourceFactory();
    static {
        PdfReader.unethicalreading = Boolean.getBoolean(UNETHICAL_PROPERTY_NAME);
    }

    public PdfReader open(PdfURLSource source) throws TaskIOException {
        try {
            return doOpen(FACTORY.createSource(source.getSource()), source.getPasswordBytes());
        } catch (IOException e) {
            throw new TaskIOException("An error occurred opening the reader.", e);
        }
    }

    public PdfReader open(PdfFileSource source) throws TaskIOException {
        try {
            return doOpen(FACTORY.createBestSource(source.getSource().getAbsolutePath()), source.getPasswordBytes());
        } catch (IOException e) {
            throw new TaskIOException("An error occurred opening the reader.", e);
        }
    }

    public PdfReader open(PdfStreamSource source) throws TaskIOException {
        try {
            return doOpen(FACTORY.createSource(source.getSource()), source.getPasswordBytes());
        } catch (IOException e) {
            throw new TaskIOException("An error occurred opening the reader.", e);
        }
    }

    private PdfReader doOpen(RandomAccessSource ras, byte[] pwd) throws TaskIOException, IOException {
        PdfReader reader;
        try {
            reader = new PdfReader(new RandomAccessFileOrArray(ras), pwd);
        } catch (BadPasswordException bpe) {
            throw new TaskWrongPasswordException("Unable to open the document due to a wrong password.", bpe);
        }

        reader.removeUnusedObjects();
        reader.consolidateNamedDestinations();
        return reader;
    }

}
