package com.silverpeas.myLinksPeas.servlets;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpUtils;

import com.silverpeas.myLinks.model.LinkDetail;
import com.silverpeas.myLinksPeas.control.MyLinksPeasSessionController;
import com.stratelia.silverpeas.peasCore.ComponentContext;
import com.stratelia.silverpeas.peasCore.ComponentSessionController;
import com.stratelia.silverpeas.peasCore.MainSessionController;
import com.stratelia.silverpeas.peasCore.URLManager;
import com.stratelia.silverpeas.peasCore.servlets.ComponentRequestRouter;
import com.stratelia.silverpeas.silvertrace.SilverTrace;

public class MyLinksPeasRequestRouter extends ComponentRequestRouter {
  /**
   * This method has to be implemented in the component request rooter class.
   * returns the session control bean name to be put in the request object ex :
   * for almanach, returns "almanach"
   */
  public String getSessionControlBeanName() {
    return "MyLinks";
  }

  /**
   * Method declaration
   * 
   * 
   * @param mainSessionCtrl
   * @param componentContext
   * 
   * @return
   * 
   * @see
   */
  public ComponentSessionController createComponentSessionController(
      MainSessionController mainSessionCtrl, ComponentContext componentContext) {
    return new MyLinksPeasSessionController(mainSessionCtrl, componentContext);
  }

