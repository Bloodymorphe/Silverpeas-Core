/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) 
 ---*/

package com.stratelia.silverpeas.personalizationPeas.servlets;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.silverpeas.util.EncodeHelper;
import com.stratelia.silverpeas.authentication.AuthenticationBadCredentialException;
import com.stratelia.silverpeas.authentication.AuthenticationException;
import com.stratelia.silverpeas.peasCore.ComponentContext;
import com.stratelia.silverpeas.peasCore.ComponentSessionController;
import com.stratelia.silverpeas.peasCore.MainSessionController;
import com.stratelia.silverpeas.peasCore.PeasCoreException;
import com.stratelia.silverpeas.peasCore.servlets.ComponentRequestRouter;
import com.stratelia.silverpeas.personalizationPeas.control.PersonalizationSessionController;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.beans.admin.UserDetail;
import com.stratelia.webactiv.beans.admin.UserFull;
import com.stratelia.webactiv.util.ResourceLocator;

/*
 * CVS Informations
 *
 * $Id: PersoPeasRequestRouter.java,v 1.20 2008/05/20 12:32:43 neysseri Exp $
 *
 * $Log: PersoPeasRequestRouter.java,v $
 * Revision 1.20  2008/05/20 12:32:43  neysseri
 * no message
 *
 * Revision 1.19.4.1  2008/05/06 09:38:59  ehugonnet
 * Ajout du champ webdavEditingStatus
 *
 * Revision 1.19  2007/08/01 15:50:56  sfariello
 * Externalisation des langues
 *
 * Revision 1.18  2007/06/12 07:52:26  neysseri
 * no message
 *
 * Revision 1.17.2.1  2007/05/04 10:23:52  cbonin
 * Personnalisation de l'activation de l'applet de drag and drop et de l'active X d'�dition de documents Office en ligne
 *
 * Revision 1.16  2007/05/04 09:42:35  cbonin
 * Personnalisation de l'activation de l'applet de drag and drop et de l'active X d'�dition de documents Office en ligne
 *
 * Revision 1.15  2007/04/20 14:24:40  neysseri
 * no message
 *
 * Revision 1.14.2.1  2007/03/16 15:53:23  cbonin
 * *** empty log message ***
 *
 * Revision 1.14  2007/02/27 15:42:03  neysseri
 * no message
 *
 * Revision 1.13  2007/02/02 10:25:46  neysseri
 * no message
 *
 * Revision 1.12  2007/01/04 09:35:25  cbonin
 * Ajout des champs custom de l'utilisateur
 *
 * Revision 1.11.2.1  2007/01/29 08:26:15  neysseri
 * no message
 *
 * Revision 1.11  2006/09/25 08:37:27  neysseri
 * no message
 *
 * Revision 1.10  2006/08/21 12:39:18  dlesimple
 * - Pas d'espaces dans le mot de passe
 * - Param�trage d'affichage et de moficiation des champs de l'onglet identiti�
 *
 * Revision 1.9  2005/11/23 11:11:15  neysseri
 * Modification de l'identit� d'un utilisateur impossible
 * Ne concerne que les utilisateurs des domaines SQL
 *
 * Revision 1.8  2005/07/25 16:07:15  neysseri
 * Ajout de l'onglet "Identit�"
 *
 * Revision 1.7.2.1  2005/06/02 16:54:53  sdevolder
 * *** empty log message ***
 *
 * Revision 1.7  2004/12/29 09:31:21  dlesimple
 * Modification mot de passe
 *
 * Revision 1.6  2004/12/15 13:45:50  dlesimple
 * Modification mot de passe utilisateur
 *
 * Revision 1.5  2004/11/30 17:02:02  neysseri
 * Espace par d�faut �tendu aux sous espaces + nettoyage sources
 *
 * Revision 1.4  2003/01/15 11:59:14  neysseri
 * The both tabs 'Preferences' and 'Appearence' has been merged
 *
 * Revision 1.3  2002/12/20 13:35:16  neysseri
 * no message
 *
 * Revision 1.2  2002/12/19 09:21:40  neysseri
 * ThesaurusInPreference branch merging
 *
 * Revision 1.1.1.1.16.1  2002/12/17 15:16:29  dlesimple
 * ThesaurusInPreference
 *
 * Revision 1.1.1.1  2002/08/06 14:47:55  nchaix
 * no message
 *
 * Revision 1.1  2002/01/30 11:07:44  tleroi
 * Move Bus peas to BusIHM
 *
 * Revision 1.1  2002/01/28 14:44:05  tleroi
 * Split clipboard and personalization
 *
 *
 */

/**
 * Class declaration
 * 
 * 
 * @author
 */
public class PersoPeasRequestRouter extends ComponentRequestRouter {

