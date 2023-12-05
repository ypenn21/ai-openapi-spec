package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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

            OpenAPIToFunctions openApiSpec = new OpenAPIToFunctions();
            List<JSONObject> functions = openApiSpec.openapiToFunctions(openapiSpec);

            for (JSONObject function : functions) {
                System.out.println(function.toJSONString());
                System.out.println();
            }

            //gcloud auth print-access-token
            String authToken = "Bearer ya29.a0AfB_byD9BgQXEkfG9tVZCbP_GZIOazIrLtQqPlYYr8kid1rheeKF0jHBu2gzFjeSM3s3-md6Qmxgaaa7ClGu1z0wkFah7i8mzUMufd2Zv7--38RId2-rdDE83SthfoSkmLB6pMJRp88bd9vhfBuzG1rQOEu3I4wNuDQF9XvDan6haCgYKAaQSAQ4SFQHGX2Mi2eNHPZH15OzaxdLokZGFXQ0179"; // Replace with your actual API key

            List<String> messages = new ArrayList<>();
    //        messages.add("{\"content\": \"" + OpenAPIToFunctions.SYSTEM_MESSAGE + "\", \"role\": \"system\"}");
    //        messages.add("{\"content\": \"" + OpenAPIToFunctions.USER_INSTRUCTION + "\", \"role\": \"user\"}");
            messages.add(OpenAPIToFunctions.SYSTEM_MESSAGE);
            messages.add( OpenAPIToFunctions.USER_INSTRUCTION);

            int numCalls = 0;
            while (numCalls < OpenAPIToFunctions.MAX_CALLS) {
                try {
                    JSONObject response = openApiSpec.postRequestVertexAI(OpenAPIToFunctions.BASE_URL, authToken, messages, functions);
                    System.out.println(response.toJSONString());
                    String pred = response.get("predictions")+"";
                    String content = ((JSONArray) parser.parse(pred)).get(0)+"";
                    JSONObject contentMetaData = (JSONObject) parser.parse(content);
                    System.out.println("content: "+contentMetaData.get("content"));
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
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}