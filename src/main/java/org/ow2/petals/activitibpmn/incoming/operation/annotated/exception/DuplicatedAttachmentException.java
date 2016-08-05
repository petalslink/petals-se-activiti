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
 * A BPMN attachment is declared twice through annotations into an operation.
 * 
 * @author Christophe DENEUX - Linagora
 * 
 */
public class DuplicatedAttachmentException extends InvalidAnnotationForOperationException {

    private static final long serialVersionUID = 2651924897865636373L;

    private static final String MESSAGE_PATTERN = "The BPMN attachment '%s' is declared twice";

    /**
     * The name of the duplicated attachment
     */
    private final String attachmentName;

    public DuplicatedAttachmentException(final QName wsdlOperation, final String attachmentName) {
        super(wsdlOperation, String.format(MESSAGE_PATTERN, attachmentName));
        this.attachmentName = attachmentName;
    }

    /**
     * @return The name of the duplicated attachment
     */
    public String getAttachmentName() {
        return this.attachmentName;
    }
}
