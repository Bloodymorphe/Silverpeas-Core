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

package com.stratelia.silverpeas.silverstatistics.control;

import com.stratelia.silverpeas.silvertrace.SilverTrace;
import javax.inject.Inject;

/**
 * @author ehugonnet
 */
public class SilverStatisticsFactory {
  private static final SilverStatisticsFactory instance = new SilverStatisticsFactory();

  @Inject
  private SilverStatistics silverStatistics;

  /**
   * Gets an instance of this PersonalizationServiceFactory class.
   * @return a PersonalizationServiceFactory instance.
   */
  public static SilverStatisticsFactory getFactory() {
    return instance;
  }

  /**
   * Gets a PersonalizationService instance.
   * @return a PersonalizationService instance.
   */
  public SilverStatistics getSilverStatistics() {
    if (silverStatistics == null) {
      SilverTrace.warn("personalization", getClass().getSimpleName() +
          ".getSilverStatisticsService()",
          "EX_NO_MESSAGES",
          "IoC container not bootstrapped or no SilverStatisticsService bean found!");
    }
    return silverStatistics;
  }

  private SilverStatisticsFactory() {
  }
}
