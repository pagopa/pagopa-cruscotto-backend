package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.AnagInstitution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnagInstitutionRepository extends JpaRepository<AnagInstitution, Long> {
    AnagInstitution findByFiscalCode(String fiscalCode);
}
