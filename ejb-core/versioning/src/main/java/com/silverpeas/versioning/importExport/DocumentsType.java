package com.silverpeas.versioning.importExport;

import java.util.List;

/**
 * Classe utilis�e pour le (un)marshalling Castor
 * @author neysseri
 */
public class DocumentsType {
	
	private List listDocuments;	//Document 

	/**
	 * @return
	 */
	public List getListDocuments() {
		return listDocuments;
	}

	/**
	 * @param list
	 */
	public void setListDocuments(List list) {
		listDocuments = list;
	}

}
