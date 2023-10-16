package io.github.mainmethod0126.search.condition.metadata.domain;

import io.github.mainmethod0126.search.condition.metadata.annotation.MetaData;
import io.github.mainmethod0126.search.condition.metadata.annotation.MetaDataField;

@MetaData(maxDepth = 3)
public class TestHuman {

    private String name;

    private int age;

    private String gender;

    @MetaDataField(maxDepth = 5)
    private TestHuman child;

    @MetaDataField(maxDepth = 2)
    private TestParent parent;
}
