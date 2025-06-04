package com.nexigroup.pagopa.cruscotto.service.filter;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StationFilter implements Serializable {

	@Serial
	private static final long serialVersionUID = 4769113909045723465L;
	
	@Pattern(regexp = "^[0-9]{0,25}$")
    private String partnerId;


    @Override
    public String toString() {
        return "StationFilter{" +
            "partnerId='" + partnerId + '\'' +
            '}';
    }
}
