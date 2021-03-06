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
package org.silverpeas.upload.web;

import com.silverpeas.accesscontrol.ComponentAccessController;
import com.silverpeas.annotation.Authenticated;
import com.silverpeas.annotation.RequestScoped;
import com.silverpeas.annotation.Service;
import com.silverpeas.util.StringUtil;
import com.silverpeas.web.RESTWebService;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.util.FileRepositoryManager;
import com.stratelia.webactiv.util.ResourceLocator;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.silverpeas.admin.component.parameter.ComponentFileFilterParameter;
import org.silverpeas.servlet.RequestParameterDecoder;
import org.silverpeas.upload.UploadSession;
import org.silverpeas.upload.UploadSessionFile;
import org.silverpeas.util.NotifierUtil;
import org.silverpeas.util.UnitUtil;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static org.silverpeas.web.util.IFrameAjaxTransportUtil.*;

/**
 * A REST Web resource that permits to upload files. It has to be used with one of the following
 * plugins:
 * <ul>
 * <li>silverpeas-filUpload.js: useful to handle file upload on resource creation</li>
 * <li>silverpeas-ddUpload.js: useful to handle drag & drop file upload on existing resources</li>
 * </ul>
 * @author Yohann Chastagnier
 */
@Service
@RequestScoped
@Path("fileUpload")
@Authenticated
public class FileUploadResource extends RESTWebService {

  private static final Semaphore requestLimit = new Semaphore(50, true);

  @Inject
  private ComponentAccessController componentAccessController;

  /**
   * Performs some verifications before starting a file upload.
   * All the verifications are checked again on the effective upload (security).
   * @return
   */
  @POST
  @Path("verify")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response verify() {
    FileUploadVerifyData fileUploadVerifyData =
        RequestParameterDecoder.decode(getHttpRequest(), FileUploadVerifyData.class);
    checkMaximumFileSize(fileUploadVerifyData.getName(), fileUploadVerifyData.getSize());
    checkAuthorizedMimeTypes(fileUploadVerifyData.getName());
    return Response.ok().build();
  }

