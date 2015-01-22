package org.motechproject.scheduler.factory;

import org.motechproject.scheduler.exception.SchedulerInstantiationException;
import org.motechproject.scheduler.exception.SchedulerShutdownException;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Properties;

import static java.lang.Boolean.getBoolean;

/**
 * The <code>MotechSchedulerFactoryBean</code> is used to create scheduler and start it.
 */
@Component("motechSchedulerFactoryBean")
public class MotechSchedulerFactoryBean {

    private ApplicationContext applicationContext;

    private SchedulerFactoryBean schedulerFactoryBean;

    private Properties schedulerProperties;

    private static final Logger LOGGER = LoggerFactory.getLogger(MotechSchedulerFactoryBean.class);

    /**
     * Constructor.
     *
     * @param applicationContext the Spring context of the Scheduler module, not null
     * @param schedulerProperties the properties of scheduler, not null
     */
    @Autowired
    public MotechSchedulerFactoryBean(ApplicationContext applicationContext, @Qualifier("sqlProperties") Properties schedulerProperties) {
        this.applicationContext = applicationContext;
        this.schedulerProperties = schedulerProperties;
    }

    /**
     * Creates the Spring {@code SchedulerFactoryBean}.
     */
    @PostConstruct
    public void init() {
        schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setQuartzProperties(schedulerProperties);
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(getBooleanWithDefault(schedulerProperties.getProperty("scheduler.waitForJobsToCompleteOnShutdown"), true));
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext");
        schedulerFactoryBean.setApplicationContext(applicationContext);
        try {
            schedulerFactoryBean.afterPropertiesSet();
            schedulerFactoryBean.getScheduler().start();
        } catch (Exception e) {
            LOGGER.error("Failed to instantiate scheduler with configuration from quartz.properties");
            throw new SchedulerInstantiationException(e);
        }
    }

    /**
     * Shuts down {@code MotechSchedulerFactoryBean}.
     */
    @PreDestroy
    public void shutdown() {
        try {
            schedulerFactoryBean.destroy();
        } catch (Exception e) {
            LOGGER.error("Failed to shutdown scheduler");
            throw new SchedulerShutdownException(e);
        }
    }

    public SchedulerFactoryBean getQuartzSchedulerFactoryBean() {
        return schedulerFactoryBean;
    }

    /**
     * Returns created scheduler.
     *
     * @return the created scheduler
     */
    public Scheduler getQuartzScheduler() {
        return getQuartzSchedulerFactoryBean().getScheduler();
    }

    private boolean getBooleanWithDefault(String s, boolean defaultValue) {
        return s != null ? getBoolean(s) : defaultValue;
    }
}
