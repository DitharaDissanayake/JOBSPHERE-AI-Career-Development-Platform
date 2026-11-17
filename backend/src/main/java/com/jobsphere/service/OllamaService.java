package com.jobsphere.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class OllamaService {

 @Value("${ollama.url}")
 private String url;

 @Value("${ollama.model}")
 private String model;

 @Value("${ollama.timeout-seconds:120}")
 private int timeout;

 public String generate(String prompt) {

  Map<String, Object> body =
          new HashMap<>();

  body.put("model", model);
  body.put("prompt", prompt);
  body.put("stream", false);

  Map<String, Object> options =
          new HashMap<>();

  /*
   * Low temperature gives more consistent JSON
   * and reduces random/unrelated questions.
   */
  options.put("temperature", 0.15);

  /*
   * Allows Ollama enough output space for
   * 10 complete questions and answers.
   */
  options.put("num_predict", 6000);

  body.put("options", options);

  try {

   RestTemplate restTemplate =
           new RestTemplate();

   restTemplate.getInterceptors().add(
           (request, bodyBytes, execution) -> {

            request.getHeaders()
                    .setContentType(
                            MediaType.APPLICATION_JSON
                    );

            return execution.execute(
                    request,
                    bodyBytes
            );
           }
   );

   ResponseEntity<Map> response =
           restTemplate.postForEntity(
                   url,
                   body,
                   Map.class
           );

   Map<?, ?> result =
           response.getBody();

   if (result == null) {

    throw new RuntimeException(
            "Ollama returned an empty response."
    );
   }

   Object responseText =
           result.get("response");

   if (responseText == null
           || responseText.toString().isBlank()) {

    throw new RuntimeException(
            "Ollama returned an empty response."
    );
   }

   return cleanJson(
           responseText.toString()
   );

  } catch (ResourceAccessException e) {

   throw new RuntimeException(
           "Ollama is not reachable. Start Ollama and make sure the configured model is installed."
   );

  } catch (Exception e) {

   if (e instanceof RuntimeException re) {
    throw re;
   }

   throw new RuntimeException(
           "Unable to complete the AI interview generation."
   );
  }
 }

 private String cleanJson(String s) {

  String x = s.trim();

  // Remove Markdown code fences
  if (x.startsWith("```")) {

   x = x.replaceFirst(
           "^```(?:json)?\\s*",
           ""
   );

   x = x.replaceFirst(
           "\\s*```$",
           ""
   );

   x = x.trim();
  }

  // Extract JSON object
  int start =
          x.indexOf("{");

  int end =
          x.lastIndexOf("}");

  if (start >= 0 && end > start) {

   x = x.substring(
           start,
           end + 1
   );
  }

  return x.trim();
 }

 public String generateChat(String prompt) {

  Map<String,Object> body = new HashMap<>();
  body.put("model", model);
  body.put("prompt", prompt);
  body.put("stream", false);

  Map<String,Object> options = new HashMap<>();
  options.put("temperature", 0.35);
  options.put("num_predict", 2500);
  body.put("options", options);

  try {
   RestTemplate restTemplate = new RestTemplate();
   ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);
   Map<?,?> result = response.getBody();
   if(result == null || result.get("response") == null || result.get("response").toString().isBlank())
    throw new RuntimeException("Ollama returned an empty response.");
   return result.get("response").toString().trim();
  } catch(ResourceAccessException e) {
   throw new RuntimeException("Ollama is not reachable. Start Ollama and make sure the configured model is installed.");
  } catch(RuntimeException e) {
   throw e;
  } catch(Exception e) {
   throw new RuntimeException("Unable to complete the AI chat request.");
  }
 }

}