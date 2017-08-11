package consul;
import java.util.Map;
import java.util.Map.Entry;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.ConsulRawClient;
import com.ecwid.consul.v1.agent.model.Service;
  
public class App {  
      
    public static void main(String[] args) {  
        ConsulRawClient client = new ConsulRawClient("192.168.1.234", 8500);  
        ConsulClient consul = new ConsulClient(client);  
        //获取所有服务  
        Map<String, Service> map = consul.getAgentServices().getValue();  
        
        //System.out.println(map);
        
        for(Entry<String, Service> en:map.entrySet()){
        	System.out.println(en.getKey()+" "+en.getValue());
        }
    }  
} 