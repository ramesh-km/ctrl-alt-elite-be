package com.ctrl_alt_elite.proxy_user_bank_application.service;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.ctrl_alt_elite.proxy_user_bank_application.model.ApplicationMethodSignature;

@Component
public class ApplicationMethodSignatureServiceImpl {
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationMethodSignatureServiceImpl.class);
	
	@Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
	
	public List<ApplicationMethodSignature> getApplicationMethodSignature(){
		List<ApplicationMethodSignature> applicationMethodSignatures = new ArrayList<ApplicationMethodSignature>();
		getControllerNames().forEach(controllerName ->{
			try {
				var result = inspectControllerMethods(Class.forName(controllerName));
				applicationMethodSignatures.addAll(result);
			} catch (ClassNotFoundException e) {
				logger.info("ClassNotFoundException in getApplicationMethodSignature() method : {}",e.getMessage());
			}
		});
		return applicationMethodSignatures;
	}

	private HashSet<String> getControllerNames() {
		HashSet<String> hashSet = new HashSet<String>();
		Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
		handlerMethods.forEach((mapping, method) -> {
			String packageName = method.getBeanType().getPackage().getName();
			if (packageName.startsWith("com.ctrl_alt_elite")) { 
				hashSet.add(method.getBeanType().getName());
			}
		});
		return hashSet;
	}
	
	private List<ApplicationMethodSignature> inspectControllerMethods(Class<?> controllerClass) {
		 
		 	List<ApplicationMethodSignature> applicationMethodSignatures = new ArrayList<ApplicationMethodSignature>();
	        if (!controllerClass.isAnnotationPresent(RequestMapping.class) && !controllerClass.isAnnotationPresent(RestController.class) && !controllerClass.isAnnotationPresent(Controller.class)) {
	            logger.info("The class is not a controller.");
	            return applicationMethodSignatures;
	        }

	        String basePath="[]";
			if (controllerClass.isAnnotationPresent(RequestMapping.class)) {
				RequestMapping classRequestMapping = controllerClass.getAnnotation(RequestMapping.class);
				basePath = Arrays.toString(classRequestMapping.value());
			}
	        Method[] methods = controllerClass.getDeclaredMethods();

	        for (Method method : methods) {
	            if (Modifier.isPublic(method.getModifiers())) {
	                String urlPath = getMethodUrlPath(method);
	                if (urlPath != null) {
	                	ApplicationMethodSignature applicationMethodSignature = new ApplicationMethodSignature();
	                	applicationMethodSignature.setMethodName(method.getName());
	                	applicationMethodSignature.setClassName(controllerClass.getSimpleName());
	                	applicationMethodSignature.setHttpMethodName(getHttpMethodName(method));
	                	applicationMethodSignature.setUrlPath(basePath.strip().replace("[", "").replace("]", "") + urlPath.strip().replace("[", "").replace("]", ""));
	                	applicationMethodSignatures.add(applicationMethodSignature);
	                }
	            }
	        }
	        return applicationMethodSignatures;
	    }

		private String getMethodUrlPath(Method method) {
			if (method.isAnnotationPresent(GetMapping.class)) {
				return Arrays.toString(method.getAnnotation(GetMapping.class).value().length > 0
						? method.getAnnotation(GetMapping.class).value()
						: method.getAnnotation(GetMapping.class).path());
			} else if (method.isAnnotationPresent(PostMapping.class)) {
				return Arrays.toString(method.getAnnotation(PostMapping.class).value().length > 0
						? method.getAnnotation(PostMapping.class).value()
						: method.getAnnotation(PostMapping.class).path());
			} else if (method.isAnnotationPresent(PutMapping.class)) {
				return Arrays.toString(method.getAnnotation(PutMapping.class).value().length > 0
						? method.getAnnotation(PutMapping.class).value()
						: method.getAnnotation(PutMapping.class).path());
			} else if (method.isAnnotationPresent(DeleteMapping.class)) {
				return Arrays.toString(method.getAnnotation(DeleteMapping.class).value().length > 0
						? method.getAnnotation(DeleteMapping.class).value()
						: method.getAnnotation(DeleteMapping.class).path());
			} else if (method.isAnnotationPresent(RequestMapping.class)) {
				return Arrays.toString(method.getAnnotation(RequestMapping.class).value().length > 0
						? method.getAnnotation(RequestMapping.class).value()
						: method.getAnnotation(RequestMapping.class).path());
			} else if (method.isAnnotationPresent(PatchMapping.class)) {
				return Arrays.toString(method.getAnnotation(PatchMapping.class).value().length > 0
						? method.getAnnotation(PatchMapping.class).value()
						: method.getAnnotation(PatchMapping.class).path());
			}

			return null;
		}
		
		private String getHttpMethodName(Method method) {
			if (method.isAnnotationPresent(GetMapping.class)) {
				return "GET";
			} else if (method.isAnnotationPresent(PostMapping.class)) {
				return "POST";
			} else if (method.isAnnotationPresent(PutMapping.class)) {
				return "PUT";
			} else if (method.isAnnotationPresent(DeleteMapping.class)) {
				return "DELETE";
			} else if (method.isAnnotationPresent(PatchMapping.class)) {
				return "PATCH";
			}

			return null;
		}

}
