/**
 * Copyright (c) 2014 Linagora
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
 * along with this program/library; If not, see <http://www.gnu.org/licenses/>
 * for the GNU Lesser General Public License version 2.1.
 */
package org.ow2.petals.activitibpmn;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.management.InvalidAttributeValueException;

import org.ow2.petals.component.framework.DefaultBootstrap;

/**
 * The component class of the Activiti BPMN Service Engine.
 * @author Bertrand Escudie - Linagora
 */
public class ActivitiSEBootstrap extends DefaultBootstrap {

    private static final String ATTR_NAME_JDBC_URL = "jdbcUrl";

	
    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAttributeList() {
        final List<String> attributes = super.getAttributeList();

        this.getLogger().info("***********************");
		this.getLogger().info("*** Start getAttributeList() in ActivitiSEBootstrap.");
        
        attributes.add("jdbcDriver");
        attributes.add(ATTR_NAME_JDBC_URL);
        attributes.add("jdbcUsername");
        attributes.add("jdbcPassword");
        attributes.add("jdbcMaxActiveConnections");
        attributes.add("jdbcMaxIdleConnections");
        attributes.add("jdbcMaxCheckoutTime");
        attributes.add("jdbcMaxWaitTime");
        attributes.add("databaseType");
        attributes.add("databaseSchemaUpdate");

        this.getLogger().info("*** End getAttributeList() in ActivitiSEBootstrap.");
        this.getLogger().info("***********************");
        
        return attributes;
    }
	
    /**
     * Get the jdbc Driver
     * 
     * @return the jdbc Driver
     */
    public String getJdbcDriver() {
    	return this.getParam(ActivitiSEConstants.DBServer.JDBC_DRIVER);
    }
 
    /**
     * Set the jdbc Driver
     * 
     * @param value the jdbc Driver
     */
    public void setJdbcDriver(final String value) {
        // TODO: Add a check to verify that the class can be instantiated, ie the class is available in the class loader
        this.setParam(ActivitiSEConstants.DBServer.JDBC_DRIVER, value);
    }
  
    /**
     * Get the jdbc URL
     * 
     * @return the jdbc URL
     */
    public String getJdbcUrl() {
    	return this.getParam(ActivitiSEConstants.DBServer.JDBC_URL);
    }
 
    /**
     * Set the jdbc URL
     * 
     * @param value the jdbc URL
     */
    public void setJdbcUrl(final String value) throws InvalidAttributeValueException {

        // Check that the given value is an URL
        try {
            new URL(value);
        } catch (final MalformedURLException e) {
            throw new InvalidAttributeValueException("Invalid value for attribute '" + ATTR_NAME_JDBC_URL
                    + "': The value must be an URL.");
        }

        this.setParam(ActivitiSEConstants.DBServer.JDBC_URL, value);
    }
  
    /**
     * Get the jdbc User Name
     * 
     * @return the jdbc User Name
     */
    public String getJdbcUsername() {
    	return this.getParam(ActivitiSEConstants.DBServer.JDBC_USERNAME);
    }
 
    /**
     * Set the jdbc User Name
     * 
     * @param value the User Name
     */
    public void setJdbcUsername(final String value) {
        this.setParam(ActivitiSEConstants.DBServer.JDBC_USERNAME, value);
    }
  
    /**
     * Get the jdbc Password
     * 
     * @return the jdbc Password
     */
    public String getJdbcPassword() {
    	return this.getParam(ActivitiSEConstants.DBServer.JDBC_PASSWORD);
    }
 
    /**
     * Set the jdbc UserName
     * 
     * @param value the UserName
     */
    public void setJdbcPassword(final String value) {
        this.setParam(ActivitiSEConstants.DBServer.JDBC_PASSWORD, value);
    }
  
    /**
     * Get the jdbc MaxActiveConnections
     * 
     * @return the jdbc MaxActiveConnections
     */
    public int getJdbcMaxActiveConnections() {
        int jdbcMaxActiveConnections;

        final String jdbcMaxActiveConnectionsString = this
                .getParam(ActivitiSEConstants.DBServer.JDBC_MAX_ACTIVE_CONNECTIONS);
        if (jdbcMaxActiveConnectionsString != null && !jdbcMaxActiveConnectionsString.trim().isEmpty()) {
            try {
                jdbcMaxActiveConnections = Integer.parseInt(jdbcMaxActiveConnectionsString);
            } catch (final NumberFormatException e) {
                // Invalid value, we use the default one
                this.getLogger().warning(
                        "Invalid value (" + jdbcMaxActiveConnectionsString + ") for the configuration parameter: "
                                + ActivitiSEConstants.DBServer.JDBC_MAX_ACTIVE_CONNECTIONS + ". Default value used: "
                                + ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_ACTIVE_CONNECTIONS);
                jdbcMaxActiveConnections = ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_ACTIVE_CONNECTIONS;
            }
        } else {
            jdbcMaxActiveConnections = ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_ACTIVE_CONNECTIONS;
        }

        return jdbcMaxActiveConnections;
    }
 
    /**
     * Set the jdbc MaxActiveConnections
     * 
     * @param value the MaxActiveConnections
     */
    public void setJdbcMaxActiveConnections(final int value) {
        this.setParam(ActivitiSEConstants.DBServer.JDBC_MAX_ACTIVE_CONNECTIONS, Integer.toString(value));
    }
  
