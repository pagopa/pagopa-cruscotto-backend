package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.IoDrilldown;
import com.nexigroup.pagopa.cruscotto.service.dto.IoDrilldownDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper for IoDrilldown entity to DTO.
 */
@Component
public class IoDrilldownMapper {
    public IoDrilldownDTO toDto(IoDrilldown entity) {
        if (entity == null) return null;
        IoDrilldownDTO dto = new IoDrilldownDTO();
        dto.setId(entity.getId());
        dto.setAnalyticDataId(entity.getKpiC1AnalyticData() != null ? entity.getKpiC1AnalyticData().getId() : null);
        dto.setInstanceId(entity.getInstance() != null ? entity.getInstance().getId() : null);
        dto.setInstanceModuleId(entity.getInstanceModule() != null ? entity.getInstanceModule().getId() : null);
        dto.setReferenceDate(entity.getReferenceDate());
        dto.setDataDate(entity.getDataDate());
        dto.setCfInstitution(entity.getCfInstitution());
        dto.setCfPartner(entity.getCfPartner());
        dto.setPositionsCount(entity.getPositionsCount());
        dto.setMessagesCount(entity.getMessagesCount());
        dto.setPercentage(entity.getPercentage());
        dto.setMeetsTolerance(entity.getMeetsTolerance());
        return dto;
    }
}
