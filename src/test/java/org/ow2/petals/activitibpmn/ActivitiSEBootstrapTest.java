/**
 * Copyright (c) 2014-2015 Linagora
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ow2.petals.activitibpmn.ActivitiSEConstants.DEFAULT_ENGINE_IDENTITY_SERVICE_CFG_FILE;
import static org.ow2.petals.activitibpmn.ActivitiSEConstants.DEFAULT_ENGINE_IDENTITY_SERVICE_CLASS_NAME;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;

import javax.management.InvalidAttributeValueException;
import javax.xml.parsers.DocumentBuilder;

import org.h2.Driver;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.ow2.petals.activitibpmn.identity.IdentityService;
import org.ow2.petals.activitibpmn.identity.IdentityServiceMock;
import org.ow2.petals.component.framework.JBIBootstrap;
import org.ow2.petals.component.framework.jbidescriptor.CDKJBIDescriptorException;
import org.ow2.petals.component.framework.jbidescriptor.JBIDescriptorBuilder;
import org.ow2.petals.component.framework.jbidescriptor.generated.Component;
import org.ow2.petals.component.framework.jbidescriptor.generated.Jbi;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ebmwebsourcing.easycommons.xml.DocumentBuilders;

/**
 * Unit tests of {@link ActivitiSEBootstrap}
 * 
 * @author Christophe DENEUX - Linagora
 * 
 */
public class ActivitiSEBootstrapTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    /**
     * Check that the component embeds the right hard-coded default configuration (values of the component JBI
     * descriptor are set to null or empty)
     */
    @Test
    public void defaultConfiguration_definedInComponentSourceCode() throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {

        // Create a minimalist JBI descriptor
        final Jbi jbiComponentConfiguration = new Jbi();
        final Component component = new Component();
        jbiComponentConfiguration.setComponent(component);
        final List<Element> params = component.getAny();

        final DocumentBuilder docBuilder = DocumentBuilders.takeDocumentBuilder();
        final Document doc = docBuilder.newDocument();
        final Element eltJdbcMaxActiveConnections = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_MAX_ACTIVE_CONNECTIONS);
        params.add(eltJdbcMaxActiveConnections);
        final Element eltJdbcMaxIdleConnections = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_MAX_IDLE_CONNECTIONS);
        params.add(eltJdbcMaxIdleConnections);
        final Element eltJdbcMaxCheckoutTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_MAX_CHECKOUT_TIME);
        params.add(eltJdbcMaxCheckoutTime);
        final Element eltJdbcMaxWaitTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_MAX_WAIT_TIME);
        params.add(eltJdbcMaxWaitTime);
        final Element eltDatabaseSchemaUpdate = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.DATABASE_SCHEMA_UPDATE);
        params.add(eltDatabaseSchemaUpdate);

        final Element eltEnableEngineJobExecutor = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_ENABLE_JOB_EXECUTOR);
        params.add(eltEnableEngineJobExecutor);
        final Element eltEnableEngineBpmnValidation = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_ENABLE_BPMN_VALIDATION);
        params.add(eltEnableEngineBpmnValidation);
        final Element eltEngineIdentityServiceClassName = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_IDENTITY_SERVICE_CLASS_NAME);
        params.add(eltEngineIdentityServiceClassName);
        final Element eltEngineIdentityServiceCfgFile = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_IDENTITY_SERVICE_CFG_FILE);
        params.add(eltEngineIdentityServiceCfgFile);

        this.assertDefaultValue(this.createActivitSEBootstrap(jbiComponentConfiguration));
    }

    /**
     * Check that the component embeds the right hard-coded default configuration when the integer or boolean values of
     * the component JBI descriptor are set to 'space'.
     */
    @Test
    public void defaultConfiguration_ValuesSetToSpace() throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {

        // Create a minimalist JBI descriptor
        final Jbi jbiComponentConfiguration = new Jbi();
        final Component component = new Component();
        jbiComponentConfiguration.setComponent(component);
        final List<Element> params = component.getAny();

        final DocumentBuilder docBuilder = DocumentBuilders.takeDocumentBuilder();
        final Document doc = docBuilder.newDocument();
        final Element eltJdbcMaxActiveConnections = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_MAX_ACTIVE_CONNECTIONS);
        eltJdbcMaxActiveConnections.setTextContent(" ");
        params.add(eltJdbcMaxActiveConnections);
        final Element eltJdbcMaxIdleConnections = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_MAX_IDLE_CONNECTIONS);
        eltJdbcMaxIdleConnections.setTextContent(" ");
        params.add(eltJdbcMaxIdleConnections);
        final Element eltJdbcMaxCheckoutTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_MAX_CHECKOUT_TIME);
        eltJdbcMaxCheckoutTime.setTextContent(" ");
        params.add(eltJdbcMaxCheckoutTime);
        final Element eltJdbcMaxWaitTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_MAX_WAIT_TIME);
        eltJdbcMaxWaitTime.setTextContent(" ");
        params.add(eltJdbcMaxWaitTime);
        final Element eltDatabaseSchemaUpdate = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.DATABASE_SCHEMA_UPDATE);
        eltDatabaseSchemaUpdate.setTextContent(" ");
        params.add(eltDatabaseSchemaUpdate);

        final Element eltEnableEngineJobExecutor = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_ENABLE_JOB_EXECUTOR);
        eltEnableEngineJobExecutor.setTextContent(" ");
        params.add(eltEnableEngineJobExecutor);
        final Element eltEnableEngineBpmnValidation = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_ENABLE_BPMN_VALIDATION);
        eltEnableEngineBpmnValidation.setTextContent(" ");
        params.add(eltEnableEngineBpmnValidation);
        final Element eltEngineIdentityServiceClassName = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_IDENTITY_SERVICE_CLASS_NAME);
        eltEngineIdentityServiceClassName.setTextContent(" ");
        params.add(eltEngineIdentityServiceClassName);
        final Element eltEngineIdentityServiceCfgFile = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_IDENTITY_SERVICE_CFG_FILE);
        eltEngineIdentityServiceCfgFile.setTextContent(" ");
        params.add(eltEngineIdentityServiceCfgFile);

        this.assertDefaultValue(this.createActivitSEBootstrap(jbiComponentConfiguration));
    }

    /**
     * Check that the component embeds the right hard-coded default configuration when the integer or boolean values of
     * the component JBI descriptor are set to invalid values.
     */
    @Test
    public void defaultConfiguration_InvalidValues() throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {

        // Create a minimalist JBI descriptor
        final Jbi jbiComponentConfiguration = new Jbi();
        final Component component = new Component();
        jbiComponentConfiguration.setComponent(component);
        final List<Element> params = component.getAny();

        final DocumentBuilder docBuilder = DocumentBuilders.takeDocumentBuilder();
        final Document doc = docBuilder.newDocument();
        final Element eltJdbcMaxActiveConnections = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_MAX_ACTIVE_CONNECTIONS);
        eltJdbcMaxActiveConnections.setTextContent("invalid-value");
        params.add(eltJdbcMaxActiveConnections);
        final Element eltJdbcMaxIdleConnections = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_MAX_IDLE_CONNECTIONS);
        eltJdbcMaxIdleConnections.setTextContent("invalid-value");
        params.add(eltJdbcMaxIdleConnections);
        final Element eltJdbcMaxCheckoutTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_MAX_CHECKOUT_TIME);
        eltJdbcMaxCheckoutTime.setTextContent("invalid-value");
        params.add(eltJdbcMaxCheckoutTime);
        final Element eltJdbcMaxWaitTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_MAX_WAIT_TIME);
        eltJdbcMaxWaitTime.setTextContent("invalid-value");
        params.add(eltJdbcMaxWaitTime);
        final Element eltDatabaseSchemaUpdate = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.DATABASE_SCHEMA_UPDATE);
        eltDatabaseSchemaUpdate.setTextContent("invalid-value");
        params.add(eltDatabaseSchemaUpdate);

        final Element eltEnableEngineJobExecutor = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_ENABLE_JOB_EXECUTOR);
        eltEnableEngineJobExecutor.setTextContent("invalid-value");
        params.add(eltEnableEngineJobExecutor);
        final Element eltEnableEngineBpmnValidation = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_ENABLE_BPMN_VALIDATION);
        eltEnableEngineBpmnValidation.setTextContent("invalid-value");
        params.add(eltEnableEngineBpmnValidation);
        final Element eltEngineIdentityServiceClassName = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_IDENTITY_SERVICE_CLASS_NAME);
        eltEngineIdentityServiceClassName.setTextContent("invalid-value");
        params.add(eltEngineIdentityServiceClassName);
        final Element eltEngineIdentityServiceCfgFile = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_IDENTITY_SERVICE_CFG_FILE);
        eltEngineIdentityServiceCfgFile.setTextContent("invalid-value");
        params.add(eltEngineIdentityServiceCfgFile);

        this.assertDefaultValue(this.createActivitSEBootstrap(jbiComponentConfiguration));
    }

    /**
     * Check that the component bootstrap get the given configuration when configuration parameters are set to valid
     * values into the component JBI descriptor.
     */
    @Test
    public void defaultConfiguration_ValidValues() throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {

        // Create a minimalist JBI descriptor
        final Jbi jbiComponentConfiguration = new Jbi();
        final Component component = new Component();
        jbiComponentConfiguration.setComponent(component);
        final List<Element> params = component.getAny();

        final DocumentBuilder docBuilder = DocumentBuilders.takeDocumentBuilder();
        final Document doc = docBuilder.newDocument();

        final Element eltJdbcMaxActiveConnections = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_MAX_ACTIVE_CONNECTIONS);
        final int jdbcMaxActiveConnections = 15;
        eltJdbcMaxActiveConnections.setTextContent(String.valueOf(jdbcMaxActiveConnections));
        params.add(eltJdbcMaxActiveConnections);

        final Element eltJdbcMaxIdleConnections = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_MAX_IDLE_CONNECTIONS);
        final int jdbcMaxIdleConnections = 4;
        eltJdbcMaxIdleConnections.setTextContent(String.valueOf(jdbcMaxIdleConnections));
        params.add(eltJdbcMaxIdleConnections);

        final Element eltJdbcMaxCheckoutTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_MAX_CHECKOUT_TIME);
        final int jdbcMaxCheckoutTime = 25000;
        eltJdbcMaxCheckoutTime.setTextContent(String.valueOf(jdbcMaxCheckoutTime));
        params.add(eltJdbcMaxCheckoutTime);

        final Element eltJdbcMaxWaitTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_MAX_WAIT_TIME);
        final int jdbcMaxWaitTime = 15000;
        eltJdbcMaxWaitTime.setTextContent(String.valueOf(jdbcMaxWaitTime));
        params.add(eltJdbcMaxWaitTime);

        final Element eltDatabaseSchemaUpdate = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.DATABASE_SCHEMA_UPDATE);
        final String databaseSchemaUpdate = "create-drop";
        eltDatabaseSchemaUpdate.setTextContent(databaseSchemaUpdate);
        params.add(eltDatabaseSchemaUpdate);

        final Element eltEnableEngineJobExecutor = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_ENABLE_JOB_EXECUTOR);
        final String engineEnableJobExecutor = Boolean.FALSE.toString();
        eltEnableEngineJobExecutor.setTextContent(engineEnableJobExecutor);
        params.add(eltEnableEngineJobExecutor);
        final Element eltEnableEngineBpmnValidation = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_ENABLE_BPMN_VALIDATION);
        final String engineEnableBpmnValidation = Boolean.FALSE.toString();
        eltEnableEngineBpmnValidation.setTextContent(engineEnableBpmnValidation);
        params.add(eltEnableEngineBpmnValidation);
        final Element eltEngineIdentityServiceClassName = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_IDENTITY_SERVICE_CLASS_NAME);
        final String engineIdentityServiceClassName = IdentityServiceMock.class.getName();
        eltEngineIdentityServiceClassName.setTextContent(engineIdentityServiceClassName);
        params.add(eltEngineIdentityServiceClassName);
        final Element eltEngineIdentityServiceCfgFile = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_IDENTITY_SERVICE_CFG_FILE);
        final String engineIdentityServiceCfgFile = "my.identity.service.cfg.file";
        eltEngineIdentityServiceCfgFile.setTextContent(engineIdentityServiceCfgFile);
        params.add(eltEngineIdentityServiceCfgFile);

        final ActivitiSEBootstrap bootstrap = this.createActivitSEBootstrap(jbiComponentConfiguration);

        assertEquals(jdbcMaxActiveConnections, bootstrap.getJdbcMaxActiveConnections());
        assertEquals(jdbcMaxIdleConnections, bootstrap.getJdbcMaxIdleConnections());
        assertEquals(jdbcMaxCheckoutTime, bootstrap.getJdbcMaxCheckoutTime());
        assertEquals(jdbcMaxWaitTime, bootstrap.getJdbcMaxWaitTime());
        assertEquals(databaseSchemaUpdate, bootstrap.getDatabaseSchemaUpdate());
        assertEquals(engineEnableJobExecutor, bootstrap.getEngineEnableJobExecutor());
        assertEquals(engineEnableBpmnValidation, bootstrap.getEngineEnableBpmnValidation());
        assertEquals(engineIdentityServiceClassName, bootstrap.getEngineIdentityServiceClassName());
        assertEquals(DEFAULT_ENGINE_IDENTITY_SERVICE_CFG_FILE, bootstrap.getEngineIdentityServiceCfgFile());
    }

    /**
     * Check that the component embeds the right default configuration in its JBI descriptor (values set to their
     * default value in jbi.xml)
     */
    @Test
    public void defaultConfiguration_definedInJbiDescriptor() throws CDKJBIDescriptorException, SecurityException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        final InputStream defaultJbiDescriptorStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("jbi/jbi.xml");
        assertNotNull("The component JBI descriptor is missing", defaultJbiDescriptorStream);
        final Jbi jbiComponentConfiguration = JBIDescriptorBuilder.buildJavaJBIDescriptor(
                defaultJbiDescriptorStream);

        this.assertDefaultValue(this.createActivitSEBootstrap(jbiComponentConfiguration));
    }

    /**
     * Create a boostrap of the component
     * 
     * @param jbiComponentConfiguration
     *            The JBI descriptor to use
     * @return
     */
    private ActivitiSEBootstrap createActivitSEBootstrap(final Jbi jbiComponentConfiguration)
            throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException {

        final ActivitiSEBootstrap bootstrap = new ActivitiSEBootstrap();
        final Field jbiComponentCfgField = JBIBootstrap.class.getDeclaredField("jbiComponentConfiguration");
        jbiComponentCfgField.setAccessible(true);
        jbiComponentCfgField.set(bootstrap, jbiComponentConfiguration);
        final Field loggerField = JBIBootstrap.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        loggerField.set(bootstrap, Logger.getLogger(this.getClass().getName()));

        return bootstrap;
    }

    private void assertDefaultValue(final ActivitiSEBootstrap bootstrap) {

        assertEquals(ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_ACTIVE_CONNECTIONS,
                bootstrap.getJdbcMaxActiveConnections());
        assertEquals(ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_IDLE_CONNECTIONS,
                bootstrap.getJdbcMaxIdleConnections());
        assertEquals(ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_CHECKOUT_TIME, bootstrap.getJdbcMaxCheckoutTime());
        assertEquals(ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_WAIT_TIME, bootstrap.getJdbcMaxWaitTime());
        assertEquals(ActivitiSEConstants.DBServer.DEFAULT_DATABASE_SCHEMA_UPDATE, bootstrap.getDatabaseSchemaUpdate());
        assertEquals(Boolean.toString(ActivitiSEConstants.DEFAULT_ENGINE_ENABLE_JOB_EXECUTOR),
                bootstrap.getEngineEnableJobExecutor());
        assertEquals(Boolean.toString(ActivitiSEConstants.DEFAULT_ENGINE_ENABLE_BPMN_VALIDATION),
                bootstrap.getEngineEnableBpmnValidation());
        assertEquals(ActivitiSEConstants.DEFAULT_ENGINE_IDENTITY_SERVICE_CLASS_NAME,
                bootstrap.getEngineIdentityServiceClassName());
    }

    /**
     * Check to set an invalid URL as JDBC URL
     */
    @Ignore(value = "Ignored until no check about JDBC URL is done")
    @Test(expected = InvalidAttributeValueException.class)
    public void setJdbcUrl_InvalidURL() throws InvalidAttributeValueException {
        final ActivitiSEBootstrap bootstrap = new ActivitiSEBootstrap();
        bootstrap.setJdbcUrl("invalid-url://path");
    }

    /**
     * Check to set a valid URL as JDBC URL
     */
    @Test
    public void setJdbcUrl_ValidURL() throws InvalidAttributeValueException, IllegalArgumentException,
            SecurityException, IllegalAccessException, NoSuchFieldException, CDKJBIDescriptorException {

        final InputStream defaultJbiDescriptorStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("jbi/jbi.xml");
        assertNotNull("The component JBI descriptor is missing", defaultJbiDescriptorStream);
        final Jbi jbiComponentConfiguration = JBIDescriptorBuilder.buildJavaJBIDescriptor(
                defaultJbiDescriptorStream);

        final String expectedUrl = "http://path";
        final ActivitiSEBootstrap bootstrap = this.createActivitSEBootstrap(jbiComponentConfiguration);
        bootstrap.setJdbcUrl(expectedUrl);
        assertEquals(expectedUrl, bootstrap.getJdbcUrl());
    }

    /**
     * Check to set the value 'space' as JDBC URL
     */
    @Test
    public void setJdbcUrl_SpaceURL() throws InvalidAttributeValueException, IllegalArgumentException,
            SecurityException, IllegalAccessException, NoSuchFieldException, CDKJBIDescriptorException {

        final InputStream defaultJbiDescriptorStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("jbi/jbi.xml");
        assertNotNull("The component JBI descriptor is missing", defaultJbiDescriptorStream);
        final Jbi jbiComponentConfiguration = JBIDescriptorBuilder.buildJavaJBIDescriptor(
                defaultJbiDescriptorStream);

        final ActivitiSEBootstrap bootstrap = this.createActivitSEBootstrap(jbiComponentConfiguration);

        bootstrap.setJdbcUrl(null);
        assertEquals("", bootstrap.getJdbcUrl());

        bootstrap.setJdbcUrl("");
        assertEquals("", bootstrap.getJdbcUrl());

        bootstrap.setJdbcUrl(" ");
        assertEquals("", bootstrap.getJdbcUrl());
    }

    /**
     * Check to set an unknown class as JDBC driver
     */
    @Test(expected = InvalidAttributeValueException.class)
    public void setJdbcDriver_Unknown() throws InvalidAttributeValueException {
        final ActivitiSEBootstrap bootstrap = new ActivitiSEBootstrap();
        bootstrap.setJdbcDriver("unknown.class");
    }

    /**
     * Check to set a known class as JDBC Driver
     */
    @Test
    public void setJdbcDriver_ValidDriver() throws InvalidAttributeValueException, IllegalArgumentException,
            SecurityException, IllegalAccessException, NoSuchFieldException, CDKJBIDescriptorException {

        final InputStream defaultJbiDescriptorStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("jbi/jbi.xml");
        assertNotNull("The component JBI descriptor is missing", defaultJbiDescriptorStream);
        final Jbi jbiComponentConfiguration = JBIDescriptorBuilder.buildJavaJBIDescriptor(
                defaultJbiDescriptorStream);

        final String expectedDriver = Driver.class.getName();
        final ActivitiSEBootstrap bootstrap = this.createActivitSEBootstrap(jbiComponentConfiguration);
        bootstrap.setJdbcDriver(expectedDriver);
        assertEquals(expectedDriver, bootstrap.getJdbcDriver());
    }

    /**
     * <p>
     * Check to set the value 'space' or <code>null</code> as JDBC Driver.
     * </p>
     * <p>
     * Expected results:
     * <ul>
     * <li>the value is successfully set,</li>
     * <li>the default value of the parameter is retrieved next.</li>
     * </ul>
     * </p>
     */
    @Test
    public void setJdbcDriver_SpaceValue() throws InvalidAttributeValueException, IllegalArgumentException,
            SecurityException, IllegalAccessException, NoSuchFieldException, CDKJBIDescriptorException {

        final InputStream defaultJbiDescriptorStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("jbi/jbi.xml");
        assertNotNull("The component JBI descriptor is missing", defaultJbiDescriptorStream);
        final Jbi jbiComponentConfiguration = JBIDescriptorBuilder.buildJavaJBIDescriptor(
                defaultJbiDescriptorStream);

        final ActivitiSEBootstrap bootstrap = this.createActivitSEBootstrap(jbiComponentConfiguration);

        bootstrap.setJdbcDriver(null);
        assertEquals(ActivitiSEConstants.DBServer.DEFAULT_JDBC_DRIVER, bootstrap.getJdbcDriver());

        bootstrap.setJdbcDriver("");
        assertEquals(ActivitiSEConstants.DBServer.DEFAULT_JDBC_DRIVER, bootstrap.getJdbcDriver());

        bootstrap.setJdbcDriver(" ");
        assertEquals(ActivitiSEConstants.DBServer.DEFAULT_JDBC_DRIVER, bootstrap.getJdbcDriver());
    }

    /**
     * <p>
     * Check to set the value 'space' or <code>null</code> as identity service class name.
     * </p>
     * <p>
     * Expected results:
     * <ul>
     * <li>the value is successfully set,</li>
     * <li>the default value of the parameter is retrieved next.</li>
     * </ul>
     * </p>
     */
    @Test
    public void setEngineIdentityServiceClassName_SpaceValue() throws IllegalArgumentException, IllegalAccessException,
            SecurityException, NoSuchFieldException, InvalidAttributeValueException, CDKJBIDescriptorException {

        final InputStream defaultJbiDescriptorStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("jbi/jbi.xml");
        assertNotNull("The component JBI descriptor is missing", defaultJbiDescriptorStream);
        final Jbi jbiComponentConfiguration = JBIDescriptorBuilder.buildJavaJBIDescriptor(
                defaultJbiDescriptorStream);

        final ActivitiSEBootstrap bootstrap = this.createActivitSEBootstrap(jbiComponentConfiguration);

        bootstrap.setEngineIdentityServiceClassName(null);
        assertEquals(DEFAULT_ENGINE_IDENTITY_SERVICE_CLASS_NAME, bootstrap.getEngineIdentityServiceClassName());

        bootstrap.setEngineIdentityServiceClassName("");
        assertEquals(DEFAULT_ENGINE_IDENTITY_SERVICE_CLASS_NAME, bootstrap.getEngineIdentityServiceClassName());

        bootstrap.setEngineIdentityServiceClassName(" ");
        assertEquals(DEFAULT_ENGINE_IDENTITY_SERVICE_CLASS_NAME, bootstrap.getEngineIdentityServiceClassName());
    }

    /**
     * Check to set a not loadable class as identity service class name
     */
    @Test(expected = InvalidAttributeValueException.class)
    public void setEngineIdentityServiceCfgFile_ClassNotLoadable() throws CDKJBIDescriptorException,
            IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException,
            InvalidAttributeValueException, IOException {

        final InputStream defaultJbiDescriptorStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("jbi/jbi.xml");
        assertNotNull("The component JBI descriptor is missing", defaultJbiDescriptorStream);
        final Jbi jbiComponentConfiguration = JBIDescriptorBuilder.buildJavaJBIDescriptor(
                defaultJbiDescriptorStream);

        final ActivitiSEBootstrap bootstrap = this.createActivitSEBootstrap(jbiComponentConfiguration);

        bootstrap.setEngineIdentityServiceClassName("not-loadable-class");
    }

    /**
     * Check to set a class not implementing {@link IdentityService} as identity service class name
     */
    @Test(expected = InvalidAttributeValueException.class)
    public void setEngineIdentityServiceCfgFile_ClassNotImplementingIdentityService() throws CDKJBIDescriptorException,
            IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException,
            InvalidAttributeValueException, IOException {

        final InputStream defaultJbiDescriptorStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("jbi/jbi.xml");
        assertNotNull("The component JBI descriptor is missing", defaultJbiDescriptorStream);
        final Jbi jbiComponentConfiguration = JBIDescriptorBuilder.buildJavaJBIDescriptor(
                defaultJbiDescriptorStream);

        final ActivitiSEBootstrap bootstrap = this.createActivitSEBootstrap(jbiComponentConfiguration);

        bootstrap.setEngineIdentityServiceClassName(String.class.getName());
    }

    /**
     * Check to set the value 'space' or <code>null</code> as identity service configuration file
     */
    @Test
    public void setEngineIdentityServiceCfgFile_SpaceValue() throws CDKJBIDescriptorException,
            IllegalArgumentException,
            IllegalAccessException, SecurityException, NoSuchFieldException, InvalidAttributeValueException {

        final InputStream defaultJbiDescriptorStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("jbi/jbi.xml");
        assertNotNull("The component JBI descriptor is missing", defaultJbiDescriptorStream);
        final Jbi jbiComponentConfiguration = JBIDescriptorBuilder.buildJavaJBIDescriptor(
                defaultJbiDescriptorStream);

        final ActivitiSEBootstrap bootstrap = this.createActivitSEBootstrap(jbiComponentConfiguration);

        bootstrap.setEngineIdentityServiceCfgFile(null);
        assertEquals(null, bootstrap.getEngineIdentityServiceCfgFile());

        bootstrap.setEngineIdentityServiceCfgFile("");
        assertEquals(null, bootstrap.getEngineIdentityServiceCfgFile());

        bootstrap.setEngineIdentityServiceCfgFile(" ");
        assertEquals(null, bootstrap.getEngineIdentityServiceCfgFile());
    }

    /**
     * Check to set an inexisting absolute file as identity service configuration file
     */
    @Test(expected = InvalidAttributeValueException.class)
    public void setEngineIdentityServiceCfgFile_InexistingFile() throws CDKJBIDescriptorException,
            IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException,
            InvalidAttributeValueException, IOException {

        final InputStream defaultJbiDescriptorStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("jbi/jbi.xml");
        assertNotNull("The component JBI descriptor is missing", defaultJbiDescriptorStream);
        final Jbi jbiComponentConfiguration = JBIDescriptorBuilder.buildJavaJBIDescriptor(
                defaultJbiDescriptorStream);

        final ActivitiSEBootstrap bootstrap = this.createActivitSEBootstrap(jbiComponentConfiguration);

        // Set an inexisting absolute file
        final File absFile = tempFolder.newFile("inexisting-abs-file.txt");
        assertTrue(absFile.delete()); // We must remove the file previously created

        bootstrap.setEngineIdentityServiceCfgFile(absFile.getAbsolutePath());
    }

}