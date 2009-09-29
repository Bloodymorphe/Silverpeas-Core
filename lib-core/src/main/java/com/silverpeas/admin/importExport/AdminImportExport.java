/*
 * Created on 17 f�vr. 2005
 */
package com.silverpeas.admin.importExport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.stratelia.webactiv.beans.admin.AdminController;
import com.stratelia.webactiv.beans.admin.ComponentInst;

/**
 * Classe de gestion des components dans le moteur d'importExport de silverpeas.
 * 
 * @author sdevolder
 */
public class AdminImportExport {

  // Variables
  AdminController ac = null;

  // M�thodes
  /**
   * M�thode r�cup�rant la liste des componentInsts des composants impliqu�s
   * dans une exportation donn�e et destin�es au marshalling.
   * 
   * @param listComponentId
   *          = liste des id des composants impliqu�s dans l'esxport en cours
   * @return l'objet ComponentsType compl�t�, null si la liste pass�e en
   *         param�tre est vide
   */
  public ComponentsType getComponents(List listComponentId) {

    ComponentsType componentsType = null;
    ComponentInst componentInst = null;
    ArrayList listComponentInst = null;
    Iterator itListComponentId = listComponentId.iterator();
    while (itListComponentId.hasNext()) {
      String componentId = (String) itListComponentId.next();
      componentInst = getAdminController().getComponentInst(componentId);
      if (listComponentInst == null)
        listComponentInst = new ArrayList();
      listComponentInst.add(componentInst);
    }
    if (listComponentInst != null)
      componentsType = new ComponentsType();
    componentsType.setListComponentInst(listComponentInst);
    return componentsType;
  }

  /**
   * @return un objet AdminController
   */
  private AdminController getAdminController() {
    if (ac == null) {
      ac = new AdminController("unknown");
    }
    return ac;
  }

}
