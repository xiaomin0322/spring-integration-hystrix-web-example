package javassist;

import javax.annotation.Resource;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/** 
 * EntityManager的实例化 
 * @author 陈丽娜 
 * @version 1.0.0 , 2015年3月30日 下午8:43:27 
 * @param <T> 
 */  
public class CollectionBase<T>{  
    /** 
     * 注入实体单元 
     */  
	@Resource(name="123")
    protected CollectionBase em;  

    @HystrixCommand(groupKey="123")
    protected CollectionBase getEntityManager() {          
        return this.em;  
    }  
}