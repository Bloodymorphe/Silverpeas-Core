/*
 * Copyright (C) 2000 - 2015 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception. You should have recieved a copy of the text describing
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
package org.silverpeas.viewer.web;

import org.silverpeas.viewer.DocumentView;
import org.silverpeas.viewer.exception.ViewerException;

import java.io.File;

/**
 * @author Yohann Chastagnier
 */
public class DocumentViewBuilder {

  public static DocumentViewBuilder getDocumentViewBuilder() {
    return new DocumentViewBuilder();
  }

  public DocumentView buildFileName(final String uriId, final String fileName) {
    return new DocumentViewMock(uriId, fileName);
  }

  private DocumentViewBuilder() {
    // Nothing to do
  }

  protected class DocumentViewMock implements DocumentView {

    private final String uriId;
    private final String fileName;

    public DocumentViewMock(final String uriId, final String fileName) {
      this.uriId = uriId;
      this.fileName = fileName;
    }

    /*
     * (non-Javadoc)
     * @see org.silverpeas.viewer.DocumentView#getDisplayLicenseKey()
     */
    @Override
    public String getDisplayLicenseKey() {
      return "licenseKey";
    }

    /*
     * (non-Javadoc)
     * @see org.silverpeas.viewer.DocumentView#getURLAsString()
     */
    @Override
    public String getURLAsString() throws ViewerException {
      return "/URL/" + fileName;
    }

    /*
     * (non-Javadoc)
     * @see org.silverpeas.viewer.DocumentView#getAttachment()
     */
    @Override
    public File getPhysicalFile() {
      return new File("URI/" + uriId);
    }

    /*
     * (non-Javadoc)
     * @see org.silverpeas.viewer.DocumentView#getOriginalFileName()
     */
    @Override
    public String getOriginalFileName() {
      return fileName;
    }

    /*
     * (non-Javadoc)
     * @see org.silverpeas.viewer.DocumentView#getWidth()
     */
    @Override
    public String getWidth() {
      return null;
    }

    /*
     * (non-Javadoc)
     * @see org.silverpeas.viewer.DocumentView#getHeight()
     */
    @Override
    public String getHeight() {
      return null;
    }

    @Override
    public int getNbPages() {
      return 10;
    }

    @Override
    public boolean isDocumentSplit() {
      return true;
    }

    @Override
    public boolean areSearchDataComputed() {
      return true;
    }
  }
}
