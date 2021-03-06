/*
 * Copyright (C) 2000 - 2016 Silverpeas
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
package com.stratelia.webactiv.beans.admin;

import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPLocalException;
import com.silverpeas.util.ArrayUtil;
import com.silverpeas.util.StringUtil;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.organization.AdminPersistenceException;
import com.stratelia.webactiv.organization.UserRow;
import com.stratelia.webactiv.util.exception.SilverpeasException;
import com.stratelia.webactiv.util.exception.WithNested;
import org.silverpeas.admin.user.constant.UserAccessLevel;
import org.silverpeas.util.BiFunction;
import org.silverpeas.util.Function;
import org.silverpeas.util.PrefixedNotationExpressionEngine;
import org.silverpeas.util.PrefixedNotationExpressionEngine.OperatorFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.silverpeas.util.CollectionUtil.intersection;
import static com.silverpeas.util.CollectionUtil.union;
import static com.stratelia.webactiv.beans.admin.AdminReference.getAdminService;
import static java.util.Arrays.asList;

/**
 * This class handles the evaluation of a synchronization rule of group.
 * <p>
 * A synchronization rule is represented by a {@link String} composed by:
 * <ul>
 * <li>
 * a simple rule
 * <ul>
 * <li><b>DS_AccessLevel = [one of {@link UserAccessLevel#getCode()}]</b> gets
 * identifiers of users which has the specified access level</li>
 * <li><b>DS_Domains = [domain identifiers separated by comma]</b> gets identifiers of users which
 * are registered into domains represented by the specified identifiers</li>
 * <li><b>DC_[user extra property name] = [aimed value]</b> gets identifiers of users
 * which the specified value is the one registered for the specified extra property
 * name</li>
 * <li><b>DR_Groups = [group identifiers separated by comma]</b> gets the
 * identifiers of users which are directly registered into the groups (and not the sub
 * groups) represented by the list of identifiers</li>
 * <li><b>DR_GroupsWithSubGroups = [group identifiers separated by comma]</b>
 * gets the identifiers of users which are registered into the groups (and the sub
 * groups) represented by the list of identifiers</li>
 * </ul>
 * </li>
 * <li>
 * a combination of simple rules. The language to write the combination is the one of
 * prefixed notation expression. Please take a look at documentation of
 * {@link PrefixedNotationExpressionEngine} class to get more information about this
 * language.<br/>
 * Each operand, here, can be:
 * <ul>
 * <li><b>a simple rule</b> one of those defined above</li>
 * <li><b>an operation with</b> one or several operands</li>
 * </ul>
 * The possible operators here:
 * <ul>
 * <li><b>&</b>: apply an AND operation between each operand, so an intersection between the
 * users ids guessed from each operand</li>
 * <li><b>|</b>: apply an OR operation between each operand, so an intersection between the
 * users ids guessed from each operand</li>
 * <li><b>!</b>: apply a negation on one operand only, so all the users ids of the
 * platform unless those guessed from the operand. Not working if several one are defined</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 * @author Yohann Chastagnier
 */
class GroupSynchronizationRule {

  private static final Pattern ACCESSLEVEL_STANDARD_DATA_PATTERN =
      Pattern.compile("(?i)^\\s*ds_accesslevel\\s*=\\s*(\\S+)\\s*$");

  private static final Pattern DOMAIN_STANDARD_DATA_PATTERN =
      Pattern.compile("(?i)^\\s*ds_domain[s]?\\s*=\\s*(.+)\\s*$");

  private static final Pattern COMPLEMENTARY_DATA_PATTERN =
      Pattern.compile("(?i)^\\s*dc_(\\S+)\\s*=\\s*(.+)\\s*$");

  private static final Pattern GROUP_RULE_DATA_PATTERN =
      Pattern.compile("(?i)^\\s*dr_groups(withsubgroups)?\\s*=\\s*(.+)\\s*$");

