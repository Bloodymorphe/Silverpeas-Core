 
package com.stratelia.webactiv.util.pool;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.util.ResourceLocator;

public class ConnectionPool {

  private static BasicDataSource pool;
  
  static {
    SilverTrace.debug("util", "ConnectionPool.getConnection", "No more free connection : we need to create a new one.");
    ResourceLocator resources = new ResourceLocator("com.stratelia.webactiv.beans.admin.admin", "");
    pool = new BasicDataSource();
    pool.setPassword(resources.getString("WaProductionPswd"));
    pool.setUsername(resources.getString("WaProductionUser"));
    pool.setDriverClassName(resources.getString("AdminDBDriver"));
    pool.setUrl(resources.getString("WaProductionDb"));
  }
      
  public static void releaseConnections() throws SQLException {
      SilverTrace.debug("util", "ConnectionPool.releaseConnections", "start");
      synchronized(pool) {
        pool.close();
      }
  }

   /**
    * permet de r�cup�rer une connection depuis un pool de connection libre.
    * On lib�re la connection en la fermant (connection.close()), elle revient alors toute seule dans le pool.
    * @return Une connection BDD vers la base de donn�e silverpeas.
    */
  public static Connection getConnection() throws SQLException {
      SilverTrace.debug("util", "ConnectionPool.getConnection", "start");
      return pool.getConnection();
  }
}