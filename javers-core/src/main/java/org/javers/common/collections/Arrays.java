package org.javers.common.collections;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.javers.common.validation.Validate.argumentIsNotNull;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * @author pawel szymczyk
 */
public class Arrays {

    /**
     * @return index -> value
     */
    public static <T> Map<Integer, T> asMap(Object array) {
        argumentIsNotNull(array);

        Map<Integer, T> result = new HashMap<>();

        for (int i=0 ;i< Array.getLength(array); i++) {
            result.put(i, (T) Array.get(array, i));
        }

        return result;
    } 
    /**
     * @return new list with elements from array
     * @throws java.lang.IllegalArgumentException
     */
    public static List<Object> asList(Object array) {
        if (!array.getClass().isArray()) {
            throw new IllegalArgumentException(array.getClass().getSimpleName() + "is not array");
        }

        List<Object> list = new ArrayList<>();

        for (int i=0 ;i< Array.getLength(array); i++) {
            list.add(Array.get(array, i));
        }

        return list;
    }
}
