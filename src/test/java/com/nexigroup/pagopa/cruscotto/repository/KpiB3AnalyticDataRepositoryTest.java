package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB3AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.KpiB3DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.KpiB3Result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class KpiB3AnalyticDataRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private KpiB3AnalyticDataRepository repository;

    private Instance instance;
    private InstanceModule instanceModule;
    private AnagStation station;
    private KpiB3Result kpiResult;
    private KpiB3DetailResult detailResult;

    @BeforeEach
    void setUp() {
        // Crea entità di test
        instance = new Instance();
        instance.setInstanceIdentification("TEST_INSTANCE");
        instance = entityManager.persistAndFlush(instance);

        instanceModule = new InstanceModule();
        instanceModule.setInstance(instance);
        instanceModule = entityManager.persistAndFlush(instanceModule);

        station = new AnagStation();
        station.setName("TEST_STATION");
        station = entityManager.persistAndFlush(station);

        kpiResult = new KpiB3Result();
        kpiResult.setInstance(instance);
        kpiResult.setInstanceModule(instanceModule);
        kpiResult.setAnalysisDate(LocalDate.now());
        kpiResult = entityManager.persistAndFlush(kpiResult);

        detailResult = new KpiB3DetailResult();
        detailResult.setInstance(instance);
        detailResult.setInstanceModule(instanceModule);
        detailResult.setAnagStation(station);
        detailResult.setKpiB3Result(kpiResult);
        detailResult.setAnalysisDate(LocalDate.now());
        detailResult.setTotalIncidents(0);
        detailResult.setTotalEvents(0);
        detailResult.setOutcome(true);
        detailResult = entityManager.persistAndFlush(detailResult);
    }

    @Test
    void shouldFindAllByInstanceModuleIdOrderByEventTimestampDesc() {
        // Given
        KpiB3AnalyticData analyticData1 = createAnalyticData("EVENT1", LocalDateTime.now().minusMinutes(30));
        KpiB3AnalyticData analyticData2 = createAnalyticData("EVENT2", LocalDateTime.now());
        
        entityManager.persistAndFlush(analyticData1);
        entityManager.persistAndFlush(analyticData2);

        // When
        List<KpiB3AnalyticData> results = repository.findAllByInstanceModuleIdOrderByEventTimestampDesc(instanceModule.getId());

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getEventTimestamp()).isAfter(results.get(1).getEventTimestamp());
    }

    @Test
    void shouldFindAllByDetailResultIdOrderByEventTimestampDesc() {
        // Given
        KpiB3AnalyticData analyticData1 = createAnalyticData("EVENT1", LocalDateTime.now().minusMinutes(30));
        KpiB3AnalyticData analyticData2 = createAnalyticData("EVENT2", LocalDateTime.now());
        
        entityManager.persistAndFlush(analyticData1);
        entityManager.persistAndFlush(analyticData2);

        // When
        List<KpiB3AnalyticData> results = repository.findAllByDetailResultIdOrderByEventTimestampDesc(detailResult.getId());

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getEventTimestamp()).isAfter(results.get(1).getEventTimestamp());
    }

    @Test
    void shouldDeleteAllByInstanceModuleId() {
        // Given
        KpiB3AnalyticData analyticData = createAnalyticData("EVENT1", LocalDateTime.now());
        entityManager.persistAndFlush(analyticData);

        // When
        int deletedCount = repository.deleteAllByInstanceModuleId(instanceModule.getId());

        // Then
        assertThat(deletedCount).isEqualTo(1);
        assertThat(repository.findAll()).isEmpty();
    }

    private KpiB3AnalyticData createAnalyticData(String eventId, LocalDateTime timestamp) {
        KpiB3AnalyticData analyticData = new KpiB3AnalyticData();
        analyticData.setInstance(instance);
        analyticData.setInstanceModule(instanceModule);
        analyticData.setAnagStation(station);
        analyticData.setKpiB3DetailResult(detailResult);
        analyticData.setEventId(eventId);
        analyticData.setEventType("STAND_IN");
        analyticData.setEventTimestamp(timestamp);
        analyticData.setStandInCount(1);
        return analyticData;
    }
}