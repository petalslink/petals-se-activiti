/**
 * Copyright (c) 2014-2019 Linagora
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
package org.ow2.petals.activitibpmn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ow2.petals.activitibpmn.ActivitiSEConstants.DEFAULT_ENGINE_IDENTITY_SERVICE_CFG_FILE;
import static org.ow2.petals.activitibpmn.ActivitiSEConstants.DEFAULT_ENGINE_IDENTITY_SERVICE_CLASS_NAME;

import java.io.File;
import java.util.List;

import javax.management.InvalidAttributeValueException;
import javax.management.MalformedObjectNameException;
import javax.xml.parsers.DocumentBuilder;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.asyncexecutor.AsyncExecutor;
import org.activiti.engine.impl.asyncexecutor.DefaultAsyncJobExecutor;
import org.h2.Driver;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.ow2.petals.activitibpmn.identity.IdentityService;
import org.ow2.petals.activitibpmn.identity.IdentityServiceMock;
import org.ow2.petals.component.framework.AbstractComponent;
import org.ow2.petals.component.framework.jbidescriptor.generated.Component;
import org.ow2.petals.component.framework.jbidescriptor.generated.Jbi;
import org.ow2.petals.component.framework.junit.mbean.AbstractBootstrapTest;
import org.ow2.petals.component.framework.junit.rule.ComponentUnderTest;
import org.ow2.petals.junit.rules.log.handler.InMemoryLogHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ebmwebsourcing.easycommons.lang.reflect.ReflectionHelper;
import com.ebmwebsourcing.easycommons.xml.DocumentBuilders;

/**
 * Unit tests of {@link ActivitiSEBootstrap}
 * 
 * @author Christophe DENEUX - Linagora
 * 
 */
public class ActivitiSEBootstrapTest extends AbstractBootstrapTest {

    @ClassRule
    public static final InMemoryLogHandler IN_MEMORY_LOG_HANDLER = new InMemoryLogHandler();

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    public ActivitiSEBootstrapTest() throws MalformedObjectNameException {
        // Constructor requires to declare the exception thrown by 'jmxClient'
    }

