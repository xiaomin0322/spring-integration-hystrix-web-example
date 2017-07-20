package org.springframework.integration.hystrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.awt.List;

import javassist.Update;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import rx.Observer;
import rx.functions.Action1;
import zk.HystrixZKClient;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import com.netflix.hystrix.contrib.javanica.command.MetaHolder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationConfig.xml" })
public class HystrixCommandTest {

	private static final String TEST_STR = "TEST_STR";
	@Autowired
	@Qualifier(value = "hystrixCommandServiceImpl")
	private Service service;

	
	@Test
	public void getFuture()throws Exception {
		System.out.println(service.getFuture(TEST_STR).get());
	}
	
	@Test
	public void getObservable() {
		
		 // blocking
	    assertEquals(TEST_STR, service.getObservable(TEST_STR).toBlocking().single());
		
	 // non-blocking 
	    // - this is a verbose anonymous inner-class approach and doesn't do assertions
		service.getObservable(TEST_STR).subscribe(new Observer() {

			@Override
			public void onCompleted() {
				// TODO Auto-generated method stub
				System.out.println("onCompleted");
			}

			@Override
			public void onError(Throwable e) {
				e.printStackTrace();
			}

			@Override
			public void onNext(Object t) {
				System.out.println("onNext: " + t);
			}
			
		});
		
		// non-blocking
	    // - also verbose anonymous inner-class
	    // - ignore errors and onCompleted signal
		
		service.getObservable(TEST_STR).subscribe(new Action1<String>() {
		        @Override
		        public void call(String v) {
		            System.out.println("Action1 onNext: " + v);
		        }

		    });
	}
	
	
	
	@Test
	public void testHystrix() {
		assertEquals(TEST_STR, service.get(TEST_STR));
	}
	
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testHystrixZk() {
		assertEquals(TEST_STR, service.get(TEST_STR));
		String path = HystrixZKClient.ROOTPATH+"/"+"org.springframework.integration.hystrix.hystrixCommandServiceImpl"+"/172.16.0.26";
		try {
			String str = HystrixZKClient.zkServer.getData(path, new Stat());
			java.util.List list = JSON.parseObject(str, java.util.List.class);
			for(Object o:list){
				System.out.println("getZK==="+o);
			}
		Thread.sleep(30000);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test
	public void testTimeoutHystrix() {
		long start = System.currentTimeMillis();
		try {
			service.withTimeout(TEST_STR);
		} catch (Exception e) {
			e.printStackTrace();
		}

		long end = System.currentTimeMillis();
		assertTrue(end - start > HystrixCommandServiceImpl.TEST_TIMEOUT);
		assertTrue(end - start < HystrixCommandServiceImpl.TEST_TIMEOUT * 1.1f);
	}

	@Test
	public void testZeroTimeoutHystrix() {
		long start = System.currentTimeMillis();
		service.withZeroTimeout(TEST_STR);

		long end = System.currentTimeMillis();
		assertTrue(end - start > HystrixCommandServiceImpl.TEST_TIMEOUT * 2);
		assertTrue(end - start < HystrixCommandServiceImpl.TEST_TIMEOUT * 2.1f);
	}

	//@Test(expected = MyException.class)
	@Test(expected = com.netflix.hystrix.exception.HystrixRuntimeException.class)
	public void testException() throws MyException {
		service.throwException();
	}

	@Test
	public void testThreaded() throws MyException {
		int threadId = Thread.currentThread().hashCode();
		int serviceThreadId = service.getThreadId();

		assertNotEquals(threadId, serviceThreadId);
	}

	@Test
	public void testNonThreaded() throws MyException {
		int threadId = Thread.currentThread().hashCode();
		int serviceThreadId = service.getNonThreadedThreadThreadId();

		assertEquals(threadId, serviceThreadId);
	}

	@Test
	public void testExceptionWithFallback() throws MyException {
		assertEquals(TEST_STR, service.exceptionWithFallback(TEST_STR));
	}

	@Test
	public void testExceptionPassingExceptionToFallback() throws MyException {
		Throwable t = service.exceptionWithFallbackIncludingException(TEST_STR);
		assertTrue(t instanceof MyRuntimeException);
	}

	@Test
	public void testThreadPoolProperties() throws Exception {
		
		
		for (int i = 0; i < 1; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					//System.out.println(service.get(TEST_STR));
					System.out.println(service.get2(TEST_STR));
				}
			}).start();
			;

		}
		Thread.sleep(1000);
		MetaHolder metaHolder = HystrixCommandAspect.METAHOLDERS.get("get2"); 		
		// TryableSemaphore _s = executionSemaphorePerCircuit.get(commandKey.name());
		System.out.println("getDefaultCommandKey=get2=="+metaHolder.getDefaultCommandKey());
		// //默认带注释的方法的运行时 类名  
        //this.threadPoolKey = initThreadPoolKey(threadPoolKey, this.commandGroup, this.properties.executionIsolationThreadPoolKeyOverride().get());
		System.out.println("getDefaultGroupKey====HystrixCommandServiceImpl"+metaHolder.getDefaultGroupKey());
		
