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

package com.silverpeas.admin.components;

import com.silverpeas.ui.DisplayI18NHelper;
import com.silverpeas.util.CollectionUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Java class for WAComponentType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;WAComponentType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;name&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;label&quot; type=&quot;{http://silverpeas.org/xml/ns/component}multilang&quot;/&gt;
 *         &lt;element name=&quot;description&quot; type=&quot;{http://silverpeas.org/xml/ns/component}multilang&quot;/&gt;
 *         &lt;element name=&quot;suite&quot; type=&quot;{http://silverpeas.org/xml/ns/component}multilang&quot;/&gt;
 *         &lt;element name=&quot;visible&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}boolean&quot;/&gt;
 *         &lt;element name=&quot;visibleInPersonalSpace&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}boolean&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;portlet&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}boolean&quot;/&gt;
 *         &lt;element name=&quot;router&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;instanceClassName&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 *         &lt;element name=&quot;profiles&quot;&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name=&quot;profile&quot; type=&quot;{http://silverpeas.org/xml/ns/component}ProfileType&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name=&quot;parameters&quot; minOccurs=&quot;0&quot;&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name=&quot;parameter&quot; type=&quot;{http://silverpeas.org/xml/ns/component}ParameterType&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WAComponentType", propOrder = { "name", "label", "description", "suite",
    "visible", "visibleInPersonalSpace", "portlet", "router", "instanceClassName", "profiles",
    "groupsOfParameters", "parameters" })
public class WAComponent {
  @XmlTransient
  private ParameterSorter sorter = new ParameterSorter();

  @XmlElement(required = true)
  protected String name;
  @XmlElement(required = true)
  @XmlJavaTypeAdapter(MultilangHashMapAdapter.class)
  protected HashMap<String, String> label;
  @XmlElement(required = true)
  @XmlJavaTypeAdapter(MultilangHashMapAdapter.class)
  protected HashMap<String, String> description;
  @XmlElement(required = true)
  @XmlJavaTypeAdapter(MultilangHashMapAdapter.class)
  protected HashMap<String, String> suite;
  protected boolean visible;
  protected boolean visibleInPersonalSpace = false;
  protected boolean portlet;
  protected String router;
  @XmlElement(required = true)
  protected String instanceClassName;
  @XmlElementWrapper(name = "profiles")
  @XmlElement(name = "profile", required = true)
  protected List<Profile> profiles;
  @XmlElementWrapper(name = "groupsOfParameters")
  @XmlElement(name = "groupOfParameters")
  protected List<GroupOfParameters> groupsOfParameters;
  @XmlElementWrapper(name = "parameters")
  @XmlElement(name = "parameter")
  protected List<Parameter> parameters;
  @XmlTransient
  protected Map<String, Parameter> indexedParametersByName = new HashMap<String, Parameter>();

  /**
   * Gets the value of the name property.
   * @return possible object is {@link String }
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of the name property.
   * @param value allowed object is {@link String }
   */
  public void setName(String value) {
    this.name = value;
  }

  /**
   * Gets the value of the label property.
   * @return possible object is {@link Multilang }
   */
  public HashMap<String, String> getLabel() {
    if (label == null) {
      label = new HashMap<String, String>();
    }
    return label;
  }
  
  public String getLabel(String lang) {
    if (getLabel().containsKey(lang)) {
      return getLabel().get(lang);
    }
    return getLabel().get(DisplayI18NHelper.getDefaultLanguage());
  }

  /**
   * Sets the value of the label property.
   * @param value allowed object is {@link Multilang }
   */
  public void setLabel(HashMap<String, String> value) {
    this.label = value;
  }

  /**
   * Gets the value of the description property.
   * @return possible object is {@link Multilang }
   */
  public HashMap<String, String> getDescription() {
    if (description == null) {
      description = new HashMap<String, String>();
    }
    return description;
  }
  
  public String getDescription(String lang) {
    if (getDescription().containsKey(lang)) {
      return getDescription().get(lang);
    }
    return getDescription().get(DisplayI18NHelper.getDefaultLanguage());
  }

  /**
   * Sets the value of the description property.
   * @param value allowed object is {@link Multilang }
   */
  public void setDescription(HashMap<String, String> value) {
    this.description = value;
  }

  /**
   * Gets the value of the suite property.
   * @return possible object is {@link Multilang }
   */
  public HashMap<String, String> getSuite() {
    if (suite == null) {
      suite = new HashMap<String, String>();
    }
    return suite;
  }
  
  public String getSuite(String lang) {
    if (getSuite().containsKey(lang)) {
      return getSuite().get(lang);
    }
    return getSuite().get(DisplayI18NHelper.getDefaultLanguage());
  }

