package com.nexigroup.pagopa.cruscotto.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;
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

    @InjectMocks
    private JobServiceImpl jobService;

    @BeforeEach
    void setUp() {
        when(schedulerFactoryBean.getScheduler()).thenReturn(scheduler);
    }

    @Test
    void testUpdateCronJob_success() throws Exception {
        when(scheduler.rescheduleJob(any(), any())).thenReturn(new Date());

        boolean result = jobService.updateCronJob("testJob", new Date(), "0 0/5 * * * ?");

        assertTrue(result);
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
    void testStartJobNow_success() throws Exception {
        doNothing().when(scheduler).triggerJob(any());

        boolean result = jobService.startJobNow("testJob");
        assertTrue(result);
    }

    @Test
    void testStartJobNow_exception() throws Exception {
        doThrow(new SchedulerException("fail")).when(scheduler).triggerJob(any());

        boolean result = jobService.startJobNow("testJob");
        assertFalse(result);
    }

    @Test
    void testCheckJobRunning_true() throws Exception {
        JobExecutionContext ctx = mock(JobExecutionContext.class);
        JobDetail detail = mock(JobDetail.class);
        JobKey jobKey = new JobKey("testJob", "DEFAULT");

        when(detail.getKey()).thenReturn(jobKey);
        when(ctx.getJobDetail()).thenReturn(detail);
        when(scheduler.getCurrentlyExecutingJobs()).thenReturn(List.of(ctx));

        boolean result = jobService.checkJobRunning("testJob");
        assertTrue(result);
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

}
