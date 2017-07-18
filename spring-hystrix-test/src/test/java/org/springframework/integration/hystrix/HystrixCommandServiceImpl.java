package org.springframework.integration.hystrix;

import java.util.concurrent.Future;

import rx.Observable;
import rx.Subscriber;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;

@org.springframework.stereotype.Service("hystrixCommandServiceImpl")
public class HystrixCommandServiceImpl implements Service {

	public static final int TEST_TIMEOUT = 2000;

	@Override
	@HystrixCommand(
			commandProperties={ @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE"),
					@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
					@HystrixProperty(name = "execution.isolation.semaphore.maxConcurrentRequests", value = "10")
					}
			)
	public String get(String str) {
		return str;
	}
	
	@Override
	@HystrixCommand(
			commandProperties={  @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD") },
			
			threadPoolProperties = { @HystrixProperty(name = "coreSize", value = "5"),
			@HystrixProperty(name = "maxQueueSize", value = "20") })
	public String get2(String str) {
		return str;
	}

	@Override
	@HystrixCommand
	public String throwException() throws MyException {
		throw new MyException();
	}

	@Override
	@HystrixCommand(commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = TEST_TIMEOUT + "") })
	public String withTimeout(String str) {
		try {
			Thread.sleep(2 * TEST_TIMEOUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return str;
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "0") })
	public String withZeroTimeout(String str) {
		try {
			Thread.sleep(2 * TEST_TIMEOUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return str;
	}

	@Override
	// executionIsolationStrategy
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD") })
	public int getThreadId() {
		return Thread.currentThread().hashCode();
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE") })
	public int getNonThreadedThreadThreadId() {
		return Thread.currentThread().hashCode();
	}

	@Override
	//@HystrixCommand(/*fallbackMethod = "fallback"*//*,ignoreExceptions={MyRuntimeException.class}*/)
	@HystrixCommand(
			fallbackMethod = "fallback",
			commandProperties={
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "4"),
			@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"),
			@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")})
	public String exceptionWithFallback(String s) {
		System.out.println("exceptionWithFallback>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		throw new MyRuntimeException();
	}

	public String fallback(String s) {
		return s;
	}

	@Override
	@HystrixCommand(fallbackMethod = "fallbackWithException"/*,ignoreExceptions={MyRuntimeException.class}*/)
	public Throwable exceptionWithFallbackIncludingException(String testStr) {
		throw new MyRuntimeException();
	}

	public Throwable fallbackWithException(String testStr, Throwable t) {
		return t;
	}

	@Override
	@HystrixCommand
	public Future<String> getFuture(final String str) {
		return new AsyncResult<String>() {
			@Override
			public String invoke() {
				return str;
			}
		};
	}

	@Override
	@HystrixCommand
	public Observable<String> getObservable(final String str) {
		 return Observable.create(new Observable.OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> observer) {
				 try {
                     if (!observer.isUnsubscribed()) {
                         observer.onNext(str);
                         observer.onCompleted();
                     }		
                 } catch (Exception e) {
                     observer.onError(e);
                 }
				
			}
         }); 
	}
}
