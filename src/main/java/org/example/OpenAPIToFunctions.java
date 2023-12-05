package org.example;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OpenAPIToFunctions {

    public static final String SYSTEM_MESSAGE = "You are a helpful assistant. Respond to the instruction prompt by using the functions listed. And then summarize actions. Ask for clarification if a user request is ambiguous.";
    public static final String USER_INSTRUCTION = "Instruction: Get all quotes. Then create a new quote named AGI Party. Then " +
            "delete quote with id 2456. Generate the functions in a java controller class, the service class, and pojo data class.";
    public static final Integer MAX_CALLS = 1;

    public static final String BASE_URL = "https://us-central1-aiplatform.googleapis.com/v1/projects/yanni-test3/locations/us-central1/publishers/google/models/text-bison:predict";

    public List<JSONObject> openapiToFunctions(JSONObject openapiSpec) {
        List<JSONObject> functions = new ArrayList<>();

        JSONObject paths = (JSONObject) openapiSpec.get("paths");
        JSONObject schemas = new JSONObject();
        schemas.put("components", openapiSpec.getOrDefault("components", new JSONObject()));
        functions.add(schemas);
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
                JSONObject requestBody = (JSONObject) spec.getOrDefault("requestBody", new JSONObject());

                List<JSONObject> params = (List<JSONObject>) spec.getOrDefault("parameters", new ArrayList<>());
                JSONObject paramProperties = new JSONObject();
                for (JSONObject param : params) {
                        paramProperties.put((String) param.get("name"), param);
                }
                JSONObject parameters = new JSONObject();
                if(!paramProperties.isEmpty())
                    parameters.put("properties", paramProperties);
                if(!requestBody.isEmpty())
                    parameters.put("requestBody", requestBody);

                JSONObject functionDetails = new JSONObject();
                functionDetails.put("name", function_name);
                functionDetails.put("description", desc);
                if(!parameters.isEmpty())
                    parameters.put("type", "object");
                functionDetails.put("parameters", parameters);
                functions.add(functionDetails);
            }
        }

        return functions;
    }

    public JSONObject postRequestVertexAI(String url, String authToken, List<String> messages, List<JSONObject> functions) throws IOException {
        // Create a JSON object to send as the request body.

        // Create a URI for the API endpoint.
        URI uri = URI.create(url);
        // create a json string with format "{ \"instances\": [ { \"prompt\": \"%s\"}], \"parameters\": { \"temperature\": 0.2, \"maxOutputTokens\": 256, \"topK\": 40, \"topP\": 0.95}}"
        //String requestBody = String.format("{ \"instances\": [ { \"prompt\": \"%s\"}], \"parameters\": { \"temperature\": 0.2, \"maxOutputTokens\": 256, \"topK\": 40, \"topP\": 0.95}}", messages.toString());

        // Create a JSON object to send as the request body.
        JSONObject requestBody = new JSONObject();
        Map prompt = new HashMap<String, List<String>>();
        // take a java string messages list concatenate all the elements into one big string
//        String prompts = messages.stream().flatMap(str ->  Stream.of(str)).collect(Collectors.joining());
        String funs = " The functions to use are as follows: ";
        funs = funs + functions.stream().flatMap(str ->  Stream.of(str.toJSONString())).collect(Collectors.joining()) +" .";
        prompt.put("prompt", SYSTEM_MESSAGE + funs + USER_INSTRUCTION);
        requestBody.put("instances", prompt);
        Map parameters = new HashMap<String, Double>();
        parameters.put("temperature", 0.2);
        parameters.put("maxOutputTokens", 1000);
        parameters.put("topK", 40);
        parameters.put("topP", 0.95);
        requestBody.put("parameters", parameters);

        // Create a POST request with the JSON object as the body.
        System.out.println(requestBody.toJSONString());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toJSONString()))
                .build();

        // Create a HttpClient client.
        HttpClient client = HttpClient.newHttpClient();

        // Send the request and get the response.
        HttpResponse<String> response = null;
        JSONObject result = null;
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
        return result;
    }
}