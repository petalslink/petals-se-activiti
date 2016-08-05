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
package org.ow2.petals.activitibpmn.incoming.operation.annotated.exception;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;

/**
 * The expression defining an attachment is invalid.
 * 
 * @author Christophe DENEUX - Linagora
 * 
 */
public class AttachmentMappingExpressionException extends InvalidAnnotationForOperationException {

    private static final long serialVersionUID = -5815444382411824607L;

    private static final String MESSAGE_PATTERN = "The mapping defining the expression of the attachment '%s' is invalid: %s";

    /**
     * Name of the attachment having an invalid expression
     */
    private final String attachmentName;

    public AttachmentMappingExpressionException(final QName wsdlOperation, final String attachmentName,
            final XPathExpressionException cause) {
        super(wsdlOperation, String.format(MESSAGE_PATTERN, attachmentName, cause.getMessage()), cause);
        this.attachmentName = attachmentName;
    }

    /**
     * @return The name of the attachment having an invalid expression
     */
    public String getAttachmentName() {
        return this.attachmentName;
    }

}
