package com.ctrl_alt_elite.proxy_user_bank_application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ctrl_alt_elite.proxy_user_bank_application.entity.ProxyUserAssignment;
import com.ctrl_alt_elite.proxy_user_bank_application.model.ProxyUserAssignmentSaveRequest;
import com.ctrl_alt_elite.proxy_user_bank_application.repository.ProxyUserAssignmentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProxyUserAssignmentServiceImpl {

	@Autowired
	private ProxyUserAssignmentRepository repository;
	
	final ObjectMapper objectMapper = new ObjectMapper();

	public List<ProxyUserAssignment> getAllProxyUserAssignments() {
		return repository.findAll();
	}

	public Optional<ProxyUserAssignment> getProxyUserAssignmentById(Long id) {
		return repository.findById(id);
	}

	public List<ProxyUserAssignment> getProxyUserAssignmentsByUser(String user) {
		LocalDateTime currentDateTime = LocalDateTime.now();
		return repository.findByUserAndFromDateLessThanEqualAndToDateGreaterThanEqual(user, currentDateTime,
				currentDateTime);
	}
	
	public List<ProxyUserAssignment> getProxyUserAssignmentsByProxyUser(String proxyUser) {
		LocalDateTime currentDateTime = LocalDateTime.now();
		return repository.findByProxyUserAndFromDateLessThanEqualAndToDateGreaterThanEqual(proxyUser, currentDateTime,
				currentDateTime);
	}

	public List<ProxyUserAssignment> getProxyUserAssignmentsByUserAndProxyUser(String user, String proxyUser) {
		return repository.findByUserAndProxyUser(user, proxyUser);
	}

	public List<ProxyUserAssignment> getProxyUserAssignmentsByUserAndProxyUserAndTime(String user, String proxyUser) {
		LocalDateTime currentDateTime = LocalDateTime.now();
		return repository.findByUserAndProxyUserAndFromDateLessThanEqualAndToDateGreaterThanEqual(user, proxyUser,
				currentDateTime, currentDateTime);
	}

	@Transactional
	public ProxyUserAssignment createProxyUserAssignment(ProxyUserAssignmentSaveRequest proxyUserAssignmentRequest) throws JsonProcessingException {
		ProxyUserAssignment proxyUserAssignment = new ProxyUserAssignment();
		proxyUserAssignment.setFromDate(proxyUserAssignmentRequest.getFromDate());
		proxyUserAssignment.setProxyUser(proxyUserAssignmentRequest.getProxyUser());
		proxyUserAssignment.setRolesAssignment(objectMapper.writeValueAsString(proxyUserAssignmentRequest.getRolesAssignment()));
		proxyUserAssignment.setToDate(proxyUserAssignmentRequest.getToDate());
		proxyUserAssignment.setUser(proxyUserAssignmentRequest.getUser());
		proxyUserAssignment.setTimestamp(LocalDateTime.now());
		return repository.save(proxyUserAssignment);
	}

	@Transactional
	public ProxyUserAssignment updateProxyUserAssignment(Long id, ProxyUserAssignmentSaveRequest updatedProxyUserAssignmentRequest) throws JsonProcessingException {
		Optional<ProxyUserAssignment> existing = repository.findById(id);
		if (existing.isPresent()) {
			ProxyUserAssignment updatedProxyUserAssignment = new ProxyUserAssignment();
			updatedProxyUserAssignment.setFromDate(updatedProxyUserAssignmentRequest.getFromDate());
			updatedProxyUserAssignment.setProxyUser(updatedProxyUserAssignmentRequest.getProxyUser());
			updatedProxyUserAssignment.setRolesAssignment(objectMapper.writeValueAsString(updatedProxyUserAssignmentRequest.getRolesAssignment()));
			updatedProxyUserAssignment.setToDate(updatedProxyUserAssignmentRequest.getToDate());
			updatedProxyUserAssignment.setUser(updatedProxyUserAssignmentRequest.getUser());
			updatedProxyUserAssignment.setId(id);
			updatedProxyUserAssignment.setTimestamp(LocalDateTime.now());
			return repository.save(updatedProxyUserAssignment);
		} else {
			return null; // or throw NotFoundException
		}
	}

	@Transactional
	public void deleteProxyUserAssignment(Long id) {
		repository.deleteById(id);
	}
}
