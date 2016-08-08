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
 * The attachment is not in the MTOM format in the incoming service request.
 * 
 * @author Christophe DENEUX - Linagora
 * 
 */
public class AttachmentMTOMException extends OperationProcessingFault {

    private static final long serialVersionUID = 5055731782521961246L;

    private static final String MESSAGE = "Attachment at '%s' is not in the MTOM format in the incoming service request !";

    /**
     * The XPath expression identifying the attachment in the incoming payload.
     */
    private final String attachmentXPath;

    public AttachmentMTOMException(final QName wsdlOperation, final String attachmentXPath) {
        super(wsdlOperation, MESSAGE);
        this.attachmentXPath = attachmentXPath;
    }

    /**
     * @return The XPath expression identifying the attachment in the incoming payload.
     */
    public String getAttachmentXPath() {
        return this.attachmentXPath;
    }

    @Override
    public Map<QName, String> getXslParameters() {
        final Map<QName, String> xslParameters = new HashMap<>();
        xslParameters.put(new QName(ActivitiOperation.SCHEMA_OUTPUT_XSLT_FAULT_PARAMS, "attachmentXPath"),
                this.attachmentXPath);
        return xslParameters;
    }

}
