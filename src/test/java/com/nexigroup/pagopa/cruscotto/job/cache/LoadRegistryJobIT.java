package com.nexigroup.pagopa.cruscotto.job.cache;

import com.nexigroup.pagopa.cruscotto.service.AnagInstitutionService;
import com.nexigroup.pagopa.cruscotto.service.AnagPartnerService;
import com.nexigroup.pagopa.cruscotto.service.AnagStationService;
import org.junit.jupiter.api.Test;
import org.quartz.JobExecutionContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class LoadRegistryJobIT {
    
    @Mock
    private AnagPartnerService anagPartnerService;
    
    @Mock
    private AnagStationService anagStationService;
    
    @Mock
    private AnagInstitutionService anagInstitutionService;

    private LoadRegistryJob loadRegistryJob;

    @org.junit.jupiter.api.BeforeEach
    public void setUp() {
        loadRegistryJob = new LoadRegistryJob(anagPartnerService, anagStationService, anagInstitutionService);
    }

    @Test
    public void testExecuteInternal_loadsPartnersStationsAndInstitutions() throws Exception {
        // Act: trigger the job
        loadRegistryJob.executeInternal(org.mockito.Mockito.mock(JobExecutionContext.class));

        // Assert: verify that the service methods are called
        verify(anagPartnerService, times(1)).loadFromPagoPA();
        verify(anagStationService, times(1)).loadFromPagoPA();
        verify(anagInstitutionService, times(1)).loadFromPagoPA();
        verify(anagStationService, times(1)).loadAssociationsFromPagoPA();
    }
}