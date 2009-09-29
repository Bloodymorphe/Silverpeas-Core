package com.silverpeas.jobDomainPeas;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.beans.admin.AdminController;
import com.stratelia.webactiv.beans.admin.Domain;
import com.stratelia.webactiv.beans.admin.Group;
import com.stratelia.webactiv.beans.admin.UserDetail;
import com.stratelia.webactiv.beans.admin.UserFull;
import com.stratelia.webactiv.util.DBUtil;
import com.stratelia.webactiv.util.JNDINames;
import com.stratelia.webactiv.util.ResourceLocator;
import com.stratelia.webactiv.util.exception.SilverpeasException;

/**
 * Class declaration Get connections data from database
 * 
 * @author
 */
public class JobDomainPeasDAO {

  private static final String DB_NAME = JNDINames.SILVERPEAS_DATASOURCE;

  /**
   * Cr�ation de la table Domain<domainName>_Group
   * 
   * 
   * @param domainName
   * 
   * @throws SQLException
   * 
   * @see
   */
  public static void createTableDomain_Group(String domainName)
      throws SQLException {
    SilverTrace
        .info("jobDomainPeas", "JobDomainPeasDAO.createTableDomain_Group",
            "root.MSG_GEN_ENTER_METHOD");

    String createQuery = " CREATE TABLE Domain" + domainName + "_Group " + "("
        + "	id int NOT NULL ," + "	superGroupId int NULL ,"
        + "	name varchar(100) NOT NULL ," + "	description varchar(400) NULL ,"
        + "	grSpecificInfo varchar(50) NULL" + ")";

    Statement stmt = null;
    Connection myCon = getConnection();

    try {
      stmt = myCon.createStatement();
      stmt.executeUpdate(createQuery);
    } finally {
      DBUtil.close(stmt);
      freeConnection(myCon);
    }
  }

  /**
   * Suppression de la table Domain<domainName>_Group
   * 
   * 
   * @param domainName
   * 
   * @throws SQLException
   * 
   * @see
   */
  public static void dropTableDomain_Group(String domainName)
      throws SQLException {
    SilverTrace.info("jobDomainPeas", "JobDomainPeasDAO.dropTableDomain_Group",
        "root.MSG_GEN_ENTER_METHOD");

    String createQuery = " DROP TABLE Domain" + domainName + "_Group ";
    Statement stmt = null;
    Connection myCon = getConnection();

    try {
      stmt = myCon.createStatement();
      stmt.executeUpdate(createQuery);
    } finally {
      DBUtil.close(stmt);
      freeConnection(myCon);
    }
  }

  /**
   * Cr�ation de la table Domain<domainName>_User
   * 
   * 
   * @param domainName
   * 
   * @throws SQLException
   * 
   * @see
   */
  public static void createTableDomain_User(String domainName)
      throws SQLException {
    SilverTrace.info("jobDomainPeas",
        "JobDomainPeasDAO.createTableDomain_User", "root.MSG_GEN_ENTER_METHOD");

    ResourceLocator propSpecificDomainSQL = new ResourceLocator(
        "com.stratelia.silverpeas.domains.templateDomainSQL", "");
    int numberOfColumns = new Integer(propSpecificDomainSQL
        .getString("property.Number")).intValue();

    String createQuery = " CREATE TABLE Domain" + domainName + "_User " + "("
        + "id int NOT NULL ," + "firstName varchar(100) NULL ,"
        + "lastName varchar(100) NULL ," + "email varchar(200) NULL ,"
        + "login varchar(20) NOT NULL ," + "password varchar(32) NULL ,"
        + "passwordValid char(1) NULL ,";

    String nameColumnTable;
    String typeColumnTable;
    for (int i = 1; i <= numberOfColumns; i++) {
      typeColumnTable = propSpecificDomainSQL.getString("property_"
          + Integer.toString(i) + ".Type");
      nameColumnTable = propSpecificDomainSQL.getString("property_"
          + Integer.toString(i) + ".MapParameter");

      createQuery += nameColumnTable + " ";

      if ("BOOLEAN".equals(typeColumnTable)) {
        createQuery += "int NOT NULL DEFAULT (0) ,";
      } else {
        createQuery += "varchar(50) NULL ,";
      }
    }

    createQuery = createQuery.substring(0, createQuery.length() - 2);
    createQuery += ")";

    Statement stmt = null;
    Connection myCon = getConnection();

    try {
      stmt = myCon.createStatement();
      stmt.executeUpdate(createQuery);
    } finally {
      DBUtil.close(stmt);
      freeConnection(myCon);
    }
  }

