/**
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
 * FLOSS exception.  You should have received a copy of the text describing
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

/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) 
 ---*/

package com.stratelia.silverpeas.notificationserver.channel.silvermail.requesthandlers;

import javax.servlet.http.HttpServletRequest;

import com.stratelia.silverpeas.notificationserver.channel.silvermail.SILVERMAILException;
import com.stratelia.silverpeas.notificationserver.channel.silvermail.SILVERMAILRequestHandler;
import com.stratelia.silverpeas.notificationserver.channel.silvermail.SILVERMAILSessionController;
import com.stratelia.silverpeas.peasCore.ComponentSessionController;

/**
 * Class declaration
 * @author
 * @version %I%, %G%
 */
public class DeleteMessage implements SILVERMAILRequestHandler {

  /**
   * Method declaration
   * @param componentSC
   * @param request
   * @return
   * @throws SILVERMAILException
   * @see
   */
  public String handleRequest(ComponentSessionController componentSC,
      HttpServletRequest request) throws SILVERMAILException {
      SILVERMAILSessionController silvermailScc = (SILVERMAILSessionController) componentSC;
      String sId = request.getParameter("ID");

      silvermailScc.deleteMessage(sId);

      if (request.getParameter("from") == null) {
        return "/SILVERMAIL/jsp/main.jsp";
      } else if (request.getParameter("from").equals("homePage")) {
        return "/SILVERMAIL/jsp/redirect.jsp?SpaceId="
          + request.getParameter("SpaceId");
      } else {
        return "/SILVERMAIL/jsp/main.jsp";
      }
  }

}
