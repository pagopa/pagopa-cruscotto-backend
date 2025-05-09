package com.nexigroup.pagopa.cruscotto.service.filter;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class StationFilter implements Serializable {

    @Pattern(regexp = "^[0-9]{0,25}$")
    private String partnerId;


    @Override
    public String toString() {
        return "StationFilter{" +
            "partnerId='" + partnerId + '\'' +
            '}';
    }
}
