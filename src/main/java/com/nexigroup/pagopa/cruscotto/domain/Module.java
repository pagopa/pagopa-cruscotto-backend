package com.nexigroup.pagopa.cruscotto.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * A Module.
 */
@Entity
@Table(name = "MODULE")
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Module extends AbstractAuditingEntity<Long> implements Serializable {

	private static final long serialVersionUID = 5160338151158081264L;

	@Id
    @Column(name = "CO_ID")
    @SequenceGenerator(name = "SQDASH_MODU01", sequenceName = "SQDASH_MODU01", allocationSize = 1)
    @GeneratedValue(generator = "SQDASH_MODU01", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "TE_CODE", length = 50, nullable = false)
    private String code;
    
    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "TE_NAME", length = 256, nullable = false)
    private String name;
    
    @NotNull
    @Size(min = 1, max = 2048)
    @Column(name = "TE_DESCRIPTION", length = 2048, nullable = false)
    private String description;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_ANALYSIS_TYPE", nullable = false)
    private AnalysisType analysisType;
    
    @NotNull
    @Column(name = "FL_ALLOW_MANUAL_OUTCOME", nullable = false)
    private boolean allowManualOutcome;    
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TE_STATUS", nullable = false)
    private ModuleStatus status;
    
//    @ManyToMany(mappedBy = "modules")
//    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
//    @JsonIgnore
//    private Set<Instance> instances = new HashSet<>();
    @JsonIgnore
    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY)
    private Set<InstanceModule> instanceModules = new HashSet<>();    
    

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Module)) {
            return false;
        }
        return id != null && id.equals(((Module) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
