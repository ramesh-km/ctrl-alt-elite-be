package com.ctrl_alt_elite.proxy_user_bank_application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ctrl_alt_elite.proxy_user_bank_application.aspect.LogExecutionTime;
import com.ctrl_alt_elite.proxy_user_bank_application.aspect.ProxyRoleAccessCheck;
import com.ctrl_alt_elite.proxy_user_bank_application.entity.ProxyUserAssignment;
import com.ctrl_alt_elite.proxy_user_bank_application.model.ProxyUserAssignmentSaveRequest;
import com.ctrl_alt_elite.proxy_user_bank_application.service.ProxyUserAssignmentServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/proxy-user-assignments")
public class ProxyUserAssignmentController {

	@Autowired
	private ProxyUserAssignmentServiceImpl proxyUserAssignmentService;

	@LogExecutionTime
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<List<ProxyUserAssignment>> getAllProxyUserAssignments() {
		return ResponseEntity.ok(proxyUserAssignmentService.getAllProxyUserAssignments());
	}

	@LogExecutionTime
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<ProxyUserAssignment> getProxyUserAssignmentById(@PathVariable Long id) {
		Optional<ProxyUserAssignment> result = proxyUserAssignmentService.getProxyUserAssignmentById(id);
		return ResponseEntity.ok(result.orElse(null)); // or handle not found
	}

	@LogExecutionTime
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@GetMapping("/by-user/{user}")
	public ResponseEntity<List<ProxyUserAssignment>> getProxyUserAssignmentsByUser(@PathVariable String user) {
		return ResponseEntity.ok(proxyUserAssignmentService.getProxyUserAssignmentsByUser(user));
	}
	
	@LogExecutionTime
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@GetMapping("/by-proxy-user/{user}")
	public ResponseEntity<List<ProxyUserAssignment>> getProxyUserAssignmentsByProxyUser(@PathVariable String user) {
		return ResponseEntity.ok(proxyUserAssignmentService.getProxyUserAssignmentsByProxyUser(user));
	}

	@LogExecutionTime
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@GetMapping("/by-user-proxy/{user}/{proxyUser}")
	public ResponseEntity<List<ProxyUserAssignment>> getProxyUserAssignmentsByUserAndProxyUser(
			@PathVariable String user, @PathVariable String proxyUser) {
		return ResponseEntity.ok(proxyUserAssignmentService.getProxyUserAssignmentsByUserAndProxyUser(user, proxyUser));
	}

	@ProxyRoleAccessCheck
	@LogExecutionTime
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@GetMapping("/by-user-proxy-time/{user}/{proxyUser}")
	public ResponseEntity<List<ProxyUserAssignment>> getProxyUserAssignmentsByUserAndProxyUserAndTime(
			@PathVariable String user, @PathVariable String proxyUser) {
		return ResponseEntity
				.ok(proxyUserAssignmentService.getProxyUserAssignmentsByUserAndProxyUserAndTime(user, proxyUser));
	}

	@LogExecutionTime
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<ProxyUserAssignment> createProxyUserAssignment(
			@RequestBody ProxyUserAssignmentSaveRequest proxyUserAssignment) throws JsonProcessingException {
		return ResponseEntity.ok(proxyUserAssignmentService.createProxyUserAssignment(proxyUserAssignment));
	}

	@LogExecutionTime
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<ProxyUserAssignment> updateProxyUserAssignment(@PathVariable Long id,
			@RequestBody ProxyUserAssignmentSaveRequest updatedProxyUserAssignment) throws JsonProcessingException {
		return ResponseEntity.ok(proxyUserAssignmentService.updateProxyUserAssignment(id, updatedProxyUserAssignment));
	}

	@LogExecutionTime
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteProxyUserAssignment(@PathVariable Long id) {
		if (proxyUserAssignmentService.getProxyUserAssignmentById(id).isPresent()) {
			proxyUserAssignmentService.deleteProxyUserAssignment(id);
			return ResponseEntity.ok("Proxy user assignment deletion successful");
		}
		return ResponseEntity.notFound().build();
	}
}
