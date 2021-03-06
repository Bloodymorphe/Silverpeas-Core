/*
 * Copyright (C) 2000 - 2013 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://www.silverpeas.org/docs/core/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.silverpeas.viewer;

import com.silverpeas.annotation.Service;
import com.silverpeas.converter.DocumentFormat;
import com.silverpeas.converter.DocumentFormatConverterFactory;
import com.silverpeas.util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.silverpeas.viewer.exception.ViewerException;
import org.silverpeas.viewer.flexpaper.TemporaryFlexPaperView;
import org.silverpeas.viewer.util.DocumentInfo;
import org.silverpeas.viewer.util.JsonPdfUtil;
import org.silverpeas.viewer.util.SwfUtil;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

import static org.silverpeas.viewer.ViewerSettings.isSilentConversionEnabled;
import static org.silverpeas.viewer.ViewerSettings.isSplitStrategyEnabled;
import static org.silverpeas.viewer.util.SwfUtil.SWF_DOCUMENT_EXTENSION;

/**
 * @author Yohann Chastagnier
 */
@Service
public class DefaultViewService extends AbstractViewerService implements ViewService {

  private static final String PROCESS_NAME = "VIEW";

  @Inject
  private PreviewService previewService;

  /*
   * (non-Javadoc)
   * @see org.silverpeas.viewer.ViewService#isViewable(java.io.File)
   */
  @Override
  public boolean isViewable(final File file) {
    final String fileName = file.getPath();
    return (SwfUtil.isPdfToSwfActivated() && file.exists() && (FileUtil.isPdf(fileName) || FileUtil
        .isOpenOfficeCompatible(fileName)));
  }

  /*
   * (non-Javadoc)
   * @see org.silverpeas.viewer.ViewService#getDocumentView(java.lang.String, java.io.File)
   */
  @Override
  public DocumentView getDocumentView(final ViewerContext viewerContext) {
    return process(PROCESS_NAME, new ViewerTreatment<DocumentView>() {
      @Override
      public DocumentView execute() {

        // Checking
        if (!isViewable(viewerContext.getOriginalSourceFile())) {
          throw new ViewerException("IT IS NOT POSSIBLE GETTING DOCUMENT VIEW");
        }

        final File pdfFile;

        /*
         * 1 - converting it into PDF document
         */

        // If the document is an Open Office one
        if (FileUtil.isOpenOfficeCompatible(viewerContext.getOriginalSourceFile().getName())) {
          pdfFile = toPdf(viewerContext.getOriginalSourceFile(),
              generateTmpFile(viewerContext, PDF_DOCUMENT_EXTENSION));
        }

        // If the document is a PDF (or plain text)
        else {
          pdfFile = generateTmpFile(viewerContext, PDF_DOCUMENT_EXTENSION);
          try {
            FileUtils.copyFile(viewerContext.getOriginalSourceFile(), pdfFile);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

        /*
         * 2 - converting the previous result into SWF file
         */
        final DocumentView documentView = toSwf(viewerContext.getOriginalFileName(), pdfFile);
        FileUtils.deleteQuietly(pdfFile);

        // Returning the result
        return documentView;
      }

      @Override
      public DocumentView performAfterSuccess(final DocumentView result) {
        if (isSilentConversionEnabled() && viewerContext.isProcessingCache() &&
            previewService.isPreviewable(viewerContext.getOriginalSourceFile())) {
          Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
              previewService.getPreview(viewerContext.clone());
            }
          });
          thread.start();
        }
        return super.performAfterSuccess(result);
      }
    }).execute(viewerContext);
  }

  /**
   * Convert into PDF
   * @param source
   * @return
   */
  private File toPdf(final File source, final File destination) {
    DocumentFormatConverterFactory.getFactory().getToPDFConverter()
        .convert(source, destination, DocumentFormat.pdf);
    return destination;
  }

  /**
   * Convert into Swf file
   * @param pdfSource
   * @return
   */
  private DocumentView toSwf(final String originalFileName, final File pdfSource) {

    // NbPages & max page width & max page height
    final DocumentInfo info = SwfUtil.getPdfDocumentInfo(pdfSource);

    File swfFile = changeFileExtension(pdfSource, SWF_DOCUMENT_EXTENSION);
    swfFile.getParentFile().mkdirs();

    boolean jsonConversion = JsonPdfToolManager.isActivated();
    boolean splitMode = isSplitStrategyEnabled();

    // Create SWF data
    if (!splitMode) {
      try {
        SwfUtil.fromPdfToSwf(pdfSource, swfFile, false);
        jsonConversion = false;
      } catch (Exception e) {
        e.printStackTrace();
        splitMode = true;
      }
    }

    if (splitMode) {
      swfFile = new File(swfFile.getParentFile(), "page." + SWF_DOCUMENT_EXTENSION);
      SwfUtil.fromPdfToSwf(pdfSource, swfFile, true);
      if (jsonConversion) {
        try {
          JsonPdfUtil.convert(pdfSource, swfFile);
        } catch (Exception e) {
          e.printStackTrace();
          jsonConversion = false;
        }
      }
    }

    // Files
    TemporaryFlexPaperView documentView =
        new TemporaryFlexPaperView(originalFileName, swfFile, info);
    documentView.markDocumentSplit(splitMode);
    documentView.markSearchDataComputed(jsonConversion);
    return documentView;
  }
}
