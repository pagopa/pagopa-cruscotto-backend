package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.AnagInstitution;
import com.nexigroup.pagopa.cruscotto.repository.AnagInstitutionRepository;
import com.nexigroup.pagopa.cruscotto.job.cache.CreditorInstitution;
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

    @Override
    public void saveAll(java.util.List<CreditorInstitution> creditorInstitutions) {
        java.util.concurrent.atomic.AtomicInteger i = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.List<AnagInstitution> toSave = new java.util.ArrayList<>();
        for (CreditorInstitution ci : creditorInstitutions) {
            AnagInstitution example = new AnagInstitution();
            example.setFiscalCode(ci.getCreditorInstitutionCode());
            example.setName(null);
            example.setEnabled(null);
            AnagInstitution anagInstitution = anagInstitutionRepository.findOne(org.springframework.data.domain.Example.of(example)).orElse(new AnagInstitution());
            anagInstitution.setFiscalCode(ci.getCreditorInstitutionCode());
            anagInstitution.setName(ci.getBusinessName());
            anagInstitution.setEnabled(ci.getEnabled() != null ? ci.getEnabled() : true);
            toSave.add(anagInstitution);
            if (i.getAndIncrement() % 50 == 0) {
                anagInstitutionRepository.flush();
            }
        }
        anagInstitutionRepository.saveAll(toSave);
    }
}
