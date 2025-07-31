package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.AnagInstitution;
import com.nexigroup.pagopa.cruscotto.repository.AnagInstitutionRepository;
import com.nexigroup.pagopa.cruscotto.service.AnagInstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnagInstitutionServiceImpl implements AnagInstitutionService {

    @Autowired
    private AnagInstitutionRepository anagInstitutionRepository;

    @Override
    public AnagInstitution findByInstitutionCode(String institutionCode) {
        return anagInstitutionRepository.findByFiscalCode(institutionCode);
    }
}
