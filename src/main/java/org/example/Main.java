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
            String authToken = "Bearer ya29.a0AfB_byBun46YkA4bpDiDU1TnjiGbFxZ_pV74bjc46yiT37JhfDpcYYd76aeRYwjj1WQ5SHjIYz9JJ4C5b3VJAlqXLZGyRKNi3lS48x1rApCt_RCMWyXhTFtD1OoDTY3W-mOW9gFJ5qM5Xl3mntnxPVdo9eKqbq9R72WoYCSdrpNpaCgYKAYoSAQ4SFQHGX2MiUYsuKAddjJXEh_km_iqreQ0179"; // Replace with your actual bearer token

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