		Thread.sleep(2000);
		
		
		HystrixCommand command = Update.update2();
		metaHolder.setHystrixCommand(command);
		metaHolder.setDefaultGroupKey(metaHolder.getDefaultGroupKey()+"2");
		
		System.out.println("==========================================================");
		Thread.sleep(1000);
		for (int i = 0; i < 150; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					//System.out.println(service.get(TEST_STR));
					System.out.println(service.get2(TEST_STR));
				}
			}).start();
			;

		}
		Thread.sleep(20000);
		
	}
	
	@Test
	public void testThreadPoolProperties2() throws Exception {
		
		
		for (int i = 0; i < 1; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println(service.get(TEST_STR));
				}
			}).start();
			;

		}
		Thread.sleep(1000);
		MetaHolder metaHolder = HystrixCommandAspect.METAHOLDERS.get("get"); 		
		// TryableSemaphore _s = executionSemaphorePerCircuit.get(commandKey.name());
		System.out.println("getDefaultCommandKey=get=="+metaHolder.getDefaultCommandKey());
		// //默认带注释的方法的运行时 类名  
        //this.threadPoolKey = initThreadPoolKey(threadPoolKey, this.commandGroup, this.properties.executionIsolationThreadPoolKeyOverride().get());
		System.out.println("getDefaultGroupKey====HystrixCommandServiceImpl"+metaHolder.getDefaultGroupKey());
		
		Thread.sleep(2000);
		
		
		HystrixCommand command = Update.update3();
		metaHolder.setHystrixCommand(command);
		//groupKeyֻ���̳߳ظ�����Ч
		metaHolder.setDefaultGroupKey(metaHolder.getDefaultGroupKey()+"2");
		//commandKey����ź���������Ч
		metaHolder.setDefaultCommandKey(metaHolder.getDefaultCommandKey()+"aaa");
		
		System.out.println("==========================================================");
		Thread.sleep(1000);
		for (int i = 0; i < 150; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					//System.out.println(service.get(TEST_STR));
					System.out.println(service.get(TEST_STR));
				}
			}).start();
			;

		}
		Thread.sleep(20000);
		
	}

	@Test
	public void testerrorThresholdPercentage() throws Exception {
		for (int i = 0; i < 100; i++) {
			try{
				
				System.out.println("index" + i + " : " + service.exceptionWithFallback(TEST_STR));
			}catch (MyRuntimeException e) {
				System.out.println("MyRuntimeException=================="+i+" "+e.getMessage());
			}catch (Exception e) {
				System.out.println("Exception=================="+i+"   "+e.getMessage());
			}
			
			Thread.sleep(1000);
			
		}
	}

}
