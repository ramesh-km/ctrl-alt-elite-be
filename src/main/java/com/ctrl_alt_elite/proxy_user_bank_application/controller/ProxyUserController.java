package com.ctrl_alt_elite.proxy_user_bank_application.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ctrl_alt_elite.proxy_user_bank_application.aspect.LogExecutionTime;
import com.ctrl_alt_elite.proxy_user_bank_application.entity.ProxyUserAssignment;
import com.ctrl_alt_elite.proxy_user_bank_application.model.UserInfoResponse;
import com.ctrl_alt_elite.proxy_user_bank_application.security.JwtUtils;
import com.ctrl_alt_elite.proxy_user_bank_application.service.ProxyUserAssignmentServiceImpl;
import com.ctrl_alt_elite.proxy_user_bank_application.service.ProxyUserServiceImpl;
import com.ctrl_alt_elite.proxy_user_bank_application.service.UserDetailsImpl;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ProxyUserController {

	@Autowired
	private ProxyUserServiceImpl proxyUserService;

	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	private ProxyUserAssignmentServiceImpl proxyUserAssignmentService;

	@LogExecutionTime
	@PostMapping(path = "/api/enable-proxy")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<Object> enableProxyUser(@RequestParam("email") String targetEmail,
			HttpServletRequest request) {
		try {
			if(jwtUtils.isProxyEnabled(jwtUtils.getJwtFromCookies(request))){
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are already acting as proxy.");
			}
			UserDetails targetUser = proxyUserService.proxyUser(targetEmail);
			

			String actualEmail = jwtUtils.getActualEmailFromJwtToken(jwtUtils.getJwtFromCookies(request));
			if(targetEmail.equalsIgnoreCase(actualEmail)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot act as proxy for self.");
			}
			
			List<ProxyUserAssignment> activeProxyAssignments = proxyUserAssignmentService
					.getProxyUserAssignmentsByUserAndProxyUserAndTime(actualEmail, targetEmail);
			
			if(activeProxyAssignments.isEmpty()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No active proxy assignments available.");
			}

			// Optionally, you can store the targetUser in the SecurityContext if needed
			Authentication newAuth = new UsernamePasswordAuthenticationToken(targetUser, targetUser.getPassword(),
					targetUser.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(newAuth);

			UserDetailsImpl targettedUserDetails = (UserDetailsImpl) newAuth.getPrincipal();
			
			ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(targettedUserDetails, actualEmail);
			ResponseCookie jwtCookieProxyEnabled = jwtUtils.generateJwtCookieProxyEnabled(jwtCookie.getValue());

			List<String> roles = targettedUserDetails.getAuthorities().stream().map(item -> item.getAuthority())
					.collect(Collectors.toList());

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.SET_COOKIE, jwtCookie.toString());
			headers.add(HttpHeaders.SET_COOKIE, jwtCookieProxyEnabled.toString());
			return ResponseEntity.ok().headers(headers).body(new UserInfoResponse(targettedUserDetails.getId(),
					targettedUserDetails.getUsername(), targettedUserDetails.getEmail(), roles));

		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + targetEmail);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to switch user: " + e.getMessage());
		}
	}

	@LogExecutionTime
	@PostMapping("/api/disable-proxy")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<Object> disableProxyUser(HttpServletRequest request) {
		String actualEmail = jwtUtils.getActualEmailFromJwtToken(jwtUtils.getJwtFromCookies(request));
		try {
			UserDetails userDetails = proxyUserService.proxyUser(actualEmail);
			Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,
					userDetails.getPassword(), userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
			ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetailsImpl);
			ResponseCookie jwtCookieProxyEnabled = jwtUtils.generateJwtCookieProxyEnabled(jwtCookie.getValue());

			List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
					.collect(Collectors.toList());

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.SET_COOKIE, jwtCookie.toString());
			headers.add(HttpHeaders.SET_COOKIE, jwtCookieProxyEnabled.toString());
			return ResponseEntity.ok().headers(headers).body(new UserInfoResponse(userDetailsImpl.getId(),
					userDetailsImpl.getUsername(), userDetailsImpl.getEmail(), roles));
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + actualEmail);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to switch user: " + e.getMessage());
		}

	}
}
