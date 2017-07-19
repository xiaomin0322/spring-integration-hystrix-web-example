package hystrix.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Future;

import org.junit.Test;

import com.netflix.hystrix.HystrixCommand.Setter;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

public class HystrixTest {

	@Test
	public void collapseCommandTest() throws Exception {
		HystrixRequestContext context = HystrixRequestContext.initializeContext();

		try {
			Future<String> result1 = new CollapseEchoHystrixCommand("xianlinbox-1").queue();
			Future<String> result2 = new CollapseEchoHystrixCommand("xianlinbox-2").queue();
			Future<String> result3 = new CollapseEchoHystrixCommand("xianlinbox-3").queue();

			assertEquals(result1.get(), "Echo: xianlinbox-1");
			assertEquals(result2.get(), "Echo: xianlinbox-2");
			assertEquals(result3.get(), "Echo: xianlinbox-3");
			
			System.out.println(HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString());
			
			System.out.println(HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
			//assertEquals(1, HystrixRequestLog.getCurrentRequest().getExecutedCommands().size());
		} finally {
			context.shutdown();
		}
	}
	
	@Test
	public void testThreadUpdateCoreSize() throws Exception {
		for (int i = 0; i < 10; i++) {
			int size = 5;
			if(i>1){
				size = 14;
			}
			Setter setter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("EchoGroup"))
					.andCommandKey(HystrixCommandKey.Factory.asKey("Echo"))
					//com.netflix.hystrix.HystrixThreadPool ����key�����̸߳���,ΪnullʱĬ����withGroupKey
					//com.netflix.hystrix.HystrixThreadPool.Factory
					//com.netflix.hystrix.HystrixThreadPool.Factory.getInstance(HystrixThreadPoolKey, Setter)EEEEE
					//根据threadKey获取对象的线程池隔离对象
					.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("EchoThreadPool"+i))
					.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
							.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD))
					.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(size));
			// һ��ʵ��ֻ�ܱ�����һ��
			final ThreadEchoCommand command1 = new ThreadEchoCommand("xianlinbox",setter);
			System.out.println("getThreadPoolKey:"+command1.getThreadPoolKey().name());
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println(command1.execute());
				}
			}).start();
		}

		Thread.sleep(10000);
	}
	
	@Test
	public void testSEMAPHOREUpdateCoreSize() throws Exception {
		for (int i = 0; i < 50; i++) {
			Setter setter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("EchoGroup"))
					//洗脑量根据commandKey获取信号量隔离对象
					//.andCommandKey(HystrixCommandKey.Factory.asKey("Echo"+i))
					.andCommandKey(HystrixCommandKey.Factory.asKey("Echo"))
					//com.netflix.hystrix.HystrixThreadPool ����key�����̸߳���,ΪnullʱĬ����withGroupKey
					//com.netflix.hystrix.HystrixThreadPool.Factory
					//com.netflix.hystrix.HystrixThreadPool.Factory.getInstance(HystrixThreadPoolKey, Setter)EEEEE
					//根据threadKey获取对象的线程池隔离对象
					.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("EchoThreadPool"))
					.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
							.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE))
					.andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationSemaphoreMaxConcurrentRequests(2))
					.andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationThreadTimeoutInMilliseconds(2000));	
					//线程池属性不生效
					//.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(size));
			// һ��ʵ��ֻ�ܱ�����һ��
			final ThreadEchoCommand command1 = new ThreadEchoCommand("xianlinbox",setter);
			System.out.println("getThreadPoolKey:"+command1.getThreadPoolKey().name());
			System.out.println("CommandKey:"+command1.getCommandKey().name());
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(command1.execute());
				}
			}).start();
		}

		Thread.sleep(10000);
	}

	@Test
	public void testThread() throws Exception {
		// final ThreadEchoCommand command1 = new
		// ThreadEchoCommand("xianlinbox");
		final ThreadEchoCommand command2 = new ThreadEchoCommand("xianlinbox");

		for (int i = 0; i < 10; i++) {
			// һ��ʵ��ֻ�ܱ�����һ��
			final ThreadEchoCommand command1 = new ThreadEchoCommand("xianlinbox");
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("ThreadKey==="+command1.getThreadPoolKey().name());
					System.out.println(command1.execute());
				}
			}).start();
		}

		Thread.sleep(10000);
	}

	@Test
	public void testCache() throws Exception {
		/*
		 * HystrixRequestContext context =
		 * HystrixRequestContext.initializeContext(); try {
		 */
		ThreadEchoCommand command1 = new ThreadEchoCommand("xianlinbox");
		ThreadEchoCommand command2 = new ThreadEchoCommand("xianlinbox");

		assertEquals(command1.execute(), "Echo: xianlinbox");
		assertEquals(command1.isResponseFromCache(), false);
		assertEquals(command2.execute(), "Echo: xianlinbox");
		System.out.println("command2.isResponseFromCache():" + command2.isResponseFromCache());
		assertEquals(command2.isResponseFromCache(), true);
		/*
		 * } finally { context.shutdown(); }
		 */

	}

	@Test
	public void requestCache() throws Exception {
		HystrixRequestContext context = HystrixRequestContext.initializeContext();
		try {
			ThreadEchoCommand command1 = new ThreadEchoCommand("xianlinbox");
			ThreadEchoCommand command2 = new ThreadEchoCommand("xianlinbox");

			assertEquals(command1.execute(), "Echo: xianlinbox");
			assertEquals(command1.isResponseFromCache(), false);
			assertEquals(command2.execute(), "Echo: xianlinbox");
			System.out.println("command2.isResponseFromCache():" + command2.isResponseFromCache());
			assertEquals(command2.isResponseFromCache(), true);
		} finally {
			context.shutdown();
		}

		context = HystrixRequestContext.initializeContext();
		try {
			ThreadEchoCommand command3 = new ThreadEchoCommand("xianlinbox");
			assertEquals(command3.execute(), "Echo: xianlinbox");
			assertEquals(command3.isResponseFromCache(), false);
		} finally {
			context.shutdown();
		}
	}

	@Test
	public void flushCacheTest() throws Exception {
		HystrixRequestContext context = HystrixRequestContext.initializeContext();
		try {
			ThreadEchoCommand command1 = new ThreadEchoCommand("xianlinbox");
			ThreadEchoCommand command2 = new ThreadEchoCommand("xianlinbox");

			assertEquals(command1.execute(), "Echo: xianlinbox");
			assertEquals(command1.isResponseFromCache(), false);
			assertEquals(command2.execute(), "Echo: xianlinbox");
			assertEquals(command2.isResponseFromCache(), true);

			ThreadEchoCommand.flushCache("xianlinbox");
			ThreadEchoCommand command3 = new ThreadEchoCommand("xianlinbox");
			assertEquals(command3.execute(), "Echo: xianlinbox");
			assertEquals(command3.isResponseFromCache(), false);
		} finally {
			context.shutdown();
		}
	}

}
