# ai-openapi-spec
Using OpenAPI specification to Make Method calls in Java

openapi spec generated from: 

https://github.com/GoogleCloudPlatform/serverless-production-readiness-java-gcp/blob/main/services/quotes/src/main/java/com/example/quotes/web/QuoteController.java

Ref:
https://cookbook.openai.com/examples/function_calling_with_an_openapi_spec


## Function calling AI in simpler terms

Imagine you're building a robot, and you need it to do something specific, like pick up a book. Instead of writing the exact code for every possible action, you can use **function calling AI**. This is like having a helpful friend suggest the right tool for the job.

**Here's how it works:**

1. You **describe what you want the robot to do** in plain English, like "pick up a book from the table".
2. You **send this description to the AI**.
3. The AI analyzes your description and **suggests a function** (a piece of code) that can do the job.
4. The AI also tells you **what arguments** (specific details) the function needs to work, like "book title" or "location on the table".
5. You **use this information to call the function** in your robot code.

**Benefits of using function calling AI:**

* **Faster development:** You don't need to write all the code yourself.
* **More efficient code:** You only write the specific details needed for the task.
* **Flexibility:** You can use different functions for different tasks.
* **Collaboration:** You can share functions with other developers.

**When to use function calling AI:**

* When you need help finding the right function for a task.
* When you want to avoid writing repetitive code.
* When you need to run tasks on different devices or platforms.

**Things to remember:**

* The AI doesn't actually run the code, it just suggests what to use.
* You need to know how to use the returned function in your own code.
* Function calling AI is still under development, so it's not perfect.

I hope this explanation makes things a bit clearer! Let me know if you have any other questions.


**Function calling AI typically involves two separate prompts:**

**1. OpenAPI Spec Prompt:**

   - In this prompt, you provide the AI with the **OpenAPI specifications** (or a similar format) of the available functions.
   - This information includes details like:
     - Function names
     - Required parameters
     - Expected outputs
     - Descriptions of their purposes
   - Think of this as giving the AI a "map" of the available tools.

**2. Task Description Prompt:**

   - In this prompt, you present the AI with a **description of the task** you want to accomplish.
   - Use clear and concise language to explain what you want the code to do.
   - The AI will analyze this task description and reference the OpenAPI specs it received earlier.
   - It will then suggest a function (or functions) that it believes can best achieve the desired outcome.
   - The AI will also provide information about the necessary arguments to call those functions correctly.

**This two-step approach allows for:**

- **Separation of concerns:** You can provide function definitions independently of specific tasks.
- **Reuse of functions:** The same function definitions can be used for multiple tasks.
- **Contextual understanding:** The AI can match functions to tasks based on a clearer understanding of the problem domain.
- **Iterative refinement:** You can provide feedback on the AI's suggestions, helping it learn and improve over time.


- ** Current code sample is incomplete for function calling. The feature is in preview, and currently don't access. **
- ** Current code the Vertex AI is utilizing PaLM 2 Text Bison pretrained llm. It is taking the open api spec as    **
- ** input and generating controller class, service class, and pojo based on those specifications.                  **
