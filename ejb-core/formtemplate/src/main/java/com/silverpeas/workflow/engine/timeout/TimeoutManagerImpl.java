package com.silverpeas.workflow.engine.timeout;

import java.util.Vector;
import java.util.Date;

import com.stratelia.silverpeas.silvertrace.SilverTrace;

import com.stratelia.webactiv.util.*;
import com.stratelia.silverpeas.scheduler.*;

import com.silverpeas.workflow.api.*;
import com.silverpeas.workflow.api.event.TimeoutEvent;
import com.silverpeas.workflow.api.model.*;
import com.silverpeas.workflow.api.instance.ProcessInstance;
import com.silverpeas.workflow.api.instance.HistoryStep;
import com.silverpeas.workflow.engine.event.TimeoutEventImpl;
import com.silverpeas.workflow.engine.WorkflowEngineThread;

/**
 * The workflow engine services relate to error management.
 */
public class TimeoutManagerImpl implements TimeoutManager,
    SchedulerEventHandler {

  // Local constants
  private static final String TIMEOUT_MANAGER_JOB_NAME = "WorkflowTimeoutManager";

  /**
   * Initialize timeout manager
   * 
   */
  public void initialize() {
    try {
      ResourceLocator settings = new ResourceLocator(
          "com.silverpeas.workflow.engine.schedulerSettings", "");
      Vector jobList = SimpleScheduler.getJobList(this);

      if (jobList.size() != 0) {
        // Remove previous scheduled job
        SimpleScheduler.removeJob(this, TIMEOUT_MANAGER_JOB_NAME);
      }

      // Create new scheduled job
      String cronString = settings.getString("timeoutSchedule");
      SimpleScheduler.getJob(this, TIMEOUT_MANAGER_JOB_NAME, cronString, this,
          "doTimeoutManagement");
    } catch (Exception e) {
      SilverTrace.error("workflowEngine", "TimeoutManagerImpl.initialize",
          "workflowEngine.EX_ERR_INITIALIZE", e);
    }

  }

  /**
   * Scheduler Event handler
   * 
   */
  public void handleSchedulerEvent(SchedulerEvent aEvent) {
    switch (aEvent.getType()) {
      case SchedulerEvent.EXECUTION_NOT_SUCCESSFULL:
        SilverTrace.error("workflowEngine",
            "TimeoutManagerImpl.handleSchedulerEvent", "The job '"
                + aEvent.getJob().getJobName() + "' was not successfull");
        break;

      case SchedulerEvent.EXECUTION_SUCCESSFULL:
        SilverTrace.debug("workflowEngine",
            "TimeoutManagerImpl.handleSchedulerEvent", "The job '"
                + aEvent.getJob().getJobName() + "' was successfull");
        break;

      default:
        SilverTrace.error("workflowEngine",
            "TimeoutManagerImpl.handleSchedulerEvent", "Illegal event type");
        break;
    }
  }

  /**
   * This method is called periodically by the scheduler, it test for each peas
   * of type processManager if associated model contains states with timeout
   * events If so, all the instances of these peas that have the "timeout"
   * states actives are read to check if timeout interval has been reached. In
   * that case, the administrator can be notified, the active state and the
   * instance are marked as timeout
   * 
   * @param currentDate
   *          the date when the method is called by the scheduler
   * 
   * @see SimpleScheduler for parameters,
   */
  public void doTimeoutManagement(Date date) {
    Date beginDate = new Date();

    try {
      // parse all "process manager" peas
      String[] peasIds = Workflow.getProcessModelManager().getAllPeasIds();
      SilverTrace.debug("workflowEngine",
          "TimeoutManagerImpl.doTimeoutManagement", "", "peas Id found : '"
              + peasIds.length);
      for (int i = 0; i < peasIds.length; i++) {
        // load abstract model
        ProcessModel model = null;
        try {
          model = Workflow.getProcessModelManager().getProcessModel(peasIds[i]);
        } catch (WorkflowException we) {
          continue;
        }

        // parse all states
        State[] states = model.getStates();
        SilverTrace.debug("workflowEngine",
            "TimeoutManagerImpl.doTimeoutManagement", "", states.length
                + " states found in model" + model.getName());
        for (int j = 0; j < states.length; j++) {
          // Check if the state has a defined timeout action
          Action timeoutAction = states[j].getTimeoutAction();
          int timeoutInterval = states[j].getTimeoutInterval();
          states[j].getTimeoutNotifyAdmin();

          try {
            if (timeoutAction != null && timeoutInterval != -1) {
              // parse all instances with this state activated
              ProcessInstance[] instances = Workflow
                  .getProcessInstanceManager().getProcessInstancesInState(
                      peasIds[i], states[j]);
              SilverTrace.debug("workflowEngine",
                  "TimeoutManagerImpl.doTimeoutManagement", "",
                  "instances found : '" + instances.length);
              for (int k = 0; k < instances.length; k++) {
                HistoryStep step = instances[k].getMostRecentStep(states[j]);
                if (step != null) {
                  Date today = new Date();
                  Date actionDate = step.getActionDate();

                  long interval = today.getTime() - actionDate.getTime();
                  long timeout = timeoutInterval * 60 * 60 * 1000;

                  if (interval > timeout) {
                    TimeoutEvent event = new TimeoutEventImpl(instances[k],
                        states[j], timeoutAction);
                    WorkflowEngineThread.addTimeoutRequest(event);
                    SilverTrace.warn("workflowEngine",
                        "TimeoutManagerImpl.doTimeoutManagement",
                        "workflowEngine.WARN_TIMEOUT_DETECTED", "model Id : '"
                            + peasIds[i] + "' instance Id : '"
                            + instances[k].getInstanceId() + "' state : '"
                            + states[j].getName() + "interval : "
                            + (interval / (60 * 60 * 1000)));
                  }
                }
              }
            }
          } catch (WorkflowException we) {
            continue;
          }
        }
      }
    } catch (Exception e) {
      SilverTrace.error("workflowEngine",
          "TimeoutManagerImpl.doTimeoutManagement",
          "workflowEngine.EX_ERR_TIMEOUT_MANAGEMENT", e);
    } finally {
      Date endDate = new Date();
      long delay = (endDate.getTime() - beginDate.getTime()) / 1000;
      SilverTrace.debug("workflowEngine",
          "TimeoutManagerImpl.doTimeoutManagement", "Dur�e de traitement : "
              + delay + " seconds.");
    }
  }
}