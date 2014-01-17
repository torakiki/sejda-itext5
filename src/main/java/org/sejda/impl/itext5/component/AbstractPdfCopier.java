/*
 * Created on 13/jan/2014
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import org.sejda.core.Sejda;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.pdf.PdfVersion;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPageLabels;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfRectangle;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStream;

/**
 * Abstract implementation using an underlying {@link PdfSmartCopy} instance.
 * 
 * @author Andrea Vacondio
 * 
 */
abstract class AbstractPdfCopier implements PdfCopier {

    private PdfSmartCopy pdfCopy = null;
    private Document pdfDocument = null;
    private boolean closed = false;
    private int numberOfCopiedPages = 0;

    /**
     * initialize the copier using the given reader and the given output version.
     * 
     * @param reader
     * @param outputStream
     *            the output stream to write to.
     * @param version
     *            version for the created pdf copy, if null the version number is taken from the input {@link PdfReader}
     */
    void init(PdfReader reader, OutputStream outputStream, PdfVersion version) throws TaskException {
        try {
            pdfDocument = new Document(reader.getPageSizeWithRotation(1));
            pdfCopy = new PdfSmartCopy(pdfDocument, outputStream);
            if (version == null) {
                pdfCopy.setPdfVersion(reader.getPdfVersion());
            } else {
                pdfCopy.setPdfVersion(version.getVersionAsCharacter());
            }
            pdfDocument.addCreator(Sejda.CREATOR);
        } catch (DocumentException e) {
            throw new TaskException("An error occurred opening the PdfSmartCopy.", e);
        }
    }

    public void open() {
        pdfDocument.open();
    }

    public void addPage(PdfReader reader, int pageNumber, PdfRectangle cropBox) throws TaskException {
        PdfImportedPage page = pdfCopy.getImportedPage(reader, pageNumber);
        PdfDictionary dictionary = reader.getPageN(pageNumber);
        dictionary.put(PdfName.MEDIABOX, cropBox);
        dictionary.put(PdfName.CROPBOX, cropBox);
        addPage(page);
    }

    public void addPage(PdfReader reader, int pageNumber) throws TaskException {
        addPage(pdfCopy.getImportedPage(reader, pageNumber));
    }

    private void addPage(PdfImportedPage page) throws TaskException {
        try {
            pdfCopy.addPage(page);
            numberOfCopiedPages++;
        } catch (BadPdfFormatException e) {
            throw new TaskException(String.format("An error occurred adding page %d to the PdfSmartCopy.", page), e);
        } catch (IOException e) {
            throw new TaskIOException(String.format("An IO error occurred adding page %d to the PdfSmartCopy.", page),
                    e);
        }
    }

    public void addBlankPage(PdfReader reader) throws TaskException {
        try {
            pdfCopy.addPage(reader.getPageSize(1), reader.getPageRotation(1));
            numberOfCopiedPages++;
        } catch (DocumentException e) {
            throw new TaskException("Unable to add blank page.", e);
        }
    }

    public void addBlankPageIfOdd(PdfReader reader) throws TaskException {
        if (reader.getNumberOfPages() % 2 != 0) {
            addBlankPage(reader);
        }
    }

    /**
     * Adds to the {@link PdfSmartCopy} all the pages from the input reader
     * 
     * @param reader
     * @throws TaskException
     */
    public void addAllPages(PdfReader reader) throws TaskException {
        try {
            pdfCopy.addDocument(reader);
        } catch (DocumentException e) {
            throw new TaskIOException("An IO error occurred adding pages to the PdfSmartCopy.", e);
        } catch (IOException e) {
            throw new TaskIOException("An IO error occurred adding pages to the PdfSmartCopy.", e);
        }
        numberOfCopiedPages += reader.getNumberOfPages();
    }

    public void setCompression(boolean compress) throws TaskException {
        if (compress) {
            try {
                pdfCopy.setFullCompression();
                pdfCopy.setCompressionLevel(PdfStream.BEST_COMPRESSION);
            } catch (DocumentException de) {
                throw new TaskException("Unable to set compression on copier", de);
            }
        }
    }

    public void freeReader(PdfReader reader) throws TaskIOException {
        try {
            pdfCopy.freeReader(reader);
        } catch (IOException e) {
            throw new TaskIOException("An IO error occurred freeing the pdf reader.", e);
        }
    }

    public void setPageLabels(PdfPageLabels labels) {
        pdfCopy.setPageLabels(labels);
    }

    public void close() {
        if (pdfDocument != null) {
            pdfDocument.close();
        }
        if (pdfCopy != null) {
            pdfCopy.close();
        }
        closed = true;
    }

    public void setOutline(List<HashMap<String, Object>> outline) {
        if (outline != null && !outline.isEmpty()) {
            pdfCopy.setOutlines(outline);
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public int getNumberOfCopiedPages() {
        return numberOfCopiedPages;
    }

    /**
     * Tells the copier to merge fields
     */
    public void mergeFields() {
        pdfCopy.setMergeFields();
    }
}
