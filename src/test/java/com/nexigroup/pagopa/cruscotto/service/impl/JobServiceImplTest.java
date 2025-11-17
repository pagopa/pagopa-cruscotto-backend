package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JobServiceImpl Tests")
class JobServiceImplTest {

    @Mock
    private SchedulerFactoryBean schedulerFactoryBean;

    @Mock
    private Scheduler scheduler;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private JobServiceImpl jobService;

    @BeforeEach
    void setUp() throws SchedulerException {
        when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
    }

    @Test
    void testUpdateCronJob_success() throws Exception {
        when(scheduler.rescheduleJob(any(), any())).thenReturn(new Date());

        boolean result = jobService.updateCronJob("testJob", new Date(), "0 0/5 * * * ?");

        assertTrue(result);
        verify(scheduler, times(1)).rescheduleJob(any(), any());
    }

    @Test
    void testUpdateCronJob_exception() throws Exception {
        when(scheduler.rescheduleJob(any(), any())).thenThrow(new SchedulerException("error"));

        boolean result = jobService.updateCronJob("testJob", new Date(), "0 0/5 * * * ?");

        assertFalse(result);
    }

    @Test
    void testPauseJob_success() throws Exception {
        doNothing().when(scheduler).pauseJob(any());

        boolean result = jobService.pauseJob("testJob");

        assertTrue(result);
        verify(scheduler, times(1)).pauseJob(any(JobKey.class));
    }

    @Test
    void testPauseJob_exception() throws Exception {
        doThrow(new SchedulerException("err")).when(scheduler).pauseJob(any());

        boolean result = jobService.pauseJob("testJob");

        assertFalse(result);
    }

    @Test
    void testResumeJob_success() throws Exception {
        doNothing().when(scheduler).resumeJob(any());

        boolean result = jobService.resumeJob("testJob");

        assertTrue(result);
    }

    @Test
    void testResumeJob_exception() throws Exception {
        doThrow(new SchedulerException("fail")).when(scheduler).resumeJob(any());

        boolean result = jobService.resumeJob("testJob");

        assertFalse(result);
    }

    @Test
    void testStartJobNow_success() throws Exception {
        doNothing().when(scheduler).triggerJob(any(JobKey.class));

        boolean result = jobService.startJobNow("testJob");

        assertTrue(result);
        verify(scheduler, times(1)).triggerJob(any(JobKey.class));
    }

    @Test
    void testStartJobNow_exception() throws Exception {
        doThrow(new SchedulerException("fail")).when(scheduler).triggerJob(any(JobKey.class));

        boolean result = jobService.startJobNow("testJob");

        assertFalse(result);
    }

    @Test
    void testStartJobNow_withParameters_success() throws Exception {
        doNothing().when(scheduler).triggerJob(any(JobKey.class), any(JobDataMap.class));

        Map<String, Object> params = Map.of("key", "value");

        boolean result = jobService.startJobNow("testJob", params);

        assertTrue(result);
        verify(scheduler, times(1)).triggerJob(any(JobKey.class), any(JobDataMap.class));
    }

    @Test
    void testStartJobNow_withParameters_exception() throws Exception {
        doThrow(new SchedulerException("fail")).when(scheduler).triggerJob(any(JobKey.class), any(JobDataMap.class));

        boolean result = jobService.startJobNow("testJob", Map.of("key", "value"));

        assertFalse(result);
    }

    @Test
    void testCheckJobRunning_false() throws Exception {
        when(scheduler.getCurrentlyExecutingJobs()).thenReturn(Collections.emptyList());

        boolean result = jobService.checkJobRunning("testJob");

        assertFalse(result);
    }

    @Test
    void testStopJob_success() throws Exception {
        when(scheduler.interrupt(any(JobKey.class))).thenReturn(true);

        boolean result = jobService.stopJob("testJob");

        assertTrue(result);
        verify(scheduler, times(1)).interrupt(any(JobKey.class));
    }

    @Test
    void testCheckJobWithName_exists() throws Exception {
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);

        boolean result = jobService.checkJobWithName("testJob");

        assertTrue(result);
    }

    @Test
    void testCheckJobWithName_notExists() throws Exception {
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);

        boolean result = jobService.checkJobWithName("testJob");

        assertFalse(result);
    }

    @Test
    void testCheckJobWithName_exception() throws Exception {
        when(scheduler.checkExists(any(JobKey.class))).thenThrow(new SchedulerException("fail"));

        boolean result = jobService.checkJobWithName("testJob");

        assertFalse(result);
    }
}