  /**
   * Permits to upload files from multipart http request.
   * If the user isn't authenticated, a 401 HTTP code is returned.
   * If a problem occurs when processing the request, a 503 HTTP code is returned.
   * @return the response in relation with jQuery plugins used on the client side: a html textarea
   * tag that contains a JSON array structure. Each line of this array contains
   * information of an uploaded file :<br/>
   * - <b>fileId</b> : the uploaded file identifier<br/>
   * - <b>name</b> : the name of the uploaded file (without its path)<br/>
   * - <b>size</b> : the byte size of the uploaded file<br/>
   * - <b>formattedSize</b> : the formatted file size according to the language of
   * user<br/>
   * - <b>iconUrl</b> : the url of the icon that represents the type of the uploaded
   * file<br/>
   */
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_HTML)
  public Response uploadFiles() throws IOException {
    UploadedRequestFile uploadedRequestFile =
        RequestParameterDecoder.decode(getHttpRequest(), UploadedRequestFile.class);

    try {
      List<JSONObject> jsonFiles = new ArrayList<JSONObject>();
      jsonFiles.add(uploadFile(FileUploadData.from(uploadedRequestFile),
          uploadedRequestFile.getRequestFile().getInputStream()));
      return Response.ok().entity(packJSonDataWithHtmlContainer(jsonFiles)).build();
    } catch (WebApplicationException ex) {
      if (AJAX_IFRAME_TRANSPORT.equals(uploadedRequestFile.getXRequestedWith()) &&
          ex.getResponse().getStatus() == Response.Status.PRECONDITION_FAILED.getStatusCode()) {

        // In case of file upload performed by Ajax IFrame transport way,
        // the exception must also be returned into a text/html response.
        ex = createWebApplicationExceptionWithJSonErrorInHtmlContainer(ex);
      }
      throw ex;
    } catch (final Exception ex) {
      throw new WebApplicationException(ex, Response.Status.SERVICE_UNAVAILABLE);
    }
  }

  /**
   * Permits to upload one file from http request.
   * If the user isn't authenticated, a 401 HTTP code is returned.
   * If a problem occurs when processing the request, a 503 HTTP code is returned.
   * @return the response in relation with jQuery plugins used on the client side: a html textarea
   * tag that contains a JSON array structure. Each line of this array contains
   * information of an uploaded file :<br/>
   * - <b>fileId</b> : the uploaded file identifier<br/>
   * - <b>name</b> : the name of the uploaded file (without its path)<br/>
   * - <b>size</b> : the byte size of the uploaded file<br/>
   * - <b>formattedSize</b> : the formatted file size according to the language of
   * user<br/>
   * - <b>iconUrl</b> : the url of the icon that represents the type of the uploaded
   * file<br/>
   */
  @POST
  @Consumes(MediaType.APPLICATION_OCTET_STREAM)
  @Produces(MediaType.TEXT_HTML)
  public Response uploadFile(InputStream inputStream) {
    try {
      JSONObject jsonFile = uploadFile(FileUploadData.from(getHttpServletRequest()), inputStream);
      return Response.ok().entity(packJSonDataWithHtmlContainer(jsonFile)).build();
    } catch (final WebApplicationException ex) {
      throw ex;
    } catch (final Exception ex) {
      throw new WebApplicationException(ex, Response.Status.SERVICE_UNAVAILABLE);
    }
  }

  /**
   * Handles the upload of one file.
   * @param fileUploadData
   * @param inputStream
   * @return a JSON representation of the uploaded file. (more information on {@link
   * FileUploadResource#uploadFiles()})
   * @throws IOException
   */
  private JSONObject uploadFile(FileUploadData fileUploadData, InputStream inputStream)
      throws Exception {

    if (StringUtil.isNotDefined(fileUploadData.getFullPath())) {
      throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    // Avoid server overload
    requestLimit.acquire();

    try {
      UploadSession uploadSession = UploadSession.from(fileUploadData.getUploadSessionId());

      if (StringUtil.isDefined(fileUploadData.getComponentInstanceId()) &&
          !componentAccessController
              .isUserAuthorized(getUserDetail().getId(), fileUploadData.getComponentInstanceId())) {
        throw new WebApplicationException(Response.Status.FORBIDDEN);
      }

      if (StringUtil.isDefined(uploadSession.getComponentInstanceId())) {
        if (!uploadSession.getComponentInstanceId()
            .equals(fileUploadData.getComponentInstanceId())) {
          throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
      } else if (StringUtil.isDefined(fileUploadData.getComponentInstanceId())) {
        uploadSession.forComponentInstanceId(fileUploadData.getComponentInstanceId());
      }

      UploadSessionFile uploadSessionFile =
          uploadSession.getUploadSessionFile(fileUploadData.getFullPath());

      // Writing the file on server
      try {
        uploadSessionFile.write(inputStream);
      } catch (IOException ioe) {
        // The file is written currently by an other process
        throw new WebApplicationException(Response.Status.CONFLICT);
      }

      try {
        // Maximum size
        checkMaximumFileSize(fileUploadData.getName(), uploadSessionFile.getServerFile().length());
        // Mime-Type verified on real data
        checkAuthorizedMimeTypes(uploadSessionFile.getServerFile().getPath());
      } catch (Exception e) {
        uploadSession.remove(fileUploadData.getFullPath());
        throw e;
      }

      // JSON response
      return toJSONObject(uploadSessionFile);

    } finally {
      requestLimit.release();
    }
  }

  /**
   * Checks the maximum size authorized.
   * @param fileName
   * @param fileSize
   */
  private void checkMaximumFileSize(final String fileName, long fileSize) {
    long maximumFileSize = FileRepositoryManager.getUploadMaximumFileSize();
    if (fileSize > maximumFileSize) {
      ResourceLocator bundle =
          new ResourceLocator("org.silverpeas.util.attachment.multilang.attachment",
              getUserPreferences().getLanguage());
      String errorMessage = bundle.getString("attachment.dialog.errorFileSize") +
          " " +
          bundle.getString("attachment.dialog.maximumFileSize") + " (" +
          UnitUtil.formatMemSize(maximumFileSize) + ")";
      errorMessage = MessageFormat.format(errorMessage, fileName);
      NotifierUtil.addError(errorMessage);
      throw new WebApplicationException(
          Response.status(Response.Status.PRECONDITION_FAILED).entity(errorMessage).build());
    }
  }

  /**
   * Checks the authorized mime-types if {@link #getComponentId()} return a defined value.<br/>
   * If no defined value is returned by {@link #getComponentId()}, nothing is verified.
   * @param fileName the file name to test.
   */
  private void checkAuthorizedMimeTypes(final String fileName) {
    String componentInstanceId = getComponentId();
    if (StringUtil.isDefined(componentInstanceId)) {

      // Component file filter that contains authorized and forbidden rules
      final ComponentFileFilterParameter componentFileFilter = ComponentFileFilterParameter
          .from(getOrganisationController().getComponentInst(componentInstanceId));

      try {
        componentFileFilter.verifyFileAuthorized(new File(fileName));
      } catch (Exception e) {
        throw new WebApplicationException(e, Response.Status.PRECONDITION_FAILED);
      }
    }
  }

  /**
   * Builds a JSON representation of the given uploaded file.
   * @param uploadSessionFile
   * @return a JSON representation of the uploaded file.(more information on {@link
   * FileUploadResource#uploadFiles()})
   */
  private JSONObject toJSONObject(UploadSessionFile uploadSessionFile) {
    JSONObject fileInfo = new JSONObject();
    fileInfo.put("uploadSessionId", uploadSessionFile.getUploadSession().getId());
    fileInfo.put("fullPath", uploadSessionFile.getFullPath());
    fileInfo.put("name", uploadSessionFile.getServerFile().getName());
    fileInfo.put("size", uploadSessionFile.getServerFile().length());
    fileInfo.put("formattedSize", UnitUtil
        .formatMemSize(new BigDecimal(String.valueOf(uploadSessionFile.getServerFile().length()))));
    fileInfo.put("iconUrl", FileRepositoryManager
        .getFileIcon(FilenameUtils.getExtension(uploadSessionFile.getServerFile().getName())));
    return fileInfo;
  }

  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  public Response delete() {
    try {
      UploadSession uploadSession = UploadSession.from(getHttpServletRequest());
      FileUploadData fileToDelete = FileUploadData.from(getHttpServletRequest());
      if (StringUtil.isDefined(fileToDelete.getFullPath())) {
        if (!uploadSession.remove(fileToDelete.getFullPath())) {
          SilverTrace.error("upload", "FileUploadResource.delete()", "",
              "Trying to delete non existing file with session id '" + uploadSession.getId() +
                  "' and fullPath '" + fileToDelete.getFullPath() + "'");
        }
      } else {
        uploadSession.clear();
      }
      return Response.ok().build();
    } catch (final WebApplicationException ex) {
      throw ex;
    } catch (final Exception ex) {
      throw new WebApplicationException(ex, Response.Status.SERVICE_UNAVAILABLE);
    }
  }

  /*
   * (non-Javadoc)
   * @see com.silverpeas.web.RESTWebService#getComponentId()
   */
  @Override
  public String getComponentId() {
    return getHttpRequest().getHeader(FileUploadData.X_COMPONENT_INSTANCE_ID);
  }
}