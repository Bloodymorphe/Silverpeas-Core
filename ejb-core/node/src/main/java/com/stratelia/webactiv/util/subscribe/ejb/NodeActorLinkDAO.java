// TODO : reporter dans CVS (done)
package com.stratelia.webactiv.util.subscribe.ejb;

import java.sql.*;
import java.util.*;

import com.stratelia.webactiv.util.DBUtil;
import com.stratelia.webactiv.util.node.model.NodePK;
import com.stratelia.silverpeas.silvertrace.*;

/*
 * CVS Informations
 * 
 * $Id: NodeActorLinkDAO.java,v 1.5 2007/08/06 15:43:48 dlesimple Exp $
 * 
 * $Log: NodeActorLinkDAO.java,v $
 * Revision 1.5  2007/08/06 15:43:48  dlesimple
 * M�nage commentaires et correction m�thode getActorPKsByNodePK (affectation de param�tre non pr�sent dans la requete SQL)
 *
 * Revision 1.4  2004/10/05 13:21:18  dlesimple
 * Couper/Coller composant
 *
 * Revision 1.3  2003/11/25 09:57:03  neysseri
 * no message
 *
 * Revision 1.2  2003/11/24 13:35:54  cbonin
 * no message
 *
 * Revision 1.1.1.1  2002/08/06 14:47:53  nchaix
 * no message
 *
 * Revision 1.8  2002/01/21 15:16:17  neysseri
 * Ajout d'une fonctionnalit� permettant d'avoir les abonn�s � un ensemble de noeuds
 *
 * Revision 1.7  2001/12/26 14:27:42  nchaix
 * no message
 *
 */

/**
 * Class declaration
 *
 *
 * @author
 */
public class NodeActorLinkDAO
{

	/* cette classe ne devrait jamais etre instanciee */

	/**
	 * Constructor declaration
	 *
	 *
	 * @see
	 */
	public NodeActorLinkDAO()
	{
	}

	/**
	 * Method declaration
	 *
	 *
	 * @param con
	 * @param tableName
	 * @param userId
	 * @param node
	 *
	 * @throws SQLException
	 *
	 * @see
	 */
	public static void add(Connection con, String tableName, String userId, NodePK node) throws SQLException
	{
		SilverTrace.info("subscribe", "NodeActorLinkDAO.add", "root.MSG_GEN_ENTER_METHOD");

		String insertStatement =
			"insert into " + tableName + " (actorId, nodeId, space, componentName) values (?, ?, ?, ? )";
		PreparedStatement prepStmt = con.prepareStatement(insertStatement);

		try
		{
			prepStmt.setString(1, userId);
			prepStmt.setInt(2, new Integer(node.getId()).intValue());
			prepStmt.setString(3, node.getSpace());
			prepStmt.setString(4, node.getComponentName());
			prepStmt.executeUpdate();
		}
		finally
		{
			DBUtil.close(prepStmt);
		}
	}

	/**
	 * Method declaration
	 *
	 *
	 * @param con
	 * @param tableName
	 * @param userId
	 * @param node
	 *
	 * @throws SQLException
	 *
	 * @see
	 */
	public static void remove(Connection con, String tableName, String userId, NodePK node) throws SQLException
	{
		SilverTrace.info("subscribe", "NodeActorLinkDAO.remove", "root.MSG_GEN_ENTER_METHOD");

		String insertStatement = "delete from " + tableName + " where actorId = ? and nodeId = ? and componentName = ?";

		PreparedStatement prepStmt = con.prepareStatement(insertStatement);
		try
		{
			prepStmt.setString(1, userId);
			prepStmt.setInt(2, new Integer(node.getId()).intValue());
			prepStmt.setString(3, node.getComponentName());
			prepStmt.executeUpdate();
		}
		finally
		{
			prepStmt.close();
		}
	}

	/**
	 * Method declaration
	 *
	 *
	 * @param con
	 * @param tableName
	 * @param userId
	 *
	 * @throws SQLException
	 *
	 * @see
	 */
	public static void removeByUser(Connection con, String tableName, String userId) throws SQLException
	{
		SilverTrace.info("subscribe", "NodeActorLinkDAO.removeByUser", "root.MSG_GEN_ENTER_METHOD");

		String insertStatement = "delete from " + tableName + " where actorId = ?";
		PreparedStatement prepStmt = con.prepareStatement(insertStatement);

		try
		{
			prepStmt.setString(1, userId);
			prepStmt.executeUpdate();
		}
		finally
		{
			prepStmt.close();
		}
	}

	/**
	 * Method declaration
	 *
	 *
	 * @param con
	 * @param tableName
	 * @param node
	 * @param path
	 *
	 * @throws SQLException
	 *
	 * @see
	 */
	public static void removeByNodePath(Connection con, String tableName, NodePK node, String path) throws SQLException
	{
		SilverTrace.info("subscribe", "NodeActorLinkDAO.removeByNodePath", "root.MSG_GEN_ENTER_METHOD");

		String insertStatement = 
			"delete from "
				+ tableName
				+ " where componentName = ? "
				+ "	and nodeId in ( "
				+ "	select  nodeId from "
				+ node.getTableName()
				+ " where nodePath like '"
				+ path
				+ "%' and "
				+ " instanceId = ? )";
		PreparedStatement prepStmt = null;

		try
		{
			prepStmt = con.prepareStatement(insertStatement);
			prepStmt.setString(1, node.getComponentName());
			prepStmt.setString(2, node.getComponentName());
			prepStmt.executeUpdate();
		}
		finally
		{
			prepStmt.close();
		}
	}

