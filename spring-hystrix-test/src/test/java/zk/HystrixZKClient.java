package zk;

/**
 * 客户端测试
 * 
 * @author liuyuehu
 */
public class HystrixZKClient {

	public static final String ROOTPATH = "/hystrix";
	public static ZkServer zkServer = new ZkServerImpl();
	private static final String hosts = "172.26.11.151:2181";
	static{
		try {
			zkServer.init(hosts);
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