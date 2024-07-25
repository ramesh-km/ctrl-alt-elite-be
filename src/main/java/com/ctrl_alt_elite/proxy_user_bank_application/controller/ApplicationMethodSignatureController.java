package com.ctrl_alt_elite.proxy_user_bank_application.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ctrl_alt_elite.proxy_user_bank_application.aspect.LogExecutionTime;
import com.ctrl_alt_elite.proxy_user_bank_application.model.ApplicationMethodSignature;
import com.ctrl_alt_elite.proxy_user_bank_application.service.ApplicationMethodSignatureServiceImpl;

@RestController
public class ApplicationMethodSignatureController {
	
	@Autowired
	private ApplicationMethodSignatureServiceImpl applicationMethodSignatureService;
	
	@LogExecutionTime
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@GetMapping("/api/list-method-names")
	public ResponseEntity<List<ApplicationMethodSignature>> methodNames() {
		return ResponseEntity.ok(applicationMethodSignatureService.getApplicationMethodSignature());
	}

}
