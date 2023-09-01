package io.github.mainmethod0126.search.condition.metadata.domain;


import java.util.List;
import java.util.Map;

import io.github.mainmethod0126.search.condition.metadata.annotation.MetaDataField;


public class TestOrder {

    private String description;

    // @MetaDataField Ignored Cases
    @MetaDataField(name = "king", type = "string", operators = {"=", "!="})
    private TestCustomer customer;

    private List<TestProduct> products;

    private Map<String, TestShippingInfo> shippingInfo;
}