  /**
   * Suppression de la table Domain<domainName>_User
   * 
   * 
   * @param domainName
   * 
   * @throws SQLException
   * 
   * @see
   */
  public static void dropTableDomain_User(String domainName)
      throws SQLException {
    SilverTrace.info("jobDomainPeas", "JobDomainPeasDAO.dropTableDomain_User",
        "root.MSG_GEN_ENTER_METHOD");

    String createQuery = " DROP TABLE Domain" + domainName + "_User ";
    Statement stmt = null;
    Connection myCon = getConnection();

    try {
      stmt = myCon.createStatement();
      stmt.executeUpdate(createQuery);
    } finally {
      DBUtil.close(stmt);
      freeConnection(myCon);
    }
  }

  /**
   * Cr�ation de la table Domain<domainName>_Group_User_Rel
   * 
   * 
   * @param domainName
   * 
   * @throws SQLException
   * 
   * @see
   */
  public static void createTableDomain_Group_User_Rel(String domainName)
      throws SQLException {
    SilverTrace.info("jobDomainPeas",
        "JobDomainPeasDAO.createTableDomain_Group_User_Rel",
        "root.MSG_GEN_ENTER_METHOD");

    String createQuery = " CREATE TABLE Domain" + domainName
        + "_Group_User_Rel " + "(" + "groupId int NOT NULL ,"
        + "userId int NOT NULL" + ")";

    Statement stmt = null;
    Connection myCon = getConnection();

    try {
      stmt = myCon.createStatement();
      stmt.executeUpdate(createQuery);
    } finally {
      DBUtil.close(stmt);
      freeConnection(myCon);
    }
  }

  /**
   * Suppression de la table Domain<domainName>_Group_User_Rel
   * 
   * 
   * @param domainName
   * 
   * @throws SQLException
   * 
   * @see
   */
  public static void dropTableDomain_Group_User_Rel(String domainName)
      throws SQLException {
    SilverTrace.info("jobDomainPeas",
        "JobDomainPeasDAO.dropTableDomain_Group_User_Rel",
        "root.MSG_GEN_ENTER_METHOD");

    String createQuery = " DROP TABLE Domain" + domainName + "_Group_User_Rel ";
    Statement stmt = null;
    Connection myCon = getConnection();

    try {
      stmt = myCon.createStatement();
      stmt.executeUpdate(createQuery);
    } finally {
      DBUtil.close(stmt);
      freeConnection(myCon);
    }
  }

