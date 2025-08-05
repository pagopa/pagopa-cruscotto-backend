package com.nexigroup.pagopa.cruscotto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nexigroup.pagopa.cruscotto.domain.AnagStationAnagInstitution;
import com.nexigroup.pagopa.cruscotto.domain.AnagStationAnagInstitutionId;

@Repository
public interface AnagStationAnagInstitutionRepository extends JpaRepository<AnagStationAnagInstitution, AnagStationAnagInstitutionId> {
    
    
    /**
     * @return Array with [stationId, count]
     */
    @Query("SELECT asai.anagStation.id, COUNT(asai.anagInstitution) " +
           "FROM AnagStationAnagInstitution asai " +
           "GROUP BY asai.anagStation.id")
    List<Object[]> countInstitutionsByStation();
    
}