  private static final Pattern EXPRESSION_PATTERN = Pattern.compile("(?i)^\\s*[(].+[)]\\s*$");

  private final Group group;
  private final boolean isSharedDomain;

  private List<String> cacheOfAllUserIds = null;

  /**
   * Gets a new instance initialized with the context of the given group.
   * @param group group instance which contains mandatory group identifier, optional domain
   * identifier and the synchronization rule.
   * @return the initialized instance.
   */
  public static GroupSynchronizationRule from(final Group group) {
    return new GroupSynchronizationRule(group);
  }

  /**
   * Hidden constructor
   */
  private GroupSynchronizationRule(final Group group) {
    this.group = group;
    this.isSharedDomain = group.getDomainId() == null || "-1".equals(group.getDomainId());
  }

  /**
   * Gets the list of user identifiers represented by the synchronization rule.
   * @return list of strings where each one represents a Silverpeas user identifier.
   * @throws AdminException
   */
  public List<String> getUserIds() throws AdminException {
    List<String> userIds = evaluateCombinationRule(group.getRule());
    return userIds == null ? Collections.<String>emptyList() : userIds;
  }

  /**
   * Gets the expression of the rule.<br/>
   * If simple value, and if no escaped character detected, parentheses are escaped.
   * @return the expression.
   */
  private static String getRuleExpression(
      PrefixedNotationExpressionEngine<List<String>> combinationEngine,
      final String combinationRule) {
    String rule = combinationRule != null ? combinationRule : "";
    Matcher matcher = EXPRESSION_PATTERN.matcher(rule);
    if (!matcher.matches() && !rule.contains("\\") && !combinationEngine.detectOperator(rule)) {
      rule = rule.replaceAll("[(]", "\\\\(").replaceAll("[)]", "\\\\)");
    }
    return rule;
  }

  /**
   * Evaluates a combination of simple silverpeas rules.
   * @param combinationRule the combination rule to evaluate.
   * @return a list of user identifiers.
   * @throws Error
   */
  private List<String> evaluateCombinationRule(String combinationRule) throws Error {
    OperatorFunction<List<String>> NEGATE_FUNCTION = new OperatorFunction<List<String>>("!",
        new BiFunction<List<String>, List<String>, List<String>>() {
          @Override
          public List<String> apply(List<String> computed, final List<String> userIds) {
            try {
              List<String> safeComputed =
                  computed == null ? getCacheOfAllUserIds() : new ArrayList<String>();
              safeComputed.removeAll(userIds);
              return safeComputed;
            } catch (AdminException e) {
              throw new RuntimeException(e);
            }
          }
        });

    OperatorFunction<List<String>> AND_FUNCTION = new OperatorFunction<List<String>>("&",
        new BiFunction<List<String>, List<String>, List<String>>() {
          @Override
          public List<String> apply(final List<String> computed, final List<String> userIds) {
            List<String> safeComputed = computed == null ? userIds : computed;
            return intersection(safeComputed, userIds);
          }
        });

    OperatorFunction<List<String>> OR_FUNCTION = new OperatorFunction<List<String>>("|",
        new BiFunction<List<String>, List<String>, List<String>>() {
          @Override
          public List<String> apply(final List<String> computed, final List<String> userIds) {
            List<String> safeComputed =
                computed == null ? Collections.<String>emptyList() : computed;
            return union(safeComputed, userIds);
          }
        });

    Function<String, List<String>> simpleSilverpeasRuleToUserIds =
        new Function<String, List<String>>() {
          @Override
          public List<String> apply(final String simpleSilverpeasRule) {
            try {
              return evaluateSimpleSilverpeasRule(simpleSilverpeasRule);
            } catch (AdminException e) {
              throw new RuntimeException(e);
            }
          }
        };

    @SuppressWarnings("unchecked") PrefixedNotationExpressionEngine<List<String>>
        combinationEngine = PrefixedNotationExpressionEngine
        .from(simpleSilverpeasRuleToUserIds, NEGATE_FUNCTION, AND_FUNCTION, OR_FUNCTION);

    try {
      String expression = getRuleExpression(combinationEngine, combinationRule);
      return combinationEngine.evaluate(expression);
    } catch (Exception e) {
      if (e instanceof Error) {
        throw (Error) e;
      }
      throw new Error(e);
    }
  }

