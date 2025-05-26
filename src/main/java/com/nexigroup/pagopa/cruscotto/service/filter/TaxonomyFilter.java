package com.nexigroup.pagopa.cruscotto.service.filter;

import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaxonomyFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = -7846373878267327L;

    @Size(max = 50)
    private String takingsIdentifier;
}
