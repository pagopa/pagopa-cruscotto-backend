package com.nexigroup.pagopa.cruscotto.service.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelFile {
    private String fileName;
    private byte[] content;
    private String description;
}
