package com.nexigroup.pagopa.cruscotto.web.rest.util;

import org.springframework.http.HttpHeaders;
import tech.jhipster.web.util.HeaderUtil;

public class HeaderJobUtil {

    /**
     * <p>createJobPauseAlert.</p>
     *
     * @param applicationName a {@link String} object.
     * @param enableTranslation a boolean.
     * @param jobName a {@link String} object.
     * @param param a {@link String} object.
     * @return a {@link HttpHeaders} object.
     */
    public static HttpHeaders createJobPauseAlert(String applicationName, boolean enableTranslation, String jobName, String param) {
        String message = enableTranslation
            ? applicationName + "." + jobName + ".pause"
            : "A Job " + jobName + " is updated with identifier " + param;
        return tech.jhipster.web.util.HeaderUtil.createAlert(applicationName, message, param);
    }

    /**
     * <p>createJobResumeAlert.</p>
     *
     * @param applicationName a {@link String} object.
     * @param enableTranslation a boolean.
     * @param jobName a {@link String} object.
     * @param param a {@link String} object.
     * @return a {@link HttpHeaders} object.
     */
    public static HttpHeaders createJobResumeAlert(String applicationName, boolean enableTranslation, String jobName, String param) {
        String message = enableTranslation
            ? applicationName + "." + jobName + ".resume"
            : "A Job " + jobName + " is resumed with identifier " + param;
        return HeaderUtil.createAlert(applicationName, message, param);
    }

    /**
     * <p>createJobStopAlert.</p>
     *
     * @param applicationName a {@link String} object.
     * @param enableTranslation a boolean.
     * @param jobName a {@link String} object.
     * @param param a {@link String} object.
     * @return a {@link HttpHeaders} object.
     */
    public static HttpHeaders createJobStopAlert(String applicationName, boolean enableTranslation, String jobName, String param) {
        String message = enableTranslation
            ? applicationName + "." + jobName + ".stop"
            : "A Job " + jobName + " is stopped with identifier " + param;
        return HeaderUtil.createAlert(applicationName, message, param);
    }

    /**
     * <p>createJobStartAlert.</p>
     *
     * @param applicationName a {@link String} object.
     * @param enableTranslation a boolean.
     * @param jobName a {@link String} object.
     * @param param a {@link String} object.
     * @return a {@link HttpHeaders} object.
     */
    public static HttpHeaders createJobStartAlert(String applicationName, boolean enableTranslation, String jobName, String param) {
        String message = enableTranslation
            ? applicationName + "." + jobName + ".start"
            : "A Job " + jobName + " is started with identifier " + param;
        return HeaderUtil.createAlert(applicationName, message, param);
    }

    /**
     * <p>createJobUpdateAlert.</p>
     *
     * @param applicationName a {@link String} object.
     * @param enableTranslation a boolean.
     * @param jobName a {@link String} object.
     * @param param a {@link String} object.
     * @return a {@link HttpHeaders} object.
     */
    public static HttpHeaders createJobUpdateAlert(String applicationName, boolean enableTranslation, String jobName, String param) {
        String message = enableTranslation
            ? applicationName + "." + jobName + ".update"
            : "A Job " + jobName + " is started with identifier " + param;
        return HeaderUtil.createAlert(applicationName, message, param);
    }
}
