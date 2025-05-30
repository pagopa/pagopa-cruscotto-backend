package com.nexigroup.pagopa.cruscotto.service.util;

import java.text.ParseException;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

/**
 * Needed to set Quartz useProperties=true when using Spring classes,
 * because Spring sets an object reference on JobDataMap that is not a String
 */
public class PersistableCronTriggerFactoryBean extends CronTriggerFactoryBean {

    public static final String JOB_DETAIL_KEY = "jobDetail";

    @Override
    public void afterPropertiesSet() throws ParseException {
        super.afterPropertiesSet();

        //Remove the JobDetail element
        getJobDataMap().remove(JOB_DETAIL_KEY);
    }
}
