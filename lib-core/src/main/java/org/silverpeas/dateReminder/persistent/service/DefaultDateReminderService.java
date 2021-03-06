/*
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
 * FLOSS exception.  You should have recieved a copy of the text describing
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
package org.silverpeas.dateReminder.persistent.service;

import com.silverpeas.annotation.Service;
import javax.inject.Inject;
import org.silverpeas.EntityReference;
import org.silverpeas.dateReminder.exception.DateReminderException;
import org.silverpeas.dateReminder.persistent.DateReminderDetail;
import org.silverpeas.dateReminder.persistent.PersistentResourceDateReminder;
import org.silverpeas.dateReminder.persistent.repository.PersistentResourceDateReminderRepository;
import org.silverpeas.persistence.repository.OperationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;

/**
 * The default implementation of the {@link PersistentDateReminderService} interface.
 *
 * @author Cécile Bonin
 */
@Service("persistentDateReminderService")
public class DefaultDateReminderService implements PersistentDateReminderService {

  @Inject
  private PersistentResourceDateReminderRepository dateReminderRepository;


  /**
   * @see PersistentDateReminderService#get(org.silverpeas.EntityReference)
   */
  @Override
  public PersistentResourceDateReminder get(final EntityReference resource) {
    return bind(dateReminderRepository.getByTypeAndResourceId(resource.getType(), resource.getId()));
  }

  /**
   * @throws DateReminderException if an error occurs while creating a date reminder.
   * @see PersistentDateReminderService#create(org.silverpeas.EntityReference, DateReminderDetail)
   */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public PersistentResourceDateReminder create(EntityReference resource,
      DateReminderDetail dateReminderDetail) throws DateReminderException {
    PersistentResourceDateReminder dateReminder = new PersistentResourceDateReminder();
    dateReminder.setResource(resource);
    dateReminder.setDateReminder(dateReminderDetail);

    // Validating
    dateReminder.validate();

    PersistentResourceDateReminder savedDateReminder = dateReminderRepository.save(OperationContext.fromUser(dateReminder.getCreatedBy()), dateReminder);
    return savedDateReminder;
  }

  /**
   * @throws DateReminderException if an error occurs while setting a date reminder.
   * @see PersistentDateReminderService#set(org.silverpeas.EntityReference, DateReminderDetail)
   */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public PersistentResourceDateReminder set(EntityReference resource, DateReminderDetail dateReminderDetail)
      throws DateReminderException {
    PersistentResourceDateReminder dateReminder = get(resource);
    dateReminder.setDateReminder(dateReminderDetail);

    // Validating
    dateReminder.validate();

    PersistentResourceDateReminder savedDateReminder = dateReminderRepository.save(OperationContext.fromUser(dateReminder.getLastUpdatedBy()),
        dateReminder);
    return savedDateReminder;
  }

  /**
   * @see PersistentDateReminderService#remove(org.silverpeas.EntityReference)
   */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void remove(final EntityReference resource) {
    final PersistentResourceDateReminder dateReminder = get(resource);
    if (dateReminder.exists()) {
      dateReminderRepository.delete(dateReminder);
    }
  }


  /**
   * Bind the specified dateReminder into a well-typed dateReminder. It actually converts any null dateReminder to a
   * NoneDateReminder that is an instance of a PersistentResourceDateReminder class, otherwise the dateReminder is simply
   * returned.
   *
   * @param dateReminder the date reminder to bind to a non null <code>PersistentResourceDateReminder</code> instance.
   * @return a non null instance of PersistentResourceDateReminder class
   */
  private PersistentResourceDateReminder bind(final PersistentResourceDateReminder dateReminder) {
    if (dateReminder == null) {
      return PersistentResourceDateReminder.NONEDATEREMINDER;
    }
    return dateReminder;
  }

  /**
   * @see PersistentDateReminderService#listAllDateReminderMaturing(Date)
   */
  @Override
  public Collection<PersistentResourceDateReminder> listAllDateReminderMaturing(Date deadLine) {
    Collection<PersistentResourceDateReminder> listResourceDateReminder = dateReminderRepository.getByDeadLine(deadLine);
    return listResourceDateReminder;
  }
}
