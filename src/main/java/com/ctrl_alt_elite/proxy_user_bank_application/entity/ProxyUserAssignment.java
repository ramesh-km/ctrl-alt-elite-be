package com.ctrl_alt_elite.proxy_user_bank_application.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "proxy_user_assignment")
public class ProxyUserAssignment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "from_date", nullable = false)
	private LocalDateTime fromDate;

	@Column(name = "proxy_user", nullable = false, length = 255, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_0900_ai_ci")
	private String proxyUser;

	@Column(name = "roles_assignment", nullable = false, columnDefinition = "LONGTEXT COLLATE utf8mb4_0900_ai_ci")
	private String rolesAssignment;

	@Column(name = "timestamp", nullable = false)
	private LocalDateTime timestamp;

	@Column(name = "to_date", nullable = false)
	private LocalDateTime toDate;

	@Column(name = "user", nullable = false, length = 255, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_0900_ai_ci")
	private String user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getProxyUser() {
		return proxyUser;
	}

	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	public LocalDateTime getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDateTime fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDateTime getToDate() {
		return toDate;
	}

	public void setToDate(LocalDateTime toDate) {
		this.toDate = toDate;
	}

	public String getRolesAssignment() {
		return rolesAssignment;
	}

	public void setRolesAssignment(String rolesAssignment) {
		this.rolesAssignment = rolesAssignment;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "ProxyUserAssignment [id=" + id + ", user=" + user + ", proxyUser=" + proxyUser + ", from=" + fromDate
				+ ", to=" + toDate + ", rolesAssignment=" + rolesAssignment + ", timestamp=" + timestamp + "]";
	}
}
