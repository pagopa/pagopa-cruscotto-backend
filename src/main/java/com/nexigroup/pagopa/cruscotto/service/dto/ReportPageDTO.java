package com.nexigroup.pagopa.cruscotto.service.dto;

import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportPageDTO {
    private List<ReportListItemDTO> content;
    private Pageable pageable;
    private long totalElements;
    private int totalPages;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Pageable {
        private int pageNumber;
        private int pageSize;
        private String sort;
    }
}
