package com.nexigroup.pagopa.cruscotto.config;

import org.jetbrains.annotations.NotNull;
import org.quartz.Job;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AutoWiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

    private AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    private int resolveAutowiringType(Class<?> clazz) {
        if (Stream.of(clazz.getConstructors()).anyMatch(constructor -> constructor.getParameterTypes().length == 0)) {
            return AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;
        }
        return AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;
    }

    @Override
    protected @NotNull Object createJobInstance(final TriggerFiredBundle bundle) {
        Class<? extends Job> jobClass = bundle.getJobDetail().getJobClass();
        // this does the job of creating an instance
        return beanFactory.autowire(jobClass, resolveAutowiringType(jobClass), true);
    }
}
