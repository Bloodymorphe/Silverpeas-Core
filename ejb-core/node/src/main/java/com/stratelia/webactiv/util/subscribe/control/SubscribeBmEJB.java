/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package com.stratelia.webactiv.util.subscribe.control;

import java.util.*;
import javax.ejb.*;
import java.sql.*;

import com.stratelia.silverpeas.silvertrace.*;
import com.stratelia.webactiv.util.DBUtil;
import com.stratelia.webactiv.util.JNDINames;
import com.stratelia.webactiv.util.exception.*;
import com.stratelia.webactiv.util.node.model.NodePK;
import com.stratelia.webactiv.util.subscribe.ejb.NodeActorLinkDAO;
import com.stratelia.webactiv.util.subscribe.model.*;

/*
 * CVS Informations
 * 
 * $Id: SubscribeBmEJB.java,v 1.2 2004/10/05 13:21:18 dlesimple Exp $
 * 
 * $Log: SubscribeBmEJB.java,v $
 * Revision 1.2  2004/10/05 13:21:18  dlesimple
 * Couper/Coller composant
 *
 * Revision 1.1.1.1  2002/08/06 14:47:53  nchaix
 * no message
 *
 * Revision 1.11  2002/01/22 13:39:51  mguillem
 * Stabilisation Lot2
 * R�organisation des Router et SessionController
 * Suppression dans les fichiers *Exception de 'implements FromModule'
 *
 * Revision 1.10  2002/01/22 10:26:32  mguillem
 * Stabilisation Lot2
 * R�organisation des Router et SessionController
 * Suppression dans les fichiers *Exception de 'implements FromModule'
 *
 * Revision 1.9  2002/01/21 15:16:12  neysseri
 * Ajout d'une fonctionnalit� permettant d'avoir les abonn�s � un ensemble de noeuds
 *
 * Revision 1.8  2001/12/26 14:27:42  nchaix
 * no message
 *
 */
 
/**
 * Class declaration
 *
 *
 * @author
 */
public class SubscribeBmEJB implements SessionBean
{

    private final String rootTableName = "subscribe";
    private String       dbName = JNDINames.SUBSCRIBE_DATASOURCE;

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public SubscribeBmEJB() {}

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    private Connection getConnection()
    {
        try
        {
            Connection con = DBUtil.makeConnection(dbName);

            return con;
        }
        catch (Exception e)
        {
            throw new SubscribeRuntimeException("SubscribeBmEJB.getConnection()", SilverpeasRuntimeException.ERROR, "root.EX_CONNECTION_OPEN_FAILED", e);
        }
    }

    /**
     * Method declaration
     *
     *
     * @param con
     *
     * @see
     */
    private void freeConnection(Connection con)
    {
        if (con != null)
        {
            try
            {
                con.close();
            }
            catch (Exception e)
            {
                SilverTrace.error("subscrive", "SubscribeBmEJB.freeConnection", "root.MSG_GEN_CONNECTION_CLOSE_FAILED");
            }
        }
    }

    /**
     * Method declaration
     *
     *
     * @param userId
     * @param node
     *
     * @see
     */
    public void addSubscribe(String userId, NodePK node)
    {
        SilverTrace.info("subscribe", "SubscribeBmEJB.addSubscribe", "root.MSG_GEN_ENTER_METHOD");
        Connection con = null;

        try
        {
            con = getConnection();
            NodeActorLinkDAO.add(con, rootTableName, userId, node);
        }
        catch (Exception e)
        {
            throw new SubscribeRuntimeException("SubscribeBmEJB.addSubscribe()", SilverpeasRuntimeException.ERROR, "subscribe.CANNOT_ADD_SUBSCRIBE", e);
        }
        finally
        {
            freeConnection(con);
        }
    }

    /**
     * Method declaration
     *
     *
     * @param userId
     * @param node
     *
     * @see
     */
    public void removeSubscribe(String userId, NodePK node)
    {
        SilverTrace.info("subscribe", "SubscribeBmEJB.removeSubscribe", "root.MSG_GEN_ENTER_METHOD");
        Connection con = null;

        try
        {
            con = getConnection();
            NodeActorLinkDAO.remove(con, rootTableName, userId, node);
        }
        catch (Exception e)
        {
            throw new SubscribeRuntimeException("SubscribeBmEJB.removeSubscribe()", SilverpeasRuntimeException.ERROR, "subscribe.CANNOT_REMOVE_SUBSCRIBE", e);
        }
        finally
        {
            freeConnection(con);
        }
    }

    /**
     * Method declaration
     *
     *
     * @param userId
     *
     * @see
     */
    public void removeUserSubscribes(String userId)
    {
        SilverTrace.info("subscribe", "SubscribeBmEJB.removeUserSubscribes", "root.MSG_GEN_ENTER_METHOD");
        Connection con = null;

        try
        {
            con = getConnection();
            NodeActorLinkDAO.removeByUser(con, rootTableName, userId);
        }
        catch (Exception e)
        {
            throw new SubscribeRuntimeException("SubscribeBmEJB.removeUserSubscribes()", SilverpeasRuntimeException.ERROR, "subscribe.CANNOT_REMOVE_USER_SUBSCRIBES", e);
        }
        finally
        {
            freeConnection(con);
        }
    }

