/*
 * Created on 9 f�vr. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.silverpeas.pdc.importExport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.stratelia.silverpeas.pdc.control.PdcBm;
import com.stratelia.silverpeas.pdc.control.PdcBmImpl;
import com.stratelia.silverpeas.pdc.model.ClassifyPosition;
import com.stratelia.silverpeas.pdc.model.ClassifyValue;
import com.stratelia.silverpeas.pdc.model.PdcException;
import com.stratelia.silverpeas.pdc.model.UsedAxis;
import com.stratelia.silverpeas.pdc.model.Value;
import com.stratelia.silverpeas.silvertrace.SilverTrace;

/**
 * Classe g�rant la manipulation des axes du pdc pour le module d'importExport.
 * @author sdevolder
 */
public class PdcImportExport {
	
	//Variables
	PdcBm pdcBm = null;
	
	//M�thodes
	/**
	 * M�thodes cr�ant les liens entre les silverObjectId et les positions d�finies dans un xml 
	 * mapp� dans la classe PdcPositionsType.
	 * @param silverObjectId - id de l'objet � lier au pdc
	 * @param componentId - id du composant ...
	 * @param positions - object contenant les classes classifyValue contenant les axes du pdc � lier
	 * @return false si une des donn�es est incorrecte, true sinon 
	 * @throws PdcException
	 */
	public boolean addPositions(int silverObjectId, String componentId, PdcPositionsType positions) throws PdcException 
	{
		boolean result = true;
		
		List listPositions = positions.getListClassifyPosition();
		
		//r�cup�ration des positions valides
		List validPositions = getValidPositions(componentId, listPositions);
		
		if (listPositions.size()!=validPositions.size())
		{
			result = false;
		}
		
		if (validPositions != null) {
			Iterator itListPosition = validPositions.iterator();
			while (itListPosition.hasNext()) {
				ClassifyPosition classifyPos = (ClassifyPosition) itListPosition.next();
				try {
					getPdcBm().addPosition(silverObjectId,classifyPos,componentId);
				} catch(PdcException ex) {
					result = false;
					SilverTrace.error("Pdc", "PdcImportExport.addPositions(int,String,PdcPositionsType)", "Pdc.CANNOT_INSERT_VALUE", ex);
				}
			}
		}
		
		return result;
	}

	public List getValidPositions(String componentId, List positions) throws PdcException
	{
		List validPositions = new ArrayList();
		
		//r�cup�ration des axes utilis�s par le composant
		List usedAxis = getPdcBm().getUsedAxisByInstanceId(componentId);
		
		if (usedAxis != null && usedAxis.size()>0)
		{
			if (positions != null) {
				Iterator itListPosition = positions.iterator();
				while (itListPosition.hasNext()) {
					ClassifyPosition classifyPos = (ClassifyPosition) itListPosition.next();
		
					if (isValidPosition(usedAxis, classifyPos))
						validPositions.add(classifyPos);
				}
			}
		}
		
		return validPositions;
	}
	
	private boolean isValidPosition(List usedAxis, ClassifyPosition position)
	{
		List 			values 		= position.getValues();
		ClassifyValue 	value 		= null;
		boolean			valueExist	= true;
		for (int v=0; valueExist && v<values.size(); v++)
		{
			value = (ClassifyValue) values.get(v);
			valueExist = isExistingValue(value);
		}
		if (!valueExist)
		{
			//une des valeurs n'est pas correcte
			return false;
		}
		else
		{
			//toutes les valeurs sont correctes
			//Il faut encore v�rifier que le classement est complet
			return isCompletePosition(usedAxis, position);
		}
	}
	
	private boolean isCompletePosition(List usedAxis, ClassifyPosition position)
	{
		UsedAxis axis = null;
		for (int u=0; u<usedAxis.size(); u++)
		{
			axis = (UsedAxis) usedAxis.get(u);
			if (!isUsedAxisOK(axis, position))
			{
				return false;
			}
		}
		return true;
	}
	
	private boolean isUsedAxisOK(UsedAxis axis, ClassifyPosition position)
	{
		if (axis.getMandatory()==1)
		{
			//l'utilisation de cet axe est obligatoire
			//Est ce qu'il y a une valeur sur cet axe
			String valueId = position.getValueOnAxis(axis.getAxisId());
			if (valueId == null)
				return false;
			else
				return true;
		}
		//le classement sur cet axe est facultatif
		return true;
		
	}
	