  /**
   * Evaluates a simple Silverpeas rule.
   * @param simpleSilverpeasRule the simple Silverpeas rule to evaluate.
   * @return a list of user identifiers.
   * @throws AdminException
   */
  private List<String> evaluateSimpleSilverpeasRule(final String simpleSilverpeasRule)
      throws AdminException {
    if (simpleSilverpeasRule == null) {
      return null;
    }

    Matcher matcher = ACCESSLEVEL_STANDARD_DATA_PATTERN.matcher(simpleSilverpeasRule);
    if (matcher.find()) {
      return getUserIdsByAccessLevel(matcher.group(1));
    }

    matcher = DOMAIN_STANDARD_DATA_PATTERN.matcher(simpleSilverpeasRule);
    if (matcher.find()) {
      // Split parameters as a list using comma separator and trimming spaces
      List<String> domainIds = asList(matcher.group(1).replaceAll("\\s", "").split(","));
      List<String> userIds = new ArrayList<String>();
      for (String domainId : domainIds) {
        userIds.addAll(getUserIdsByDomain(domainId));
      }
      return userIds;
    }

    matcher = GROUP_RULE_DATA_PATTERN.matcher(simpleSilverpeasRule);
    if (matcher.find()) {
      boolean withSubGroups = StringUtil.isDefined(matcher.group(1));
      // Split parameters as a list using comma separator and trimming spaces
      String groupValues = matcher.group(2).replaceAll("\\s", "");
      List<String> groupIds = asList(groupValues.split(","));
      return getUserIdsByGroups(groupIds, withSubGroups);
    }

    matcher = COMPLEMENTARY_DATA_PATTERN.matcher(simpleSilverpeasRule);
    if (matcher.find()) {
      String propertyName = matcher.group(1);
      String propertyValue = matcher.group(2);
      return getUserIdsBySpecificProperty(propertyName, propertyValue);
    }

    SilverTrace.error("admin", "Admin.synchronizeGroup", "admin.MSG_ERR_SYNCHRONIZE_GROUP",
        "ground rule '" + simpleSilverpeasRule + "' for groupId '" + group.getId() +
            "' is not correct !");

    throw new GroundRuleError(simpleSilverpeasRule);
  }

  /**
   * Gets the users of the given groups represented by their identifiers.
   * @param groupIds the group identifiers.
   * @param withSubGroups true in order to get the users of the sub groups of the given ones, false
   * otherwise.
   * @return
   * @throws AdminException
   */
  private List<String> getUserIdsByGroups(List<String> groupIds, boolean withSubGroups)
      throws AdminException {
    // Add each group passed as a parameter
    Set<String> allGroupIds = new HashSet<String>();
    for (String currentGroupId : new HashSet<String>(groupIds)) {
      allGroupIds.add(currentGroupId);
      // Add sub groups recursively if recursive option is selected
      if (withSubGroups) {
        allGroupIds.addAll(getGroupManager().getAllSubGroupIdsRecursively(currentGroupId));
      }
    }
    // Add all users belonging to any group in the list
    return getUserManager().getAllUserIdsOfGroups(new ArrayList<String>(allGroupIds));
  }

  /**
   * Gets the users of the domain represented by the given identifier.<br/>
   * This method returns user identifiers only into the context of shared domain search.
   * @param domainId the identifier of the aimed domain.
   * @return a list of user identifiers.
   * @throws AdminPersistenceException
   */
  private List<String> getUserIdsByDomain(String domainId) throws AdminPersistenceException {
    DomainDriverManager domainDriverManager =
        DomainDriverManagerFactory.getCurrentDomainDriverManager();
    List<String> userIds = Collections.emptyList();
    if (isSharedDomain) {
      userIds = asList(domainDriverManager.getOrganization().user
          .getUserIdsOfDomain(Integer.parseInt(domainId)));
    }
    return userIds;
  }

