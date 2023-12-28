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
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        OpenAPIToFunctions openApiSpec = new OpenAPIToFunctions();
        List<JSONObject> functions = openApiSpec.openapiToFunctions(openapiSpec);

        for (JSONObject function : functions) {
            System.out.println(function.toJSONString());
            System.out.println();
        }

            //gcloud auth print-access-token
        String authToken = "Bearer ya29.a0AfB_byAPm5rp16SCX5njrcBRsRqu715YqH4CqrBTVSgHnQjVlxB60fjbwPGlD2Vrqu9qbeiCdMgKwK7sRULXDv9OnKK6cPPzo2mh_WHVuqxYLeW1AcxEr45ck-MJI7YjQMSjmvxcYbawLIE5QxBHIZwc3IkpYH3K1xbZdRrjPG9vaCgYKAQ0SAQ4SFQHGX2Mi93ElUUH8S24WXny50Y8WsQ0179"; // Replace with your actual bearer token

        List<String> messages = new ArrayList<>();
    //        messages.add("{\"content\": \"" + OpenAPIToFunctions.SYSTEM_MESSAGE + "\", \"role\": \"system\"}");
    //        messages.add("{\"content\": \"" + OpenAPIToFunctions.USER_INSTRUCTION + "\", \"role\": \"user\"}");
        messages.add(OpenAPIToFunctions.SYSTEM_MESSAGE);
        messages.add( OpenAPIToFunctions.USER_INSTRUCTION);

        int numCalls = 0;
        while (numCalls < OpenAPIToFunctions.MAX_CALLS) {
            try {
                    // Handle the JSON response here
                    // Parse the response and process according to your requirements
                JSONArray response = openApiSpec.postRequestVertexAI(OpenAPIToFunctions.BASE_URL, authToken, messages, functions);
                System.out.println(response.toJSONString());
                for(int i=0; i<response.size();i++) {
                    JSONObject content = (JSONObject) response.get(i);
                    JSONArray candidate = (JSONArray) content.get("candidates");
                    System.out.println(candidate.toJSONString());
                }
//                JSONArray pred = (JSONArray) response.get("predictions");
//                JSONObject content = (JSONObject)pred.get(0);
//                System.out.println("content: "+content.get("content"));
            } catch (IOException e) {
                System.out.println("Message");

                System.out.println("Exception:"+e.getMessage());
                break;
            }

                // Simulating the loop condition
            numCalls++;
            if (numCalls >= OpenAPIToFunctions.MAX_CALLS) {
                System.out.println("Reached max chained function calls: " + OpenAPIToFunctions.MAX_CALLS);
                break;
            }
        }
    }
}
