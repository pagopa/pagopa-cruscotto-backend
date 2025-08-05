package com.nexigroup.pagopa.cruscotto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nexigroup.pagopa.cruscotto.domain.AnagInstitution;

@Repository
public interface AnagInstitutionRepository extends JpaRepository<AnagInstitution, Long> {
    List<AnagInstitution> findByFiscalCode(String fiscalCode);
}
