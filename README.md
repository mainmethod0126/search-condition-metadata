<!-- TOC -->

- [Generate MetaData](#generate-metadata)
  - [Reason for production](#reason-for-production)
  - [Usage](#usage)
    - [Add dependency](#add-dependency)
      - [for Gradle](#for-gradle)
      - [for Maven](#for-maven)
    - [Example](#example)
      - [@MetaData Annotation](#metadata-annotation)
      - [Default](#default)
      - [Specifying a Value Directly Using @MetaDataField](#specifying-a-value-directly-using-metadatafield)
      - [Store metadata using a container](#store-metadata-using-a-container)
        - [Default](#default-1)
        - [Result](#result)
      - [Use `@MetaData` key()](#use-metadata-key)
        - [Result](#result-1)

<!-- /TOC -->
<!-- /TOC -->

# Generate MetaData

This project is a JAVA library that assists the server in creating and providing additional information related to search criteria when clients use the search API, making it easier for clients to create search conditions.

## Reason for production

I created this to send search metadata information about domains from the server, so that client-side developers can easily generate search filters for the respective domains. This way, it can potentially reduce the workload for client developers.

---

## Usage

### Add dependency

[Go to 🚀 maven central repository](https://central.sonatype.com/artifact/io.github.mainmethod0126/search-condition-metadata)

#### for Gradle

```groovy
implementation group: 'io.github.mainmethod0126', name: 'search-condition-metadata', version: '0.1.0'
```

- short

```groovy
implementation 'io.github.mainmethod0126:search-condition-metadata:0.1.0'
```

- kotlin

```kotlin
implementation("io.github.mainmethod0126:search-condition-metadata:0.1.0")
```

#### for Maven

```xml
<dependency>
    <groupId>io.github.mainmethod0126</groupId>
    <artifactId>search-condition-metadata</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Example

Here is an example of metadata generation for the 'TestOrder' domain class.

#### @MetaData Annotation

The domain class intending to generate metadata must use the '@MetaData' annotation as a mandatory requirement.

#### Default

```java
public class MetaDataGeneratorTest {

    @Test
    @DisplayName("Generates metadata from a valid domain class")
    public void testGenerator_whenNormalParam_thenSuccess() {

        String result = MetaDataGenerator.generate(TestOrder.class);

        System.out.println("metadata : " + result);

        assertThat(result).isNotNull().isNotEmpty();

    }

}
```

**Result**

```bash
metadata : [
  {
    "name": "description",
    "type": "string",
    "operators": ["=", "!=", "in", "not in", "regex", "wildcard"]
  },
  {
    "name": "customer.user.id",
    "type": "number",
    "operators": ["=", "!=", ">=", "<=", ">", "<"]
  },
  {
    "name": "customer.user.name",
    "type": "string",
    "operators": ["=", "!=", "in", "not in", "regex", "wildcard"]
  },
  {
    "name": "customer.description",
    "type": "string",
    "operators": ["=", "!=", "in", "not in", "regex", "wildcard"]
  },
  {
    "name": "products.name",
    "type": "string",
    "operators": ["=", "!=", "in", "not in", "regex", "wildcard"]
  },
  {
    "name": "products.price",
    "type": "number",
    "operators": ["=", "!=", ">=", "<=", ">", "<"]
  },
  {
    "name": "shippingInfo.productId",
    "type": "number",
    "operators": ["=", "!=", ">=", "<=", ">", "<"]
  },
  {
    "name": "shippingInfo.quantity",
    "type": "string",
    "operators": ["=", "!=", "in", "not in", "regex", "wildcard"]
  },
  {
    "name": "shippingInfo.wrapping.style",
    "type": "string",
    "operators": ["=", "!=", "in", "not in", "regex", "wildcard"]
  }
]
```

#### Specifying a Value Directly Using @MetaDataField

You can use the @MetaDataField annotation when you want to use a separate value other than the one that defaults.

> The metadata field annotation is only applied to the last primitive-type field of the domain object. If the annotation is added to a non-primitive type, it will not work and will be ignored.

**Proper Functioning**

```java
@MetaData
public class TestUser {

    @MetaDataField(name = "uuid", type = "number", operators = {"=", "!="})
    private Long id;

    private String name;

}
```

**Ignored**

```java
@MetaData
public class TestOrder {

    private String description;

    // @MetaDataField Ignored Cases
    @MetaDataField(name = "king", type = "string", operators = {"=", "!="})
    private TestCustomer customer;

    private List<TestProduct> products;

    private Map<String, TestShippingInfo> shippingInfo;
}
```

**Result**

```text
metadata : [
  {
    "name": "description",
    "type": "string",
    "operators": ["=", "!=", "in", "not in", "regex", "wildcard"]
  },
  { "name": "customer.user.uuid", "type": "number", "operators": ["=", "!="] }, <---- The point where the @MetaDataField was applied
  {
    "name": "customer.user.name",
    "type": "string",
    "operators": ["=", "!=", "in", "not in", "regex", "wildcard"]
  },
  {
    "name": "customer.description",
    "type": "string",
    "operators": ["=", "!=", "in", "not in", "regex", "wildcard"]
  },
  {
    "name": "products.name",
    "type": "string",
    "operators": ["=", "!=", "in", "not in", "regex", "wildcard"]
  },
  {
    "name": "products.price",
    "type": "number",
    "operators": ["=", "!=", ">=", "<=", ">", "<"]
  },
  {
    "name": "shippingInfo.productId",
    "type": "number",
    "operators": ["=", "!=", ">=", "<=", ">", "<"]
  },
  {
    "name": "shippingInfo.quantity",
    "type": "string",
    "operators": ["=", "!=", "in", "not in", "regex", "wildcard"]
  },
  {
    "name": "shippingInfo.wrapping.style",
    "type": "string",
    "operators": ["=", "!=", "in", "not in", "regex", "wildcard"]
  }
]
```

#### Store metadata using a container

You can store the generated MetaData in the MetaDataContainer along with a unique key and retrieve it based on the key when needed.

##### Default

```java
@MetaData
public class Order {

    @MetaDataField(name = "redefine_description", type = "number", operators = { "=", "!=", ">" })
    private String description;

    private Product product;

    private Customer customer;
}
```

<Callout type="info" emoji="ℹ️">
  If you do not specify a separate key, the default key is set to the package including the class name.
</Callout>

<Callout type="info" emoji="ℹ️">
  The current example was based on a project using the Spring framework, but it can also be used in projects that do not use the Spring framework.
</Callout>

```java
@SpringBootApplication
public class SpringSearchConditionMetadataGeneratorSampleApplication {

	public static void main(String[] args) throws ClassNotFoundException {
		SpringApplication.run(SpringSearchConditionMetadataGeneratorSampleApplication.class, args);

		MetaDataContainer metaDataContainer = MetaDataContainer.getInstance();
		metaDataContainer.setBasePackage("io.github.mainmethod0126.springsearchconditionmetadatageneratorsample");
		metaDataContainer.scan();

        // Since no separate key value was specified for the @MetaData annotation used in the Order class, it attempts to look up by the name of the Order class.
        System.out.println("metadata : " + metaDataContainer.get(Order.class.getName()));
	}
}
```

##### Result

```text
metadata :[
  {
    "name": "redefine_description",
    "type": "number",
    "operators": ["=", "!=", ">"]
  },
  {
    "name": "product.name",
    "type": "string",
    "operators": ["=", "!=", "in", "not in", "regex", "wildcard"]
  },
  {
    "name": "product.price",
    "type": "number",
    "operators": ["=", "!=", ">=", "<=", ">", "<"]
  },
  {
    "name": "customer.user.id",
    "type": "number",
    "operators": ["=", "!=", ">=", "<=", ">", "<"]
  },
  {
    "name": "customer.user.name",
    "type": "string",
    "operators": ["=", "!=", "in", "not in", "regex", "wildcard"]
  },
  {
    "name": "customer.description",
    "type": "string",
    "operators": ["=", "!=", "in", "not in", "regex", "wildcard"]
  }
]

```

#### Use `@MetaData` key()

```java
@MetaData(key = "test_order")
public class Order {

    @MetaDataField(name = "redefine_description", type = "number", operators = { "=", "!=", ">" })
    private String description;

    private Product product;

    private Customer customer;
}
```

```java
@SpringBootApplication
public class SpringSearchConditionMetadataGeneratorSampleApplication {

	public static void main(String[] args) throws ClassNotFoundException {
		SpringApplication.run(SpringSearchConditionMetadataGeneratorSampleApplication.class, args);

		MetaDataContainer metaDataContainer = MetaDataContainer.getInstance();
		metaDataContainer.setBasePackage("io.github.mainmethod0126.springsearchconditionmetadatageneratorsample");
		metaDataContainer.scan();

        // Since a separate key value was specified for the @MetaData annotation used in the Order class, it looks up by that key value.
        System.out.println("metadata : " + metaDataContainer.get("test_order"));
	}
}
```

##### Result

```text
metadata :[
  {
    "name": "redefine_description",
    "type": "number",
    "operators": ["=", "!=", ">"]
  },
  {
    "name": "product.name",
    "type": "string",
    "operators": ["=", "!=", "in", "not in", "regex", "wildcard"]
  },
  {
    "name": "product.price",
    "type": "number",
    "operators": ["=", "!=", ">=", "<=", ">", "<"]
  },
  {
    "name": "customer.user.id",
    "type": "number",
    "operators": ["=", "!=", ">=", "<=", ">", "<"]
  },
  {
    "name": "customer.user.name",
    "type": "string",
    "operators": ["=", "!=", "in", "not in", "regex", "wildcard"]
  },
  {
    "name": "customer.description",
    "type": "string",
    "operators": ["=", "!=", "in", "not in", "regex", "wildcard"]
  }
]

```
