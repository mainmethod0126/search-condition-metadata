package io.github.mainmethod0126.search.condition.metadata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.mainmethod0126.search.condition.metadata.domain.TestHuman;
import io.github.mainmethod0126.search.condition.metadata.domain.TestLocalDateTime;
import io.github.mainmethod0126.search.condition.metadata.domain.TestOrder;
import io.github.mainmethod0126.search.condition.metadata.exception.AnnotationNotFoundException;
import io.github.mainmethod0126.search.condition.metadata.util.MetaDataGenerator;

class MetaDataGeneratorTest {

    @Test
    @DisplayName("Generates metadata from a valid domain class")
    void testGenerator_whenNormalParam_thenSuccess() throws AnnotationNotFoundException {

        String result = MetaDataGenerator.generate(TestOrder.class);

        System.out.println("metadata : " + result);

        assertThat(result).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Generates metadata from a domain class with a recursive structure")
    void testGenerator_whenRecursiveParam_thenSuccess() throws AnnotationNotFoundException {

        String result = MetaDataGenerator.generate(TestHuman.class);

        System.out.println("metadata : " + result);

        assertThat(result).isNotNull().isNotEmpty();

    }

    @Test
    @DisplayName("Generates metadata from a domain class that has a member of LocalDateTime type")
    void testGenerator_whenLocalDateTimeParam_thenSuccess() throws AnnotationNotFoundException {

        String result = MetaDataGenerator.generate(TestLocalDateTime.class);

        System.out.println("metadata : " + result);

        assertThat(result).isNotNull().isNotEmpty();

    }

}
