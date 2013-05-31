package org.motechproject.scheduler.factory;

import org.motechproject.scheduler.exception.SchedulerInstantiationException;
import org.motechproject.server.config.SettingsFacade;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

import static java.lang.Boolean.getBoolean;

@Component
public class MotechSchedulerFactoryBean {

    private ApplicationContext applicationContext;
    private SettingsFacade schedulerSettings;

    private SchedulerFactoryBean schedulerFactoryBean;

    private Logger log = LoggerFactory.getLogger(MotechSchedulerFactoryBean.class);

    @Autowired
    public MotechSchedulerFactoryBean(ApplicationContext applicationContext, SettingsFacade schedulerSettings) {
        this.applicationContext = applicationContext;
        this.schedulerSettings = schedulerSettings;
    }

    @PostConstruct
    public void init() {
        Properties schedulerProperties = schedulerSettings.getProperties("quartz.properties");
        schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setQuartzProperties(schedulerProperties);
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(getBooleanWithDefault(schedulerProperties.getProperty("scheduler.waitForJobsToCompleteOnShutdown"), true));
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext");
        schedulerFactoryBean.setApplicationContext(applicationContext);
        try {
            schedulerFactoryBean.afterPropertiesSet();
            schedulerFactoryBean.getScheduler().start();
        } catch (Exception e) {
            log.error("Failed to instantiate scheduler with configuration from quartz.properties");
            throw new SchedulerInstantiationException(e);
        }
    }

    public SchedulerFactoryBean getQuartzSchedulerFactoryBean() {
        return schedulerFactoryBean;
    }

    public Scheduler getQuartzScheduler() {
        return getQuartzSchedulerFactoryBean().getScheduler();
    }

    private boolean getBooleanWithDefault(String s, boolean defaultValue) {
        return s != null? getBoolean(s) : defaultValue;
    }
}
