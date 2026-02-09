package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.AnagStationAnagInstitution;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationAnagInstitutionRepository;
import com.nexigroup.pagopa.cruscotto.service.AnagStationAnagInstitutionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AnagStationAnagInstitutionServiceImpl implements AnagStationAnagInstitutionService {

    private final AnagStationAnagInstitutionRepository anagStationAnagInstitutionRepository;

    public AnagStationAnagInstitutionServiceImpl (
        AnagStationAnagInstitutionRepository anagStationAnagInstitutionRepository
    ) {
        this.anagStationAnagInstitutionRepository = anagStationAnagInstitutionRepository;
    }

    @Override
    @Transactional
    public void saveAll(List<AnagStationAnagInstitution> associations) {
        anagStationAnagInstitutionRepository.saveAll(associations);
    }
}
