package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.KpiConfiguration;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link KpiConfiguration} and its DTO {@link KpiConfigurationDTO}.
 */
@Mapper(componentModel = "spring", uses = { ModuleMapper.class })
public interface KpiConfigurationMapper extends EntityMapper<KpiConfigurationDTO, KpiConfiguration> {

    @Mapping(source = "moduleCode", target = "module")
    KpiConfiguration toEntity(KpiConfigurationDTO kpiConfigurationDTO);

    @Mapping(source = "module.id", target = "moduleCode")
    KpiConfigurationDTO toDto(KpiConfiguration kpiConfiguration);

    default KpiConfiguration fromId(Long id) {
        if (id == null) {
            return null;
        }
        KpiConfiguration kpiConfiguration = new KpiConfiguration();
        kpiConfiguration.setId(id);
        return kpiConfiguration;
    }
}
