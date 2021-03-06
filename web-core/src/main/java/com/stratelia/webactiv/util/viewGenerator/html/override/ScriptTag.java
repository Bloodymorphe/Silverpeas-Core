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

package com.stratelia.webactiv.util.viewGenerator.html.override;

import com.stratelia.silverpeas.peasCore.URLManager;
import com.stratelia.webactiv.util.viewGenerator.html.JavascriptPluginInclusion;
import org.apache.ecs.ElementContainer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

public class ScriptTag extends TagSupport {
  private static final long serialVersionUID = 1439996118011433471L;

  private String webContext = URLManager.getApplicationURL();
  private String src;

  public void setWebContext(final String webContext) {
    this.webContext = webContext;
  }

  public String getWebContext() {
    return webContext;
  }

  public void setSrc(final String src) {
    this.src = src;
  }

  public String getSrc() {
    return src;
  }

  @Override
  public int doEndTag() throws JspException {
    ElementContainer script = new ElementContainer();
    String source = src.startsWith("/") && !src.startsWith(webContext) ? webContext + src : src;
    script.addElement(JavascriptPluginInclusion.script(source));
    script.output(pageContext.getOut());
    return EVAL_PAGE;
  }
}
