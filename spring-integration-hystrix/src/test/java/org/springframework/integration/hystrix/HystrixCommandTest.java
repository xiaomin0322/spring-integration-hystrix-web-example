package org.springframework.integration.hystrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationConfig.xml" })
public class HystrixCommandTest {

	private static final String TEST_STR = "TEST_STR";
	@Autowired
	@Qualifier(value = "hystrixCommandServiceImpl")
	private Service service;

	@Test
	public void testHystrix() {
		assertEquals(TEST_STR, service.get(TEST_STR));
	}

	@Test
	public void testTimeoutHystrix() {
		long start = System.currentTimeMillis();
		try {
			service.withTimeout(TEST_STR);
		} catch (CircuitBreakerTimeoutException e) {
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

	@Test(expected = MyException.class)
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
		for (int i = 0; i < 100; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println(service.get(TEST_STR));
				}
			}).start();
			;

		}

		Thread.sleep(10000);
	}

	@Test
	/**
	 * 熔断测试
	 * circuitBreaker.requestVolumeThreshold 熔断触发的最小个数/10s 默认值：20
	 * circuitBreaker.sleepWindowInMilliseconds 熔断多少秒后去尝试请求 默认值：5000
	 * circuitBreaker.errorThresholdPercentage 失败率达到多少百分比后熔断 默认值：50
	 * 主要根据依赖重要性进行调整 circuitBreaker.forceClosed 是否强制关闭熔断 如果是强依赖，应该设置为true 
	 * @throws MyException
	 */
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