	/**
	 * Method declaration
	 *
	 *
	 * @param con
	 * @param tableName
	 * @param userId
	 *
	 * @return
	 *
	 * @throws SQLException
	 *
	 * @see
	 */
	public static Collection getNodePKsByActor(Connection con, String tableName, String userId) throws SQLException
	{
		SilverTrace.info("subscribe", "NodeActorLinkDAO.getNodePKsByActor", "root.MSG_GEN_ENTER_METHOD");
		String selectStatement = "select nodeId, componentName from " + tableName + " where actorId = ?";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
	
		try
		{
			prepStmt = con.prepareStatement(selectStatement);
			prepStmt.setString(1, userId);
			rs = prepStmt.executeQuery();

			ArrayList list = new ArrayList();
			while (rs.next())
			{
				String id = String.valueOf(rs.getInt(1));
				String componentName = rs.getString(2);
				NodePK nodePK = new NodePK(id, null, componentName);
				list.add(nodePK);
			}
			return list;
		}
		finally
		{
			DBUtil.close(rs, prepStmt);
		}		
	}


	/**
	 * Method declaration
	 *
	 *
	 * @param con
	 * @param tableName
	 * @param userId
	 * @param componentName
	 *
	 * @return
	 *
	 * @throws SQLException
	 *
	 * @see
	 */
	public static Collection getNodePKsByActorComponent(
		Connection con,
		String tableName,
		String userId,
		String componentName)
		throws SQLException
	{
		SilverTrace.info(
			"subscribe",
			"NodeActorLinkDAO.getNodePKsByActorComponent",
			"root.MSG_GEN_ENTER_METHOD");
		String selectStatement =
			"select nodeId from " + tableName + " where actorId = ? " + " and componentName = ? ";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;

		try
		{
			prepStmt = con.prepareStatement(selectStatement);
			prepStmt.setString(1, userId);
			prepStmt.setString(2, componentName);
			rs = prepStmt.executeQuery();
			ArrayList list = new ArrayList();
			while (rs.next())
			{
				String id = String.valueOf(rs.getInt(1));
				NodePK nodePK = new NodePK(id, null, componentName);
				list.add(nodePK);
			}
			return list;
		}
		finally
		{
			DBUtil.close(rs, prepStmt);
		}
	}
//	NEWF DLE 

	/**
	 * Method declaration
	 *
	 *
	 * @param con
	 * @param tableName
	 * @param node
	 *
	 * @return
	 *
	 * @throws SQLException
	 *
	 * @see
	 */
	public static Collection getActorPKsByNodePK(Connection con, String tableName, NodePK node) throws SQLException
	{
		SilverTrace.info("subscribe", "NodeActorLinkDAO.getActorPKsByNodePK", "root.MSG_GEN_ENTER_METHOD");
		String selectStatement = "select actorId from " + tableName + " where nodeId = ? and componentName = ?";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		
		try
		{
			prepStmt = con.prepareStatement(selectStatement);
			prepStmt.setInt(1, new Integer(node.getId()).intValue());
			prepStmt.setString(2, node.getComponentName());

			rs = prepStmt.executeQuery();
			ArrayList list = new ArrayList();
			while (rs.next())
			{
				String id = rs.getString(1);
				list.add(id);
			}
			return list;
		}
		finally
		{
			DBUtil.close(rs, prepStmt);
		}
	}

	public static Collection getActorPKsByNodePKs(Connection con, String tableName, Collection nodePKs)
		throws SQLException
	{
		SilverTrace.info("subscribe", "NodeActorLinkDAO.getActorPKsByNodePKs", "root.MSG_GEN_ENTER_METHOD");
		ArrayList list = new ArrayList();
		if (nodePKs != null && nodePKs.size() > 0)
		{
			String whereClause = "(";
			Iterator it = nodePKs.iterator();
			NodePK nodePK = null;
			while (it.hasNext())
			{
				nodePK = (NodePK) it.next();
				whereClause += "nodeId = " + nodePK.getId();
				if (it.hasNext())
					whereClause += " or ";
			}
			whereClause += ")";
			String selectStatement = "select distinct(actorId) from "
								+ tableName + " where " + whereClause + " and componentName = ?";

			PreparedStatement prepStmt = null;
			ResultSet rs = null;
			try
			{
				prepStmt = con.prepareStatement(selectStatement);
				prepStmt.setString(1, nodePK.getComponentName());
				rs = prepStmt.executeQuery();				
				while (rs.next())
				{
					String id = rs.getString(1);
					list.add(id);
				}
			}
			finally
			{
				DBUtil.close(rs, prepStmt);
			}
		}
		return list;		
	}
}