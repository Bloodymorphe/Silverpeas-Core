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
package com.silverpeas.admin.notification;

public interface AdminNotificationService {

  /**
   * Notifies the registered beans a given space comes to be deleted.
   *
   * @param spaceId the id of the space to be deleted.
   * @param userId the id of the user deleting the space.
   */
  public abstract void notifyOnDeletionOf(final String spaceId, final String userId);

  /**
   * Notifies the registered beans fof configuration updates for components.
   *
   * @param componentId the id of the component whose configuration has changed.
   * @param userId the id of the user updating the configuration.
   * @param type type of component.
   * @param changes the configuration changes.
   */
  public abstract void notifyOfComponentConfigurationChange(final String componentId,
      final String userId, final ComponentJsonPatch changes);

}
