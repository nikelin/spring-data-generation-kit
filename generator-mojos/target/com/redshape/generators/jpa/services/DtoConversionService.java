
package com.redshape.generators.jpa.services;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.a5000.platform.api.annotations.generators.jpa.entities.Test;
import com.a5000.platform.api.annotations.generators.jpa.entities.TestParent;
import org.springframework.stereotype.Service;

@Service
public class DtoConversionService {

    private final static Map<Class, Method> METHODS = new HashMap<Class, Method>();

    static {
        for (Method method: DtoConversionService.class.getMethods()) {
            if (method.equals("convertToDto")) {
                continue;
            }
            if ((method.getParameterTypes().length == 0)||(method.getParameterTypes()[ 0 ] == Object.class)) {
                continue;
            }
            METHODS.put(method.getParameterTypes()[ 0 ], method);
        }
    }

    public<T >T convertToDto(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Collection) {
            return ((T) this.convertToDtoList(((Collection) value)));
        }
        Method method = METHODS.get(value.getClass());
        if (method == null) {
            throw new IllegalStateException("Conversion method not found: com.sun.codemodel.JInvocation@4490bc23");
        }
        try {
            return ((T) method.invoke(this, new Object[] {value }));
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public<T, V >List<T> convertToDtoList(Collection<V> records) {
        if (records == null) {
            throw new IllegalStateException("<null>");
        }
        List result = new ArrayList();
        for (Object record: records) {
            if (record == null) {
                continue;
            }
            result.add(this.convertToDto(record));
        }
        return ((List<T> ) result);
    }

    public com.a5000.platform.api.annotations.generators.jpa.entities.TestDTO convertToDto(Test value) {
        if (value == null) {
            return null;
        }
        com.a5000.platform.api.annotations.generators.jpa.entities.TestDTO result = new com.a5000.platform.api.annotations.generators.jpa.entities.TestDTO();
        return result;
    }

    public com.a5000.platform.api.annotations.generators.jpa.entities.TestParentDTO convertToDto(TestParent value) {
        if (value == null) {
            return null;
        }
        com.a5000.platform.api.annotations.generators.jpa.entities.TestParentDTO result = new com.a5000.platform.api.annotations.generators.jpa.entities.TestParentDTO();
        return result;
    }

}
