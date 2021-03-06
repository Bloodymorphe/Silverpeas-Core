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
package com.silverpeas.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.silverpeas.util.StringUtil;
import com.stratelia.silverpeas.peasCore.MainSessionController;
import com.stratelia.webactiv.util.EJBUtilitaire;
import com.stratelia.webactiv.util.JNDINames;
import com.stratelia.webactiv.util.publication.control.PublicationBm;
import com.stratelia.webactiv.util.publication.model.PublicationDetail;
import com.stratelia.webactiv.util.publication.model.PublicationPK;
import com.stratelia.webactiv.util.statistic.control.StatisticBm;
import com.stratelia.webactiv.util.statistic.model.HistoryObjectDetail;

public class MyLastPubliReadPortlet extends GenericPortlet implements FormNames {
  
  @Override
  public void doView(RenderRequest request, RenderResponse response)
      throws PortletException, IOException {
    PortletSession session = request.getPortletSession();
    MainSessionController mainSessionCtrl = (MainSessionController) session
        .getAttribute(MainSessionController.MAIN_SESSION_CONTROLLER_ATT,
            PortletSession.APPLICATION_SCOPE);

    Collection<PublicationDetail> listPubli = new ArrayList<PublicationDetail>();
    PortletPreferences pref = request.getPreferences();
    int nbPublis = 5;
    if (StringUtil.isInteger(pref.getValue("nbPublis", "5"))) {
      nbPublis = Integer.parseInt(pref.getValue("nbPublis", "5"));
    }
    Collection<HistoryObjectDetail> listObject =
        getStatisticBm().getLastHistoryOfObjectsForUser(mainSessionCtrl.getUserId(), 1,
            "Publication", nbPublis);
    for (HistoryObjectDetail object : listObject) {
      PublicationPK pubPk = new PublicationPK(object.getForeignPK().getId(),
          object.getForeignPK().getComponentName());
      PublicationDetail pubDetail = getPublicationBm().getDetail(pubPk);
      if (pubDetail != null) {
        // the publication exists (it wasn't deleted)
        listPubli.add(pubDetail);
      }
    }
   
    request.setAttribute("Publications", listPubli);

    include(request, response, "portlet.jsp");
  }

  /**
   * Include a page.
   */
  private void include(RenderRequest request, RenderResponse response, String pageName)
      throws PortletException {
    response.setContentType(request.getResponseContentType());
    if (!StringUtil.isDefined(pageName)) {
      // assert
      throw new NullPointerException("null or empty page name");
    }
    try {
      PortletRequestDispatcher dispatcher =
          getPortletContext().getRequestDispatcher("/portlets/jsp/myLastPubliRead/" + pageName);
      dispatcher.include(request, response);
    } catch (IOException ioe) {
      throw new PortletException(ioe);
    }
  }
  
  private StatisticBm getStatisticBm() {
    return EJBUtilitaire.getEJBObjectRef(JNDINames.STATISTICBM_EJBHOME, StatisticBm.class);
  }
  
  private PublicationBm getPublicationBm() {
    return EJBUtilitaire.getEJBObjectRef(JNDINames.PUBLICATIONBM_EJBHOME, PublicationBm.class);
  }
  
  @Override
  public void doHelp(RenderRequest request, RenderResponse response)
      throws PortletException {
    include(request, response, "help.jsp");
  }
}
