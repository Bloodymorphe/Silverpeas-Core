/*
 * Copyright (C) 2000 - 2015 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception. You should have recieved a copy of the text describing
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
package org.silverpeas.chart;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.silverpeas.util.StringUtil.defaultStringIfNotDefined;

/**
 * @author Yohann Chastagnier
 */
public abstract class AbstractChartItem<DATA_TYPE> implements ChartItem {

  private String title = "";
  private Map<String, Object> extra = null;

  @Override
  public String getTitle() {
    return title;
  }

  /**
   * Adds an extra information associated to the chart, but not necessary for the chart
   * rendering. Useful to provide data from a treatment to an other one.
   * @param key the key at which the given information is registered.
   * @param value the value registered.
   * @param <T>
   * @return the instance of the chart itself.
   */
  @SuppressWarnings("unchecked")
  public <T extends AbstractChartItem<DATA_TYPE>> T addExtra(final String key, Object value) {
    if (extra == null) {
      extra = new LinkedHashMap<String, Object>();
    }
    extra.put(key, value);
    return (T) this;
  }

  /**
   * Gets the value of an information associated to the chart, but not necessary for the chart
   * rendering. Useful to provide data from a treatment to an other one.
   * @param key the ket at which the extra data is registered.
   * @return the extra data as it has been registered.
   */
  public Object getExtra(String key) {
    return extra == null ? null : extra.get(key);
  }

  /**
   * Sets the title that defines the item.
   * @param title a title as string.
   * @param <T>
   * @return the instance of the chart itself.
   */
  @SuppressWarnings("unchecked")
  public <T extends AbstractChartItem<DATA_TYPE>> T withTitle(final String title) {
    this.title = defaultStringIfNotDefined(title);
    return (T) this;
  }

  /**
   * Gets the json representation of the item.
   * @return a string that represents the item as a json array.
   */
  @Override
  public final String asJson() {
    JSONObject itemAsJson = new JSONObject();
    itemAsJson.put("title", getTitle());
    if (extra != null) {
      JSONObject jsonExtra = new JSONObject();
      for (Map.Entry<String, Object> entry : extra.entrySet()) {
        jsonExtra.put(entry.getKey(), entry.getValue());
      }
      itemAsJson.put("extra", jsonExtra);
    }
    computeDataAsJson(itemAsJson);
    return itemAsJson.toString();
  }

  /**
   * Computes the data of the item into a JSON representation.
   */
  abstract protected void computeDataAsJson(JSONObject itemAsJson);
}
