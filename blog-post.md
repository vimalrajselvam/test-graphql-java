# Introducing Test GraphQL Java

I was recently asked by one of my friend how can we test the GraphQL APIs in Java. He is currently exploring `Karate's` capability, however, he doesn't want to use `Karate` just for this as they're already using `TestNG` based framework. And there is a [graphql-java](https://github.com/graphql-java/graphql-java) library which let's you to implement `GraphQL` in Java and test, but using Spring Boot. My main goal is not to introduce Spring Boot just for the sake of testing the `GraphQL` API. I wanted to keep it simple!

Goals were:

- [x] Should not bring various dependencies (this library currently depends only on `jackson`).
- [x] Should just turn the graphql file into request payload string.
- [x] Should be able to use any HTTP client library.
- [x] Should be able to use any Java testing framework.

That is how this library born. Let's directly jump in how can we use it.

## Getting Started

Adding maven dependency:

```xml
<dependency>
    <groupId>com.vimalselvam</groupId>
    <artifactId>test-graphql-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

I don't use `Gradle`, but it should be straight forward to add this library as a `Gradle` dependency.

Let's test the [Pokemon GraphQL API](https://graphql-pokemon.now.sh/). We're going to test the following query:

```graphql
query pokemon {
  pokemon(name: "Pikachu") {
    name
  }
}
```

We'll trigger this query and assert the successful response code and the response body where the `name` key contains `Pikachu`.

Take the above query and put it into a `pokemon.graphql` under `src/test/resources/graphql/` in your maven project directory.

We can load this file in our test using either of the following two ways:

- using `InputStream`:

    ```java
    InputStream iStream = getClass().getResourceAsStream("/graphql/pokemon.graphql");
    ```

- using `File`:
  
    ```java
    File file = new File("src/test/resources/graphql/pokemon.graphql");
    ```

For this example, I'm using the 2nd approach, `File`.

Once you read the file, just pass it to `GraphqlTemplate` class to parse as follows:

```java
String graphqlPayload = GraphqlTemplate.parseGraphql(file, null);
```

The 2nd argument is `variables` which is used to parameterize your GraphQL query. I'll show you how to use that in a short while, till that let's keep it `null`.

That's it! You now have the graphql query string which you can directly pass as a request payload on your preferred HTTP client library.

Let's talk about `variables`. The `GraphQL` has a feature to set some `variables` and pass those variables at run time during execution of query. For that, we'll modify our `Pokemon` query.

Open the `pokemon.graphql` file and change it as:

```graphql
query pokemon($name:String!) {
  pokemon(name: $name) {
    name
  }
}
```

Here `$name` is the variable and it accepts only `String`. The `!` operator denotes that this is mandatory variable. Let's see how we can handle this `variables` scenario in our code.

Before converting the GraphQL query to the plain string, let's create the variables:

```java
ObjectNode variables = new ObjectMapper().createObjectNode();
variables.put("name", "Pikachu");
```

Here we're using `com.fasterxml.jackson.databind.node.ObjectNode` to create variables. This `ObjectNode` can be passed as 2nd parameter in our `GraphqlTemplate` class.

```java
String graphqlPayload = GraphqlTemplate.parseGraphql(file, variables);
```

That simple it is! 

I've open sourced this library and can be found here: https://github.com/vimalrajselvam/test-graphql-java.

Contributions are welcome. If you have any thoughts or issues, kindly open an issue in the above github link.

Let's see the full example code:

```java
package com.vimalselvam.graphql;

import java.io.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.testng.Assert;
import org.testng.annotations.Test;

import okhttp3.*;

public class TestClass {
    private static final OkHttpClient client = new OkHttpClient();
    private final String graphqlUri = "https://graphql-pokemon.now.sh/graphql";

    private Response prepareResponse(String graphqlPayload) throws IOException {
        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), graphqlPayload);
        Request request = new Request.Builder().url(graphqlUri).post(body).build();
        return client.newCall(request).execute();
    }

    @Test
    public void testGraphqlWithInputStream() throws IOException {
        // Read a graphql file as an input stream
        InputStream iStream = TestClass.class.getResourceAsStream("/graphql/pokemon.graphql");

        // Create a variables to pass to the graphql query
        ObjectNode variables = new ObjectMapper().createObjectNode();
        variables.put("name", "Pikachu");

        // Now parse the graphql file to a request payload string
        String graphqlPayload = GraphqlTemplate.parseGraphql(iStream, variables);

        // Build and trigger the request
        Response response = prepareResponse(graphqlPayload);

        Assert.assertEquals(response.code(), 200, "Response Code Assertion");

        String jsonData = response.body().string();
        JsonNode jsonNode = new ObjectMapper().readTree(jsonData);
        Assert.assertEquals(jsonNode.get("data").get("pokemon").get("name").asText(), "Pikachu");
    }

    @Test
    public void testGraphqlWithFile() throws IOException {
        // Read a graphql file
        File file = new File("src/test/resources/graphql/pokemon.graphql");

        // Create a variables to pass to the graphql query
        ObjectNode variables = new ObjectMapper().createObjectNode();
        variables.put("name", "Pikachu");

        // Now parse the graphql file to a request payload string
        String graphqlPayload = GraphqlTemplate.parseGraphql(file, variables);

        // Build and trigger the request
        Response response = prepareResponse(graphqlPayload);

        Assert.assertEquals(response.code(), 200, "Response Code Assertion");

        String jsonData = response.body().string();
        JsonNode jsonNode = new ObjectMapper().readTree(jsonData);
        Assert.assertEquals(jsonNode.get("data").get("pokemon").get("name").asText(), "Pikachu");
    }

    @Test
    public void testGraphqlWithNoVariables() throws IOException {
        // Read a graphql file
        File file = new File("src/test/resources/graphql/pokemon-with-no-variable.graphql");

        // Now parse the graphql file to a request payload string
        String graphqlPayload = GraphqlTemplate.parseGraphql(file, null);

        // Build and trigger the request
        Response response = prepareResponse(graphqlPayload);

        Assert.assertEquals(response.code(), 200, "Response Code Assertion");

        String jsonData = response.body().string();
        JsonNode jsonNode = new ObjectMapper().readTree(jsonData);
        Assert.assertEquals(jsonNode.get("data").get("pokemon").get("name").asText(), "Pikachu");
    }
}
```
