package com.eden.orchid.api.converters;

import com.eden.common.util.EdenPair;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FlexibleMapConverter implements TypeConverter<Map> {

    @Inject
    public FlexibleMapConverter() {
    }

    public Class<Map> resultClass() {
        return Map.class;
    }

    @Override
    public EdenPair<Boolean, Map> convert(Object object) {
        return convert(object, null);
    }

    public EdenPair<Boolean, Map> convert(Object object, String keyName) {
        if(object != null) {
            if (object instanceof Map) {
                return new EdenPair<>(true, (Map) object);
            }
            else if(object instanceof JSONObject) {
                return new EdenPair<>(true, (Map) ((JSONObject) object).toMap());
            }
            else {
                return new EdenPair<>(false, (Map) Collections.singletonMap(null, object));
            }
        }

        return new EdenPair<>(false, (Map) new HashMap());
    }

}
