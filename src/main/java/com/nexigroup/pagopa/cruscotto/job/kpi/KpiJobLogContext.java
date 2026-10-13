package com.nexigroup.pagopa.cruscotto.job.kpi;

import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import java.util.StringJoiner;
import org.slf4j.MDC;

public final class KpiJobLogContext implements AutoCloseable {

    private static final String EVENT_ACTION = "event_action";
    private static final String CTX_DETAILS = "ctx_details";

    private final String previousEventAction;
    private final String previousContextDetails;

    private KpiJobLogContext(String eventAction, String contextDetails) {
        previousEventAction = MDC.get(EVENT_ACTION);
        previousContextDetails = MDC.get(CTX_DETAILS);
        MDC.put(EVENT_ACTION, eventAction);
        MDC.put(CTX_DETAILS, contextDetails);
    }

    public static KpiJobLogContext open(String eventAction, InstanceDTO instance) {
        StringJoiner contextDetails = new StringJoiner(";");
        contextDetails.add("instanceId=" + instance.getId());
        contextDetails.add("instanceNumber=" + instance.getInstanceIdentification());
        contextDetails.add("analysisPeriodStart=" + instance.getAnalysisPeriodStartDate());
        contextDetails.add("analysisPeriodEnd=" + instance.getAnalysisPeriodEndDate());
        return new KpiJobLogContext(eventAction, contextDetails.toString());
    }

    @Override
    public void close() {
        restore(EVENT_ACTION, previousEventAction);
        restore(CTX_DETAILS, previousContextDetails);
    }

    private static void restore(String key, String value) {
        if (value == null) {
            MDC.remove(key);
        } else {
            MDC.put(key, value);
        }
    }
}