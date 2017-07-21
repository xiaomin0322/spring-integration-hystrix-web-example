package zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * 客户端测试
 * 
 * @author liuyuehu
 */
public class HystrixZKClient {

	public static final String ROOTPATH = "/hystrix";
	public static ZkServer zkServer = new ZkServerImpl();
	private static final String hosts = "localhost:2181";
	static{
		try {
			zkServer.init(hosts);
			Thread.sleep(3000);
			appendPresistentNode(ROOTPATH, ROOTPATH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void appendEphemeralNode(String path, String date) throws Exception {
		
		
		
		if (!zkServer.exist(path)) {
			zkServer.appendEphemeralNode(path, date);
			System.out.println("成功创建[" + path + "]节点!");
		}
		
	}
	
	public static void appendPresistentNode(String path, String date) throws Exception {
		ZkServer zkServer = new ZkServerImpl();
		zkServer.init(hosts);
		
		if (!zkServer.exist(path)) {
			zkServer.appendPresistentNode(path, date);
			System.out.println("成功创建[" + path + "]节点!");
		}
		
	}

	

	public static void main(String[] args) throws Exception {
		appendPresistentNode(ROOTPATH, "123");
		appendEphemeralNode(ROOTPATH+"/test", "123");
		Thread.sleep(10000);
	}
}