    /**
     * Check that the component embeds the right hard-coded default configuration (values of the component JBI
     * descriptor are set to null or empty)
     */
    @Test
    public void defaultConfiguration_definedInComponentSourceCode() throws Exception {

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
        final Element eltDatabaseType = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.DATABASE_TYPE);
        params.add(eltDatabaseType);
        final Element eltJdbcUrl = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_URL);
        params.add(eltJdbcUrl);
        final Element eltJdbcUsername = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_USERNAME);
        params.add(eltJdbcUsername);
        final Element eltJdbcPassword = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_PASSWORD);
        params.add(eltJdbcPassword);

        final Element eltEnableEngineJobExecutor = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_ENABLE_JOB_EXECUTOR);
        params.add(eltEnableEngineJobExecutor);
        final Element eltEngineJobExecutorCorePoolSize = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_COREPOOLSIZE);
        params.add(eltEngineJobExecutorCorePoolSize);
        final Element eltEngineJobExecutorMaxPoolSize = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_MAXPOOLSIZE);
        params.add(eltEngineJobExecutorMaxPoolSize);
        final Element eltEngineJobExecutorKeepAliveTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_KEEPALIVETIME);
        params.add(eltEngineJobExecutorKeepAliveTime);
        final Element eltEngineJobExecutorQueueSize = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_QUEUESIZE);
        params.add(eltEngineJobExecutorQueueSize);
        final Element eltEngineJobExecutorMaxTimerJobsPerAcquisition = doc.createElementNS(
                ActivitiSEConstants.NAMESPACE_COMP, ActivitiSEConstants.ENGINE_JOB_EXECUTOR_MAXTIMERJOBSPERACQUISITION);
        params.add(eltEngineJobExecutorMaxTimerJobsPerAcquisition);
        final Element eltEngineJobExecutorMaxAsyncJobsDuePerAcquisition = doc.createElementNS(
                ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_MAXASYNCJOBSDUEPERACQUISITION);
        params.add(eltEngineJobExecutorMaxAsyncJobsDuePerAcquisition);
        final Element eltEngineJobExecutorAsyncJobDueAcquireWaitTime = doc.createElementNS(
                ActivitiSEConstants.NAMESPACE_COMP, ActivitiSEConstants.ENGINE_JOB_EXECUTOR_ASYNCJOBACQUIREWAITTIME);
        params.add(eltEngineJobExecutorAsyncJobDueAcquireWaitTime);
        final Element eltEngineJobExecutorTimerJobAcquireWaitTime = doc.createElementNS(
                ActivitiSEConstants.NAMESPACE_COMP, ActivitiSEConstants.ENGINE_JOB_EXECUTOR_TIMERJOBACQUIREWAITTIME);
        params.add(eltEngineJobExecutorTimerJobAcquireWaitTime);
        final Element eltEngineJobExecutorTimerLockTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_TIMERLOCKTIME);
        params.add(eltEngineJobExecutorTimerLockTime);
        final Element eltEngineJobExecutorAsyncJobLockTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_ASYNCJOBLOCKTIME);
        params.add(eltEngineJobExecutorAsyncJobLockTime);

        final Element eltEnableEngineBpmnValidation = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_ENABLE_BPMN_VALIDATION);
        params.add(eltEnableEngineBpmnValidation);
        final Element eltEngineIdentityServiceClassName = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_IDENTITY_SERVICE_CLASS_NAME);
        params.add(eltEngineIdentityServiceClassName);
        final Element eltEngineIdentityServiceCfgFile = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_IDENTITY_SERVICE_CFG_FILE);
        params.add(eltEngineIdentityServiceCfgFile);

        this.embeddedJmxSrvCon.registerConfigurationInstallerMBean(
                this.initBootstrap(new ActivitiSEBootstrap(), jbiComponentConfiguration));

        this.assertDefaultConfigurationValues("", "", "", "");
    }

    /**
     * Check that the component embeds the right hard-coded default configuration when the integer or boolean values of
     * the component JBI descriptor are set to 'space'.
     */
    @Test
    public void defaultConfiguration_ValuesSetToSpace() throws Exception {

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
        final Element eltDatabaseType = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.DATABASE_TYPE);
        eltDatabaseType.setTextContent(" ");
        params.add(eltDatabaseType);
        final Element eltDatabaseSchemaUpdate = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.DATABASE_SCHEMA_UPDATE);
        eltDatabaseSchemaUpdate.setTextContent(" ");
        params.add(eltDatabaseSchemaUpdate);
        final Element eltJdbcUrl = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_URL);
        eltJdbcUrl.setTextContent(" ");
        params.add(eltJdbcUrl);
        final Element eltJdbcUsername = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_USERNAME);
        eltJdbcUsername.setTextContent(" ");
        params.add(eltJdbcUsername);
        final Element eltJdbcPassword = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_PASSWORD);
        eltJdbcPassword.setTextContent(" ");
        params.add(eltJdbcPassword);

        final Element eltEnableEngineJobExecutor = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_ENABLE_JOB_EXECUTOR);
        eltEnableEngineJobExecutor.setTextContent(" ");
        params.add(eltEnableEngineJobExecutor);
        final Element eltEngineJobExecutorCorePoolSize = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_COREPOOLSIZE);
        eltEngineJobExecutorCorePoolSize.setTextContent(" ");
        params.add(eltEngineJobExecutorCorePoolSize);
        final Element eltEngineJobExecutorMaxPoolSize = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_MAXPOOLSIZE);
        eltEngineJobExecutorMaxPoolSize.setTextContent(" ");
        params.add(eltEngineJobExecutorMaxPoolSize);
        final Element eltEngineJobExecutorKeepAliveTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_KEEPALIVETIME);
        eltEngineJobExecutorKeepAliveTime.setTextContent(" ");
        params.add(eltEngineJobExecutorKeepAliveTime);
        final Element eltEngineJobExecutorQueueSize = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_QUEUESIZE);
        eltEngineJobExecutorQueueSize.setTextContent(" ");
        params.add(eltEngineJobExecutorQueueSize);
        final Element eltEngineJobExecutorMaxTimerJobsPerAcquisition = doc.createElementNS(
                ActivitiSEConstants.NAMESPACE_COMP, ActivitiSEConstants.ENGINE_JOB_EXECUTOR_MAXTIMERJOBSPERACQUISITION);
        eltEngineJobExecutorMaxTimerJobsPerAcquisition.setTextContent(" ");
        params.add(eltEngineJobExecutorMaxTimerJobsPerAcquisition);
        final Element eltEngineJobExecutorMaxAsyncJobsDuePerAcquisition = doc.createElementNS(
                ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_MAXASYNCJOBSDUEPERACQUISITION);
        eltEngineJobExecutorMaxAsyncJobsDuePerAcquisition.setTextContent(" ");
        params.add(eltEngineJobExecutorMaxAsyncJobsDuePerAcquisition);
        final Element eltEngineJobExecutorAsyncJobDueAcquireWaitTime = doc.createElementNS(
                ActivitiSEConstants.NAMESPACE_COMP, ActivitiSEConstants.ENGINE_JOB_EXECUTOR_ASYNCJOBACQUIREWAITTIME);
        eltEngineJobExecutorAsyncJobDueAcquireWaitTime.setTextContent(" ");
        params.add(eltEngineJobExecutorAsyncJobDueAcquireWaitTime);
        final Element eltEngineJobExecutorTimerJobAcquireWaitTime = doc.createElementNS(
                ActivitiSEConstants.NAMESPACE_COMP, ActivitiSEConstants.ENGINE_JOB_EXECUTOR_TIMERJOBACQUIREWAITTIME);
        eltEngineJobExecutorTimerJobAcquireWaitTime.setTextContent(" ");
        params.add(eltEngineJobExecutorTimerJobAcquireWaitTime);
        final Element eltEngineJobExecutorTimerLockTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_TIMERLOCKTIME);
        eltEngineJobExecutorTimerLockTime.setTextContent(" ");
        params.add(eltEngineJobExecutorTimerLockTime);
        final Element eltEngineJobExecutorAsyncJobLockTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_ASYNCJOBLOCKTIME);
        eltEngineJobExecutorAsyncJobLockTime.setTextContent(" ");
        params.add(eltEngineJobExecutorAsyncJobLockTime);

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

        this.embeddedJmxSrvCon.registerConfigurationInstallerMBean(
                this.initBootstrap(new ActivitiSEBootstrap(), jbiComponentConfiguration));

        this.assertDefaultConfigurationValues(" ", " ", " ", " ");
    }

    /**
     * Check that the component embeds the right hard-coded default configuration when the integer or boolean values of
     * the component JBI descriptor are set to invalid values.
     */
    @Test
    public void defaultConfiguration_InvalidValues() throws Exception {

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
        final Element eltEngineJobExecutorCorePoolSize = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_COREPOOLSIZE);
        eltEngineJobExecutorCorePoolSize.setTextContent("invalid-value");
        params.add(eltEngineJobExecutorCorePoolSize);
        final Element eltEngineJobExecutorMaxPoolSize = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_MAXPOOLSIZE);
        eltEngineJobExecutorMaxPoolSize.setTextContent("invalid-value");
        params.add(eltEngineJobExecutorMaxPoolSize);
        final Element eltEngineJobExecutorKeepAliveTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_KEEPALIVETIME);
        eltEngineJobExecutorKeepAliveTime.setTextContent("invalid-value");
        params.add(eltEngineJobExecutorKeepAliveTime);
        final Element eltEngineJobExecutorQueueSize = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_QUEUESIZE);
        eltEngineJobExecutorQueueSize.setTextContent("invalid-value");
        params.add(eltEngineJobExecutorQueueSize);
        final Element eltEngineJobExecutorMaxTimerJobsPerAcquisition = doc.createElementNS(
                ActivitiSEConstants.NAMESPACE_COMP, ActivitiSEConstants.ENGINE_JOB_EXECUTOR_MAXTIMERJOBSPERACQUISITION);
        eltEngineJobExecutorMaxTimerJobsPerAcquisition.setTextContent("invalid-value");
        params.add(eltEngineJobExecutorMaxTimerJobsPerAcquisition);
        final Element eltEngineJobExecutorMaxAsyncJobsDuePerAcquisition = doc.createElementNS(
                ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_MAXASYNCJOBSDUEPERACQUISITION);
        eltEngineJobExecutorMaxAsyncJobsDuePerAcquisition.setTextContent("invalid-value");
        params.add(eltEngineJobExecutorMaxAsyncJobsDuePerAcquisition);
        final Element eltEngineJobExecutorAsyncJobDueAcquireWaitTime = doc.createElementNS(
                ActivitiSEConstants.NAMESPACE_COMP, ActivitiSEConstants.ENGINE_JOB_EXECUTOR_ASYNCJOBACQUIREWAITTIME);
        eltEngineJobExecutorAsyncJobDueAcquireWaitTime.setTextContent("invalid-value");
        params.add(eltEngineJobExecutorAsyncJobDueAcquireWaitTime);
        final Element eltEngineJobExecutorTimerJobAcquireWaitTime = doc.createElementNS(
                ActivitiSEConstants.NAMESPACE_COMP, ActivitiSEConstants.ENGINE_JOB_EXECUTOR_TIMERJOBACQUIREWAITTIME);
        eltEngineJobExecutorTimerJobAcquireWaitTime.setTextContent("invalid-value");
        params.add(eltEngineJobExecutorTimerJobAcquireWaitTime);
        final Element eltEngineJobExecutorTimerLockTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_TIMERLOCKTIME);
        eltEngineJobExecutorTimerLockTime.setTextContent("invalid-value");
        params.add(eltEngineJobExecutorTimerLockTime);
        final Element eltEngineJobExecutorAsyncJobLockTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_ASYNCJOBLOCKTIME);
        eltEngineJobExecutorAsyncJobLockTime.setTextContent("invalid-value");
        params.add(eltEngineJobExecutorAsyncJobLockTime);

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

        this.embeddedJmxSrvCon.registerConfigurationInstallerMBean(
                this.initBootstrap(new ActivitiSEBootstrap(), jbiComponentConfiguration));

        this.assertDefaultConfigurationValues(null, null, null, null);
    }

    /**
     * Check that the component bootstrap get the given configuration when configuration parameters are set to valid
     * values into the component JBI descriptor.
     */
    @Test
    public void defaultConfiguration_ValidValues() throws Exception {

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

        final Element eltDatabaseType = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.DATABASE_TYPE);
        final String databaseType = "postgres";
        eltDatabaseType.setTextContent(databaseType);
        params.add(eltDatabaseType);

        final Element eltJdbcUrl = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_URL);
        final String jdbcUrl = this.tempFolder.newFile(ActivitiSEConstants.DBServer.DEFAULT_JDBC_URL_DATABASE_FILENAME)
                .getAbsolutePath();
        eltJdbcUrl.setTextContent(jdbcUrl);
        params.add(eltJdbcUrl);

        final Element eltJdbcUsername = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_USERNAME);
        final String jdbcUsername = "sa";
        eltJdbcUsername.setTextContent(jdbcUsername);
        params.add(eltJdbcUsername);

        final Element eltJdbcPassword = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.DBServer.JDBC_PASSWORD);
        final String jdbcPassword = "sa.password";
        eltJdbcPassword.setTextContent(jdbcPassword);
        params.add(eltJdbcPassword);

        final Element eltEnableEngineJobExecutor = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_ENABLE_JOB_EXECUTOR);
        final String engineEnableJobExecutor = Boolean.FALSE.toString();
        eltEnableEngineJobExecutor.setTextContent(engineEnableJobExecutor);
        params.add(eltEnableEngineJobExecutor);

        final Element eltEngineJobExecutorCorePoolSize = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_COREPOOLSIZE);
        final int jobExecutorCorePoolSize = 10;
        eltEngineJobExecutorCorePoolSize.setTextContent(String.valueOf(jobExecutorCorePoolSize));
        params.add(eltEngineJobExecutorCorePoolSize);

        final Element eltEngineJobExecutorMaxPoolSize = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_MAXPOOLSIZE);
        final int jobExecutorMaxPoolSize = 100;
        eltEngineJobExecutorMaxPoolSize.setTextContent(String.valueOf(jobExecutorMaxPoolSize));
        params.add(eltEngineJobExecutorMaxPoolSize);

        final Element eltEngineJobExecutorKeepAliveTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_KEEPALIVETIME);
        final int jobExecutorKeepAliveTime = 105000;
        eltEngineJobExecutorKeepAliveTime.setTextContent(String.valueOf(jobExecutorKeepAliveTime));
        params.add(eltEngineJobExecutorKeepAliveTime);

        final Element eltEngineJobExecutorQueueSize = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_QUEUESIZE);
        final int jobExecutorQueueSize = 1050;
        eltEngineJobExecutorQueueSize.setTextContent(String.valueOf(jobExecutorQueueSize));
        params.add(eltEngineJobExecutorQueueSize);

        final Element eltEngineJobExecutorMaxTimerJobsPerAcquisition = doc.createElementNS(
                ActivitiSEConstants.NAMESPACE_COMP, ActivitiSEConstants.ENGINE_JOB_EXECUTOR_MAXTIMERJOBSPERACQUISITION);
        final int jobExecutorMaxTimerJobsPerAcquisition = 50;
        eltEngineJobExecutorMaxTimerJobsPerAcquisition
                .setTextContent(String.valueOf(jobExecutorMaxTimerJobsPerAcquisition));
        params.add(eltEngineJobExecutorMaxTimerJobsPerAcquisition);

        final Element eltEngineJobExecutorMaxAsyncJobsDuePerAcquisition = doc.createElementNS(
                ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_MAXASYNCJOBSDUEPERACQUISITION);
        final int jobExecutorMaxAsyncJobsDuePerAcquisition = 50;
        eltEngineJobExecutorMaxAsyncJobsDuePerAcquisition
                .setTextContent(String.valueOf(jobExecutorMaxAsyncJobsDuePerAcquisition));
        params.add(eltEngineJobExecutorMaxAsyncJobsDuePerAcquisition);

        final Element eltEngineJobExecutorAsyncJobDueAcquireWaitTime = doc.createElementNS(
                ActivitiSEConstants.NAMESPACE_COMP, ActivitiSEConstants.ENGINE_JOB_EXECUTOR_ASYNCJOBACQUIREWAITTIME);
        final int jobExecutorAsyncJobDueAcquireWaitTime = 50000;
        eltEngineJobExecutorAsyncJobDueAcquireWaitTime
                .setTextContent(String.valueOf(jobExecutorAsyncJobDueAcquireWaitTime));
        params.add(eltEngineJobExecutorAsyncJobDueAcquireWaitTime);

        final Element eltEngineJobExecutorTimerJobAcquireWaitTime = doc.createElementNS(
                ActivitiSEConstants.NAMESPACE_COMP, ActivitiSEConstants.ENGINE_JOB_EXECUTOR_TIMERJOBACQUIREWAITTIME);
        final int jobExecutorTimerJobAcquireWaitTime = 50000;
        eltEngineJobExecutorTimerJobAcquireWaitTime.setTextContent(String.valueOf(jobExecutorTimerJobAcquireWaitTime));
        params.add(eltEngineJobExecutorTimerJobAcquireWaitTime);

        final Element eltEngineJobExecutorTimerLockTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_TIMERLOCKTIME);
        final int jobExecutorTimerLockTime = 50000;
        eltEngineJobExecutorTimerLockTime.setTextContent(String.valueOf(jobExecutorTimerLockTime));
        params.add(eltEngineJobExecutorTimerLockTime);

        final Element eltEngineJobExecutorAsyncJobLockTime = doc.createElementNS(ActivitiSEConstants.NAMESPACE_COMP,
                ActivitiSEConstants.ENGINE_JOB_EXECUTOR_ASYNCJOBLOCKTIME);
        final int jobExecutorAsyncJobLockTime = 50000;
        eltEngineJobExecutorAsyncJobLockTime.setTextContent(String.valueOf(jobExecutorAsyncJobLockTime));
        params.add(eltEngineJobExecutorAsyncJobLockTime);

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

        this.embeddedJmxSrvCon.registerConfigurationInstallerMBean(
                this.initBootstrap(new ActivitiSEBootstrap(), jbiComponentConfiguration));

        assertEquals(ActivitiSEConstants.DBServer.DEFAULT_JDBC_DRIVER,
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_JDBC_DRIVER));
        assertEquals(jdbcUrl, this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_JDBC_URL));
        assertEquals(jdbcUsername,
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_JDBC_USERNAME));
        assertEquals(jdbcPassword,
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_JDBC_PASSWORD));
        assertEquals(jdbcMaxActiveConnections,
                this.jmxClient.getBootstrapAttributeAsInt(ActivitiSEBootstrap.ATTR_NAME_JDBC_MAX_ACTIVE_CONNECTIONS));
        assertEquals(jdbcMaxIdleConnections,
                this.jmxClient.getBootstrapAttributeAsInt(ActivitiSEBootstrap.ATTR_NAME_JDBC_MAX_IDLE_CONNECTIONS));
        assertEquals(jdbcMaxCheckoutTime,
                this.jmxClient.getBootstrapAttributeAsInt(ActivitiSEBootstrap.ATTR_NAME_JDBC_MAX_CHECKOUT_TIME));
        assertEquals(jdbcMaxWaitTime,
                this.jmxClient.getBootstrapAttributeAsInt(ActivitiSEBootstrap.ATTR_NAME_JDBC_MAX_WAIT_TIME));
        assertEquals(databaseType,
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_DATABASE_TYPE));
        assertEquals(databaseSchemaUpdate,
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_DATABASE_SCHEMA_UPDATE));
        assertEquals(engineEnableBpmnValidation, this.jmxClient
                .getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_ENGINE_ENABLE_BPMN_VALIDATION));
        assertEquals(engineIdentityServiceClassName, this.jmxClient
                .getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CLASS_NAME));
        assertEquals(DEFAULT_ENGINE_IDENTITY_SERVICE_CFG_FILE, this.jmxClient
                .getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CFG_FILE));
        assertEquals(engineEnableJobExecutor,
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_ENGINE_ENABLE_JOB_EXECUTOR));
        assertEquals(jobExecutorCorePoolSize, this.jmxClient
                .getBootstrapAttributeAsInt(ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_COREPOOLSIZE));
        assertEquals(jobExecutorMaxPoolSize, this.jmxClient
                .getBootstrapAttributeAsInt(ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_MAXPOOLSIZE));
        assertEquals(jobExecutorKeepAliveTime, this.jmxClient
                .getBootstrapAttributeAsLong(ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_KEEPALIVETIME));
        assertEquals(jobExecutorQueueSize,
                this.jmxClient.getBootstrapAttributeAsInt(ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_QUEUESIZE));

        // TODO: What to do with this config parameter not available at Mbean level ?
        // assertEquals(jobExecutorMaxTimerJobsPerAcquisition,
        // bootstrap.getEngineJobExecutorMaxTimerJobsPerAcquisition());
        // assertEquals(jobExecutorMaxAsyncJobsDuePerAcquisition,
        // bootstrap.getEngineJobExecutorMaxAsyncJobsDuePerAcquisition());
        // assertEquals(jobExecutorAsyncJobDueAcquireWaitTime,
        // bootstrap.getEngineJobExecutorAsyncJobDueAcquireWaitTime());
        // assertEquals(jobExecutorTimerJobAcquireWaitTime, bootstrap.getEngineJobExecutorTimerJobAcquireWaitTime());
        // assertEquals(jobExecutorTimerLockTime, bootstrap.getEngineJobExecutorTimerLockTime());
        // assertEquals(jobExecutorAsyncJobLockTime, bootstrap.getEngineJobExecutorAsyncJobLockTime());
    }

    /**
     * Check that the component embeds the right default configuration in its JBI descriptor (values set to their
     * default value in jbi.xml)
     */
    @Test
    public void defaultConfiguration_definedInJbiDescriptor() throws Exception {

        this.embeddedJmxSrvCon.registerConfigurationInstallerMBean(this.initBootstrap(new ActivitiSEBootstrap()));

        this.assertDefaultConfigurationValues("", "sa", "", "");
    }

    private void assertDefaultConfigurationValues(final String expectedJdbcUrl, final String expectedJdbcUsername,
            final String expectedJdbcPassword, final String expectedDatabaseType) throws Exception {

        assertEquals(ActivitiSEConstants.DBServer.DEFAULT_JDBC_DRIVER,
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_JDBC_DRIVER));
        assertEquals(expectedJdbcUrl,
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_JDBC_URL));
        assertEquals(expectedJdbcUsername,
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_JDBC_USERNAME));
        assertEquals(expectedJdbcPassword,
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_JDBC_PASSWORD));
        assertEquals(ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_ACTIVE_CONNECTIONS,
                this.jmxClient.getBootstrapAttributeAsInt(ActivitiSEBootstrap.ATTR_NAME_JDBC_MAX_ACTIVE_CONNECTIONS));
        assertEquals(ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_IDLE_CONNECTIONS,
                this.jmxClient.getBootstrapAttributeAsInt(ActivitiSEBootstrap.ATTR_NAME_JDBC_MAX_IDLE_CONNECTIONS));
        assertEquals(ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_CHECKOUT_TIME,
                this.jmxClient.getBootstrapAttributeAsInt(ActivitiSEBootstrap.ATTR_NAME_JDBC_MAX_CHECKOUT_TIME));
        assertEquals(ActivitiSEConstants.DBServer.DEFAULT_JDBC_MAX_WAIT_TIME,
                this.jmxClient.getBootstrapAttributeAsInt(ActivitiSEBootstrap.ATTR_NAME_JDBC_MAX_WAIT_TIME));
        assertEquals(expectedDatabaseType,
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_DATABASE_TYPE));
        assertEquals(ActivitiSEConstants.DBServer.DEFAULT_DATABASE_SCHEMA_UPDATE,
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_DATABASE_SCHEMA_UPDATE));
        assertEquals(String.valueOf(ActivitiSEConstants.DEFAULT_ENGINE_ENABLE_BPMN_VALIDATION), this.jmxClient
                .getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_ENGINE_ENABLE_BPMN_VALIDATION));
        assertEquals(DEFAULT_ENGINE_IDENTITY_SERVICE_CLASS_NAME, this.jmxClient
                .getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CLASS_NAME));
        assertEquals(DEFAULT_ENGINE_IDENTITY_SERVICE_CFG_FILE, this.jmxClient
                .getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CFG_FILE));
        assertEquals(String.valueOf(ActivitiSEConstants.DEFAULT_ENGINE_ENABLE_JOB_EXECUTOR),
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_ENGINE_ENABLE_JOB_EXECUTOR));
        assertEquals(ActivitiSEConstants.DEFAULT_ENGINE_JOB_EXECUTOR_COREPOOLSIZE, this.jmxClient
                .getBootstrapAttributeAsInt(ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_COREPOOLSIZE));
        assertEquals(ActivitiSEConstants.DEFAULT_ENGINE_JOB_EXECUTOR_MAXPOOLSIZE, this.jmxClient
                .getBootstrapAttributeAsInt(ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_MAXPOOLSIZE));
        assertEquals(ActivitiSEConstants.DEFAULT_ENGINE_JOB_EXECUTOR_KEEPALIVETIME, this.jmxClient
                .getBootstrapAttributeAsLong(ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_KEEPALIVETIME));
        assertEquals(ActivitiSEConstants.DEFAULT_ENGINE_JOB_EXECUTOR_QUEUESIZE,
                this.jmxClient.getBootstrapAttributeAsInt(ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_QUEUESIZE));
        assertEquals(ActivitiSEConstants.DEFAULT_ENGINE_JOB_EXECUTOR_MAXTIMERJOBSPERACQUISITION,
                this.jmxClient.getBootstrapAttributeAsInt(
                        ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_MAXTIMERJOBSPERACQUISITION));
        assertEquals(ActivitiSEConstants.DEFAULT_ENGINE_JOB_EXECUTOR_MAXASYNCJOBSDUEPERACQUISITION,
                this.jmxClient.getBootstrapAttributeAsInt(
                        ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_MAXASYNCJOBSDUEPERACQUISITION));
        assertEquals(ActivitiSEConstants.DEFAULT_ENGINE_JOB_EXECUTOR_ASYNCJOBACQUIREWAITTIME, this.jmxClient
                .getBootstrapAttributeAsInt(ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_ASYNCJOBACQUIREWAITTIME));
        assertEquals(ActivitiSEConstants.DEFAULT_ENGINE_JOB_EXECUTOR_TIMERJOBACQUIREWAITTIME, this.jmxClient
                .getBootstrapAttributeAsInt(ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_TIMERJOBACQUIREWAITTIME));
        assertEquals(ActivitiSEConstants.DEFAULT_ENGINE_JOB_EXECUTOR_TIMERLOCKTIME, this.jmxClient
                .getBootstrapAttributeAsInt(ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_TIMERLOCKTIME));
        assertEquals(ActivitiSEConstants.DEFAULT_ENGINE_JOB_EXECUTOR_ASYNCJOBLOCKTIME, this.jmxClient
                .getBootstrapAttributeAsInt(ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_ASYNCJOBLOCKTIME));
    }

    /**
     * Check to set an invalid URL as JDBC URL
     */
    @Test(expected = InvalidAttributeValueException.class)
    public void setJdbcUrl_InvalidURL() throws Exception {

        this.embeddedJmxSrvCon.registerConfigurationInstallerMBean(this.initBootstrap(new ActivitiSEBootstrap()));

        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_URL, "// invalid-url/foo:http+ftp");
    }

    /**
     * Check to set a valid URL as JDBC URL
     */
    @Test
    public void setJdbcUrl_ValidURL() throws Exception {

        this.embeddedJmxSrvCon.registerConfigurationInstallerMBean(this.initBootstrap(new ActivitiSEBootstrap()));

        // A simple URL
        final String expectedSimpleUrl = "http://path";
        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_URL, expectedSimpleUrl);
        assertEquals(expectedSimpleUrl,
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_JDBC_URL));

        // A JDBC URL that is not an URL
        final String expectedUrlNotUrl = "jdbc:h2:tcp://localhost/mem:activiti";
        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_URL, expectedUrlNotUrl);
        assertEquals(expectedUrlNotUrl,
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_JDBC_URL));

    }

    /**
     * Check to set the value 'space' as JDBC URL
     */
    @Test
    public void setJdbcUrl_SpaceURL() throws Exception {

        this.embeddedJmxSrvCon.registerConfigurationInstallerMBean(this.initBootstrap(new ActivitiSEBootstrap()));

        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_URL, null);
        assertEquals("", this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_JDBC_URL));

        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_URL, "");
        assertEquals("", this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_JDBC_URL));

        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_URL, " ");
        assertEquals("", this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_JDBC_URL));
    }

    /**
     * Check to set an unknown class as JDBC driver
     */
    @Test(expected = InvalidAttributeValueException.class)
    public void setJdbcDriver_Unknown() throws Exception {

        this.embeddedJmxSrvCon.registerConfigurationInstallerMBean(this.initBootstrap(new ActivitiSEBootstrap()));

        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_DRIVER, "unknown.class");
    }

    /**
     * Check to set a known class as JDBC Driver
     */
    @Test
    public void setJdbcDriver_ValidDriver() throws Exception {

        this.embeddedJmxSrvCon.registerConfigurationInstallerMBean(this.initBootstrap(new ActivitiSEBootstrap()));

        final String expectedDriver = Driver.class.getName();
        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_DRIVER, expectedDriver);
        assertEquals(expectedDriver,
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_JDBC_DRIVER));
    }

    /**
     * <p>
     * Check to set the value 'space' or <code>null</code> as JDBC Driver.
     * </p>
     * <p>
     * Expected results:
     * </p>
     * <ul>
     * <li>the value is successfully set,</li>
     * <li>the default value of the parameter is retrieved next.</li>
     * </ul>
     */
    @Test
    public void setJdbcDriver_SpaceValue() throws Exception {

        this.embeddedJmxSrvCon.registerConfigurationInstallerMBean(this.initBootstrap(new ActivitiSEBootstrap()));

        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_DRIVER, null);
        assertEquals(ActivitiSEConstants.DBServer.DEFAULT_JDBC_DRIVER,
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_JDBC_DRIVER));

        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_DRIVER, "");
        assertEquals(ActivitiSEConstants.DBServer.DEFAULT_JDBC_DRIVER,
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_JDBC_DRIVER));

        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_DRIVER, " ");
        assertEquals(ActivitiSEConstants.DBServer.DEFAULT_JDBC_DRIVER,
                this.jmxClient.getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_JDBC_DRIVER));
    }

    /**
     * <p>
     * Check to set the value 'space' or <code>null</code> as identity service class name.
     * </p>
     * <p>
     * Expected results:
     * </p>
     * <ul>
     * <li>the value is successfully set,</li>
     * <li>the default value of the parameter is retrieved next.</li>
     * </ul>
     */
    @Test
    public void setEngineIdentityServiceClassName_SpaceValue() throws Exception {

        this.embeddedJmxSrvCon.registerConfigurationInstallerMBean(this.initBootstrap(new ActivitiSEBootstrap()));

        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CLASS_NAME, null);
        assertEquals(DEFAULT_ENGINE_IDENTITY_SERVICE_CLASS_NAME, this.jmxClient
                .getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CLASS_NAME));

        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CLASS_NAME, "");
        assertEquals(DEFAULT_ENGINE_IDENTITY_SERVICE_CLASS_NAME, this.jmxClient
                .getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CLASS_NAME));

        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CLASS_NAME, " ");
        assertEquals(DEFAULT_ENGINE_IDENTITY_SERVICE_CLASS_NAME, this.jmxClient
                .getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CLASS_NAME));
    }

    /**
     * Check to set a not loadable class as identity service class name
     */
    @Test(expected = InvalidAttributeValueException.class)
    public void setEngineIdentityServiceCfgFile_ClassNotLoadable() throws Exception {

        this.embeddedJmxSrvCon.registerConfigurationInstallerMBean(this.initBootstrap(new ActivitiSEBootstrap()));

        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CLASS_NAME,
                "not-loadable-class");
    }

    /**
     * Check to set a class not implementing {@link IdentityService} as identity service class name
     */
    @Test(expected = InvalidAttributeValueException.class)
    public void setEngineIdentityServiceCfgFile_ClassNotImplementingIdentityService() throws Exception {

        this.embeddedJmxSrvCon.registerConfigurationInstallerMBean(this.initBootstrap(new ActivitiSEBootstrap()));

        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CLASS_NAME,
                String.class.getName());
    }

    /**
     * Check to set the value 'space' or <code>null</code> as identity service configuration file
     */
    @Test
    public void setEngineIdentityServiceCfgFile_SpaceValue() throws Exception {

        this.embeddedJmxSrvCon.registerConfigurationInstallerMBean(this.initBootstrap(new ActivitiSEBootstrap()));

        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CFG_FILE, null);
        assertEquals(null, this.jmxClient
                .getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CFG_FILE));

        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CFG_FILE, "");
        assertEquals(null, this.jmxClient
                .getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CFG_FILE));

        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CFG_FILE, " ");
        assertEquals(null, this.jmxClient
                .getBootstrapAttributeAsString(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CFG_FILE));
    }

    /**
     * Check to set an inexisting absolute file as identity service configuration file
     */
    @Test(expected = InvalidAttributeValueException.class)
    public void setEngineIdentityServiceCfgFile_InexistingFile() throws Exception {

        this.embeddedJmxSrvCon.registerConfigurationInstallerMBean(this.initBootstrap(new ActivitiSEBootstrap()));

        // Set an unexisting absolute file
        final File absFile = this.tempFolder.newFile();
        assertTrue(absFile.delete()); // We must remove the file previously created

        this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CFG_FILE,
                absFile.getAbsolutePath());
    }

    /**
     * Check that valid values set through the component bootstrap through JMX are correctly used by component.
     */
    @Test
    public void setValuesAreUsed() throws Throwable {
        // To access the component bootstrap through JMX, the component has not to be installed, only loaded
        final ComponentUnderTest componentUnderTest = new ComponentUnderTest(false, false)
                .addLogHandler(IN_MEMORY_LOG_HANDLER.getHandler()).addEmbeddedJmxSrv(this.embeddedJmxSrvCon);
        componentUnderTest.create();

        try {
            final AbstractComponent component = componentUnderTest.getComponentObject();
            assertNotNull(component);
            assertTrue(component instanceof ActivitiSE);
            final ActivitiSE activitiComponent = (ActivitiSE) component;

            // Set values using the component bootstrap through JMX
            final String databaseType = "h2";
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_DATABASE_TYPE, databaseType);
            final String databaseSchemaUpdate = "create-drop";
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_DATABASE_SCHEMA_UPDATE,
                    databaseSchemaUpdate);

            final String jdbcDriver = Driver.class.getName();
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_DRIVER, jdbcDriver);
            final String jdbcUsername = "my-user";
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_USERNAME, jdbcUsername);
            final String jdbcPassword = "my-user-pwd";
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_PASSWORD, jdbcPassword);
            final File databaseFile = this.tempFolder.newFile("my-database.db");
            final String jdbcUrl = String.format("jdbc:h2:%s", databaseFile.toURI().toURL().toExternalForm());
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_URL, jdbcUrl);
            final int jdbcMaxActiveConnections = 12;
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_MAX_ACTIVE_CONNECTIONS,
                    Integer.valueOf(jdbcMaxActiveConnections));
            final int jdbcMaxIdleConnections = 21;
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_MAX_IDLE_CONNECTIONS,
                    Integer.valueOf(jdbcMaxIdleConnections));
            final int jdbcMaxCheckoutTime = 1592;
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_MAX_CHECKOUT_TIME,
                    Integer.valueOf(jdbcMaxCheckoutTime));
            final int jdbcMaxWaitTime = 56842;
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_JDBC_MAX_WAIT_TIME,
                    Integer.valueOf(jdbcMaxWaitTime));

            final boolean jobExecutorEnableJobExecutor = true;
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_ENGINE_ENABLE_JOB_EXECUTOR,
                    String.valueOf(jobExecutorEnableJobExecutor));

            final int jobExecutorCorePoolSize = 27;
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_COREPOOLSIZE,
                    Integer.valueOf(jobExecutorCorePoolSize));
            final int jobExecutorMaxPoolSize = 52;
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_MAXPOOLSIZE,
                    Integer.valueOf(jobExecutorMaxPoolSize));
            final long jobExecutorKeepAliveTime = 559862;
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_KEEPALIVETIME,
                    Long.valueOf(jobExecutorKeepAliveTime));
            final int jobExecutorQueueSize = 123;
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_QUEUESIZE,
                    Integer.valueOf(jobExecutorQueueSize));
            final int jobExecutorMaxTimerJobsPerAcquisition = 682;
            this.jmxClient.setBootstrapAttribute(
                    ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_MAXTIMERJOBSPERACQUISITION,
                    Integer.valueOf(jobExecutorMaxTimerJobsPerAcquisition));
            final int jobExecutorMaxAsyncJobsDuePerAcquisition = 357;
            this.jmxClient.setBootstrapAttribute(
                    ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_MAXASYNCJOBSDUEPERACQUISITION,
                    Integer.valueOf(jobExecutorMaxAsyncJobsDuePerAcquisition));
            final int jobExecutorAsyncJobAcquireWaitTime = 623;
            this.jmxClient.setBootstrapAttribute(
                    ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_ASYNCJOBACQUIREWAITTIME,
                    Integer.valueOf(jobExecutorAsyncJobAcquireWaitTime));
            final int jobExecutorTimerJobAcquireWaitTime = 51397;
            this.jmxClient.setBootstrapAttribute(
                    ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_TIMERJOBACQUIREWAITTIME,
                    Integer.valueOf(jobExecutorTimerJobAcquireWaitTime));
            final int jobExecutorTimerLockTime = 26842;
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_TIMERLOCKTIME,
                    Integer.valueOf(jobExecutorTimerLockTime));
            final int jobExecutorAsyncJobLockTime = 512;
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_ENGINE_JOB_EXECUTOR_ASYNCJOBLOCKTIME,
                    Integer.valueOf(jobExecutorAsyncJobLockTime));

            final boolean engineEnableBpmnValidation = true;
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_ENGINE_ENABLE_BPMN_VALIDATION,
                    String.valueOf(engineEnableBpmnValidation));

            final String engineIdentityServiceClass = "org.ow2.petals.activitibpmn.identity.file.FileIdentityService";
            this.jmxClient.setBootstrapAttribute(ActivitiSEBootstrap.ATTR_NAME_ENGINE_IDENTITY_SERVICE_CLASS_NAME,
                    engineIdentityServiceClass);

            // We continue component lifecycle to be able to check are correctly used
            componentUnderTest.install();
            componentUnderTest.start();

            // Some parameter are set in the Activiti engine configuration
            final ProcessEngine processEngine = activitiComponent.getProcessEngine();
            assertNotNull(processEngine);
            final ProcessEngineConfiguration pec = processEngine.getProcessEngineConfiguration();
            assertNotNull(pec);
            final AsyncExecutor asyncExecutor = pec.getAsyncExecutor();
            assertTrue(asyncExecutor instanceof DefaultAsyncJobExecutor);
            final DefaultAsyncJobExecutor defaultAsyncExecutor = (DefaultAsyncJobExecutor) asyncExecutor;

            assertEquals(databaseType, pec.getDatabaseType());
            assertEquals(databaseSchemaUpdate, pec.getDatabaseSchemaUpdate());

            assertEquals(jdbcDriver, pec.getJdbcDriver());
            assertEquals(jdbcUrl, pec.getJdbcUrl());
            assertEquals(jdbcUsername, pec.getJdbcUsername());
            assertEquals(jdbcPassword, pec.getJdbcPassword());
            assertEquals(jdbcMaxActiveConnections, pec.getJdbcMaxActiveConnections());
            assertEquals(jdbcMaxIdleConnections, pec.getJdbcMaxIdleConnections());
            assertEquals(jdbcMaxCheckoutTime, pec.getJdbcMaxCheckoutTime());
            assertEquals(jdbcMaxWaitTime, pec.getJdbcMaxWaitTime());

            assertEquals(jobExecutorEnableJobExecutor, pec.isAsyncExecutorEnabled());

            assertEquals(jobExecutorCorePoolSize, defaultAsyncExecutor.getCorePoolSize());
            assertEquals(jobExecutorMaxPoolSize, defaultAsyncExecutor.getMaxPoolSize());
            assertEquals(jobExecutorKeepAliveTime, defaultAsyncExecutor.getKeepAliveTime());
            assertEquals(jobExecutorQueueSize, defaultAsyncExecutor.getQueueSize());
            assertEquals(jobExecutorMaxTimerJobsPerAcquisition, defaultAsyncExecutor.getMaxTimerJobsPerAcquisition());
            assertEquals(jobExecutorMaxAsyncJobsDuePerAcquisition,
                    defaultAsyncExecutor.getMaxAsyncJobsDuePerAcquisition());
            assertEquals(jobExecutorAsyncJobAcquireWaitTime,
                    defaultAsyncExecutor.getDefaultAsyncJobAcquireWaitTimeInMillis());
            assertEquals(jobExecutorTimerJobAcquireWaitTime,
                    defaultAsyncExecutor.getDefaultTimerJobAcquireWaitTimeInMillis());
            assertEquals(jobExecutorTimerLockTime, defaultAsyncExecutor.getTimerLockTimeInMillis());
            assertEquals(jobExecutorAsyncJobLockTime, defaultAsyncExecutor.getAsyncJobLockTimeInMillis());

            assertEquals(engineEnableBpmnValidation,
                    ReflectionHelper.getFieldValue(ActivitiSuManager.class,
                            (ActivitiSuManager) activitiComponent.getServiceUnitManager(),
                            "enableActivitiBpmnValidation", false));

        } finally {
            componentUnderTest.delete();
        }
    }
}
