package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.IoDrilldown;
import com.nexigroup.pagopa.cruscotto.repository.IoDrilldownRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service layer for IO drilldown negative evidences.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class IoDrilldownService {
    private final IoDrilldownRepository ioDrilldownRepository;

    public List<IoDrilldown> saveAll(List<IoDrilldown> rows) {
        log.debug("Saving {} IoDrilldown negative evidence rows", rows.size());
        return ioDrilldownRepository.saveAll(rows);
    }

    @Transactional(readOnly = true)
    public List<IoDrilldown> findByAnalyticDataId(Long analyticDataId) {
        return ioDrilldownRepository.findByAnalyticDataId(analyticDataId);
    }

    @Transactional(readOnly = true)
    public List<IoDrilldown> findByInstanceModuleAndReferenceDate(Long instanceModuleId, LocalDate referenceDate) {
        return ioDrilldownRepository.findByInstanceModuleAndReferenceDate(instanceModuleId, referenceDate);
    }
}
