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
package org.sejda.impl.itext5.component;

import static org.sejda.impl.itext5.util.TransitionUtils.getTransition;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.sejda.core.Sejda;
import org.sejda.model.exception.TaskException;
import org.sejda.model.exception.TaskIOException;
import org.sejda.model.pdf.PdfMetadataKey;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.pdf.transition.PdfPageTransition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.PdfTransition;

/**
 * Component responsible for handling operations related to a {@link PdfStamper} instance.
 * 
 * @author Andrea Vacondio
 * 
 */
public final class PdfStamperHandler implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(PdfStamperHandler.class);

    private PdfStamper stamper = null;
    private FileOutputStream ouputStream = null;

    /**
     * Creates a new instance initializing the inner {@link PdfStamper} instance.
     * 
     * @param reader
     *            input reader
     * @param ouputFile
     *            {@link File} to stamp on
     * @param version
     *            version for the created stamper, if null the version number is taken from the input {@link PdfReader}
     * @throws TaskException
     *             in case of error
     */
    public PdfStamperHandler(PdfReader reader, File ouputFile, PdfVersion version) throws TaskException {
        try {
            ouputStream = new FileOutputStream(ouputFile);
            if (version != null) {
                stamper = new PdfStamper(reader, ouputStream, version.getVersionAsCharacter());
            } else {
                stamper = new PdfStamper(reader, ouputStream);
            }
            Map<String, String> meta = reader.getInfo();
            meta.put(PdfMetadataKey.CREATOR.getKey(), Sejda.CREATOR);
            stamper.setMoreInfo(meta);
        } catch (DocumentException e) {
            throw new TaskException("An error occurred opening the PdfStamper.", e);
        } catch (IOException e) {
            throw new TaskIOException("An IO error occurred opening the PdfStamper.", e);
        }
    }

    /**
     * Enables compression if compress is true
     * 
     * @param compress
     * @throws TaskException
     * @throws DocumentException
     */
    public void setCompression(boolean compress, PdfReader reader) throws TaskException {
        if (compress) {
            try {
                stamper.getWriter().setCompressionLevel(PdfStream.BEST_COMPRESSION);
                int total = reader.getNumberOfPages() + 1;
                for (int i = 1; i < total; i++) {
                    reader.setPageContent(i, reader.getPageContent(i));
                }
                stamper.setFullCompression();
            } catch (DocumentException de) {
                throw new TaskException("Unable to set compression on stamper", de);
            } catch (IOException e) {
                throw new TaskException("Unable to set compression on stamper", e);
            }
        }
    }

    public void close() throws IOException {
        try {
            stamper.close();
        } catch (DocumentException e) {
            LOG.error("Error closing the PdfStamper.", e);
        }
        IOUtils.closeQuietly(ouputStream);
    }

    /**
     * Adds the input set of metadata to the info dictionary that will be written by the {@link PdfStamper}
     * 
     * @param meta
     */
    public void setMetadata(Set<Entry<PdfMetadataKey, String>> meta) {
        Map<String, String> info = stamper.getMoreInfo();
        for (Entry<PdfMetadataKey, String> current : meta) {
            LOG.trace("'{}' -> '{}'", current.getKey().getKey(), current.getValue());
            info.put(current.getKey().getKey(), current.getValue());
        }
    }

    /**
     * Sets the encryption for this document delegating encryption to the stamper.
     * 
     * @see PdfStamper#setEncryption(int, String, String, int)
     * @param encryptionType
     * @param userPassword
     * @param ownerPassword
     * @param permissions
     * @throws TaskException
     *             wraps the {@link DocumentException} that can be thrown by the stamper
     */
    public void setEncryption(int encryptionType, String userPassword, String ownerPassword, int permissions)
            throws TaskException {
        try {
            stamper.setEncryption(encryptionType, userPassword, ownerPassword, permissions);
        } catch (DocumentException e) {
            throw new TaskException("An error occured while setting encryption on the document", e);
        }
    }

    /**
     * Applies the given transition to the given page.
     * 
     * @param page
     * @param transition
     */
    public void setTransition(Integer page, PdfPageTransition transition) {
        Integer transitionStyle = getTransition(transition.getStyle());
        if (transitionStyle != null) {
            stamper.setDuration(transition.getDisplayDuration(), page);
            stamper.setTransition(new PdfTransition(transitionStyle, transition.getTransitionDuration()), page);
        } else {
            LOG.warn("Transition {} not applied to page {}. Not supported by iText.", transition.getStyle(), page);
        }
    }

    /**
     * Sets the viewer preferences on the stamper
     * 
     * @see PdfStamper#setViewerPreferences(int)
     * @param preferences
     */
    public void setViewerPreferences(int preferences) {
        stamper.setViewerPreferences(preferences);
    }

    /**
     * adds the viewer preferences to the stamper
     * 
     * @see PdfStamper#addViewerPreference(PdfName, PdfObject)
     * @param preferences
     *            a map of preferences with corresponding value to be set on the documents
     */
    public void addViewerPreferences(Map<PdfName, PdfObject> preferences) {
        for (Entry<PdfName, PdfObject> entry : preferences.entrySet()) {
            stamper.addViewerPreference(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 
     * @return the inner {@link PdfStamper} instance
     */
    public PdfStamper getStamper() {
        return stamper;
    }
}
