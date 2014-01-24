/*
 * Created on 13/gen/2014
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.sejda.model.exception.TaskException;
import org.sejda.model.pdf.PdfVersion;

import com.itextpdf.text.pdf.PdfReader;

/**
 * Default pdf copier implementation using a {@link File} as output.
 * 
 * @author Andrea Vacondio
 * 
 */
public class DefaultPdfCopier extends AbstractPdfCopier {

    private OutputStream outputStream;

    /**
     * Creates a copier that writes to the given output file.
     * 
     * @param reader
     * @param outputFile
     * @param version
     *            version for the created pdf copy, if null the version number is taken from the input PdfReader.
     * @throws TaskException
     *             if the file is not found or an error occur opening the underlying copier.
     */
    public DefaultPdfCopier(PdfReader reader, File outputFile, PdfVersion version) throws TaskException {
        try {
            outputStream = new FileOutputStream(outputFile);
            init(reader, outputStream, version);
        } catch (FileNotFoundException e) {
            throw new TaskException(String.format("Unable to find the output file %s", outputFile.getPath()), e);
        }
    }

    @Override
    public void close() {
        super.close();
        IOUtils.closeQuietly(outputStream);
    }
}
