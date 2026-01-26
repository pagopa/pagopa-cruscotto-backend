package com.nexigroup.pagopa.cruscotto.service.report.pdf.wrapper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WrapperPdfFiles
{
    String name;
    byte[] content;

}
