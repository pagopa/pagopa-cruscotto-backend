package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.AnagStationAnagInstitution;
import com.nexigroup.pagopa.cruscotto.domain.AnagStationAnagInstitutionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnagStationAnagInstitutionRepository extends JpaRepository<AnagStationAnagInstitution, AnagStationAnagInstitutionId> {
    // Custom query methods if needed
}
