package com.nexigroup.pagopa.cruscotto.config;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.nexigroup.pagopa.cruscotto.config.decoder.BackOfficeDecoder;
import com.nexigroup.pagopa.cruscotto.job.client.ApiKeyInterceptor;
import com.nexigroup.pagopa.cruscotto.job.client.PagoPaBackOfficeClient;
import com.nexigroup.pagopa.cruscotto.job.client.PagoPaCacheClient;
import com.nexigroup.pagopa.cruscotto.job.client.PagoPaStandInClient;
import com.nexigroup.pagopa.cruscotto.job.client.PagoPaTaxonomyClient;

import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import okhttp3.OkHttpClient;

@Configuration
public class FeignConfiguration {

    @Bean
    public PagoPaTaxonomyClient pagoPaTaxonomyClient(ApplicationProperties applicationProperties) {
        return Feign.builder()
            .logger(new Slf4jLogger(PagoPaTaxonomyClient.class))
            .logLevel(Logger.Level.BASIC)
            .contract(new SpringMvcContract())
            .client(new feign.okhttp.OkHttpClient(buildOkHttpClient(applicationProperties)))
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .target(PagoPaTaxonomyClient.class, applicationProperties.getPagoPaClient().getTaxonomy().getUrl());
    }

    @Bean
    public PagoPaBackOfficeClient pagoPaBackOfficeClient(ApplicationProperties applicationProperties, Environment environment) {
        return Feign.builder()
            .requestInterceptor(new ApiKeyInterceptor(environment))
            .logger(new Slf4jLogger(PagoPaBackOfficeClient.class))
            .logLevel(Logger.Level.BASIC)
            .contract(new SpringMvcContract())
            .client(new feign.okhttp.OkHttpClient(buildOkHttpClient(applicationProperties)))
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .target(PagoPaBackOfficeClient.class, applicationProperties.getPagoPaClient().getBackOffice().getUrl());
    }

    @Bean
    public PagoPaCacheClient pagoPaCacheClient(ApplicationProperties applicationProperties, Environment environment) {
        return Feign.builder()
            .requestInterceptor(new ApiKeyInterceptor(environment))
            .logger(new Slf4jLogger(PagoPaCacheClient.class))
            .logLevel(Logger.Level.FULL)
            .contract(new SpringMvcContract())
            .client(new feign.okhttp.OkHttpClient(buildOkHttpClient(applicationProperties)))
            .encoder(new JacksonEncoder())
            .decoder(new BackOfficeDecoder())
            .target(PagoPaCacheClient.class, applicationProperties.getPagoPaClient().getCache().getUrl());
    }

    @Bean
    public PagoPaStandInClient pagoPaStandInClient(ApplicationProperties applicationProperties, Environment environment) {
        
        return Feign.builder()
                .requestInterceptor(new ApiKeyInterceptor(environment))
                .logger(new Slf4jLogger(PagoPaStandInClient.class))
                .logLevel(Logger.Level.BASIC)
                .contract(new SpringMvcContract())
                .client(new feign.okhttp.OkHttpClient(buildOkHttpClient(applicationProperties)))
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(PagoPaStandInClient.class, applicationProperties.getPagoPaClient().getStandIn().getUrl());
    }

    private OkHttpClient buildOkHttpClient(ApplicationProperties applicationProperties) {
        return new OkHttpClient.Builder().proxy(buildProxy(applicationProperties)).build();
    }

    private Proxy buildProxy(ApplicationProperties applicationProperties) {
        Proxy proxy = null;
        if (
            StringUtils.isNotEmpty(applicationProperties.getPagoPaClient().getProxyHost()) &&
            applicationProperties.getPagoPaClient().getProxyPort() != null
        ) proxy = new Proxy(
            Proxy.Type.HTTP,
            new InetSocketAddress(
                applicationProperties.getPagoPaClient().getProxyHost(),
                applicationProperties.getPagoPaClient().getProxyPort()
            )
        );

        return proxy;
    }
}
