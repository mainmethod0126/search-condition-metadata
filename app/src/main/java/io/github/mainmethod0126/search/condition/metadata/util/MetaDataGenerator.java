package io.github.mainmethod0126.search.condition.metadata.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.mainmethod0126.search.condition.metadata.annotation.MetaData;
import io.github.mainmethod0126.search.condition.metadata.annotation.MetaDataField;

public class MetaDataGenerator {

    private MetaDataGenerator() {
        // util class
    }

    /**
     * Receives a domain class, creates and returns JSON-formatted metadata for
     * searching
     * 
     * @param clazz Domain class
     * @return json Type MetaData
     */
    public static String generate(Class<?> clazz) {

        Map<String, Integer> scanDepthCounterMap = new HashMap<>();

        JsonArray fields = new JsonArray();

        for (Field field : clazz.getDeclaredFields()) {
            if (clazz.getAnnotation(MetaData.class) != null) {
                toJson(clazz, fields, "", field, scanDepthCounterMap);
            }
        }

        return fields.toString();
    }

    private static void countDepth(Field field, Map<String, Integer> scanDepthCounterMap) {

        String key = field.getType().getName() + field.getName();

        if (scanDepthCounterMap.containsKey(key)) {
            int depthCount = scanDepthCounterMap.get(key);
            scanDepthCounterMap.replace(key, ++depthCount);
        } else {
            scanDepthCounterMap.put(key, 0);
        }
    }

    private static boolean isEnd(Class<?> rootClazz, Field field, Map<String, Integer> scanDepthCounterMap) {

        MetaDataField metaDataFieldAnno = field.getAnnotation(MetaDataField.class);
        MetaData metaDataAnno = rootClazz.getAnnotation(MetaData.class);
        String key = field.getType().getName() + field.getName();

        if (metaDataFieldAnno != null) {
            return metaDataFieldAnno.maxDepth() <= scanDepthCounterMap.get(key);
        } else {
            return metaDataAnno.maxDepth() <= scanDepthCounterMap.get(key);
        }
    }

    /**
     * 
     * @param fields
     * @param parentName
     * @param field
     * @param scanDepthCounterMap
     */
    private static void toJson(Class<?> rootClazz, JsonArray fields, String parentName, Field field,
            Map<String, Integer> scanDepthCounterMap) {

        JsonObject jsonObject = new JsonObject();

        Class<?> type = field.getType();

        if (!parentName.isEmpty()) {
            parentName += ".";
        }

        if (isNumeric(type)) {

            jsonObject.addProperty("name", parentName + field.getName());

            jsonObject.addProperty("type", "number");

            JsonArray operators = new JsonArray();
            operators.add("=");
            operators.add("!=");
            operators.add(">=");
            operators.add("<=");
            operators.add(">");
            operators.add("<");

            jsonObject.add("operators", operators);

            Annotation annotation = field.getAnnotation(MetaDataField.class);
            if (annotation != null) {
                overwriteCustomValue(annotation, jsonObject, parentName);
            }

            fields.add(jsonObject);

        } else if (type == String.class || type.isEnum()) {

            jsonObject.addProperty("name", parentName + field.getName());

            jsonObject.addProperty("type", "string");

            JsonArray operators = new JsonArray();
            operators.add("=");
            operators.add("!=");
            operators.add("in");
            operators.add("not in");
            operators.add("regex");
            operators.add("wildcard");

            jsonObject.add("operators", operators);

            Annotation annotation = field.getAnnotation(MetaDataField.class);
            if (annotation != null) {
                overwriteCustomValue(annotation, jsonObject, parentName);
            }

            fields.add(jsonObject);

        } else if (Collection.class.isAssignableFrom(type)) {

            Class<?> elementType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

            for (Field f : elementType.getDeclaredFields()) {
                toJson(rootClazz, fields, parentName + field.getName(), f, scanDepthCounterMap);
            }

        } else if (Map.class.isAssignableFrom(type)) {

            /*
             * The key value must always be in the String format
             */
            Class<?> valueType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1];

            for (Field f : valueType.getDeclaredFields()) {
                toJson(rootClazz, fields, parentName + field.getName(), f, scanDepthCounterMap);
            }

        } else {

            countDepth(field, scanDepthCounterMap);

            if (isEnd(rootClazz, field, scanDepthCounterMap)) {
                return;
            }

            for (Field f : type.getDeclaredFields()) {
                toJson(rootClazz, fields, parentName + field.getName(), f, scanDepthCounterMap);
            }
        }
    }

    private static void overwriteCustomValue(Annotation annotation, JsonObject jsonObject, String parentName) {

        MetaDataField metaDataField = (MetaDataField) annotation;

        if (!metaDataField.name().isBlank()) {
            jsonObject.addProperty("name", parentName + metaDataField.name());
        }

        if (!metaDataField.type().isBlank()) {
            jsonObject.addProperty("type", metaDataField.type());
        }

        JsonArray operators = new JsonArray();

        for (String operator : Arrays.asList(metaDataField.operators())) {
            operators.add(operator);
        }

        if (!operators.isEmpty()) {
            jsonObject.add("operators", operators);
        }

    }

    private static boolean isNumeric(Class<?> type) {

        return type == int.class || type == double.class || type == float.class || type == long.class
                || type == short.class || type == Integer.class || type == Double.class || type == Float.class
                || type == Long.class || type == Short.class;

    }

}
