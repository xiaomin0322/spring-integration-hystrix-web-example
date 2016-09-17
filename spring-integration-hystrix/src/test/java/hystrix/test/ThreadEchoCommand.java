package hystrix.test;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixRequestCache;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;


/**
 * 请求缓存（Request Cache）：即当用户调用HystrixCommand时，HystrixCommand直接从缓存中取而不需要调用外部服务。HystrixCommand从缓存中取需要3个条件： 
1. 该HystrixCommand被包裹一个HystrixRequestContext中 
2. 该HystrixCommand实现了getCacheKey()方法 
3. 在HystrixRequestContext中已有相同Cache Key值的缓存 
 * @author Administrator
 *
 */
public class ThreadEchoCommand extends HystrixCommand<String> {
	private String input;

	protected ThreadEchoCommand(String input) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("EchoGroup"))
				.andCommandKey(HystrixCommandKey.Factory.asKey("Echo"))
				//.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("EchoThreadPool"))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(5)));
		this.input = input;
	}
	
	protected ThreadEchoCommand(String input,Setter setter) {
		super(setter);
		this.input = input;
	}
	
	
	/**
	 * 必须初始化 HystrixRequestContext context = HystrixRequestContext.initializeContext();
	 */
	/*@Override
	protected String getCacheKey() {
		return input;
	}*/

	public static void flushCache(String cacheKey) {
		HystrixRequestCache
				.getInstance(HystrixCommandKey.Factory.asKey("Echo"), HystrixConcurrencyStrategyDefault.getInstance())
				.clear(cacheKey);
	}

	@Override
	protected String run() throws Exception {
		return "Echo: " + input;
	}

}