/**
 * Copyright (c) 2017-2019 Linagora
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
package org.ow2.petals.activitibpmn.incoming.operation;

import java.util.Map;
import java.util.logging.Logger;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.ow2.petals.activitibpmn.incoming.operation.annotated.MessageStartEventAnnotatedOperation;
import org.ow2.petals.activitibpmn.incoming.operation.exception.OperationProcessingException;

import com.ebmwebsourcing.easycommons.uuid.SimpleUUIDGenerator;

/**
 * The operation to create a new instance of a process
 * 
 * @author Christophe DENEUX - Linagora
 * 
 */
public class MessageStartEventOperation extends StartEventOperation {

    /**
     * The start event message name on which the action must be realized on the BPMN process side
     */
    protected final String startEventMessageName;

    /**
     * The tenant identifier in which the process definition is deployed
     */
    protected final String tenantId;

    /**
     * @param annotatedOperation
     *            Annotations of the operation to create
     * @param identityService
     *            The identity service of the BPMN engine
     * @param runtimeService
     *            The runtime service of the BPMN engine
     * @param historyService
     *            The history service of the BPMN engine
     * @param simpleUUIDGenerator
     *            A UUID generator
     * @param logger
     */
    public MessageStartEventOperation(final MessageStartEventAnnotatedOperation annotatedOperation,
            final IdentityService identityService, final RuntimeService runtimeService,
            final HistoryService historyService, final SimpleUUIDGenerator simpleUUIDGenerator, final Logger logger) {
        super(annotatedOperation, identityService, runtimeService, historyService, simpleUUIDGenerator, logger);
        this.startEventMessageName = annotatedOperation.getStartEventMessageName();
        this.tenantId = annotatedOperation.getTenantId();
    }

    @Override
    protected ProcessInstance createProcessInstance(final Map<String, Object> processVars)
            throws OperationProcessingException {

        return this.runtimeService.startProcessInstanceByMessageAndTenantId(this.startEventMessageName, processVars,
                this.tenantId);
    }

    @Override
    protected void logActivitiOperation() {
        super.logActivitiOperation();
        this.logger.fine("Activiti Start Event Message Name = " + this.startEventMessageName);
    }

}