  /**
	 *
	 */
  private static final long serialVersionUID = 1L;

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
    return new PersonalizationSessionController(mainSessionCtrl,
        componentContext);
  }

  /**
   * This method has to be implemented in the component request rooter class.
   * returns the session control bean name to be put in the request object ex :
   * for notificationUser, returns "notificationUser"
   */
  public String getSessionControlBeanName() {
    return "personalizationPeas";
  }

  /**
   * This method has to be implemented by the component request rooter it has to
   * compute a destination page
   * 
   * @param function
   *          The entering request function (ex : "Main.jsp")
   * @param componentSC
   *          The component Session Control, build and initialised.
   * @param request
   *          The entering request. The request rooter need it to get parameters
   * @return The complete destination URL for a forward (ex :
   *         "/notificationUser/jsp/notificationUser.jsp?flag=user")
   */
  public String getDestination(String function,
      ComponentSessionController componentSC, HttpServletRequest request) {
    SilverTrace.info(getSessionControlBeanName(),
        "PersoPeasRequestRouter.getDestination()", "root.MSG_GEN_PARAM_VALUE",
        "function = " + function);
    PersonalizationSessionController personalizationScc = (PersonalizationSessionController) componentSC;

    ResourceLocator rl = new ResourceLocator(
        "com.stratelia.silverpeas.personalizationPeas.settings.personalizationPeasSettings",
        "");

    String destination = "";
    String selectedLanguage = null;
    String thesaurusStatus = null;
    String dragDropStatus = null;
    String onlineEditingStatus = null;
    String webdavEditingStatus = null;
    String selectedWorkSpace = null;
    String selectedLook = null;

    UserDetail currentUser = personalizationScc.getUserDetail();
    try {
      if (function.startsWith("Main")) {
        // language parameter
        selectedLanguage = personalizationScc.getFavoriteLanguage();

        // thesaurus parameter
        thesaurusStatus = new Boolean(personalizationScc.getThesaurusStatus())
            .toString();

        // drag and drop parameter
        dragDropStatus = new Boolean(personalizationScc.getDragAndDropStatus())
            .toString();

        // online editing parameter
        onlineEditingStatus = new Boolean(personalizationScc
            .getOnlineEditingStatus()).toString();
        // online editing parameter
        webdavEditingStatus = new Boolean(personalizationScc
            .getWebdavEditingStatus()).toString();

        // favorite look
        selectedLook = personalizationScc.getFavoriteLook();
        request.setAttribute("selectedLanguage", selectedLanguage);
        request.setAttribute("thesaurusStatus", thesaurusStatus);
        request.setAttribute("dragDropStatus", dragDropStatus);
        request.setAttribute("onlineEditingStatus", onlineEditingStatus);
        request.setAttribute("webdavEditingStatus", webdavEditingStatus);
        request.setAttribute("FavoriteSpace", personalizationScc
            .getFavoriteSpace());
        request.setAttribute("selectedLook", selectedLook);
        request.setAttribute("SpaceTreeview", personalizationScc
            .getSpaceTreeview());
        request.setAttribute("AllLanguages", personalizationScc
            .getAllLanguages());

        destination = "/personalizationPeas/jsp/personalization_Language.jsp";
      } else if (function.equals("SavePreferences")) {
        selectedLanguage = request.getParameter("SelectedLanguage");
        thesaurusStatus = request.getParameter("opt_thesaurusStatus");
        dragDropStatus = request.getParameter("opt_dragDropStatus");
        onlineEditingStatus = request.getParameter("opt_onlineEditingStatus");
        webdavEditingStatus = request.getParameter("opt_webdavEditingStatus");
        selectedWorkSpace = request.getParameter("SelectedWorkSpace");
        selectedLook = request.getParameter("SelectedLook");

        Boolean mustBeReloaded = isFramesetMustBeReloaded(selectedLanguage,
            selectedLook, personalizationScc);

        Vector languages = new Vector();
        languages.add(selectedLanguage);
        personalizationScc.setLanguages(languages);

        personalizationScc.setFavoriteLook(selectedLook);
        personalizationScc.setThesaurusStatus(new Boolean(thesaurusStatus)
            .booleanValue());
        personalizationScc.setDragAndDropStatus(new Boolean(dragDropStatus)
            .booleanValue());
        personalizationScc.setOnlineEditingStatus(new Boolean(
            onlineEditingStatus).booleanValue());
        personalizationScc.setWebdavEditingStatus(new Boolean(
            webdavEditingStatus).booleanValue());

        if (selectedWorkSpace == null || selectedWorkSpace.equals("null"))
          personalizationScc.setPersonalWorkSpace(null);
        else
          personalizationScc.setPersonalWorkSpace(selectedWorkSpace);

        request.setAttribute("FramesetMustBeReloaded", mustBeReloaded);

        destination = getDestination("Main", componentSC, request);
      } else if (function.startsWith("personalization_Language")) {
        SilverTrace.info(getSessionControlBeanName(),
            "PersoPeasRequestRouter.getDestination()",
            "root.MSG_GEN_PARAM_VALUE");
        destination = getDestination("Main", componentSC, request);
      } else if (function.startsWith("ChangePassword")) {
        // D�termination du domaine du user
        boolean domainRW = personalizationScc.isUserDomainRW();
        if (domainRW)
          request.setAttribute("action", "userModify");
        else
          request.setAttribute("action", "userMS");

        UserFull uf = personalizationScc.getTargetUserFull();

        boolean updateIsAllowed = domainRW
            || personalizationScc.isPasswordChangeAllowed()
            || (uf.isPasswordValid() && uf.isPasswordAvailable());

        request.setAttribute("userObject", uf);
        request.setAttribute("UpdateIsAllowed", new Boolean(updateIsAllowed));
        request.setAttribute("minLengthPwd", new Integer(personalizationScc
            .getMinLengthPwd()));
        request.setAttribute("blanksAllowedInPwd", new Boolean(
            personalizationScc.isBlanksAllowedInPwd()));
        destination = "/personalizationPeas/jsp/changePassword.jsp";
      } else if (function.startsWith("EffectiveChangePassword")) {
        // Update informations only if updateMode is allowed for each field
        try {
          boolean updateFirstNameIsAllowed = rl.getBoolean("updateFirstName",
              false);
          boolean updateLastNameIsAllowed = rl.getBoolean("updateLastName",
              false);
          boolean updateEmailIsAllowed = rl.getBoolean("updateEmail", false);
          String userFirstName = updateFirstNameIsAllowed ? request
              .getParameter("userFirstName") : currentUser.getFirstName();
          String userLastName = updateLastNameIsAllowed ? request
              .getParameter("userLastName") : currentUser.getLastName();
          String userEmail = updateEmailIsAllowed ? request
              .getParameter("userEMail") : currentUser.geteMail();
          SilverTrace.info(getSessionControlBeanName(),
              "PersoPeasRequestRouter.getDestination()",
              "root.MSG_GEN_PARAM_VALUE", "userFirstName=" + userFirstName
                  + " - userLastName=" + userLastName + " userEmail="
                  + userEmail);

          // process extra properties
          HashMap properties = new HashMap();
          Enumeration parameters = request.getParameterNames();
          String parameterName = null;
          String property = null;
          while (parameters.hasMoreElements()) {
            parameterName = (String) parameters.nextElement();
            if (parameterName.startsWith("prop_")) {
              property = parameterName.substring(5, parameterName.length()); // remove
              // "prop_"
              properties.put(property, request.getParameter(parameterName));
            }
          }

          personalizationScc.modifyUser(currentUser.getId(), EncodeHelper
              .htmlStringToJavaString(userLastName), EncodeHelper
              .htmlStringToJavaString(userFirstName), EncodeHelper
              .htmlStringToJavaString(userEmail), EncodeHelper
              .htmlStringToJavaString(request.getParameter("userAccessLevel")),
              EncodeHelper.htmlStringToJavaString(request
                  .getParameter("OldPassword")), EncodeHelper
                  .htmlStringToJavaString(request.getParameter("NewPassword")),
              properties);
          request.setAttribute("Message", personalizationScc
              .getString("MessageOK"));
        } catch (AuthenticationBadCredentialException e) {
          request.setAttribute("Message", personalizationScc
              .getString("Error_bad_credential"));
        } catch (AuthenticationException e) {
          request.setAttribute("Message", personalizationScc
              .getString("Error_unknown"));
        }
        destination = getDestination("ChangePassword", componentSC, request);
      } else {
        destination = "/personalizationPeas/jsp/" + function;
      }
    } catch (Exception e) {
      request.setAttribute("javax.servlet.jsp.jspException", e);
      destination = "/admin/jsp/errorpageMain.jsp";
    }

    SilverTrace.info(getSessionControlBeanName(),
        "PersoPeasRequestRouter.getDestination()", "root.MSG_GEN_PARAM_VALUE",
        "destination = " + destination);
    return destination;
  }

  private Boolean isFramesetMustBeReloaded(String newLanguage, String newLook,
      PersonalizationSessionController personalizationScc)
      throws PeasCoreException {
    String actualLanguage = personalizationScc.getFavoriteLanguage();
    String actualLook = personalizationScc.getFavoriteLook();

    if (!newLanguage.equals(actualLanguage) || !newLook.equals(actualLook)) {
      return new Boolean(true);
    } else {
      return new Boolean(false);
    }
  }
}