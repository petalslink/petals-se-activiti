/**
 * Copyright (c) 2015-2016 Linagora
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
package org.ow2.petals.activitibpmn.event;

import java.util.Map;
import java.util.logging.Logger;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.ow2.petals.activitibpmn.ActivitiSEConstants;
import org.ow2.petals.activitibpmn.monitoring.ProcessInstanceFlowStepEndLogData;
import org.ow2.petals.component.framework.logger.AbstractFlowLogData;

/**
 * The event listener fired when a process instance is completed to log a MONIT trace.
 * 
 * @author Christophe DENEUX - Linagora
 *
 */
public class ProcessInstanceCompletedEventListener extends AbstractMonitDirectLoggerEventListener implements
        ActivitiEventListener {

    public ProcessInstanceCompletedEventListener(final Logger log) {
        super(ActivitiEventType.PROCESS_COMPLETED, log);
    }

    @Override
    protected AbstractFlowLogData createLogData(final ActivitiEvent event) {

        final String processInstanceId = event.getProcessInstanceId();
        this.log.fine("The process instance '" + processInstanceId + "' is completed.");

        final HistoricProcessInstanceQuery processQuery = event.getEngineServices().getHistoryService()
                .createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).includeProcessVariables();
        final HistoricProcessInstance processResult = processQuery.singleResult();

        final Map<String, Object> processVariables = processResult.getProcessVariables();

        final String flowInstanceId = (String) processVariables
                .get(ActivitiSEConstants.Activiti.VAR_PETALS_FLOW_INSTANCE_ID);
        final String flowStepId = (String) processVariables
                .get(ActivitiSEConstants.Activiti.VAR_PETALS_FLOW_STEP_ID);

        return new ProcessInstanceFlowStepEndLogData(flowInstanceId, flowStepId);

    }
}