  /**
   * Gets the users which have their access level equals to the specified one.
   * @param accessLevel the access level the users must have.
   * @return a list of user identifiers.
   * @throws AdminException
   */
  private List<String> getUserIdsByAccessLevel(String accessLevel) throws AdminException {
    List<String> userIds;
    DomainDriverManager domainDriverManager = DomainDriverManagerFactory.
        getCurrentDomainDriverManager();
    if ("*".equalsIgnoreCase(accessLevel)) {
      // In case of "Shared domain" then retrieving all users of all domains
      // Otherwise getting only users of group's domain
      if (isSharedDomain) {
        userIds = asList(getUserManager().getAllUsersIds(domainDriverManager));
      } else {
        userIds =
            asList(getUserManager().getUserIdsOfDomain(domainDriverManager, group.getDomainId()));
      }
    } else {
      // All users by access level
      if (isSharedDomain) {
        userIds = asList(domainDriverManager.getOrganization().user
            .getUserIdsByAccessLevel(UserAccessLevel.fromCode(accessLevel)));
      } else {
        userIds = asList(getUserManager()
            .getUserIdsOfDomainAndAccessLevel(domainDriverManager, group.getDomainId(),
                UserAccessLevel.fromCode(accessLevel)));
      }
    }
    return userIds;
  }

  /**
   * Gets the users which the value of the extra property name matches the given ones.
   * @param propertyName the name of the aimed extra property.
   * @param propertyValue the value the property must verify.
   * @return a list of user identifiers.
   * @throws AdminException
   */
  private List<String> getUserIdsBySpecificProperty(String propertyName, String propertyValue)
      throws AdminException {
    List<String> userIds = new ArrayList<String>();
    if (isSharedDomain) {
      // All users by extra information
      Domain[] domains = getAdminService().getAllDomains();
      for (Domain domain : domains) {
        userIds.addAll(getUserIdsBySpecificProperty(domain.getId(), propertyName, propertyValue));
      }
    } else {
      userIds = getUserIdsBySpecificProperty(group.getDomainId(), propertyName, propertyValue);
    }
    return userIds;
  }

  /**
   * Gets the users which the value of the extra property name matches the given ones.
   * @param domainId the identifier of the aimed domain.
   * @param propertyName the name of the aimed extra property.
   * @param propertyValue the value the property must verify.
   * @return a list of user identifiers.
   * @throws AdminException
   */
  private List<String> getUserIdsBySpecificProperty(String domainId, String propertyName,
      String propertyValue) throws AdminException {
    final int domainIdAsInteger = Integer.parseInt(domainId);
    UserDetail[] users = ArrayUtil.EMPTY_USER_DETAIL_ARRAY;
    DomainDriverManager domainDriverManager =
        DomainDriverManagerFactory.getCurrentDomainDriverManager();
    DomainDriver domainDriver = null;
    try {
      domainDriver = domainDriverManager.getDomainDriver(domainIdAsInteger);
    } catch (Exception e) {
      reportInfo("admin.getUserIdsBySpecificProperty",
          "Erreur ! Domaine " + domainId + " inaccessible !");
    }

    if (domainDriver != null) {
      try {
        users = domainDriver.getUsersBySpecificProperty(propertyName, propertyValue);
        if (users == null) {
          reportInfo("admin.getUserIdsBySpecificProperty",
              "La propriété '" + propertyName + "' n'est pas définie dans le domaine " +
                  domainId);
        }
      } catch (Exception e) {
        if (e instanceof AdminException) {
          Exception cause = ((AdminException) e).getNested();
          if (cause instanceof LDAPLocalException ||
              cause instanceof org.ietf.ldap.LDAPLocalException) {
            reportInfo("admin.getUserIdsBySpecificProperty",
                "Domain " + domainId + ": " + cause.toString());
          } else {
            throw (AdminException) e;
          }
        } else {
          throw new AdminException("GroupSynchronizationRule.getUserIdsBySpecificProperty",
              SilverpeasException.ERROR, "admin.MSG_ERR_GET_ALL_USERS", e);
        }
      }
    }

    List<String> specificIds = new ArrayList<String>();
    if (users != null) {
      for (UserDetail user : users) {
        specificIds.add(user.getSpecificId());
      }
    }

    // We have to find users according to theirs specificIds
    UserRow[] usersInDomain = domainDriverManager.getOrganization().user
        .getUsersBySpecificIds(domainIdAsInteger, specificIds);
    List<String> userIds = new ArrayList<String>();
    if (usersInDomain != null) {
      for (UserRow userInDomain : usersInDomain) {
        userIds.add(Integer.toString(userInDomain.id));
      }
    }

    return userIds;
  }

