package com.nexigroup.pagopa.cruscotto.job.cache;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.PartnerStatus;
import com.nexigroup.pagopa.cruscotto.job.client.PagoPaCacheClient;
import com.nexigroup.pagopa.cruscotto.service.AnagPartnerService;
import com.nexigroup.pagopa.cruscotto.service.AnagStationService;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPartnerDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagStationDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PartnerIdentificationDTO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.ArgumentMatchers.anyList;
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class LoadRegistryJobIT {
    @Mock
    private PagoPaCacheClient pagoPaCacheClient;

    @Mock
    private AnagPartnerService anagPartnerService;

    @Mock
    private AnagStationService anagStationService;

    private LoadRegistryJob loadRegistryJob;

    @org.junit.jupiter.api.BeforeEach
    public void setUp() {
        loadRegistryJob = new LoadRegistryJob(pagoPaCacheClient, anagPartnerService, anagStationService);
    }

    @Test
    public void testExecuteInternal_loadsPartnersAndStations() throws Exception {
        // Arrange: mock the responses from pagoPaCacheClient
        Partner partner = new Partner();
        partner.setBrokerCode("PARTNER1");
        partner.setDescription("Partner One");
        partner.setEnabled(true);
        when(pagoPaCacheClient.partners()).thenReturn(new Partner[]{partner});

        Station station = new Station();
        station.setStationCode("STATION1");
        station.setBrokerCode("PARTNER1");
        station.setPrimitiveVersion(1);
        station.setIsPaymentOptionsEnabled(false);
        station.setEnabled(true);
        when(pagoPaCacheClient.stations()).thenReturn(new Station[]{station});

        // Mock findOneByFiscalCode to return a DTO with PartnerIdentification
        AnagPartnerDTO partnerDTO = new AnagPartnerDTO();
        PartnerIdentificationDTO identificationDTO = new PartnerIdentificationDTO();
        identificationDTO.setId(1L);
        identificationDTO.setFiscalCode("PARTNER1");
        partnerDTO.setPartnerIdentification(identificationDTO);
        partnerDTO.setStatus(PartnerStatus.ATTIVO);
        when(anagPartnerService.findOneByFiscalCode("PARTNER1")).thenReturn(Optional.of(partnerDTO));

        // Act: trigger the job
        loadRegistryJob.executeInternal(org.mockito.Mockito.mock(JobExecutionContext.class));

        // Assert: verify that saveAll and updateStationsCount are called as expected
        verify(anagPartnerService, atLeastOnce()).saveAll(anyList());
        verify(anagStationService, atLeastOnce()).saveAll(anyList());
        verify(anagPartnerService, atLeastOnce()).updateStationsCount(eq(1L), eq(1L));
    }
}
