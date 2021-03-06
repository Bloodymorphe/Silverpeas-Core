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
package com.silverpeas.subscribe.service;

import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.naming.NamingException;

import com.silverpeas.components.model.AbstractJndiCase;
import com.silverpeas.components.model.SilverpeasJndiCase;
import com.silverpeas.subscribe.SubscriptionSubscriber;

import com.stratelia.webactiv.util.node.model.NodePK;

import org.dbunit.database.IDatabaseConnection;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * @author ehugonnet
 */
public class SubscriptionDAOPerformanceTest extends AbstractJndiCase {

  private static SubscriptionDao subscriptionDao = new SubscriptionDao();
  private static final List<NodeSubscriptionResource> nodePks = Arrays.asList(
      NodeSubscriptionResource.from(new NodePK("0", "100", "kmelia60")),
      NodeSubscriptionResource.from(new NodePK("1", "100", "kmelia60")),
      NodeSubscriptionResource.from(new NodePK("10", "100", "kmelia60")),
      NodeSubscriptionResource.from(new NodePK("20", "100", "kmelia60")),
      NodeSubscriptionResource.from(new NodePK("30", "100", "kmelia60")),
      NodeSubscriptionResource.from(new NodePK("40", "100", "kmelia60")));

  @BeforeClass
  public static void generalSetUp() throws IOException, NamingException, Exception {
    baseTest = new SilverpeasJndiCase(
        "com/silverpeas/subscribe/service/node-actors-performance-test-dataset.xml",
        "create-database.ddl");
    baseTest.configureJNDIDatasource();
    IDatabaseConnection databaseConnection = baseTest.getDatabaseTester().getConnection();
    executeDDL(databaseConnection, baseTest.getDdlFile());
    baseTest.getDatabaseTester().closeConnection(databaseConnection);
  }

  @AfterClass
  public static void generalTearDown() throws IOException, NamingException, Exception {
    baseTest.shudown();
  }

  public SubscriptionDAOPerformanceTest() {
  }

  @Test
  public void testGetActorPKsByNodePKsInLoop() throws Exception {
    IDatabaseConnection dataSetConnection = baseTest.getDatabaseTester().getConnection();
    try {
      Connection connection = dataSetConnection.getConnection();
      long startTime = System.currentTimeMillis();
      for (int i = 0; i < 10000; i++) {
        Collection<SubscriptionSubscriber> result = subscriptionDao.getSubscribers(connection,
            nodePks, null);
        assertThat(result, hasSize(15));
        assertThat(result, hasItem("1"));
        assertThat(result, hasItem("2"));
        assertThat(result, hasItem("3"));
        assertThat(result, hasItem("4"));
        assertThat(result, hasItem("5"));
        assertThat(result, hasItem("11"));
        assertThat(result, hasItem("12"));
        assertThat(result, hasItem("13"));
        assertThat(result, hasItem("14"));
        assertThat(result, hasItem("15"));
        assertThat(result, hasItem("21"));
        assertThat(result, hasItem("22"));
        assertThat(result, hasItem("23"));
        assertThat(result, hasItem("24"));
        assertThat(result, hasItem("25"));
      }
      long duration = System.currentTimeMillis() - startTime;
      System.out.println("GetActorPKsByNodePKsInLoop " + duration);
    } finally {
      baseTest.getDatabaseTester().closeConnection(dataSetConnection);
    }
  }

  /**
   * Centralization.
   *
   * @param userId
   * @return
   */
  private Matcher<Iterable<? super UserSubscriptionSubscriber>> hasItem(String userId) {
    return Matchers.hasItem(UserSubscriptionSubscriber.from(userId));
  }
}
