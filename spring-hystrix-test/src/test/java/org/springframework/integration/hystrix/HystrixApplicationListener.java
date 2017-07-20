package org.springframework.integration.hystrix;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import zk.HystrixZKClient;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Component
public class HystrixApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

	Logger logger = LoggerFactory.getLogger(HystrixApplicationListener.class);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		//logger.debug("------初始化执行----");
		System.out.println("------初始化执行----");
		try {
			// 获取上下文
			ApplicationContext context = event.getApplicationContext();
			// 获取所有beanNames
			String[] beanNames = context.getBeanNamesForType(Object.class);
			for (String beanName : beanNames) {
				    Class<?> objClass = AopTargetUtils.getTarget(context.getBean(beanName)).getClass();
					Method[] methods = objClass
							.getDeclaredMethods();
					for (Method method : methods) {
						

						List<HystrixCommandVo> hystrixCommandVos  = new ArrayList<HystrixCommandVo>();
						String serviceNodeName = null;
						String serverId = null;
 						if (method.isAnnotationPresent(HystrixCommand.class)) {
							HystrixCommand hystrixCommand = method
									.getAnnotation(HystrixCommand.class);
							// do something
							HystrixCommandVo commandVo = new HystrixCommandVo(hystrixCommand);
							commandVo.setMethodName(method.getName());
							commandVo.setClassName(beanName);
							commandVo.setPackageName(objClass.getPackage().getName());
							commandVo.setProjectName(Class.class.getClass().getResource("/").getPath());
							commandVo.setServiceIp(InetAddress.getLocalHost().getHostAddress());
							hystrixCommandVos.add(commandVo);
							serviceNodeName = commandVo.getPackageName()+"."+commandVo.getClassName();
							serverId = commandVo.getServiceIp();
							System.out.println("注解方法：" + method.getName() + ",====" + commandVo);
						}
 						if(serviceNodeName!=null){
 							String classNodeNamePath = HystrixZKClient.ROOTPATH+"/"+serviceNodeName;
 							HystrixZKClient.appendPresistentNode(classNodeNamePath, classNodeNamePath);
 							String methodsNodeNamePath = classNodeNamePath+"/"+ serverId;
 							HystrixZKClient.appendEphemeralNode(methodsNodeNamePath, JSON.toJSONString(hystrixCommandVos));
 						}
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}