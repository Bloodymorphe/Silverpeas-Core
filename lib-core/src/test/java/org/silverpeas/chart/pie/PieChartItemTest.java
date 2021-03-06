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
package org.silverpeas.chart.pie;

import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Yohann Chastagnier
 */
public class PieChartItemTest extends AbstractPieChartTest {

  @Test
  public void withoutTitleAndWithoutData() {
    PieChartItem item = new PieChartItem("", null);
    assertThat(item.asJson(), is(expJsItem("").toString()));
  }

  @Test
  public void withoutTitleAndWithoutDataButWithExtraInformation() {
    PieChartItem item = new PieChartItem("", null).addExtra("youpi", "tralala").addExtra("26", 98);
    assertThat(item.asJson(),
        is(expJsItem("").put("extra", new JSONObject().put("youpi", "tralala").put("26", 98))
            .toString()));
  }

  @Test
  public void withTitleButWithoutData() {
    PieChartItem item = new PieChartItem("", null).withTitle("A title");
    assertThat(item.asJson(), is(expJsItem("A title").toString()));
  }

  @Test
  public void withoutTitleAndIntData() {
    PieChartItem item = new PieChartItem("Part 1", 26);
    assertThat(item.asJson(), is(expJsItem("", "Part 1", 26).toString()));
  }

  @Test
  public void withoutTitleAndLongData() {
    PieChartItem item = new PieChartItem("Part 1", 564654564l);
    assertThat(item.asJson(), is(expJsItem("", "Part 1", 564654564l).toString()));
  }

  @Test
  public void withoutTitleAndDoubleData() {
    PieChartItem item = new PieChartItem("Part 2", 38.26d);
    assertThat(item.asJson(), is(expJsItem("", "Part 2", 38.26d).toString()));
  }

  @Test
  public void withTitleAndFloatData() {
    PieChartItem item = new PieChartItem("Part 1", 36.5998f).withTitle("Float");
    assertThat(item.asJson(), is(expJsItem("Float", "Part 1", 36.5998f).toString()));
  }

  @Test
  public void withTitleAndTwoDataComposedOfOneFloatAndOneBigDecimal() {
    PieChartItem item = new PieChartItem("Part 2", new BigDecimal("2.8")).withTitle("BigDecimal");
    assertThat(item.asJson(),
        is(expJsItem("BigDecimal", "Part 2", new BigDecimal("2.8")).toString()));
  }
}