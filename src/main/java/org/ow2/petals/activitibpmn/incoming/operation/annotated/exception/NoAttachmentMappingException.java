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

/**
 * An attachment was declared for the given WSDL binding operation, so the expression defining its value is required.
 * 
 * @author Christophe DENEUX - Linagora
 * 
 */
public class NoAttachmentMappingException extends InvalidAnnotationForOperationException {

    private static final long serialVersionUID = 4319434829333648782L;

    private static final String MESSAGE_PATTERN = "The expression defining the value of the attachment '%s' is required";

    /**
     * The name of the attachment for which the expression defining its value is missing.
     */
    private final String attachmentName;

    public NoAttachmentMappingException(final QName wsdlOperation, final String attachmentName) {
        super(wsdlOperation, String.format(MESSAGE_PATTERN, attachmentName));
        this.attachmentName = attachmentName;
    }

    /**
     * @return The name of the attachment for which the expression defining its value is missing
     */
    public String getAttachmentName() {
        return this.attachmentName;
    }
}
