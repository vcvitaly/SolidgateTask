package io.github.vcvitaly.solidgate.task.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtil {

    private static final ObjectMapper OM = new ObjectMapper();

    public String objToString(Object obj) {
        try {
            return OM.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <K, V> Map<K, V> strToMap(String str, Class<K> keyClass, Class<V> valueClass) {
        try {
            return OM.readValue(
                    str, TypeFactory.defaultInstance()
                            .constructMapType(HashMap.class, keyClass, valueClass)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
