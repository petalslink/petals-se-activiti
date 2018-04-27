/**
 * Copyright (c) 2014-2018 Linagora
 * 
 * This program/library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or (at your
 * option) any later version.
 * 
 * This program/library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program/library; If not, see http://www.gnu.org/licenses/
 * for the GNU Lesser General Public License version 2.1.
 */
package org.ow2.petals.activitibpmn;

import static org.ow2.petals.activitibpmn.ActivitiSEConstants.DBServer.DEFAULT_JDBC_URL_DATABASE_FILENAME;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.ow2.petals.activitibpmn.junit.ActivitiClient;
import org.ow2.petals.component.framework.junit.rule.ComponentUnderTest;
import org.ow2.petals.junit.rules.log.handler.InMemoryLogHandler;

/**
 * Abstract class for unit tests about request processing
 * 
 * @author Christophe DENEUX - Linagora
 * 
 */
public abstract class AbstractTestEnvironment extends AbstractTest {

    protected static final String NATIVE_TASKS_SVC_CFG = "native-tasks";

    protected static final String NATIVE_PROCESSINSTANCES_SVC_CFG = "native-process-instances";

    protected static final InMemoryLogHandler IN_MEMORY_LOG_HANDLER = new InMemoryLogHandler();

    protected static final TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

    @Rule
    public ActivitiClient activitiClient = new ActivitiClient(
            new File(new File(this.getComponentUnderTest().getBaseDirectory(), "work"),
                    DEFAULT_JDBC_URL_DATABASE_FILENAME),
            "su/valid/identityService.properties");

    protected abstract ComponentUnderTest getComponentUnderTest();

    /**
     * All log traces must be cleared before starting a unit test
     */
    @Before
    public void clearLogTraces() {
        IN_MEMORY_LOG_HANDLER.clear();
    }

    /**
     * Assertion about the pending state of a process instance. The process instance is not finished.
     * 
     * @param processInstanceId
     *            The process instance identifier
     * @param processDefinitionKey
     *            The process definition key
     */
    protected void assertProcessInstancePending(final String processInstanceId, final String processDefinitionKey) {

        final ProcessInstanceQuery processInstQuery = this.activitiClient.getRuntimeService()
                .createProcessInstanceQuery();
        final ProcessInstance processInstance = processInstQuery.processInstanceId(processInstanceId).singleResult();
        assertNotNull(processInstance);
        assertEquals(processInstanceId, processInstance.getProcessInstanceId());
        assertEquals(processDefinitionKey, processInstance.getProcessDefinitionKey());
        assertFalse(processInstance.isEnded());
        assertFalse(processInstance.isSuspended());
    }

    /**
     * @param processDefinitionKey
     *            The process definition identifier
     * @return The number of process instances that are not finished and associated to the given process definition
     */
    protected int getProcessInstanceNumber(final String processDefinitionKey) {
        final ProcessInstanceQuery processInstQuery = this.activitiClient.getRuntimeService()
                .createProcessInstanceQuery();
        final List<ProcessInstance> processInstances = processInstQuery.processDefinitionKey(processDefinitionKey)
                .list();
        assertNotNull(processInstances);
        return processInstances.size();
    }

    /**
     * Assertion about the creation of a process instance. The process instance is not finished.
     * 
     * @param processInstanceId
     *            The process instance identifier
     */
    protected void assertProcessInstanceFinished(final String processInstanceId) {

        final HistoricProcessInstanceQuery query = this.activitiClient.getHistoryService()
                .createHistoricProcessInstanceQuery();
        final HistoricProcessInstance processInstance = query.processInstanceId(processInstanceId).singleResult();
        assertNotNull(processInstance);
    }

    /**
     * Assertion about the current user task.
     * 
     * @param processInstanceId
     *            The process instance identifier
     * @param taskDefinitionKey
     *            The process definition key
     * @param user
     *            The task can be completed by the given user
     */
    protected void assertCurrentUserTask(final String processInstanceId, final String taskDefinitionKey,
            final String user) {

        final TaskQuery taskQuery = this.activitiClient.getTaskService().createTaskQuery();
        final Task nextTask = taskQuery.processInstanceId(processInstanceId).taskCandidateUser(user).singleResult();
        assertNotNull(nextTask);
        assertEquals(processInstanceId, nextTask.getProcessInstanceId());
        assertEquals(taskDefinitionKey, nextTask.getTaskDefinitionKey());
    }

    /**
     * Assertion about a user task completed.
     * 
     * @param processInstanceId
     *            The process instance identifier
     * @param taskDefinitionKey
     *            The process definition key
     * @param user
     *            The task has been completed by the given user
     */
    protected void assertUserTaskEnded(final String processInstanceId, final String taskDefinitionKey,
            final String user) {

        final HistoricTaskInstanceQuery taskQuery = this.activitiClient.getHistoryService()
                .createHistoricTaskInstanceQuery();
        final HistoricTaskInstance nextTask = taskQuery.processInstanceId(processInstanceId)
                .taskDefinitionKey(taskDefinitionKey).singleResult();
        assertNotNull(nextTask);
        assertEquals(user, nextTask.getAssignee());
    }

    /**
     * Wait that a process instance ends.
     * 
     * @param processInstanceId
     *            The process instance identifier of the process instance to wait its end.
     */
    protected void waitEndOfProcessInstance(final String processInstanceId) throws InterruptedException {
        final CountDownLatch lock = new CountDownLatch(1);
        final Thread waitingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean run = true;
                try {
                    while (run) {
                        Thread.sleep(250);
                        final HistoricProcessInstanceQuery histProcInstQuery = AbstractTestEnvironment.this.activitiClient
                                .getHistoryService().createHistoricProcessInstanceQuery()
                                .processInstanceId(processInstanceId);
                        if (histProcInstQuery.singleResult() != null) {
                            // the process instance is finished
                            run = false;
                            lock.countDown();
                        }
                    }
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        waitingThread.start();
        lock.await(60, TimeUnit.SECONDS);
    }

    /**
     * Wait that a service task of a process instance ends.
     * 
     * @param processInstanceId
     *            The process instance identifier of the service task to wait its end.
     * @param taskDefinitionKey
     *            The service task identifier in the process definition
     */
    protected void waitEndOfServiceTask(final String processInstanceId, final String taskDefinitionKey)
            throws InterruptedException {
        final CountDownLatch lock = new CountDownLatch(1);
        final Thread waitingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean run = true;
                try {
                    while (run) {
                        Thread.sleep(250);
                        // Caution: service tasks are stored in activity historic, not in task historic.
                        final HistoricActivityInstanceQuery histSvcTaskQuery = AbstractTestEnvironment.this.activitiClient
                                .getHistoryService().createHistoricActivityInstanceQuery()
                                .processInstanceId(processInstanceId).activityId(taskDefinitionKey).finished();
                        if (histSvcTaskQuery.singleResult() != null) {
                            // the process instance is finished
                            run = false;
                            lock.countDown();
                        }
                    }
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        waitingThread.start();
        lock.await(60, TimeUnit.SECONDS);
    }

}
