package com.ctrl_alt_elite.proxy_user_bank_application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ctrl_alt_elite.proxy_user_bank_application.aspect.LogExecutionTime;
import com.ctrl_alt_elite.proxy_user_bank_application.aspect.ProxyRoleAccessCheck;
import com.ctrl_alt_elite.proxy_user_bank_application.security.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;

//for Angular Client (withCredentials)
//@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials="true")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

	@Autowired
	JwtUtils jwtUtils;

	@LogExecutionTime
	@GetMapping("/all")
	public ResponseEntity<String> allAccess(HttpServletRequest request) {
		return ResponseEntity
				.ok("Public Content. " + jwtUtils.getUserNameFromJwtToken(jwtUtils.getJwtFromCookies(request)));
	}

	@LogExecutionTime
	@ProxyRoleAccessCheck
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<String> userAccess(HttpServletRequest request) {
		return ResponseEntity
				.ok("User Content. " + jwtUtils.getUserNameFromJwtToken(jwtUtils.getJwtFromCookies(request)));
	}

	@LogExecutionTime
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> adminAccess(HttpServletRequest request) {
		return ResponseEntity
				.ok("Admin Board. " + jwtUtils.getUserNameFromJwtToken(jwtUtils.getJwtFromCookies(request)));
	}
}
