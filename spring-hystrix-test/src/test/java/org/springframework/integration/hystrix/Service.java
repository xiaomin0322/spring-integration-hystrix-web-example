package org.springframework.integration.hystrix;

import java.util.concurrent.Future;

public interface Service {
	public String get(String str);
	public String get2(String str);
	public String throwException() throws MyException;
	public String withTimeout(String str);
	public int getThreadId();
	public int getNonThreadedThreadThreadId();
	String withZeroTimeout(String str);
	public String exceptionWithFallback(String testStr);
	public Throwable exceptionWithFallbackIncludingException(String testStr);
	
	
	public Future<String> getFuture(String str);
	
	public rx.Observable<String> getObservable(String str);
}
