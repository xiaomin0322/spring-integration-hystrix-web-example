package org.springframework.integration.hystrix;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@org.springframework.stereotype.Service("hystrixCommandServiceImpl")
public class HystrixCommandServiceImpl implements Service {

	public static final int TEST_TIMEOUT = 2000;

	/**
	 * 
	 * 
	 * 配置线程池大小,默认值10个. 建议值:请求高峰时99.5%的平均响应时间 + 向上预留一些即可
	 *
	 * HystrixThreadPoolProperties.Setter().withCoreSize(int value)
	 * 配置线程值等待队列长度,默认值:-1 建议值:-1表示不等待直接拒绝,测试表明线程池使用直接决绝策略+
	 * 合适大小的非回缩线程池效率最高.所以不建议修改此值。
	 * 当使用非回缩线程池时，queueSizeRejectionThreshold,keepAliveTimeMinutes 参数无效
	 * HystrixThreadPoolProperties.Setter().withMaxQueueSize(int value)
	 */
	@Override
	@HystrixCommand(
			commandProperties={ @HystrixProperty(name = "executionIsolationStrategy", value = "SEMAPHORE") },
			
			threadPoolProperties = { @HystrixProperty(name = "coreSize", value = "50"),
			@HystrixProperty(name = "maxQueueSize", value = "-1") })
	public String get(String str) {
		return str;
	}

	@Override
	@HystrixCommand
	public String throwException() throws MyException {
		throw new MyException();
	}

	@Override
	@HystrixCommand(commandProperties = {
			@HystrixProperty(name = "executionTimeoutInMilliseconds", value = TEST_TIMEOUT + "") })
	public String withTimeout(String str) {
		try {
			Thread.sleep(2 * TEST_TIMEOUT);
		} catch (InterruptedException e) {
		}
		return str;
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "executionTimeoutInMilliseconds", value = "0") })
	public String withZeroTimeout(String str) {
		try {
			Thread.sleep(2 * TEST_TIMEOUT);
		} catch (InterruptedException e) {
		}
		return str;
	}

	@Override
	// executionIsolationStrategy
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "executionIsolationStrategy", value = "THREAD") })
	public int getThreadId() {
		return Thread.currentThread().hashCode();
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "executionIsolationStrategy", value = "SEMAPHORE") })
	public int getNonThreadedThreadThreadId() {
		return Thread.currentThread().hashCode();
	}

	@Override
	//@HystrixCommand(/*fallbackMethod = "fallback"*//*,ignoreExceptions={MyRuntimeException.class}*/)
	@HystrixCommand(
			fallbackMethod = "fallback",
			commandProperties={
			//4次异常短路
			@HystrixProperty(name = "circuitBreakerRequestVolumeThreshold", value = "4"),
			//5秒后发生重试
			@HystrixProperty(name = "circuitBreakerSleepWindowInMilliseconds", value = "5000")})
	public String exceptionWithFallback(String s) {
		System.out.println("熔断之后讲不会调用该方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		throw new MyRuntimeException();
	}

	public String fallback(String s) {
		return s;
	}

	@Override
	@HystrixCommand(fallbackMethod = "fallbackWithException")
	public Throwable exceptionWithFallbackIncludingException(String testStr) {
		throw new MyRuntimeException();
	}

	public Throwable fallbackWithException(String testStr, Throwable t) {
		return t;
	}
}
