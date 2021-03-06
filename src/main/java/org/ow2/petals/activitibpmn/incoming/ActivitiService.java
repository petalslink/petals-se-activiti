/**
 * Copyright (c) 2015-2019 Linagora
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
package org.ow2.petals.activitibpmn.incoming;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ow2.petals.component.framework.api.message.Exchange;

/**
 * <p>
 * A service provided by the SE, that can be invoked by service consumer. Each WSDL operation has its associated
 * {@link ActivitiService}.
 * </p>
 * <p>
 * Note: implementations of {@link ActivitiService} must be thread safe.
 * 
 * @author Christophe DENEUX
 *
 */
public interface ActivitiService {

    /**
     * <p>
     * Execute the operation
     * </p>
     * <p>
     * The reply XML payload or fault or error of the operation processing is set into the exchange.
     * </p>
     */
    public void execute(final Exchange exchange);

    public void log(final Logger logger, final Level logLevel);

}
