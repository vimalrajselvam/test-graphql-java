package com.vimalselvam.graphql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A class which helps to convert the given graphql file / inputstream to the
 * request payload string
 */
public class GraphqlTemplate {
    private GraphqlTemplate() {
        // Preventing instantiation
    }

    /**
     * Parses the given input stream graphql to the string suitable for the request
     * payload.
     *
     * @param inputStream - A {@link InputStream} of Graphql File
     * @param variables   - The variables in the form of {@link ObjectNode}
     * @return A string suitable for the request payload.
     * @throws IOException
     */
    public static String parseGraphql(InputStream inputStream, ObjectNode variables) throws IOException {
        String graphqlFileContent = convertInputStreamToString(inputStream);
        return convertToGraphqlString(graphqlFileContent, variables);
    }

    /**
     * Parses the given graphql file object to the string suitable for the request
     * payload.
     *
     * @param file      - A {@link File} object
     * @param variables - The variables in the form of {@link ObjectNode}
     * @return A string suitable for the request payload.
     * @throws IOException
     */
    public static String parseGraphql(File file, ObjectNode variables) throws IOException {
        String graphqlFileContent = convertInputStreamToString(new FileInputStream(file));
        return convertToGraphqlString(graphqlFileContent, variables);
    }

    private static String convertToGraphqlString(String graphql, ObjectNode variables) throws JsonProcessingException {
        ObjectMapper oMapper = new ObjectMapper();
        ObjectNode oNode = oMapper.createObjectNode();
        oNode.put("query", graphql);
        oNode.set("variables", variables);
        return oMapper.writeValueAsString(oNode);
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}
