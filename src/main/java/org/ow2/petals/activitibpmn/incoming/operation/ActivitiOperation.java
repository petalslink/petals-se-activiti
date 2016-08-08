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
package org.ow2.petals.activitibpmn.incoming.operation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.jbi.messaging.Fault;
import javax.jbi.messaging.MessagingException;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.FormValue;
import org.ow2.petals.activitibpmn.incoming.ActivitiService;
import org.ow2.petals.activitibpmn.incoming.operation.annotated.AnnotatedOperation;
import org.ow2.petals.activitibpmn.incoming.operation.annotated.XPathExpressionBean;
import org.ow2.petals.activitibpmn.incoming.operation.exception.AttachmentMTOMException;
import org.ow2.petals.activitibpmn.incoming.operation.exception.AttachmentMTOMHrefException;
import org.ow2.petals.activitibpmn.incoming.operation.exception.AttachmentNotFoundException;
import org.ow2.petals.activitibpmn.incoming.operation.exception.NoAttachmentIdValueException;
import org.ow2.petals.activitibpmn.incoming.operation.exception.NoUserIdValueException;
import org.ow2.petals.activitibpmn.incoming.operation.exception.OperationProcessingException;
import org.ow2.petals.activitibpmn.incoming.operation.exception.OperationProcessingFault;
import org.ow2.petals.activitibpmn.utils.XslUtils;
import org.ow2.petals.component.framework.api.message.Exchange;
import org.ow2.petals.component.framework.util.MtomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ebmwebsourcing.easycommons.xml.XMLHelper;
import com.ebmwebsourcing.easycommons.xml.XMLPrettyPrinter;

public abstract class ActivitiOperation implements ActivitiService {

    /**
     * Namespace of special parameters for the output XSLT style-sheet
     */
    protected static final String SCHEMA_OUTPUT_XSLT_SPECIAL_PARAMS = "http://petals.ow2.org/se/bpmn/output-params/1.0/special";

    /**
     * Local part of the special parameter name about the process instance identifier for the output XSLT style-sheet
     */
    protected static final String SCHEMA_OUTPUT_XSLT_PARAM_PROCESS_INSTANCE_ID = "processInstanceId";

    /**
     * Local part of the special parameter name about the user identifier for the output XSLT style-sheet
     */
    protected static final String SCHEMA_OUTPUT_XSLT_PARAM_USER_ID = "userId";

    /**
     * Namespace of process instance parameters for the output XSLT style-sheet
     */
    protected static final String SCHEMA_OUTPUT_XSLT_PROCESS_INSTANCE_PARAMS = "http://petals.ow2.org/se/bpmn/output-params/1.0/process-instance";

    /**
     * Namespace of task parameters for the output XSLT style-sheet
     */
    protected static final String SCHEMA_OUTPUT_XSLT_TASK_PARAMS = "http://petals.ow2.org/se/bpmn/output-params/1.0/task";

    /**
     * Namespace of fault parameters for the fault XSLT style-sheet
     */
    public static final String SCHEMA_OUTPUT_XSLT_FAULT_PARAMS = "http://petals.ow2.org/se/bpmn/faults/1.0";

    /**
     * The WSDL operation name associated to this {@link ActivitiOperation}
     */
    protected final QName wsdlOperation;

    /**
     * The process definition identifier
     */
    protected final String processDefinitionId;

    /**
     * The identifier of the deployed process definition, different from the process definition identifier.
     */
    protected String deployedProcessDefinitionId = null;

    /**
     * The task identifier on which the action must be realize on the BPMN process side
     */
    protected final String actionId;

    /**
     * The compiled XPath expression of the process instance identifier placeholder
     */
    protected final XPathExpression proccesInstanceIdXPathExpr;

    /**
     * The compiled XPath expression of the user identifier placeholder
     */
    protected final XPathExpression userIdXPathExpr;

    /**
     * The definition of variables of the operation
     */
    protected final Map<String, XPathExpression> variables;

    /**
     * Types of variables
     */
    protected final Map<String, FormProperty> variableTypes;

    /**
     * The definition of attachments of the operation
     */
    protected final Map<String, XPathExpressionBean> attachments;

    /**
     * The output XSLT style-sheet compiled
     */
    private final Templates outputTemplate;

    /**
     * The XSLT style-sheet compiled associated to WSDL faults. The key is the class simple name of the exception
     * associated to the fault.
     */
    private final Map<String, Templates> faultTemplates;

    protected final Logger logger;