  /**
   * Sets the value of the suite property.
   * @param value allowed object is {@link Multilang }
   */
  public void setSuite(HashMap<String, String> value) {
    this.suite = value;
  }

  /**
   * Gets the value of the visible property.
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Sets the value of the visible property.
   */
  public void setVisible(boolean value) {
    this.visible = value;
  }

  /**
   * Gets the value of the visibleInPersonalSpace property.
   * @return possible object is {@link Boolean }
   */
  public boolean isVisibleInPersonalSpace() {
    return visibleInPersonalSpace;
  }

  /**
   * Sets the value of the visibleInPersonalSpace property.
   * @param value allowed object is {@link Boolean }
   */
  public void setVisibleInPersonalSpace(boolean value) {
    this.visibleInPersonalSpace = value;
  }

  /**
   * Gets the value of the portlet property.
   */
  public boolean isPortlet() {
    return portlet;
  }

  /**
   * Sets the value of the portlet property.
   */
  public void setPortlet(boolean value) {
    this.portlet = value;
  }

  /**
   * Gets the value of the router property.
   * @return possible object is {@link String }
   */
  public String getRouter() {
    return router;
  }

  /**
   * Sets the value of the router property.
   * @param value allowed object is {@link String }
   */
  public void setRouter(String value) {
    this.router = value;
  }

  /**
   * Gets the value of the instanceClassName property.
   * @return possible object is {@link String }
   */
  public String getInstanceClassName() {
    return instanceClassName;
  }

  /**
   * Sets the value of the instanceClassName property.
   * @param value allowed object is {@link String }
   */
  public void setInstanceClassName(String value) {
    this.instanceClassName = value;
  }

  /**
   * Gets the value of the profiles property.
   * @return list of {@link Profile }
   */
  public List<Profile> getProfiles() {
    if (profiles == null) {
      profiles = new ArrayList<Profile>();
    }
    return profiles;
  }

  public Profile getProfile(String name) {
    for (Profile profile : getProfiles()) {
      if (profile.getName().equals(name)) {
        return profile;
      }
    }
    return null;
  }

  /**
   * Sets the value of the profiles property.
   * @param profiles list of {@link Profile}
   */
  public void setProfiles(List<Profile> profiles) {
    this.profiles = profiles;
  }

  /**
   * Gets the value of the parameters property.
   * @return list of {@link Parameter}
   */
  public List<Parameter> getParameters() {
    if (parameters == null) {
      parameters = new ArrayList<Parameter>();
    }
    return parameters;
  }

  /**
   * Gets defined parameters indexed by their names.
   * @return
   */
  private Map<String, Parameter> getIndexedParametersByName() {
    List<Parameter> definedParameters = getParameters();
    if (CollectionUtil.isNotEmpty(definedParameters) &&
        definedParameters.size() != indexedParametersByName.size()) {
      for (Parameter parameter : definedParameters) {
        indexedParametersByName.put(parameter.getName(), parameter);
      }
    }
    List<GroupOfParameters> groupsOfParameters = getGroupsOfParameters();
    for (GroupOfParameters group : groupsOfParameters) {
      for (Parameter parameter : group.getParameters()) {
        indexedParametersByName.put(parameter.getName(), parameter);
      }
    }
    return indexedParametersByName;
  }

  /**
   * Sets the value of the parameters property.
   * @param parameters list of {@link Parameter}
   */
  public void setParameters(List<Parameter> parameters) {
    this.parameters = parameters;
    indexedParametersByName.clear();
  }

  /**
   * Indicates if a parameter is defined which name is equal to the given method parameter.
   * @param parameterName the parameter name to perform.
   * @return true if a parameter is defined behind the specified method parameter, false otherwise.
   */
  public boolean hasParameterDefined(String parameterName) {
    return getIndexedParametersByName().get(parameterName) != null;
  }

  public List<Parameter> getSortedParameters() {
    Collections.sort(getParameters(), sorter);
    return this.parameters;
  }

  public ParameterList getAllParameters() {
    ParameterList result = new ParameterList();
    for (Parameter param : getParameters()) {
      result.add(param);
    }
    for (GroupOfParameters group : getGroupsOfParameters()) {
      result.addAll(group.getParameters());
    }
    return result;
  }
  
  public List<GroupOfParameters> getGroupsOfParameters() {
    if (groupsOfParameters == null) {
      groupsOfParameters = new ArrayList<GroupOfParameters>();
    }
    return groupsOfParameters;
  }
  
  public List<GroupOfParameters> getSortedGroupsOfParameters() {
    Collections.sort(getGroupsOfParameters(), new GroupOfParametersSorter());
    return this.groupsOfParameters;
  }

}