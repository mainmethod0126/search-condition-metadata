package io.github.mainmethod0126.search.condition.metadata;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.github.mainmethod0126.annotation.scanner.AnnotationScanner;
import io.github.mainmethod0126.search.condition.metadata.annotation.MetaData;
import io.github.mainmethod0126.search.condition.metadata.util.MetaDataGenerator;

public class MetaDataContainer implements ConcurrentMap<String, String> {

    private String basePackage = "";

    private static final MetaDataContainer instance = new MetaDataContainer();

    private ConcurrentMap<String, String> metadatas = new ConcurrentHashMap<>();

    private MetaDataContainer() {
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public static MetaDataContainer getInstance() {
        return instance;
    }

    public void scan() throws ClassNotFoundException {

        if (this.basePackage.isEmpty()) {
            throw new IllegalArgumentException(
                    "basePackage is empty, Please set the value of the basePackage variable first, then try again");
        }

        // scan with annotation scanner
        AnnotationScanner annotationScanner = new AnnotationScanner(this.basePackage);
        List<Class<?>> scannedClasses = annotationScanner.scanClass(MetaData.class);

        for (Class<?> clazz : scannedClasses) {

            Annotation annotation = clazz.getAnnotation(MetaData.class);
            MetaData metaDataAnno = (MetaData) annotation;

            // If no explicitly specified key value exists, the default value is the
            // package-qualified class name.
            String key = metaDataAnno.key().isEmpty() ? clazz.getName() : metaDataAnno.key();

            if (this.metadatas.containsKey(key)) {
                throw new IllegalStateException(
                        "The '@MetaData' key is already in use. Please specify a different value to avoid duplication. Duplicate key values : "
                                + key);
            }

            this.metadatas.put(key, MetaDataGenerator.generate(clazz));
        }
    }

    @Override
    public int size() {
        return metadatas.size();
    }

    @Override
    public boolean isEmpty() {
        return metadatas.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return metadatas.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return metadatas.containsValue(value);
    }

    @Override
    public String get(Object key) {
        return metadatas.get(key);
    }

    @Override
    public String put(String key, String value) {
        return metadatas.put(key, value);
    }

    @Override
    public String remove(Object key) {
        return metadatas.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        metadatas.putAll(m);
    }

    @Override
    public void clear() {
        metadatas.clear();
    }

    @Override
    public Set<String> keySet() {
        return metadatas.keySet();
    }

    @Override
    public Collection<String> values() {
        return metadatas.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return metadatas.entrySet();
    }

    @Override
    public String putIfAbsent(String key, String value) {
        return metadatas.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return metadatas.remove(key, value);
    }

    @Override
    public boolean replace(String key, String oldValue, String newValue) {
        return metadatas.replace(key, oldValue, newValue);
    }

    @Override
    public String replace(String key, String value) {
        return metadatas.replace(key, value);
    }
}
