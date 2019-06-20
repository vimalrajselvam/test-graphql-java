[![Build Status](https://travis-ci.org/vimalrajselvam/test-graphql-java.svg?branch=master)](https://travis-ci.org/vimalrajselvam/test-graphql-java)  [![Maven Central](https://img.shields.io/maven-central/v/com.vimalselvam/test-graphql-java.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.vimalselvam%22%20AND%20a:%22test-graphql-java%22)

# Test GraphQL Java

This library let's you to convert the `GraphQL` file to a simple string which can be used as a request payload using any HTTP client library.

I've written this library for the simple `GraphQL` API Testing. There is a [graphql-java](https://github.com/graphql-java/graphql-java) library which let's you to implement `GraphQL` server in Java. My main goal is not to introduce Spring Boot just for the sake of testing the `GraphQL` API. I wanted to keep it simple!

To use this library, you can download from `Maven`:

```xml
<dependency>
    <groupId>com.vimalselvam</groupId>
    <artifactId>test-graphql-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

I don't use `Gradle`, but it should be simple to add this as a gradle dependency.

## Getting Started

There are two ways to load the `GraphQL` file.

- using `InputStream`:

    ```java
    InputStream iStream = getClass().getResourceAsStream("/graphql/pokemon.graphql");
    ```

    Here the `pokemon.graphql` file in under `src/test/resources/graphql/pokemon.graphql`.

- using `File`:
  
    ```java
    File file = new File("src/test/resources/graphql/pokemon.graphql");
    ```

Once you read the file, just pass it to `GraphqlTemplate` class to parse as follows:

```java
String graphqlPayload = GraphqlTemplate.parseGraphql(file, variables);
```

Here the `variables` is the `com.fasterxml.jackson.databind.node.ObjectNode`. If no variables, you just pass `null`.

To build the `variables`, you can do:

```java
ObjectNode variables = new ObjectMapper().createObjectNode();
variables.put("name", "Pikachu");
```

Then you can use any HTTP Client and pass the `graphqlPayload` string as a body.

A sample test can be found [here](https://github.com/vimalrajselvam/test-graphql-java/blob/master/src/test/java/com/vimalselvam/graphql/TestClass.java)
