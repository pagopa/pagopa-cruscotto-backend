package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.AnagStationAnagInstitution;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationAnagInstitutionRepository;
import com.nexigroup.pagopa.cruscotto.service.AnagStationAnagInstitutionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AnagStationAnagInstitutionServiceImpl implements AnagStationAnagInstitutionService {

    @Autowired
    private AnagStationAnagInstitutionRepository anagStationAnagInstitutionRepository;

    @Override
    @Transactional
    public void saveAll(List<AnagStationAnagInstitution> associations) {
        anagStationAnagInstitutionRepository.saveAll(associations);
    }
}
