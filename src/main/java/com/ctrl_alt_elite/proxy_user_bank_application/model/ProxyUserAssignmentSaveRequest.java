package com.ctrl_alt_elite.proxy_user_bank_application.model;

import java.time.LocalDateTime;
import java.util.List;

public class ProxyUserAssignmentSaveRequest {
	
	private LocalDateTime fromDate;

	private String proxyUser;

	private List<ApplicationMethodSignature> rolesAssignment;

	private LocalDateTime timestamp;

	private LocalDateTime toDate;

	private String user;
	
	public LocalDateTime getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDateTime fromDate) {
		this.fromDate = fromDate;
	}

	public String getProxyUser() {
		return proxyUser;
	}

	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	public List<ApplicationMethodSignature> getRolesAssignment() {
		return rolesAssignment;
	}

	public void setRolesAssignment(List<ApplicationMethodSignature> rolesAssignment) {
		this.rolesAssignment = rolesAssignment;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public LocalDateTime getToDate() {
		return toDate;
	}

	public void setToDate(LocalDateTime toDate) {
		this.toDate = toDate;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "ProxyUserAssignmentSaveRequest [fromDate=" + fromDate + ", proxyUser=" + proxyUser
				+ ", rolesAssignment=" + rolesAssignment + ", timestamp=" + timestamp + ", toDate=" + toDate + ", user="
				+ user + "]";
	}

}
