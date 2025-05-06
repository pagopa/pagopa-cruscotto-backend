package com.nexigroup.pagopa.cruscotto.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.service.dto.ModuleDTO;

/**
 * Mapper for the entity {@link Module} and its DTO {@link ModuleDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ModuleMapper extends EntityMapper<ModuleDTO, Module> {

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    Module toEntity(ModuleDTO moduleDTO);

    ModuleDTO toDto(Module instance);
    

    default Module fromId(Long id) {
        if (id == null) {
            return null;
        }
        Module module = new Module();
        module.setId(id);
        return module;
    }
}
