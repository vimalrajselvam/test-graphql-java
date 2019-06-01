package com.vimalselvam.graphql;

import java.io.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.testng.Assert;
import org.testng.annotations.Test;

import okhttp3.*;

/**
 * Test
 */
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
