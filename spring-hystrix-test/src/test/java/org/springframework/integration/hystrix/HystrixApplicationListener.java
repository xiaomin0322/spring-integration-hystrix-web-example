package org.springframework.integration.hystrix;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javassist.HystrixCommandAnnotationLoad;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import zk.HystrixZKClient;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import com.netflix.hystrix.contrib.javanica.command.MetaHolder;

@Component
public class HystrixApplicationListener implements
		ApplicationListener<ContextRefreshedEvent> {

	Logger logger = LoggerFactory.getLogger(HystrixApplicationListener.class);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// logger.debug("------初始化执行----");
		System.out.println("------初始化执行----");
		try {
			// 获取上下文
			ApplicationContext context = event.getApplicationContext();
			// 获取所有beanNames
			String[] beanNames = context.getBeanNamesForType(Object.class);
			for (String beanName : beanNames) {
				Class<?> objClass = AopTargetUtils.getTarget(
						context.getBean(beanName)).getClass();
				Method[] methods = objClass.getDeclaredMethods();
				List<HystrixCommandVo> hystrixCommandVos = new ArrayList<HystrixCommandVo>();

				for (Method method : methods) {
					HystrixCommand hystrixCommand = method
							.getAnnotation(HystrixCommand.class);
					if (hystrixCommand == null) {
						continue;
					}
					// do something
					HystrixCommandVo commandVo = new HystrixCommandVo(
							hystrixCommand);
					commandVo.setMethodName(method.getName());
					commandVo.setClassName(objClass.getSimpleName());
					commandVo.setPackageName(objClass.getPackage().getName());
					commandVo.setProjectName(Class.class.getClass()
							.getResource("/").getPath());
					commandVo.setServiceIp(InetAddress.getLocalHost()
							.getHostAddress());
					commandVo.setClassNameAll(objClass.getName());
					hystrixCommandVos.add(commandVo);

					System.out.println("注解方法：" + method.getName() + ",===="
							+ commandVo);

					// /hystrix/org.springframework.integration.hystrix.HystrixCommandServiceImpl
					String classNodeNamePath = HystrixZKClient.ROOTPATH + "/"
							+ commandVo.getClassNameAll();
					HystrixZKClient.appendPresistentNode(classNodeNamePath,
							classNodeNamePath);
					// /hystrix/org.springframework.integration.hystrix.hystrixCommandServiceImpl_method_get_ip
					final String methodsNodeNamePath = classNodeNamePath
							+ "/method_" + commandVo.getMethodName() + "_"
							+ commandVo.getServiceIp();
					HystrixZKClient.appendEphemeralNode(methodsNodeNamePath,
							JSON.toJSONString(commandVo));
					// 注册监听

					final Watcher watcher = new Watcher() {
						@Override
						public void process(WatchedEvent event) {
							if (event.getType() == null
									|| "".equals(event.getType())) {
								return;
							}
							// 事件类型，状态，和检测的路径
							EventType eventType = event.getType();
							//KeeperState state = event.getState();
							String watchPath = event.getPath();

							// System.out.println("回调watcher1实例： 路径" +
							// event.getPath() + " 类型："+ event.getType()
							// +" state :"+state +" watchPath: "+watchPath);

							switch (eventType) {
							case NodeCreated:
								break;
							case NodeDataChanged:
								try {
									String rslut = HystrixZKClient.zkServer
											.getData(watchPath, new Stat());
									// System.out.println("watchPath 节点发生变化===="+watchPath
									// +" 内容："+rslut);
									HystrixCommandVo commandVo = JSON
											.parseObject(rslut,
													HystrixCommandVo.class);
									HystrixCommand hystrixCommand = HystrixCommandAnnotationLoad
											.createHystrixCommand(commandVo);
									MetaHolder holder = HystrixCommandAspect.METAHOLDERS
											.get(commandVo.getClassNameAll()+"."
													+ commandVo.getMethodName());
									holder.setHystrixCommand(hystrixCommand);
									System.out.println("nPath=" + watchPath
											+ " 更新后的 values = " + rslut);
								} catch (Exception e) {
									e.printStackTrace();
								}
								break;
							case NodeChildrenChanged:
								break;
							default:
								break;
							}

							try {
								HystrixZKClient.zkServer.getData(
										methodsNodeNamePath, this, new Stat());
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					};

					HystrixZKClient.zkServer.getData(methodsNodeNamePath,
							watcher, new Stat());

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}