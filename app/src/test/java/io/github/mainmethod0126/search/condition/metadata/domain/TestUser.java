package io.github.mainmethod0126.search.condition.metadata.domain;

import io.github.mainmethod0126.search.condition.metadata.annotation.MetaDataField;

public class TestUser {

    @MetaDataField(name = "uuid", type = "number", operators = {"=", "!="})
    private Long id;

    private String name;

}
