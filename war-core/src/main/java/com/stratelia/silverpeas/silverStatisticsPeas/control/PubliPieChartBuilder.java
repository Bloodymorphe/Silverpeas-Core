/**
 * Copyright (C) 2000 - 2013 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have received a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://www.silverpeas.org/docs/core/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.stratelia.silverpeas.silverStatisticsPeas.control;

import com.silverpeas.util.StringUtil;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.beans.admin.AdminReference;
import com.stratelia.webactiv.beans.admin.SpaceInstLight;
import com.stratelia.webactiv.util.ResourceLocator;
import org.silverpeas.chart.pie.PieChart;
import org.silverpeas.core.admin.OrganisationController;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * <p/>
 * <p/>
 * @author CBONIN
 */
public class PubliPieChartBuilder extends AbstractPieChartBuilder {

  private String dateStat;
  private String dateFormate;
  private String currentUserId;
  private String filterIdGroup;
  private String filterIdUser;
  private String spaceId;
  private OrganisationController organizationController;
  private ResourceLocator message;

  public PubliPieChartBuilder(String dateStat, String dateFormate, String currentUserId,
      String filterIdGroup, String filterIdUser, String spaceId,
      OrganisationController organizationController, ResourceLocator message) {
    this.dateStat = dateStat;
    this.dateFormate = dateFormate;
    this.currentUserId = currentUserId;
    this.filterIdGroup = filterIdGroup;
    this.filterIdUser = filterIdUser;
    this.spaceId = spaceId;
    this.organizationController = organizationController;
    this.message = message;
  }

  /*
   * (non-Javadoc)
   * @see com.stratelia.silverpeas.silverStatisticsPeas.control.AbstractPieChartBuilder
   * #getChartTitle()
   */
  @Override
  public String getChartTitle() {
    String title = message.getString("silverStatisticsPeas.VolumeNumber") + " ";
    if (StringUtil.isDefined(this.filterIdGroup) && !StringUtil.isDefined(this.filterIdUser)) {
      title += message.getString("silverStatisticsPeas.EvolutionAccessGroup")
          + " " + this.organizationController.getGroup(this.filterIdGroup).getName() + " ";
    }
    if (StringUtil.isDefined(this.filterIdUser)) {
      title += message.getString("silverStatisticsPeas.EvolutionAccessUser")
          + " " + this.organizationController.getUserDetail(this.filterIdUser).getDisplayedName() + " ";
    }

    try {
      if (StringUtil.isDefined(this.spaceId) && (!"WA0".equals(this.spaceId))) {
        SpaceInstLight space = AdminReference.getAdminService().getSpaceInstLightById(this.spaceId);
        title += message.getString("silverStatisticsPeas.FromSpace") + " \""
            + space.getName() + "\" ";
      }
    } catch (Exception e) {
      SilverTrace.error("silverStatisticsPeas", "PubliPieChartBuilder.getChartTitle()",
          "root.EX_SQL_QUERY_FAILED", e);
    }
    title += message.getString("silverStatisticsPeas.Until") + " " + this.dateFormate;
    return title;
  }

  /*
   * (non-Javadoc)
   * @see com.stratelia.silverpeas.silverStatisticsPeas.control.AbstractPieChartBuilder
   * #getCmpStats()
   */
  @Override
  Map<String, String[]> getCmpStats() {
    // Hashtable key=componentId, value=new String[3] {tout, groupe, user}
    Map<String, String[]> cmpStats = new HashMap<String, String[]>();
    try {
      cmpStats.putAll(SilverStatisticsPeasDAOAccesVolume.getStatsPublicationsVentil(dateStat,
          currentUserId, filterIdGroup, filterIdUser));
    } catch (SQLException e) {
      SilverTrace.error("silverStatisticsPeas",
          "PubliPieChartBuilder.getCmpStats()", "root.EX_SQL_QUERY_FAILED", e);
    }
    return cmpStats;
  }
  
  @Override
  public PieChart getChart(String spaceId, String currentUserId, Vector<String[]> currentStats) {
    setScope(AbstractPieChartBuilder.FINESSE_TOUS);
    if (StringUtil.isDefined(filterIdGroup)) {
      setScope(AbstractPieChartBuilder.FINESSE_GROUPE);
    } else if (StringUtil.isDefined(filterIdUser)) {
      setScope(AbstractPieChartBuilder.FINESSE_USER);
    }
    return super.getChart(spaceId, currentUserId, currentStats);
  }
}
