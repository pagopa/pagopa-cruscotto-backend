package com.nexigroup.pagopa.cruscotto.config.decoder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexigroup.pagopa.cruscotto.job.cache.Partner;
import com.nexigroup.pagopa.cruscotto.job.cache.Station;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;

public class BackOfficeDecoder implements Decoder {

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        if (response.status() != 404 && response.status() != 204) {
            if (response.body() == null) {
                return null;
            } else {
                if (type == Partner[].class) {
                    JSONArray jsonArray = responseToJsonArray(
                        Util.toString(response.body().asReader(StandardCharsets.UTF_8)),
                        "creditorInstitutionBrokers"
                    );

                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readValue(jsonArray.toString(), new TypeReference<Partner[]>() {});
                } else if (type == Station[].class) {
                    JSONArray jsonArray = responseToJsonArray(Util.toString(response.body().asReader(StandardCharsets.UTF_8)), "stations");

                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readValue(jsonArray.toString(), new TypeReference<Station[]>() {});
                }
                return new JacksonDecoder().decode(response, type);
            }
        } else {
            return Util.emptyValueOf(type);
        }
    }

    private JSONArray responseToJsonArray(String responseBody, String field) {
        JSONObject responseJsonObject = new JSONObject(responseBody);
        JSONObject jsonObject = responseJsonObject.getJSONObject(field);
        Iterator<String> x = jsonObject.keys();
        JSONArray jsonArray = new JSONArray();

        while (x.hasNext()) {
            String key = x.next();
            jsonObject.get(key);
            jsonArray.put(jsonObject.get(key));
        }

        return jsonArray;
    }
}