  /**
   * This method has to be implemented by the component request rooter it has to
   * compute a destination page
   * 
   * @param function
   *          The entering request function (ex : "Main.jsp")
   * @param componentSC
   *          The component Session Control, build and initialised.
   * @return The complete destination URL for a forward (ex :
   *         "/almanach/jsp/almanach.jsp?flag=user")
   */
  public String getDestination(String function,
      ComponentSessionController componentSC, HttpServletRequest request) {
    MyLinksPeasSessionController myLinksSC = (MyLinksPeasSessionController) componentSC;
    SilverTrace.info("myLinksPeas",
        "MyLinksPeasRequestRouter.getDestination()",
        "root.MSG_GEN_PARAM_VALUE", "User=" + componentSC.getUserId()
            + " Function=" + function);

    String destination = "";
    String rootDest = "/myLinksPeas/jsp/";

    try {
      if (function.startsWith("Main")) {
        myLinksSC.setScope(MyLinksPeasSessionController.SCOPE_USER);
        myLinksSC.setUrl(null);

        destination = getDestination("ViewLinks", myLinksSC, request);
      } else if (function.equals("ComponentLinks")) {
        // recupere l'id de l'instance
        String instanceId = (String) request.getParameter("InstanceId");
        String url = (String) request.getParameter("UrlReturn");
        myLinksSC.setInstanceId(instanceId);
        myLinksSC.setUrl(url);
        request.setAttribute("UrlReturn", url);
        request.setAttribute("InstanceId", instanceId);

        destination = getDestination("ViewLinks", myLinksSC, request);
      } else if (function.equals("ObjectLinks")) {
        // recupere l'id de l'objet et de l'instance
        String objectId = (String) request.getParameter("ObjectId");
        String instanceId = (String) request.getParameter("InstanceId");
        String url = (String) request.getParameter("UrlReturn");
        myLinksSC.setUrl(url);
        myLinksSC.setInstanceId(instanceId);
        myLinksSC.setObjectId(objectId);
        request.setAttribute("UrlReturn", url);

        destination = getDestination("ViewLinks", myLinksSC, request);
      } else if (function.equals("ViewLinks")) {
        Collection links = null;
        int scope = myLinksSC.getScope();
        switch (scope) {
          case MyLinksPeasSessionController.SCOPE_COMPONENT:
            links = myLinksSC.getAllLinksByInstance();
            break;
          case MyLinksPeasSessionController.SCOPE_OBJECT:
            links = myLinksSC.getAllLinksByObject();
            break;
          default:
            links = myLinksSC.getAllLinksByUser();
        }
        request.setAttribute("Links", links);
        request.setAttribute("UrlReturn", myLinksSC.getUrl());
        request.setAttribute("InstanceId", myLinksSC.getInstanceId());
        destination = rootDest + "viewLinks.jsp";
      } else if (function.equals("NewLink")) {
        boolean isVisible = false;
        if (myLinksSC.getScope() == MyLinksPeasSessionController.SCOPE_USER)
          isVisible = true;
        request.setAttribute("IsVisible", new Boolean(isVisible));
        // appel jsp
        destination = rootDest + "linkManager.jsp";
      } else if (function.equals("CreateLink")) {
        // r�cup�ration des param�tres venus de l'�cran de saisie et cr�ation de
        // l'objet LinkDetail
        LinkDetail link = generateLink(request);
        myLinksSC.createLink(link);
        // retour sur le liste des liens
        destination = getDestination("ViewLinks", myLinksSC, request);
      } else if (function.equals("CreateLinkFromComponent")) {
        // r�cup�ration des param�tres transmis et cr�ation de l'objet
        // LinkDetail
        LinkDetail link = generateLink(request);
        myLinksSC.createLink(link);
        // affichage d'une fen�tre de confirmation
        destination = rootDest + "confirm.jsp";
      } else if (function.equals("EditLink")) {
        String linkId = request.getParameter("LinkId");
        LinkDetail link = myLinksSC.getLink(linkId);
        request.setAttribute("Link", link);
        boolean isVisible = false;
        if (myLinksSC.getScope() == MyLinksPeasSessionController.SCOPE_USER)
          isVisible = true;
        request.setAttribute("IsVisible", new Boolean(isVisible));
        // appel jsp
        destination = rootDest + "linkManager.jsp";
      } else if (function.equals("UpdateLink")) {
        // r�cup�ration des param�tres venus de l'�cran de saisie
        String linkId = request.getParameter("LinkId");
        LinkDetail link = generateLink(request);
        link.setLinkId(Integer.parseInt(linkId));
        // modification du lien
        myLinksSC.updateLink(link);
        // retour sur le liste des liens
        destination = getDestination("ViewLinks", myLinksSC, request);
      } else if (function.equals("DeleteLinks")) {
        Object o = request.getParameterValues("linkCheck");
        if (o != null) {
          String[] links = (String[]) o;
          myLinksSC.deleteLinks(links);
        }
        destination = getDestination("ViewLinks", myLinksSC, request);
      } else {
        destination = getDestination("ViewLinks", myLinksSC, request);
      }

    } catch (Exception e) {
      request.setAttribute("javax.servlet.jsp.jspException", e);
      destination = "/admin/jsp/errorpageMain.jsp";
    }

    SilverTrace.info("myLinks", "MyLinksPeasRequestRouter.getDestination()",
        "root.MSG_GEN_PARAM_VALUE", "Destination=" + destination);
    return destination;
  }

  private LinkDetail generateLink(HttpServletRequest request) {
    String name = request.getParameter("Name");
    String description = request.getParameter("Description");
    String url = request.getParameter("Url");
    // supprimer le context en d�but d'url
    String sRequestURL = HttpUtils.getRequestURL(request).toString();
    String m_sAbsolute = sRequestURL.substring(0, sRequestURL.length()
        - request.getRequestURI().length());
    String urlServeur = m_sAbsolute;
    if (url.startsWith(urlServeur))
      url = url.substring(urlServeur.length(), url.length());
    String context = URLManager.getApplicationURL();
    if (url.startsWith(context))
      url = url.substring(context.length(), url.length());
    boolean visible = false;
    if ("true".equals(request.getParameter("Visible")))
      visible = true;
    boolean popup = false;
    if ("true".equals(request.getParameter("Popup")))
      popup = true;
    /*
     * String instanceId = request.getParameter("InstanceId"); if (instanceId !=
     * null) return new LinkDetail(name, description,url,visible,popup,
     * instanceId); else
     */
    return new LinkDetail(name, description, url, visible, popup);
  }
}