  /**
   * Gets all user identifiers of the Silverpeas platform.<br/>
   * The ids are cached.
   * @return a list of user identifiers.
   * @throws AdminException
   */
  private List<String> getCacheOfAllUserIds() throws AdminException {
    if (cacheOfAllUserIds == null) {
      // In case of "Shared domain", retrieving all users of all domains
      // Otherwise retrieving only users of group's domain
      DomainDriverManager domainDriverManager =
          DomainDriverManagerFactory.getCurrentDomainDriverManager();
      if (isSharedDomain) {
        cacheOfAllUserIds = asList(getUserManager().getAllUsersIds(domainDriverManager));
      } else {
        cacheOfAllUserIds =
            asList(getUserManager().getUserIdsOfDomain(domainDriverManager, group.getDomainId()));
      }
    }
    return new ArrayList<String>(cacheOfAllUserIds);
  }

  private void reportInfo(String clazz, String message) {
    SynchroGroupReport.info(clazz, message, null);
  }

  private UserManager getUserManager() {
    return UserManager.get();
  }

  private GroupManager getGroupManager() {
    return GroupManager.get();
  }

  /**
   * An error.
   */
  public static class Error extends RuntimeException {
    private static final long serialVersionUID = -3732193660967552614L;

    public Error(final Throwable cause) {
      super(cause);
    }

    /**
     * Gets a message handled by the synchronization rule process.<br/>
     * If not handled, like a {@link NullPointerException}, empty message is returned.
     * @return handled message, empty otherwise.
     */
    public String getHandledMessage() {
      String message = "";
      Throwable exceptionSource = getCause();
      Throwable previousExceptionSource = null;
      while (exceptionSource != null && previousExceptionSource != exceptionSource) {
        previousExceptionSource = exceptionSource;
        if (StringUtil.isDefined(exceptionSource.getMessage()) &&
            exceptionSource.getMessage().startsWith("expression.")) {
          message = exceptionSource.getMessage();
          break;
        } else if (exceptionSource instanceof LDAPException ||
            exceptionSource instanceof org.ietf.ldap.LDAPLocalException) {
          message = exceptionSource.toString();
          break;
        }
        if (exceptionSource instanceof WithNested) {
          exceptionSource = ((WithNested) exceptionSource).getNested();
        } else {
          exceptionSource = exceptionSource.getCause();
        }
      }
      return message;
    }
  }

  /**
   * An error.
   */
  public static class GroundRuleError extends Error {
    private static final long serialVersionUID = 4003102352897715610L;

    private final String baseRulePart;

    public GroundRuleError(String baseRulePart) {
      super(new IllegalArgumentException("expression.groundrule.unknown"));
      this.baseRulePart = baseRulePart;
    }

    public String getBaseRulePart() {
      return baseRulePart;
    }
  }
}
