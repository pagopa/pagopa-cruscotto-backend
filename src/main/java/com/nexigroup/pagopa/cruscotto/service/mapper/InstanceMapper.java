package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Instance} and its DTO {@link InstanceDTO}.
 */
@Mapper(componentModel = "spring", uses = { AuthUserMapper.class, AnagPartnerMapper.class })
public interface InstanceMapper extends EntityMapper<InstanceDTO, Instance> {
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(source = "partnerId", target = "partner")
    @Mapping(source = "assignedUserId", target = "assignedUser")
    Instance toEntity(InstanceDTO instanceDTO);

    @Mapping(source = "partner.id", target = "partnerId")
    @Mapping(source = "partner.name", target = "partnerName")
    @Mapping(source = "partner.fiscalCode", target = "partnerFiscalCode")
    @Mapping(source = "assignedUser.id", target = "assignedUserId")
    @Mapping(source = "assignedUser.firstName", target = "assignedFirstName")
    @Mapping(source = "assignedUser.lastName", target = "assignedLastName")
    @Mapping(target = "latestCompletedReportId", ignore = true)
    InstanceDTO toDto(Instance instance);

    default Instance fromId(Long id) {
        if (id == null) {
            return null;
        }
        Instance instance = new Instance();
        instance.setId(id);
        return instance;
    }
}
