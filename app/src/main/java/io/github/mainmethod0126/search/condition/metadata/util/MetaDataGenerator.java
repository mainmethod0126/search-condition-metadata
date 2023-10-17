package io.github.mainmethod0126.search.condition.metadata.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.mainmethod0126.search.condition.metadata.annotation.MetaData;
import io.github.mainmethod0126.search.condition.metadata.annotation.MetaDataField;
import io.github.mainmethod0126.search.condition.metadata.exception.AnnotationNotFoundException;

/**
 * This utility class, MetaDataGenerator, is responsible for generating
 * JSON-formatted metadata
 * for searching based on domain classes. It includes methods for converting
 * field information to JSON format,
 * checking recursion depth, and handling custom annotations for metadata
 * fields.
 */
public class MetaDataGenerator {

    private MetaDataGenerator() {
        // utility class
    }

    /**
     * Receives a domain class, creates and returns JSON-formatted metadata for
     * searching
     * 
     * @param clazz Domain class
     * @return json Type MetaData
     * @throws AnnotationNotFoundException
     */
    public static String generate(Class<?> clazz) throws AnnotationNotFoundException {

        Map<String, Integer> scanDepthCounterMap = new HashMap<>();

        JsonArray fields = new JsonArray();

        if (clazz.getAnnotation(MetaData.class) == null) {
            throw new AnnotationNotFoundException("this class does not utilize the mandatory annotation MetaData");
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                toJson(clazz, fields, "", field, scanDepthCounterMap);
            }
        }

        return fields.toString();
    }

    /**
     * Counts the depth of the field
     * 
     * @param field               The field to count the depth of
     * @param scanDepthCounterMap A map to store the depth counter
     */
    private static void countDepth(Field field, Map<String, Integer> scanDepthCounterMap) {

        String key = field.getType().getName() + field.getName();

        if (scanDepthCounterMap.containsKey(key)) {
            int depthCount = scanDepthCounterMap.get(key);
            scanDepthCounterMap.replace(key, ++depthCount);
        } else {
            scanDepthCounterMap.put(key, 0);
        }
    }

    /**
     * It checks whether the recursion counter for the given field has reached its
     * limit.
     * 
     * @param rootClazz           The root class
     * @param field               The field to check
     * @param scanDepthCounterMap A map to store the depth counter
     * @return If the recursion counter has reached, it returns true; otherwise, it
     *         returns false.
     */
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
     * Converts a field to metadata JSON format
     * 
     * @param rootClazz           The root class
     * @param fields              The JsonArray to store the fields
     * @param parentName          The parent field's name
     * @param field               The field to convert
     * @param scanDepthCounterMap A map to store the depth counter
     */
    private static void toJson(Class<?> rootClazz, JsonArray fields, String parentName, Field field,
            Map<String, Integer> scanDepthCounterMap) {

        if (Modifier.isStatic(field.getModifiers())) {
            return;
        }

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

    /**
     * Overwrites custom values in JsonObject based on MetaDataField annotation
     * 
     * @param annotation The MetaDataField annotation
     * @param jsonObject The JsonObject to update
     * @param parentName The parent field's name
     */
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

    /**
     * Checks if the type is numeric
     * 
     * @param type The class type to check
     * @return True if the type is numeric, otherwise false
     */
    private static boolean isNumeric(Class<?> type) {

        return type == int.class || type == double.class || type == float.class || type == long.class
                || type == short.class || type == Integer.class || type == Double.class || type == Float.class
                || type == Long.class || type == Short.class;

    }

}
