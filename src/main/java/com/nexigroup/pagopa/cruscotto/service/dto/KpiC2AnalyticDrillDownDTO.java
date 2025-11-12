package com.nexigroup.pagopa.cruscotto.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiC2AnalyticData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A KpiC2Evidence.
 *
 * Rappresenta le evidenze di KPI C.2,
 * con informazioni sui partner, pagamenti e notifiche.
 */
@Getter
@Setter
public class KpiC2AnalyticDrillDownDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Long id;

    @NotNull
    private Long instanceId;

    @NotNull
    private Long instanceModuleId;

    @NotNull
    private LocalDate analysisDate;

    private String partnerCf;

    @NotNull
    private Long numPayment;

    @NotNull
    private Long numNotification;

    @NotNull
    private BigDecimal percentNotification;

    private KpiC2AnalyticData kpiC2AnalyticData;

    // === Metodi standard ===


    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "KpiC2AnalyticDrillDownDTO{" +
            "id=" + id +
            ", instanceId=" + instanceId +
            ", instanceModuleId=" + instanceModuleId +
            ", analysisDate=" + analysisDate +
            ", partnerCf='" + partnerCf + '\'' +
            ", numPayment=" + numPayment +
            ", numNotification=" + numNotification +
            ", percentNotification=" + percentNotification +
            ", kpiC2AnalyticData=" + kpiC2AnalyticData +
            '}';
    }
}
