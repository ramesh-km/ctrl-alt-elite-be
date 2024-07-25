package com.ctrl_alt_elite.proxy_user_bank_application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
	public class ProxyUserServiceImpl {

	    @Autowired
	    private UserDetailsServiceImpl userDetailsService;

	    public UserDetails proxyUser(String targetUsername) {
	        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
	        if (currentAuth == null) {
	            throw new IllegalStateException("No authentication found in the security context");
	        }

	        // Perform authorization checks here if needed
	        // Example: Check if currentAuth has authority to act as targetUsername

	        // Load UserDetails of the target user
	        return userDetailsService.loadUserByUsername(targetUsername);
	    }
	}
