package org.example;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class OpenAPIToFunctions {

    public List<JSONObject> openapiToFunctions(JSONObject openapiSpec) {
        List<JSONObject> functions = new ArrayList<>();

        JSONObject paths = (JSONObject) openapiSpec.get("paths");
        for (Object pathKey : paths.keySet()) {
            String path = (String) pathKey;
            JSONObject methods = (JSONObject) paths.get(path);
            for (Object methodKey : methods.keySet()) {
                String method = (String) methodKey;
                JSONObject specWithRef = (JSONObject) methods.get(method);

                // 1. Resolve JSON references.
                JSONParser parser = new JSONParser();
                JSONObject spec;
                try {
                    spec = (JSONObject) parser.parse(specWithRef.toJSONString());
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue; // Skip if unable to parse
                }

                // 2. Extract a name for the functions.
                String function_name = (String) spec.get("operationId");

                // 3. Extract a description and parameters.
                String desc = (String) spec.getOrDefault("description", spec.getOrDefault("summary", ""));

                JSONObject schema = new JSONObject();
                JSONObject requestBody = ((JSONObject) ((JSONObject) ((JSONObject) spec.getOrDefault("requestBody", new JSONObject()))
                        .getOrDefault("content", new JSONObject()))
                        .getOrDefault("application/json", new JSONObject()));
                if (!requestBody.isEmpty()) {
                    schema.put("requestBody", requestBody.get("schema"));
                }

                List<JSONObject> params = (List<JSONObject>) spec.getOrDefault("parameters", new ArrayList<>());
                JSONObject paramProperties = new JSONObject();
                for (JSONObject param : params) {
                    if (param.containsKey("schema")) {
                        paramProperties.put((String) param.get("name"), param.get("schema"));
                    }
                }
                JSONObject parameters = new JSONObject();
                parameters.put("type", "object");
                parameters.put("properties", paramProperties);
                schema.put("parameters", parameters);

                JSONObject functionDetails = new JSONObject();
                functionDetails.put("name", function_name);
                functionDetails.put("description", desc);
                functionDetails.put("parameters", schema);
                functions.add(functionDetails);
            }
        }

        return functions;
    }

    public static final String SYSTEM_MESSAGE = "You are a helpful assistant.Respond to the following prompt by using function_call " +
                                                 "and then summarize actions. Ask for clarification if a user request is ambiguous.";
    public static final String USER_INSTRUCTION = "Instruction: Get all the events. Then create a new event named AGI Party. Then " +
                                                   "delete event with id 2456.";
    public static final Integer MAX_CALLS = 5;

    public static final String BASE_URL = "https://us-central1-aiplatform.googleapis.com/v1";

    public String postRequest(String url, String authToken, List<String> messages) throws IOException {
        // Create a JSON object to send as the request body.

        // Create a URI for the API endpoint.
        URI uri = URI.create(url);

        // Create a POST request with the JSON object as the body.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("x-api-key", authToken)
                .POST(HttpRequest.BodyPublishers.ofString(messages.toString()))
                .build();

        // Create a HttpClient client.
        HttpClient client = HttpClient.newHttpClient();

        // Send the request and get the response.
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Check the response status code.
        if (response.statusCode() == 200) {
            // The request was successful.
            // Get the response body as a JSON object.
            String body = response.body().toString();
//            InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(body.getBytes()));
            JSONParser parser = new JSONParser();
            // Parse the JSON string into a JSONObject object
            JSONObject result = null;
            try {
                result = (JSONObject) parser.parse(body);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // Process the response body.
            System.out.println("The response body is:");
            System.out.println("response body:"+ result);
        } else {
            // The request failed.
            // Get the response body as a string.

            // Print the response status code and body.
            System.out.println("The response status code is: " + response.statusCode());
            System.out.println("The response body is: " + response.body().toString());
        }
        return response.body().toString();
    }
}