  /**
   * Method declaration
   * 
   * 
   * @return
   * 
   * @see
   */
  private static Connection getConnection() {
    try {
      Connection con = DBUtil.makeConnection(DB_NAME);

      return con;
    } catch (Exception e) {
      throw new JobDomainPeasRuntimeException(
          "JobDomainPeasDAO.getConnection()", SilverpeasException.ERROR,
          "root.EX_CONNECTION_OPEN_FAILED", "DbName=" + DB_NAME, e);
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
  private static void freeConnection(Connection con) {
    if (con != null) {
      try {
        con.close();
      } catch (Exception e) {
        SilverTrace.error("JobDomainPeas", "JobDomainPeasDAO.freeConnection()",
            "root.EX_CONNECTION_CLOSE_FAILED", "", e);
      }
    }
  }

  /**
   * S�lection des groupes � synchroniser en insert ou update de la table
   * Domain<domainName>_Group
   * 
   * 
   * @param domainName
   * @return Collection de Group
   * @throws SQLException
   * 
   * @see
   */
  public static Collection selectGroupSynchroInsertUpdateTableDomain_Group(
      Domain domain) throws SQLException {
    SilverTrace.info("jobDomainPeas",
        "JobDomainPeasDAO.selectGroupSynchroInsertUpdateTableDomain_Group",
        "root.MSG_GEN_ENTER_METHOD");

    String propDomainFileName = domain.getPropFileName();
    String domainName = propDomainFileName.substring(39);

    AdminController adminCtrl = new AdminController(null);

    // s�lectionne les users dans Silverpeas
    Collection listRes = new ArrayList();

    String query = " SELECT g.id" + " FROM Domain" + domainName
        + "_Group d, ST_Group g " + " WHERE g.domainId = " + domain.getId()
        + " AND g.specificId = CAST(d.id AS varchar)";

    Statement stmt = null;
    Connection myCon = getConnection();
    try {
      stmt = myCon.createStatement();
      ResultSet resultSet = stmt.executeQuery(query);

      String groupId;
      Group group;
      while (resultSet.next()) {
        groupId = new Integer(resultSet.getInt(1)).toString();
        group = adminCtrl.getGroupById(groupId);
        if (!group.isSynchronized())
          listRes.add(group);
      }
    } finally {
      DBUtil.close(stmt);
      freeConnection(myCon);
    }

    return listRes; // Collection de Group
  }

  /**
   * S�lection des utilisateurs � synchroniser en insert ou update de la table
   * Domain<domainName>_User
   * 
   * 
   * @param domainName
   * @return Collection de UserFull
   * @throws SQLException
   * 
   * @see
   */
  public static Collection selectUserSynchroInsertUpdateTableDomain_User(
      Domain domain) throws SQLException {
    SilverTrace.info("jobDomainPeas",
        "JobDomainPeasDAO.selectUserSynchroInsertUpdateTableDomain_User",
        "root.MSG_GEN_ENTER_METHOD");

    String propDomainFileName = domain.getPropFileName();
    String domainName = propDomainFileName.substring(39);

    AdminController adminCtrl = new AdminController(null);

    // s�lectionne les users dans Silverpeas
    Collection listRes = new ArrayList();
    UserFull userFull;

    String query = " SELECT u.id" + " FROM Domain" + domainName
        + "_User d, ST_User u " + " WHERE u.domainId = " + domain.getId()
        + " AND u.specificId = CAST(d.id AS varchar)";

    Statement stmt = null;
    Connection myCon = getConnection();
    try {
      stmt = myCon.createStatement();
      ResultSet resultSet = stmt.executeQuery(query);

      String userId;
      while (resultSet.next()) {
        userId = new Integer(resultSet.getInt(1)).toString();
        userFull = adminCtrl.getUserFull(userId);

        listRes.add(userFull);
      }
    } finally {
      DBUtil.close(stmt);
      freeConnection(myCon);
    }

    // Collection listRes2 = new ArrayList();
    /*
     * Iterator itUserSilverpeas = listRes.iterator(); String specificId; String
     * specificIdGroup; String groupId; while(itUserSilverpeas.hasNext()) {
     * userFull = (UserFull) itUserSilverpeas.next();
     * 
     * //groupe specificId = userFull.getSpecificId(); specificIdGroup =
     * selectGroupTableDomain_Group_User_Rel(domain, new
     * Integer(specificId).intValue()); groupId =
     * selectGroupTableST_Group(domain, specificIdGroup);
     * userFull.setSpecificId(groupId); }
     */

    return listRes; // Collection de UserFull
  }

  /**
   * S�lection du groupId pour le userId sp�cifi� dans la table
   * Domain<domainName>_Group_User_Rel
   * 
   * @return String
   * @throws SQLException
   * 
   * @see
   */
  /*
   * private static String selectGroupTableDomain_Group_User_Rel(Domain domain,
   * int userSpecificId) throws SQLException { SilverTrace.info(
   * "jobDomainPeas", "JobDomainPeasDAO.selectGroupTableDomain_Group_User_Rel",
   * "root.MSG_GEN_ENTER_METHOD");
   * 
   * String propDomainFileName = domain.getPropFileName(); String domainName =
   * propDomainFileName.substring(39);
   * 
   * String groupId = "";
   * 
   * String query = " SELECT d.groupId"+
   * " FROM Domain"+domainName+"_Group_User_Rel d "+
   * " WHERE d.userId = "+userSpecificId;
   * 
   * Statement stmt = null; Connection myCon = getConnection(); try { stmt =
   * myCon.createStatement(); ResultSet resultSet = stmt.executeQuery(query);
   * 
   * if (resultSet.next()) { groupId = new
   * Integer(resultSet.getInt(1)).toString(); } } finally { DBUtil.close(stmt);
   * freeConnection(myCon); }
   * 
   * return groupId; }
   */

  /**
   * S�lection du groupId pour le specificGroupId sp�cifi� dans la table
   * ST_Group
   * 
   * @return String
   * @throws SQLException
   * 
   * @see
   */
  /*
   * private static String selectGroupTableST_Group(Domain domain, String
   * groupSpecificId) throws SQLException { SilverTrace.info( "jobDomainPeas",
   * "JobDomainPeasDAO.selectGroupTableST_Group", "root.MSG_GEN_ENTER_METHOD");
   * 
   * String propDomainFileName = domain.getPropFileName(); String domainName =
   * propDomainFileName.substring(39);
   * 
   * String groupId = "";
   * 
   * String query = " SELECT g.id"+
   * " FROM Domain"+domainName+"_Group d, ST_Group g "+
   * " WHERE g.domainId = "+domain.getId() +
   * " AND g.specificId = "+groupSpecificId;
   * 
   * Statement stmt = null; Connection myCon = getConnection(); try { stmt =
   * myCon.createStatement(); ResultSet resultSet = stmt.executeQuery(query);
   * 
   * if (resultSet.next()) { groupId = new
   * Integer(resultSet.getInt(1)).toString(); } } finally { DBUtil.close(stmt);
   * freeConnection(myCon); }
   * 
   * return groupId; }
   */

  /**
   * S�lection des utilisateurs � synchroniser en delete de la table
   * Domain<domainName>_User
   * 
   * 
   * @param domainName
   * @return Collection de UserDetail
   * @throws SQLException
   * 
   * @see
   */
  public static Collection selectUserSynchroDeleteTableDomain_User(Domain domain)
      throws SQLException {
    SilverTrace.info("jobDomainPeas",
        "JobDomainPeasDAO.selectUserSynchroDeleteTableDomain_User",
        "root.MSG_GEN_ENTER_METHOD");

    AdminController adminCtrl = new AdminController(null);

    // s�lectionne les users dans Silverpeas
    Collection listRes = new ArrayList();
    UserDetail userDetail;

    String query = " SELECT id " + " FROM ST_User " + " WHERE domainId = "
        + domain.getId() + " AND accessLevel = 'R'";

    Statement stmt = null;
    Connection myCon = getConnection();
    try {
      stmt = myCon.createStatement();
      ResultSet resultSet = stmt.executeQuery(query);

      String userId;
      while (resultSet.next()) {
        userId = resultSet.getString(1);
        userDetail = adminCtrl.getUserDetail(userId);
        listRes.add(userDetail);
      }
    } finally {
      DBUtil.close(stmt);
      freeConnection(myCon);
    }

    return listRes; // Collection de UserDetail
  }
}