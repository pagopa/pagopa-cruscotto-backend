package com.nexigroup.pagopa.cruscotto.config;

import com.nexigroup.pagopa.cruscotto.job.client.PagoPaClient;
import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
public class FeignConfiguration {

    @Bean
    public PagoPaClient pagoPaClient(ApplicationProperties applicationProperties) {
       return Feign
            .builder()
            .logger(new Slf4jLogger(PagoPaClient.class))
            .logLevel(Logger.Level.BASIC)
            .contract(new SpringMvcContract())
            .client(new feign.okhttp.OkHttpClient(buildOkHttpClient(applicationProperties)))
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .target(PagoPaClient.class, applicationProperties.getPagoPaClient().getHost());
    }

    private OkHttpClient buildOkHttpClient(ApplicationProperties applicationProperties) {
        return new OkHttpClient.Builder().proxy(buildProxy(applicationProperties)).build();
    }

    private Proxy buildProxy(ApplicationProperties applicationProperties) {
        Proxy proxy = null;
        if (StringUtils.isNotEmpty(applicationProperties.getPagoPaClient().getProxyHost()) && applicationProperties.getPagoPaClient().getProxyPort() != null) proxy =
            new Proxy(
                Proxy.Type.HTTP,
                new InetSocketAddress(applicationProperties.getPagoPaClient().getProxyHost(), applicationProperties.getPagoPaClient().getProxyPort())
            );

        return proxy;
    }
}
