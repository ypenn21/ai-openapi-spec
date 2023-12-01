package org.example;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Assuming openapiSpec is a JSONObject representing the OpenAPI specification
        // Create a JSONParser object
        JSONParser parser = new JSONParser();
        JSONObject openapiSpec = new JSONObject(); // Replace this with your actual JSON object
        // Parse the JSON string into a JSONObject object
        try {
            JSONObject json = new JSONObject();

            // Use the ClassLoader class to get the input stream for the JSON file.
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("openapi-spec-quotes.json");
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String jsonString = scanner.hasNext() ? scanner.next() : "";
            System.out.println(jsonString);
            // Pass the input stream to the JSONObject constructor.
            openapiSpec = (JSONObject) parser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        OpenAPIToFunctions openApiSpec = new OpenAPIToFunctions();
        List<JSONObject> functions = openApiSpec.openapiToFunctions(openapiSpec);

        for (JSONObject function : functions) {
            System.out.println(function.toJSONString());
            System.out.println();
        }

        String authToken = "YOUR_API_KEY_HERE"; // Replace with your actual API key

        List<String> messages = new ArrayList<>();
        messages.add("{\"content\": \"" + OpenAPIToFunctions.SYSTEM_MESSAGE + "\", \"role\": \"system\"}");
        messages.add("{\"content\": \"" + OpenAPIToFunctions.USER_INSTRUCTION + "\", \"role\": \"user\"}");

        int numCalls = 0;
        while (numCalls < OpenAPIToFunctions.MAX_CALLS) {
            try {
                String response = openApiSpec.postRequest(OpenAPIToFunctions.BASE_URL, authToken, messages);
                System.out.println(response);
            } catch (IOException e) {
                System.out.println("Message");

                System.out.println("Exception:"+e.getMessage());
                break;
            }
            // Handle the JSON response here
            // Parse the response and process according to your requirements
            // For brevity, the actual parsing and handling of the response are omitted

            // Simulating the loop condition
            numCalls++;
            if (numCalls >= OpenAPIToFunctions.MAX_CALLS) {
                System.out.println("Reached max chained function calls: " + OpenAPIToFunctions.MAX_CALLS);
                break;
            }
        }
    }
}