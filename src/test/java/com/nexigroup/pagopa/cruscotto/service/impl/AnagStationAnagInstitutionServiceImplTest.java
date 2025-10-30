package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.AnagStationAnagInstitution;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationAnagInstitutionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;


import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnagStationAnagInstitutionServiceImpl Tests")
class AnagStationAnagInstitutionServiceImplTest   {

    @Mock
    private AnagStationAnagInstitutionRepository repository;

    @InjectMocks
    private AnagStationAnagInstitutionServiceImpl service;

    @Test
    @DisplayName("Test saveAll calls repository saveAll")
    void testSaveAll() {
        // Dati di test
        AnagStationAnagInstitution entity1 = new AnagStationAnagInstitution();
        AnagStationAnagInstitution entity2 = new AnagStationAnagInstitution();
        List<AnagStationAnagInstitution> list = Arrays.asList(entity1, entity2);

        // Chiamata al metodo da testare
        service.saveAll(list);

        // Verifica che il repository sia stato chiamato correttamente
        verify(repository, times(1)).saveAll(list);
    }

}