    /**
     * @param annotatedOperation
     *            Annotations of the operation to create
     * @param logger
     */
    protected ActivitiOperation(final AnnotatedOperation annotatedOperation, final Logger logger) {
        this.wsdlOperation = annotatedOperation.getWsdlOperation();
        this.processDefinitionId = annotatedOperation.getProcessDefinitionId();
        this.actionId = annotatedOperation.getActionId();
        this.proccesInstanceIdXPathExpr = annotatedOperation.getProcessInstanceIdHolder();
        this.userIdXPathExpr = annotatedOperation.getUserIdHolder();
        this.variables = annotatedOperation.getVariables();
        this.variableTypes = annotatedOperation.getVariableTypes();
        this.attachments = annotatedOperation.getAttachments();
        this.outputTemplate = annotatedOperation.getOutputTemplate();
        this.faultTemplates = annotatedOperation.getFaultTemplates();
        this.logger = logger;
    }

    /**
     * @param deployedProcessDefinitionId
     *            The identifier of the deployed process definition, different from the process definition identifier.
     */
    public void setDeployedProcessDefinitionId(final String deployedProcessDefinitionId) {
        this.deployedProcessDefinitionId = deployedProcessDefinitionId;
    }

    /**
     * @return The action to realize on the BPMN process side (ie, the name of the BPMN action)
     */
    public abstract String getAction();

    @Override
    public final void execute(final Exchange exchange) {

        try {
            final Document incomingPayload = exchange.getInMessageContentAsDocument();

            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("*** incomingPayload = " + XMLPrettyPrinter.prettyPrint(incomingPayload));
            }

            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("Activiti processDefId = " + processDefinitionId);
                this.logger.fine("Activiti Action = " + this.getClass().getSimpleName());
                this.logger.fine("Activiti ActionType (TaskId) = " + this.getAction());
            }

            incomingPayload.getDocumentElement().normalize();

