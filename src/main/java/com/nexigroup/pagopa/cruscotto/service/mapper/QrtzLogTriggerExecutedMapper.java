package com.nexigroup.pagopa.cruscotto.service.mapper;

import com.nexigroup.pagopa.cruscotto.domain.QrtzLogTriggerExecuted;
import com.nexigroup.pagopa.cruscotto.service.dto.QrtzLogTriggerExecutedDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link QrtzLogTriggerExecuted} and its DTO {@link QrtzLogTriggerExecutedDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface QrtzLogTriggerExecutedMapper extends EntityMapper<QrtzLogTriggerExecutedDTO, QrtzLogTriggerExecuted> {
    @Mapping(
        expression = "java( qrtzLogTriggerExecutedDTO.getMessageException() != null ? qrtzLogTriggerExecutedDTO.getMessageException().getBytes() : null )",
        target = "messageException"
    )
    QrtzLogTriggerExecuted toEntity(QrtzLogTriggerExecutedDTO qrtzLogTriggerExecutedDTO);

    @Mapping(
        expression = "java( qrtzLogTriggerExecuted.getMessageException() != null ? new String(qrtzLogTriggerExecuted.getMessageException(), java.nio.charset.StandardCharsets.UTF_8) : null)",
        target = "messageException"
    )
    QrtzLogTriggerExecutedDTO toDto(QrtzLogTriggerExecuted qrtzLogTriggerExecuted);

    default QrtzLogTriggerExecuted fromId(Long id) {
        if (id == null) {
            return null;
        }
        QrtzLogTriggerExecuted qrtzLogTriggerExecuted = new QrtzLogTriggerExecuted();
        qrtzLogTriggerExecuted.setId(id);
        return qrtzLogTriggerExecuted;
    }
}
