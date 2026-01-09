package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.AnagInstitution;
import com.nexigroup.pagopa.cruscotto.job.cache.CreditorInstitution;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagInstitutionDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstitutionIdentificationDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagInstitutionFilter;
import com.nexigroup.pagopa.cruscotto.service.filter.InstitutionFilter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface AnagInstitutionService {

	AnagInstitution findByInstitutionCode(String institutionCode);

    Page<InstitutionIdentificationDTO> findAll(InstitutionFilter filter, Pageable pageable);

    List<AnagInstitutionDTO> findAllNoPaging(AnagInstitutionFilter filter);

    Page<AnagInstitutionDTO> findAll(AnagInstitutionFilter filter, Pageable pageable);
    void saveAll(java.util.List<CreditorInstitution> creditorInstitutions);
    
    /**
     * Load institutions from PagoPA.
     */
    void loadFromPagoPA();
}
