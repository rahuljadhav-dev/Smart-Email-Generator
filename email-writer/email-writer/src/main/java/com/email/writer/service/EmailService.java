package com.email.writer.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.email.writer.request.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmailService {
	
	private final WebClient client;
	public EmailService(WebClient.Builder client ) {
		this.client=client.build();
		
	}
	@Value("${gemini.api.url}")
	private String geminiApiUrl;
	@Value("${gemini.api.key}")
	private String geminiApiKey;
	
	public String emailReply(EmailRequest request) {
		//building the prompt
		String prompt=buildPrompt(request);
		//crafting a request
		Map<String, Object>requestBody=Map.of(
				"contents",new Object[] {
						Map.of("parts",new Object[] {
								Map.of("text",prompt)
						})
				}
				);
		//do request and get request
		String response=client.post()
				.uri(geminiApiUrl+geminiApiKey)
				.header("Content-Type", "application/json")
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		
		//return the response
		return extractResponseContent(response);
	}

	private String extractResponseContent(String response) {
		try {
			ObjectMapper mapper=new ObjectMapper();
			JsonNode node=mapper.readTree(response);
			return node.path("candidates")
					.get(0)
					.path("content")
					.path("parts")
					.get(0)
					.path("text")
					.asText();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	private String buildPrompt(EmailRequest request) {
		StringBuilder prompt=new StringBuilder();
		prompt.append("Generate a professional email reply for the following email content.Please don't generate a subject line ");
		if(request.getTone()!=null && !request.getTone().isEmpty()) {
			prompt.append("Use a").append(request.getTone()).append(" tone");
			
		}
		prompt.append("\nOriginal email: \n").append(request.getEmailContent());
		return prompt.toString();
	}

}
