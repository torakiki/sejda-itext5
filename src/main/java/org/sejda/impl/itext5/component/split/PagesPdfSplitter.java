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
package org.sejda.impl.itext5.component.split;

import java.io.File;

import org.sejda.core.support.prefix.model.NameGenerationRequest;
import org.sejda.impl.itext5.component.DefaultPdfCopier;
import org.sejda.impl.itext5.component.PdfCopier;
import org.sejda.model.exception.TaskException;
import org.sejda.model.parameter.AbstractSplitByPageParameters;
import org.sejda.model.pdf.PdfVersion;
import org.sejda.model.split.NextOutputStrategy;
import org.sejda.model.split.SplitPages;

import com.itextpdf.text.pdf.PdfReader;

/**
 * Splitter implementation to split at a given set of page numbers.
 * 
 * @author Andrea Vacondio
 * @param <T>
 *            type of the parameter the splitter needs to perform the split.
 */
public class PagesPdfSplitter<T extends AbstractSplitByPageParameters> extends AbstractPdfSplitter<T> {

    private SplitPages splitPages;

    public PagesPdfSplitter(PdfReader reader, T parameters) {
        super(reader, parameters);
        this.splitPages = new SplitPages(parameters.getPages(super.getTotalNumberOfPages()));
    }

    @Override
    PdfCopier openCopier(PdfReader reader, File outputFile, PdfVersion version) throws TaskException {
        return new DefaultPdfCopier(reader, outputFile, version);
    }

    @Override
    NextOutputStrategy nextOutputStrategy() {
        return splitPages;
    }

    @Override
    NameGenerationRequest enrichNameGenerationRequest(NameGenerationRequest request) {
        return request;
    }
}
