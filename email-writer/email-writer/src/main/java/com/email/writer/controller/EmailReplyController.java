package com.email.writer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.email.writer.request.EmailRequest;
import com.email.writer.service.EmailService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class EmailReplyController {
	private final EmailService service;
	@PostMapping("/generate")
	public ResponseEntity<String>generateEmail(@RequestBody EmailRequest request){
		String response=service.emailReply(request);
		return ResponseEntity.ok(response);
	}

}
