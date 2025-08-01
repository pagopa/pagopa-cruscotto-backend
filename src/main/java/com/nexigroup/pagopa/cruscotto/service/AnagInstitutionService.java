package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.AnagInstitution;
import com.nexigroup.pagopa.cruscotto.job.cache.CreditorInstitution;

import com.nexigroup.pagopa.cruscotto.domain.AnagInstitution;

import java.util.List;

public interface AnagInstitutionService {
    AnagInstitution findByInstitutionCode(String institutionCode);
    void saveAll(java.util.List<CreditorInstitution> creditorInstitutions);
}