    /**
     * Get the jdbc MaxIdleConnections
     * 
     * @return the jdbc MaxIdleConnections
     */
    public int getJdbcMaxIdleConnections() {
        int jdbcMaxIdleConnections;

        final String jdbcMaxIdleConnectionsString = this
                .getParam(ActivitiSEConstants.DBServer.JDBC_MAX_IDLE_CONNECTIONS);
        if (jdbcMaxIdleConnectionsString != null && !jdbcMaxIdleConnectionsString.trim().isEmpty()) {
            try {
                jdbcMaxIdleConnections = Integer.parseInt(jdbcMaxIdleConnectionsString);
            } catch (final NumberFormatException e) {
                // Invalid value, we use the default one
                this.getLogger().warning(
                        "Invalid value (" + jdbcMaxIdleConnectionsString + ") for the configuration parameter: "
                                + ActivitiSEConstants.DBServer.JDBC_MAX_IDLE_CONNECTIONS + ". Default value used: "
                                + ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_IDLE_CONNECTIONS);
                jdbcMaxIdleConnections = ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_IDLE_CONNECTIONS;
            }
        } else {
            jdbcMaxIdleConnections = ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_IDLE_CONNECTIONS;
        }

        return jdbcMaxIdleConnections;
    }
 
    /**
     * Set the jdbc MaxIdleConnections
     * 
     * @param value the MaxIdleConnections
     */
    public void setJdbcMaxIdleConnections(final int value) {
        this.setParam(ActivitiSEConstants.DBServer.JDBC_MAX_IDLE_CONNECTIONS, Integer.toString(value));
    }
  
    /**
     * Get the jdbc MaxCheckoutTime
     * 
     * @return the jdbc MaxCheckoutTime
     */
    public int getJdbcMaxCheckoutTime() {
        int jdbcMaxCheckoutTime = 0;

        final String jdbcMaxCheckoutTimeString = this.getParam(ActivitiSEConstants.DBServer.JDBC_MAX_CHECKOUT_TIME);
        if (jdbcMaxCheckoutTimeString != null && !jdbcMaxCheckoutTimeString.trim().isEmpty()) {
            try {
                jdbcMaxCheckoutTime = Integer.parseInt(jdbcMaxCheckoutTimeString);
            } catch (final NumberFormatException e) {
                // Invalid value, we use the default one
                this.getLogger().warning(
                        "Invalid value (" + jdbcMaxCheckoutTimeString + ") for the configuration parameter: "
                                + ActivitiSEConstants.DBServer.JDBC_MAX_CHECKOUT_TIME + ". Default value used: "
                                + ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_CHECKOUT_TIME);
                jdbcMaxCheckoutTime = ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_CHECKOUT_TIME;
            }
        } else {
            jdbcMaxCheckoutTime = ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_CHECKOUT_TIME;
        }

        return jdbcMaxCheckoutTime;
    }
 
    /**
     * Set the jdbc MaxCheckoutTime
     * 
     * @param value the MaxCheckoutTime
     */
    public void setJdbcMaxCheckoutTime(final int value) {
        this.setParam(ActivitiSEConstants.DBServer.JDBC_MAX_CHECKOUT_TIME, Integer.toString(value));
    }
 
    /**
     * Get the jdbc MaxWaitTime
     * 
     * @return the jdbc MaxWaitTime
     */
    public int getJdbcMaxWaitTime() {
        int jdbcMaxWaitTime = 0;

        final String jdbcMaxWaitTimeString = this.getParam(ActivitiSEConstants.DBServer.JDBC_MAX_WAIT_TIME);
        if (jdbcMaxWaitTimeString != null && !jdbcMaxWaitTimeString.trim().isEmpty()) {
            try {
                jdbcMaxWaitTime = Integer.parseInt(jdbcMaxWaitTimeString);
            } catch (final NumberFormatException e) {
                // Invalid value, we use the default one
                this.getLogger().warning(
                        "Invalid value (" + jdbcMaxWaitTimeString + ") for the configuration parameter: "
                                + ActivitiSEConstants.DBServer.JDBC_MAX_WAIT_TIME + ". Default value used: "
                                + ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_WAIT_TIME);
                jdbcMaxWaitTime = ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_WAIT_TIME;
            }
        } else {
            jdbcMaxWaitTime = ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_WAIT_TIME;
        }

        return jdbcMaxWaitTime;
    }
 
    /**
     * Set the jdbc MaxWaitTime
     * 
     * @param value the MaxWaitTime
     */
    public void setJdbcMaxWaitTime(final int value) {
        this.setParam(ActivitiSEConstants.DBServer.JDBC_MAX_WAIT_TIME, Integer.toString(value));
    }
 
    /**
     * Get the databaseType
     * 
     * @return the databaseType
     */
    public String getDatabaseType() {
    	return this.getParam(ActivitiSEConstants.DBServer.DATABASE_TYPE);
    }
     
    /**
     * Set the databaseType
     * 
     * @param value the databaseType
     */
    public void setDatabaseType(final String value) {
        // TODO: Add a check about valid values
        this.setParam(ActivitiSEConstants.DBServer.DATABASE_TYPE, value);
    }
  
    /**
     * Get the databaseSchemaUpdate
     * 
     * @return the databaseSchemaUpdate
     */
    public String getDatabaseSchemaUpdate() {
    	return this.getParam(ActivitiSEConstants.DBServer.DATABASE_SCHEMA_UPDATE);
    }
     
    /**
     * Set the databaseSchemaUpdate
     * 
     * @param value the databaseSchemaUpdate
     */
    public void setDatabaseSchemaUpdate(final String value) {
        // TODO: Add a check about valid values
        this.setParam(ActivitiSEConstants.DBServer.DATABASE_SCHEMA_UPDATE, value);
    }
     
}
