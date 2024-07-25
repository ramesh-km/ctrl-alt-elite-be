package com.ctrl_alt_elite.proxy_user_bank_application.aspect;

import java.util.List;
import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UriTemplate;

import com.ctrl_alt_elite.proxy_user_bank_application.entity.ProxyUserAssignment;
import com.ctrl_alt_elite.proxy_user_bank_application.model.ApplicationMethodSignature;
import com.ctrl_alt_elite.proxy_user_bank_application.security.JwtUtils;
import com.ctrl_alt_elite.proxy_user_bank_application.service.ProxyUserAssignmentServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
@Order(1)
public class ProxyRoleAccessCheckAspect {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private ProxyUserAssignmentServiceImpl proxyUserAssignmentService;

	@Around("@annotation(ProxyRoleAccessCheck)")
	public Object proxyRoleAccess(ProceedingJoinPoint joinPoint) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		final String url = request.getRequestURI();
		final String methodName = joinPoint.getSignature().getName();
		final String requestMethod = request.getMethod();
		Object handler = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
		final String controllerName = getControllerName(handler);
		if (jwtUtils.isProxyEnabled(jwtUtils.getJwtFromCookies(request))) {
			var proxyUser = jwtUtils.getImpersonatedEmailFromJwtToken(jwtUtils.getJwtFromCookies(request));
			var user = jwtUtils.getActualEmailFromJwtToken(jwtUtils.getJwtFromCookies(request));

			List<ProxyUserAssignment> activeProxyAssignments = proxyUserAssignmentService
					.getProxyUserAssignmentsByUserAndProxyUserAndTime(user, proxyUser);
			if (Objects.nonNull(activeProxyAssignments) && !activeProxyAssignments.isEmpty()) {
				if(Objects.isNull(activeProxyAssignments.get(0).getRolesAssignment()) || activeProxyAssignments.get(0).getRolesAssignment().isBlank()){
					return ResponseEntity.status(HttpStatus.FORBIDDEN)
							.body("Proxy user dont have permissions to access service.");
				}
				List<ApplicationMethodSignature> applicationMethodSignatures = objectMapper.readValue(
						activeProxyAssignments.get(0).getRolesAssignment(),
						new TypeReference<List<ApplicationMethodSignature>>() {
						});
				List<ApplicationMethodSignature> filteredList = applicationMethodSignatures.stream().filter(
						signature -> matchesSignature(signature, controllerName, methodName, url, requestMethod))
						.toList();
				if (filteredList.isEmpty()) {
					return ResponseEntity.status(HttpStatus.FORBIDDEN)
							.body("Proxy user dont have permissions to access service.");
				}
			}
		}
		return joinPoint.proceed();
	}

	private String getControllerName(Object handler) {
		if (handler != null && handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			var controllerName = handlerMethod.getBeanType().getSimpleName();
			return controllerName;
		}
		return "";

	}

	private boolean matchesSignature(ApplicationMethodSignature signature, String controllerName, String methodName,
			String url, String requestMethod) {
		UriTemplate uriTemplate = new UriTemplate(signature.getUrlPath());
		return signature.getClassName().equalsIgnoreCase(controllerName)
				&& signature.getMethodName().equalsIgnoreCase(methodName)
				&& (signature.getUrlPath().equalsIgnoreCase(url) || uriTemplate.matches(url))
				&& signature.getHttpMethodName().equalsIgnoreCase(requestMethod);
	}
}