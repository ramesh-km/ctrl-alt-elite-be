package com.ctrl_alt_elite.proxy_user_bank_application.model;

public class ApplicationMethodSignature {
	
	private String className;
	private String methodName;
	private String urlPath;
	private String httpMethodName;
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getUrlPath() {
		return urlPath;
	}
	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}
	public String getHttpMethodName() {
		return httpMethodName;
	}
	public void setHttpMethodName(String httpMethodName) {
		this.httpMethodName = httpMethodName;
	}
	@Override
	public String toString() {
		return "ApplicationMethodSignature [className=" + className + ", methodName=" + methodName + ", urlPath=" + urlPath
				+ ", httpMethodName=" + httpMethodName + "]";
	}

}
