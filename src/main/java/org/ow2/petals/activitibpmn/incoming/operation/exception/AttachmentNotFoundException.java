/**
 * Copyright (c) 2016 Linagora
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
package org.ow2.petals.activitibpmn.incoming.operation.exception;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.ow2.petals.activitibpmn.incoming.operation.ActivitiOperation;


/**
 * The attachment with the given identifier is not found in the incoming service request.
 * 
 * @author Christophe DENEUX - Linagora
 * 
 */
public class AttachmentNotFoundException extends OperationProcessingFault {

    private static final long serialVersionUID = 5999151994794664194L;

    private static final String MESSAGE = "Attachment '%s' is not attached to the incoming service request !";

    /**
     * The attachment identifier for which no attachment is joined to the incoming service request.
     */
    private final String attachmentId;

    public AttachmentNotFoundException(final QName wsdlOperation, final String attachmentId) {
        super(wsdlOperation, MESSAGE);
        this.attachmentId = attachmentId;
    }

    /**
     * @return The attachment identifier for which no attachment is joined to the incoming service request.
     */
    public String getAttachmentId() {
        return this.attachmentId;
    }

    @Override
    public Map<QName, String> getXslParameters() {
        final Map<QName, String> xslParameters = new HashMap<>();
        xslParameters.put(new QName(ActivitiOperation.SCHEMA_OUTPUT_XSLT_FAULT_PARAMS, "attachmentId"),
                this.attachmentId);
        return xslParameters;
    }

}