	private boolean isExistingValue(ClassifyValue value)
	{
		int 	axisId 	= value.getAxisId();
		String 	path 	= value.getValue();
		String	leafId	= extractLeaf(path);
		
		Value existingValue = null;
		try
		{
			existingValue = getPdcBm().getValue(Integer.toString(axisId), leafId);
			
			if (existingValue == null)
			{
				return false;
			}
			else
			{
				//Si la valeur existe, on v�rifie que le chemin fournit est correct
				if (!existingValue.getFullPath().equals(path))
					return false;
			}
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}
	
	private String extractLeaf(String path)
	{
		path = path.substring(0, path.length()-1);
		String leaf = path.substring(path.lastIndexOf("/")+1);
		return leaf;
	}
	
	 /**
	 * M�thode de r�cup�ration des position pdc pour un objet silverpeas donn�.
	 * @param silverObjectId
	 * @param sComponentId
	 * @return - liste de ClassifyPosition
	 * @throws PdcException
	 */
	public List getPositions(int silverObjectId, String sComponentId) throws PdcException {
		List list = getPdcBm().getPositions(silverObjectId,sComponentId);
		if (list.size() == 0)//Pas g�r� dans l'EJB!!!
			return null;
	 	return list;
	 }
	
	public boolean isClassifyingMandatory(String componentId) throws PdcException
	{
		return getPdcBm().isClassifyingMandatory(componentId); 
	}
	
	/**
	 * M�thodes r�cup�rant la totalit� des axes utilis�s par les positions de la liste en param�tre
	 * @param listClassifyPosition - liste des positions dont on veut les axes
	 * @return un objet PdcType contenant les axes recherch�s
	 * @throws PdcException
	 */
	public PdcType getPdc(ArrayList listClassifyPosition) throws PdcException {
		
		//On construit une liste des axes � exporter
		HashSet set = new HashSet();
		Iterator itListClassifyPosition	= listClassifyPosition.iterator();
		while (itListClassifyPosition.hasNext()) {
			ClassifyPosition classPos = (ClassifyPosition)itListClassifyPosition.next();
			List listClassVal = classPos.getListClassifyValue();
			Iterator itListClassVal	= listClassVal.iterator();
			while (itListClassVal.hasNext()) {
				ClassifyValue classVal = (ClassifyValue) itListClassVal.next();
				set.add(new Integer(classVal.getAxisId()));
			}
		}
		
		//On parcours la liste des axes � exporter
		PdcType pdcType = new PdcType();
		ArrayList listAxisType = new ArrayList();
		pdcType.setListAxisType(listAxisType);
		Iterator itSet = set.iterator();
		while (itSet.hasNext()) {
			int axisId = ((Integer)itSet.next()).intValue();
			//R�cup�ration de la "value" root de l'axe
			Value valueRoot = getPdcBm().getRoot(Integer.toString(axisId));
			AxisType axisType = new AxisType();
			axisType.setId(axisId);
			axisType.setName(valueRoot.getName());
			axisType.setPath(valueRoot.getFullPath());
			listAxisType.add(axisType);
			//R�cup�ration de la totalit� de l'arbre de l'axe avec la m�thode r�cursive getValueTree
			ArrayList listPdcValueType = getValueTree(axisId,valueRoot.getPK().getId());
			axisType.setListPdcValueType(listPdcValueType);
		}
		return pdcType;
	}
	
	
	/**
	 * M�thode r�cursive utilis�e par la m�thode getPdc de r�cup�ration d'axes.
	 * @param axisId - id de l'axe que l'on veut r�cup�r�
	 * @param fatherValueId - id de la "value" dont on veut les fils
	 * @return - liste des values, fils du value d id fatherValueId, null si le p�re est une feuille de l'arbre
	 * @throws PdcException
	 */
	private ArrayList getValueTree(int axisId, String fatherValueId) throws PdcException {
		
		List		listValueId				=	null;
		ArrayList	listChildrenPdcValue	=	null;
		//R�cup�ration des ids des valeurs filles directes du value p�re
		listValueId = getPdcBm().getDaughterValues(Integer.toString(axisId),fatherValueId);
		if (listValueId != null) {//L'exception oject non trouv� n'est pas g�r�e dans la m�thode DAO!!!
			Iterator itListValues = listValueId.iterator();
			//Parcours des id des valeurs filles trouv�s
			while (itListValues.hasNext()) {
				String valueId = (String) itListValues.next();
				//R�cup�ration de l'objet value et remplissage de l'objet de mapping
				Value value = getPdcBm().getValue(Integer.toString(axisId),valueId);
				PdcValueType pdcValueType = new PdcValueType();
				pdcValueType.getPK().setId(valueId);
				pdcValueType.setName(value.getName());
				pdcValueType.setDescription(value.getDescription());
				pdcValueType.setPath(value.getFullPath());
				if (listChildrenPdcValue == null)
					listChildrenPdcValue = new ArrayList();
				listChildrenPdcValue.add(pdcValueType);
				//Parcours r�cursif
				pdcValueType.setListPdcValueType(getValueTree(axisId,pdcValueType.getPK().getId()));
			}
		}
		
		return listChildrenPdcValue;
	}
	
	/**
	 * @return l'EJB PdcBm
	 */
	private PdcBm getPdcBm()
	{
		if (pdcBm == null)
		{
			pdcBm = new PdcBmImpl();
		}
		return pdcBm;
	}
}