            try {
                // Get the userId
                final String userId = this.extractUserId(incomingPayload);

                // Get the bpmn variables
                final Map<String, Object> variableValues = this.extractBpmnVariableValues(incomingPayload);

                // Get the attachments
                final Map<String, DataHandler> extractedAttachments = this.extractBpmnAttachments(incomingPayload,
                        exchange);

                // Extract process flow data
                final Map<QName, String> xslParameters = new HashMap<>();
                this.doExecute(incomingPayload, userId, variableValues, extractedAttachments, xslParameters, exchange);

                try {
                    exchange.setOutMessageContent(XslUtils.createXmlPayload(this.outputTemplate, xslParameters,
                            this.logger));
                } catch (final TransformerException e) {
                    throw new OperationProcessingException(this.wsdlOperation, e);
                }

            } catch (final OperationProcessingFault e) {
                // A fault occurs during the operation processing
                final Templates faultTemplate = this.faultTemplates.get(e.getClass().getSimpleName());
                if (faultTemplate == null) {
                    // No fault mapped --> Processing as an error
                    throw new OperationProcessingException(this.wsdlOperation, e);
                }
                final Fault fault = exchange.createFault();
                try {
                    fault.setContent(XslUtils.createXmlPayload(faultTemplate, e.getXslParameters(), this.logger));
                    exchange.setFault(fault);
                } catch (final TransformerException e1) {
                    throw new OperationProcessingException(this.wsdlOperation, e1);
                }
            }
        } catch (final OperationProcessingException e) {
            this.logger.log(Level.SEVERE, "Exchange " + exchange.getExchangeId() + " encountered a problem.", e);
            // Technical error, it would be set as a Fault by the CDK
            exchange.setError(e);
        } catch (final MessagingException e) {
            this.logger.log(Level.SEVERE, "Exchange " + exchange.getExchangeId() + " encountered a problem.", e);
            // Technical error, it would be set as a Fault by the CDK
            exchange.setError(e);
        }
    }

    /**
     * Extract the user identifier value from the given XML payload according to the right XPath expression
     * 
     * @param incomingPayload
     * @return The user identifier value
     * @throws NoUserIdValueException
     * @throws OperationProcessingException
     */
    private String extractUserId(final Document incomingPayload)
            throws NoUserIdValueException, OperationProcessingException {
        try {
            final String userId = this.userIdXPathExpr.evaluate(incomingPayload);
            if (userId == null || userId.trim().isEmpty()) {
                throw new NoUserIdValueException(this.wsdlOperation);
            }

            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("User identifier value: " + userId);
            }

            return userId;
        } catch (final XPathExpressionException e) {
            throw new OperationProcessingException(this.wsdlOperation, e);
        }
    }

    /**
     * Extract the process instance variable values from the given XML payload according to the right XPath expressions
     * 
     * @param incomingPayload
     * @return Variable values
     * @throws OperationProcessingException
     * @throws MessagingException
     */
    private Map<String, Object> extractBpmnVariableValues(final Document incomingPayload)
            throws OperationProcessingException, MessagingException {
        final Map<String, Object> variableValues = new HashMap<>();
        for (final Entry<String, XPathExpression> variable : this.variables.entrySet()) {
            final String variableName = variable.getKey();
            try {
                final String variableValueAsStr = variable.getValue().evaluate(incomingPayload);
                if (variableValueAsStr == null || variableValueAsStr.trim().isEmpty()) {
                    if (this.variableTypes.get(variableName).isRequired()) {
                        throw new MessagingException("The task: " + this.getClass().getSimpleName() + " of process: "
                                + this.processDefinitionId + " required the variable: " + variableName);
                    } else {
                        if (this.logger.isLoggable(Level.FINE)) {
                            this.logger.fine("variable: " + variableName + "=> no value");
                        }
                    }
                } else {
                    if (this.logger.isLoggable(Level.FINE)) {
                        this.logger.fine("variable: " + variableName + "=> value: " + variableValueAsStr);
                    }

                    // Get the type of the bpmn variable
                    final FormProperty variableProperties = this.variableTypes.get(variableName);
                    final String varType = variableProperties.getType();
                    // Put the value in Map of activiti variable in the right format
                    if (varType.equals("string")) {
                        variableValues.put(variableName, variableValueAsStr);
                    } else if (varType.equals("long")) {
                        try {
                            variableValues.put(variableName, Long.valueOf(variableValueAsStr));
                        } catch (final NumberFormatException e) {
                            throw new MessagingException("The value of the variable '" + variableName
                                    + "' must be a long ! Current value is: " + variableValueAsStr);
                        }
                    } else if (varType.equals("enum")) {
                        boolean validValue = false;
                        for (final FormValue enumValue : variableProperties.getFormValues()) {
                            if (variableValueAsStr.equals(enumValue.getId())) {
                                validValue = true;
                            }
                        }
                        if (!validValue) {
                            throw new MessagingException("The value of the variable '" + variableName
                                    + " does not belong to the enum of Activiti variable ! Current value is: "
                                    + variableValueAsStr);
                        } else {
                            variableValues.put(variableName, variableValueAsStr);
                        }
                    } else if (varType.equals("date")) {
                        try {
                            variableValues.put(variableName,
                                    DatatypeConverter.parseDateTime(variableValueAsStr).getTime());
                        } catch (final IllegalArgumentException e) {
                            throw new MessagingException("The value of the variable '" + variableName
                                    + "' must be a valid date ! Current value is: " + variableValueAsStr);
                        }
                    } else if (varType.equals("boolean")) {
                        if (variableValueAsStr.equalsIgnoreCase("true")
                                || variableValueAsStr.equalsIgnoreCase("false")) {
                            variableValues.put(variableName, (Boolean) Boolean.valueOf(variableValueAsStr));
                        } else {
                            throw new MessagingException("The value of the variable '" + variableName
                                    + "' must be a boolean value \"true\" or \"false\" ! Current value is: "
                                    + variableValueAsStr);
                        }
                    }
                }
            } catch (final XPathExpressionException e) {
                throw new OperationProcessingException(this.wsdlOperation, e);
            }
        }

        return variableValues;
    }

    /**
     * Extract the attachments from the given XML payload according to the right XPath expressions
     * 
     * @param incomingPayload
     *            Incoming payload in which attachment references are available according to MTOM format.
     * @param exchange
     *            Incoming Petals message exchange in which attachment contents are available.
     * @return Attachments
     * @throws AttachmentNotFoundException
     *             An attachment content is not found in the incoming Petals message exchange.
     * @throws NoAttachmentIdValueException
     *             An attachment reference value is required.
     * @throws AttachmentMTOMException
     *             The attachment reference is not in the MTOM format.
     *             @throws AttachmentMTOMHrefException The attachment reference is in the MTOM format but attribute 'href' is missing.
     * @throws MessagingException
     *             An error occurs retrieving an attachment content from the incoming Petals message exchange.
     * @throws OperationProcessingException
     *             Another error occurs
     */
    private Map<String, DataHandler> extractBpmnAttachments(final Document incomingPayload, final Exchange exchange)
            throws AttachmentNotFoundException, NoAttachmentIdValueException, AttachmentMTOMException,
            AttachmentMTOMHrefException, OperationProcessingException, MessagingException {
        final Map<String, DataHandler> extractedAttachments = new HashMap<>();
        for (final Entry<String, XPathExpressionBean> attachment : this.attachments.entrySet()) {
            final String attachmentName = attachment.getKey();
            try {
                final Object attachmentXpathValueObject = attachment.getValue().getCompiledXpathExpr().evaluate(incomingPayload, XPathConstants.NODE);
                if (attachmentXpathValueObject instanceof Node) {
                    final Node attachmentNode = (Node)attachmentXpathValueObject;

                    // Only attachment optimized with MTOM is accepted
                    // So we retrieve the attachment name from XML tag <xop:Include href="cid:..." />
                    final Node xopIncludeNode = XMLHelper.findChild(attachmentNode, MtomUtil.MTOM_NSURI, MtomUtil.MTOM_INCLUDE_TAG, false);
                    if (xopIncludeNode != null) {
                        final NamedNodeMap xopIncludeAttributes = xopIncludeNode.getAttributes();
                        if (xopIncludeAttributes != null) {
                            // TODO: Create a constant for "href" in MtomUtil
                            final Node hrefNode = xopIncludeAttributes.getNamedItem("href");
                            if (hrefNode != null) {
                                final String attachmentId = hrefNode.getTextContent().replaceFirst("cid:", "");
                                if (!attachmentId.isEmpty()) {
                                    final DataHandler incomingAttachment = exchange.getInMessageAttachment(attachmentId);
                                    if (incomingAttachment != null) {
                                        if (this.logger.isLoggable(Level.FINE)) {
                                            this.logger.fine("attachment '" + attachmentId + "' found in exchange");
                                        }
                                        extractedAttachments.put(attachmentName, incomingAttachment);
                                    } else {
                                        if (this.logger.isLoggable(Level.FINE)) {
                                            this.logger.fine("attachment '" + attachmentId + "' not found in exchange");
                                        }
                                        throw new AttachmentNotFoundException(this.wsdlOperation, attachmentId);
                                    }
                                } else {
                                    throw new NoAttachmentIdValueException(this.wsdlOperation);
                                }
                            } else {
                                throw new AttachmentMTOMHrefException(this.wsdlOperation, attachment.getValue().getXpathExpr());
                            }
                        } else {
                            // TODO: Add a unit test for this use-case
                            throw new AttachmentMTOMException(this.wsdlOperation, attachment.getValue().getXpathExpr());
                        }                        
                    } else {
                        throw new AttachmentMTOMException(this.wsdlOperation, attachment.getValue().getXpathExpr());
                    }
                } else {
                    // TODO: Add a unit test for this use-case
                    throw new NoAttachmentIdValueException(this.wsdlOperation);
                }
            } catch (final XPathExpressionException e) {
                throw new OperationProcessingException(this.wsdlOperation, e);
            }
        }
        return extractedAttachments;
    }

    /**
     * 
     * @param domSource
     *            The incoming XML payload
     * @param userId
     *            The user identifier
     * @param operationVars
     *            Operation variables
     * @param attachments
     *            Process attachments
     * @param outputNamedValues
     *            The output named values to generate response
     * @param exchange
     *            The exchange
     * @throws OperationProcessingException
     *             An error occurs when processing the operation
     */
    protected abstract void doExecute(final Document incomingPayload, final String userId,
            final Map<String, Object> operationVars, final Map<String, DataHandler> attachments,
            final Map<QName, String> outputNamedValues, final Exchange exchange)
            throws OperationProcessingException;

    @Override
    public void log(final Logger logger, final Level logLevel) {
        if (logger.isLoggable(logLevel)) {
            logger.log(logLevel, "operation '" + this.getClass().getSimpleName() + "':");
            logger.log(logLevel, "  - processDefinitionId = " + this.processDefinitionId);
            logger.log(logLevel, "  - processInstanceId => compiled XPath expression");
            logger.log(logLevel, "  - action = " + this.getClass().getSimpleName());
            for (final String variableName : this.variables.keySet()) {
                logger.log(logLevel, "  - variable => name: " + variableName + " => compiled XPath expression");
            }
            logger.log(logLevel, "  - Activiti variable types");
            for (final Entry<String, FormProperty> entry : this.variableTypes.entrySet()) {
                final String key = entry.getKey();
                final FormProperty value = entry.getValue();
                logger.log(logLevel, "      - bpmn variable : " + key + " - Name = " + value.getName() + " - Type = "
                        + value.getType());
                if (value.getType().equals("enum")) {
                    for (final FormValue enumValue : value.getFormValues()) {
                        logger.log(logLevel, "        |------  enum value Id = " + enumValue.getId() + " - Value = "
                                + enumValue.getName());
                    }
                } else if (value.getType().equals("date")) {
                    logger.log(logLevel, "        |------  Date pattern = " + value.getDatePattern());
                }
            }
        }
    }

    public String getProcessDefinitionId() {
        return this.processDefinitionId;
    }

    /**
     * @return The WSDL operation associated to this {@link ActivitiOperation}
     */
    public QName getWsdlOperation() {
        return this.wsdlOperation;
    }
}