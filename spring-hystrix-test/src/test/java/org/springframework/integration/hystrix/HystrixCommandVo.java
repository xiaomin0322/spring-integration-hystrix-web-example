package org.springframework.integration.hystrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;


public class HystrixCommandVo {
	
	//方法名称
	private String methodName = null;
	//类名称
	private String className = null;
	//包名称
	private String packageName = null;
	//项目名称
	private String projectName = null;
    //服务ip
	private String serviceIp = null;
	
	private String commandKey = null;
	
	private String groupKey = null;
	
	private String fallbackMethod = null;
	
	private String threadPoolKey = null;
	  
	private List<org.springframework.integration.hystrix.HystrixPropertyVo> threadPoolProperties = null;  
	
	private List<org.springframework.integration.hystrix.HystrixPropertyVo> commandProperties = null;
	
	private List<Class<? extends Throwable>> ignoreExceptions = null;
	
	public HystrixCommandVo(){}
	
	
	public HystrixCommandVo(com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand hystrixCommand){
		String commandKey = hystrixCommand.commandKey();
		this.commandKey = commandKey;
		
		String groupKey = hystrixCommand.groupKey();
		this.groupKey = groupKey;
		
		String fallbackMethod = hystrixCommand.fallbackMethod();
		this.fallbackMethod = fallbackMethod;
		
		String threadPoolKey = hystrixCommand.threadPoolKey();
		this.threadPoolKey = threadPoolKey;
		  
		HystrixProperty[] threadPoolProperties = hystrixCommand.threadPoolProperties();  
		if(threadPoolProperties!=null){
			this.threadPoolProperties = new ArrayList<org.springframework.integration.hystrix.HystrixPropertyVo>();
			for(HystrixProperty h:threadPoolProperties){
				org.springframework.integration.hystrix.HystrixPropertyVo hystrixProperty = new org.springframework.integration.hystrix.HystrixPropertyVo();
				hystrixProperty.setName(h.name());
				hystrixProperty.setValue(h.value());
				this.threadPoolProperties.add(hystrixProperty);
			}
		}
		HystrixProperty[] commandProperties =hystrixCommand.commandProperties();
		if(commandProperties!=null){
			this.commandProperties = new ArrayList<org.springframework.integration.hystrix.HystrixPropertyVo>();
			for(HystrixProperty h:commandProperties){
				org.springframework.integration.hystrix.HystrixPropertyVo hystrixProperty = new org.springframework.integration.hystrix.HystrixPropertyVo();
				hystrixProperty.setName(h.name());
				hystrixProperty.setValue(h.value());
				this.commandProperties.add(hystrixProperty);
			}
		}
		
	    Class<? extends Throwable>[] ignoreExceptions = hystrixCommand.ignoreExceptions();
	    if(ignoreExceptions!=null){
	    	this.ignoreExceptions = Arrays.asList(ignoreExceptions);
	    }
		
	}


	public String getMethodName() {
		return methodName;
	}


	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}


	public String getClassName() {
		return className;
	}


	public void setClassName(String className) {
		this.className = className;
	}


	public String getPackageName() {
		return packageName;
	}


	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}


	public String getProjectName() {
		return projectName;
	}


	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}


	public String getServiceIp() {
		return serviceIp;
	}


	public void setServiceIp(String serviceIp) {
		this.serviceIp = serviceIp;
	}


	public String getCommandKey() {
		return commandKey;
	}


	public void setCommandKey(String commandKey) {
		this.commandKey = commandKey;
	}


	public String getGroupKey() {
		return groupKey;
	}


	public void setGroupKey(String groupKey) {
		this.groupKey = groupKey;
	}


	public String getFallbackMethod() {
		return fallbackMethod;
	}


	public void setFallbackMethod(String fallbackMethod) {
		this.fallbackMethod = fallbackMethod;
	}


	public String getThreadPoolKey() {
		return threadPoolKey;
	}


	public void setThreadPoolKey(String threadPoolKey) {
		this.threadPoolKey = threadPoolKey;
	}


	public List<org.springframework.integration.hystrix.HystrixPropertyVo> getThreadPoolProperties() {
		return threadPoolProperties;
	}


	public void setThreadPoolProperties(
			List<org.springframework.integration.hystrix.HystrixPropertyVo> threadPoolProperties) {
		this.threadPoolProperties = threadPoolProperties;
	}


	public List<org.springframework.integration.hystrix.HystrixPropertyVo> getCommandProperties() {
		return commandProperties;
	}


	public void setCommandProperties(
			List<org.springframework.integration.hystrix.HystrixPropertyVo> commandProperties) {
		this.commandProperties = commandProperties;
	}


	public List<Class<? extends Throwable>> getIgnoreExceptions() {
		return ignoreExceptions;
	}


	public void setIgnoreExceptions(
			List<Class<? extends Throwable>> ignoreExceptions) {
		this.ignoreExceptions = ignoreExceptions;
	}


	@Override
	public String toString() {
		return "HystrixCommandVo [methodName=" + methodName + ", className="
				+ className + ", packageName=" + packageName + ", projectName="
				+ projectName + ", serviceIp=" + serviceIp + ", commandKey="
				+ commandKey + ", groupKey=" + groupKey + ", fallbackMethod="
				+ fallbackMethod + ", threadPoolKey=" + threadPoolKey
				+ ", threadPoolProperties=" + threadPoolProperties
				+ ", commandProperties=" + commandProperties
				+ ", ignoreExceptions=" + ignoreExceptions + "]";
	}

	

}