    /**
     * Method declaration
     *
     *
     * @param node
     * @param path
     *
     * @see
     */
    public void removeNodeSubscribes(NodePK node, String path)
    {
        SilverTrace.info("subscribe", "SubscribeBmEJB.removeNodeSubscribes", "root.MSG_GEN_ENTER_METHOD");
        Connection con = null;

        try
        {
            con = getConnection();
            NodeActorLinkDAO.removeByNodePath(con, rootTableName, node, path);
        }
        catch (Exception e)
        {
            throw new SubscribeRuntimeException("SubscribeBmEJB.removeNodeSubscribes()", SilverpeasRuntimeException.ERROR, "subscribe.CANNOT_REMOVE_NODE_SUBSCRIBES", e);
        }
        finally
        {
            freeConnection(con);
        }
    }

    /**
     * Method declaration
     *
     *
     * @param userId
     *
     * @return
     *
     * @see
     */
    public Collection getUserSubscribePKs(String userId)
    {
        SilverTrace.info("subscribe", "SubscribeBmEJB.getUserSubscribePKs", "root.MSG_GEN_ENTER_METHOD");
        Connection con = null;

        try
        {
            con = getConnection();
            Collection result = NodeActorLinkDAO.getNodePKsByActor(con, rootTableName, userId);

            return result;
        }
        catch (Exception e)
        {
            throw new SubscribeRuntimeException("SubscribeBmEJB.getUserSubscribePKs()", SilverpeasRuntimeException.ERROR, "subscribe.CANNOT_GET_USER_SUBSCRIBES", e);
        }
        finally
        {
            freeConnection(con);
        }
    }

// NEWD DLE
    /**
     * Method declaration
     *
     *
     * @param userId
     * @param space
     * @param componentName
     *
     * @return
     *
     * @see
     */
/*    public Collection getUserSubscribePKsBySpaceAndComponent(String userId, String space, String componentName)
    {
        SilverTrace.info("subscribe", "SubscribeBmEJB.getUserSubscribePKsBySpaceAndComponent", "root.MSG_GEN_ENTER_METHOD");
        Connection con = null;

        try
        {
            con = getConnection();
            Collection result = NodeActorLinkDAO.getNodePKsByActorSpaceAndComponent(con, rootTableName, userId, space, componentName);

            return result;
        }
        catch (Exception e)
        {
            throw new SubscribeRuntimeException("SubscribeBmEJB.getUserSubscribesPKsByspaceAndcomponent()", SilverpeasRuntimeException.ERROR, "subscribe.CANNOT_GET_USER_SUBSCRIBES_SPACE_COMPONENT", e);
        }
        finally
        {
            freeConnection(con);
        }
    }
*/
	/**
	 * Method declaration
	 *
	 *
	 * @param userId
	 * @param componentName
	 *
	 * @return
	 *
	 * @see
	 */
    public Collection getUserSubscribePKsByComponent(String userId, String componentName)
	{
		SilverTrace.info("subscribe", "SubscribeBmEJB.getUserSubscribePKsByComponent", "root.MSG_GEN_ENTER_METHOD");
		Connection con = null;

		try
		{
			con = getConnection();
			Collection result = NodeActorLinkDAO.getNodePKsByActorComponent(con, rootTableName, userId, componentName);
			return result;
		}
		catch (Exception e)
		{
			throw new SubscribeRuntimeException("SubscribeBmEJB.getUserSubscribesPKsByspaceAndcomponent()", SilverpeasRuntimeException.ERROR, "subscribe.CANNOT_GET_USER_SUBSCRIBES_SPACE_COMPONENT", e);
		}
		finally
		{
			freeConnection(con);
		}
	}
//	NEWF DLE

    /**
     * Method declaration
     *
     *
     * @param node
     *
     * @return
     *
     * @see
     */
    public Collection getNodeSubscriberDetails(NodePK node)
    {
        SilverTrace.info("subscribe", "SubscribeBmEJB.getNodeSubscriberDetails", "root.MSG_GEN_ENTER_METHOD");
        Connection con = null;

        try
        {
            con = getConnection();
            Collection result = NodeActorLinkDAO.getActorPKsByNodePK(con, rootTableName, node);

            return result;
        }
        catch (Exception e)
        {
            throw new SubscribeRuntimeException("SubscribeBmEJB.getNodeSubscriberDetails()", SilverpeasRuntimeException.ERROR, "subscribe.CANNOT_GET_NODE_SUBSCRIBERS", e);
        }
        finally
        {
            freeConnection(con);
        }
    }

	public Collection getNodeSubscriberDetails(Collection nodePKs){
		SilverTrace.info("subscribe", "SubscribeBmEJB.getNodeSubscriberDetails", "root.MSG_GEN_ENTER_METHOD");
		Connection con = null;
		try
		{
			con = getConnection();
			Collection result = NodeActorLinkDAO.getActorPKsByNodePKs(con, rootTableName, nodePKs);

			return result;
		} 
		catch (Exception e)
		{
			throw new SubscribeRuntimeException("SubscribeBmEJB.getNodeSubscriberDetails()", SilverpeasRuntimeException.ERROR, "subscribe.CANNOT_GET_NODE_SUBSCRIBERS", e);
		}
		finally
		{
			freeConnection(con);
		}
	}

    /**
     * Method declaration
     *
     *
     * @throws CreateException
     *
     * @see
     */
    public void ejbCreate() throws CreateException
    {
    }

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void ejbRemove()
    {
    }

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void ejbActivate()
    {
    }

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void ejbPassivate()
    {
    }

    /**
     * Method declaration
     *
     *
     * @param sc
     *
     * @see
     */
    public void setSessionContext(SessionContext sc)
    {
    }
}
