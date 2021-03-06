/**
 * Copyright (C) 2000 - 2013 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of the GPL, you may
 * redistribute this Program in connection with Free/Libre Open Source Software ("FLOSS")
 * applications as described in Silverpeas's FLOSS exception. You should have received a copy of the
 * text describing the FLOSS exception, and it is also available here:
 * "http://www.silverpeas.org/docs/core/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.silverpeas.servlets;

import com.silverpeas.util.FileUtil;
import com.stratelia.silverpeas.peasCore.servlets.SilverpeasAuthenticatedHttpServlet;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.util.FileRepositoryManager;
import com.stratelia.webactiv.util.ResourceLocator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URLDecoder;

import static org.silverpeas.util.data.TemporaryWorkspaceTranslation
    .startWithTranslationDescriptorPrefix;

/**
 * To get files from temp directory
 *
 * @author neysseri
 */
public class TempFileServer extends SilverpeasAuthenticatedHttpServlet {

  private static final long serialVersionUID = 5483484250458795672L;

  @Override
  public void init(ServletConfig config) {
    try {
      super.init(config);
    } catch (ServletException se) {
      SilverTrace.fatal("peasUtil", "TempFileServer.init", "peasUtil.CANNOT_ACCESS_SUPERCLASS");
    }
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException,
      IOException {
    doPost(req, res);
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,
      IOException {
    SilverTrace.info("peasUtil", "TempFileServer.doPost", "root.MSG_GEN_ENTER_METHOD");
    String encodedFileName = req.getPathInfo();
    String fileName = URLDecoder.decode(encodedFileName, "UTF-8");

    // Verifying that the path is a valid one (security)
    if (!isValid(fileName)) {
      throwHttpForbiddenError();
    }

    File requestedFile = new File(FileRepositoryManager.getTemporaryPath(), fileName);
    res.setContentType(FileUtil.getMimeType(fileName));
    res.setHeader("Content-Disposition", "inline; filename=\"" + requestedFile.getName() + '"');
    write(res, requestedFile);

  }

  /**
   * This method writes the result of the preview action.&
   *
   * @param res - The HttpServletResponse where the html code is write
   * @param htmlFilePath - the canonical path of the html document generated by the parser tools. if
   * this String is null that an exception had been catched the html document generated is empty !!
   * also, we display a warning html page
   */
  private void write(HttpServletResponse res, File htmlFilePath) throws IOException {
    OutputStream out2 = res.getOutputStream();
    int read;
    BufferedInputStream input = null; // for the html document generated
    SilverTrace.info("peasUtil", "TempFileServer.write()", "root.MSG_GEN_ENTER_METHOD",
        " htmlFilePath " + htmlFilePath.getPath());
    try {
      input = new BufferedInputStream(new FileInputStream(htmlFilePath));
      read = input.read();
      SilverTrace.info("peasUtil", "TempFileServer.write()", "root.MSG_GEN_ENTER_METHOD",
          " BufferedInputStream read " + read);
      if (read == -1) {
        writeWarningMessage(res);
      } else {
        while (read != -1) {
          out2.write(read); // writes bytes into the response
          read = input.read();
        }
      }
    } catch (Exception e) {
      SilverTrace.warn("peasUtil", "TempFileServer.write", "root.EX_CANT_READ_FILE", "file name="
          + htmlFilePath);
      writeWarningMessage(res);
    } finally {
      // we must close the in and out streams
      try {
        if (input != null) {
          input.close();
        }
        out2.close();
      } catch (Exception e) {
        SilverTrace.warn("peasUtil", "TempFileServer.write", "root.EX_CANT_READ_FILE",
            "close failed");
      }
    }
  }

  private void writeWarningMessage(HttpServletResponse res) throws IOException {
    StringReader sr = null;
    OutputStream out2 = res.getOutputStream();
    int read;
    ResourceLocator resourceLocator = new ResourceLocator(
        "com.stratelia.webactiv.util.peasUtil.multiLang.fileServerBundle", "");

    sr = new StringReader(resourceLocator.getString("warning"));
    try {
      read = sr.read();
      while (read != -1) {
        out2.write(read); // writes bytes into the response
        read = sr.read();
      }
    } catch (Exception e) {
      SilverTrace.warn("peasUtil", "TempFileServer.displayWarningMessage",
          "root.EX_CANT_READ_FILE",
          "warning properties");
    } finally {
      try {
        if (sr != null) {
          sr.close();
        }
        out2.close();
      } catch (Exception e) {
        SilverTrace.warn("peasUtil", "TempFileServer.displayWarningMessage",
            "root.EX_CANT_READ_FILE", "close failed");
      }
    }
  }

  /**
   * Checks the specified path is valid according to some security rules. For example, check there
   * is no attempt to go up the path to access a forbidden resource.
   * @param path the patch to check.
   * @return true if given path is valid, false otherwise.
   */
  private static boolean isValid(String path) {
    return !path.contains("..") && !startWithTranslationDescriptorPrefix(path);
  }
}
