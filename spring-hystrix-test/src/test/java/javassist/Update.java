package javassist;

import java.util.ArrayList;
import java.util.List;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

public class Update {
	
	public static void main(String[] args)throws Exception {
		update3();
	}
	
	
	public static void update()throws Exception{
		  ClassPool pool = ClassPool.getDefault();      
          //鑾峰彇闇�瑕佷慨鏀圭殑绫�  
          CtClass ct = pool.get("org.springframework.integration.hystrix.HystrixCommandServiceImpl");   
            
          //鑾峰彇绫婚噷鐨勬墍鏈夋柟娉�  
          CtMethod[] cms = ct.getDeclaredMethods();  
          CtMethod cm = cms[1];      
          System.out.println("鏂规硶鍚嶇О====" + cm.getName());  
            
          MethodInfo minInfo = cm.getMethodInfo();  
          
         /* CtMethod ctMethod= ct.getMethod("get2", "java.lang.String");
          
          minInfo = ctMethod.getMethodInfo();*/
            
            
          ConstPool cp = minInfo.getConstPool();  
          //鑾峰彇娉ㄨВ淇℃伅  
          AnnotationsAttribute attribute2 = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);  
          Annotation annotation = new Annotation("com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand", cp);  
          
          //淇敼鍚嶇О涓簎nitName鐨勬敞瑙�  
          annotation.addMemberValue("groupKey", new StringMemberValue("123", cp));  
          attribute2.setAnnotation(annotation);  
          minInfo.addAttribute(attribute2);  
            
          //鎵撳嵃淇敼鍚庢柟娉�  
          Annotation annotation2 = attribute2.getAnnotation("com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand");  
          String text = ((StringMemberValue)annotation2.getMemberValue("groupKey")).getValue();  
          System.out.println("淇敼鍚庣殑娉ㄨВ鍚嶇О===" + text);  
          
          com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand command= (HystrixCommand) annotation2.toAnnotationType(Thread.currentThread().getContextClassLoader(), pool);
          
          System.out.println(command.groupKey());
          
	}
	
	
	public static HystrixCommand update2()throws Exception{
		  ClassPool pool = ClassPool.getDefault();      
        //鑾峰彇闇�瑕佷慨鏀圭殑绫�  
        CtClass ct = pool.get("org.springframework.integration.hystrix.HystrixCommandServiceImpl");   
          
        //鑾峰彇绫婚噷鐨勬墍鏈夋柟娉�  
        CtMethod[] cms = ct.getDeclaredMethods();  
        CtMethod cm = cms[1];      
        System.out.println("鏂规硶鍚嶇О====" + cm.getName());  
          
        MethodInfo minInfo = cm.getMethodInfo();  
        
        
        AnnotationsAttribute attribute = (AnnotationsAttribute) minInfo.getAttribute(AnnotationsAttribute.visibleTag);  
        
        /*System.out.println(attribute.getAnnotations().length);
        
        for(Annotation a:attribute.getAnnotations()){
        	System.out.println(a.getClass());
        }*/
        ConstPool cp = minInfo.getConstPool();  
          
        //鎵撳嵃淇敼鍚庢柟娉�  
        Annotation annotation2 = attribute.getAnnotation("com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand");  
       /* String text = ((StringMemberValue)annotation2.getMemberValue("groupKey")).getValue();  
        System.out.println("淇敼鍚庣殑娉ㄨВ鍚嶇О===" + text);  */
        System.out.println(annotation2);
        
        
      //淇敼鍚嶇О涓簎nitName鐨勬敞瑙�  
      /*  annotation2.addMemberValue("groupKey", new StringMemberValue("123456", cp));  
        attribute.setAnnotation(annotation2);  
        minInfo.addAttribute(attribute); */
        
        //鏁扮粍绫诲瀷娉ㄨВ
        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(cp);
        
        List<MemberValue> list = new ArrayList<MemberValue>();
        //鍗曚釜娉ㄨВ
        Annotation annotationHystrixProperty = new Annotation("com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty", cp);  
        annotationHystrixProperty.addMemberValue("name",new StringMemberValue("coreSize", cp));  
        annotationHystrixProperty.addMemberValue("value",new StringMemberValue("200", cp));  
        
        AnnotationMemberValue annotationMemberValueHystrixProperty = new AnnotationMemberValue(cp);
        annotationMemberValueHystrixProperty.setValue(annotationHystrixProperty);
        list.add(annotationMemberValueHystrixProperty);
        
        annotationHystrixProperty = new Annotation("com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty", cp);  
        annotationHystrixProperty.addMemberValue("name",new StringMemberValue("maxQueueSize", cp));  
        annotationHystrixProperty.addMemberValue("value",new StringMemberValue("520", cp));  
        
        annotationMemberValueHystrixProperty = new AnnotationMemberValue(cp);
        annotationMemberValueHystrixProperty.setValue(annotationHystrixProperty);
        list.add(annotationMemberValueHystrixProperty);
        
        
        arrayMemberValue.setValue(list.toArray(new MemberValue[]{}));
        
        
        
        
        
        annotation2.addMemberValue("threadPoolProperties",arrayMemberValue);  
        attribute.setAnnotation(annotation2);  
        minInfo.addAttribute(attribute); 
        
        com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand command= (HystrixCommand) annotation2.toAnnotationType(Thread.currentThread().getContextClassLoader(), pool);
        
        System.out.println(command);
        
        return command;
        
	}
	
	public static HystrixCommand update3()throws Exception{
		  ClassPool pool = ClassPool.getDefault();      
      //鑾峰彇闇�瑕佷慨鏀圭殑绫�  
      CtClass ct = pool.get("org.springframework.integration.hystrix.HystrixCommandServiceImpl");   
        
      //鑾峰彇绫婚噷鐨勬墍鏈夋柟娉�  
      CtMethod[] cms = ct.getDeclaredMethods();  
      CtMethod cm = cms[0];      
      System.out.println("鏂规硶鍚嶇О====" + cm.getName());  
        
      MethodInfo minInfo = cm.getMethodInfo();  
      
      
      AnnotationsAttribute attribute = (AnnotationsAttribute) minInfo.getAttribute(AnnotationsAttribute.visibleTag);  
      
      /*System.out.println(attribute.getAnnotations().length);
      
      for(Annotation a:attribute.getAnnotations()){
      	System.out.println(a.getClass());
      }*/
      ConstPool cp = minInfo.getConstPool();  
        
      //鎵撳嵃淇敼鍚庢柟娉�  
      Annotation annotation2 = attribute.getAnnotation("com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand");  
     /* String text = ((StringMemberValue)annotation2.getMemberValue("groupKey")).getValue();  
      System.out.println("淇敼鍚庣殑娉ㄨВ鍚嶇О===" + text);  */
      System.out.println(annotation2);
      
      
    //淇敼鍚嶇О涓簎nitName鐨勬敞瑙�  
    /*  annotation2.addMemberValue("groupKey", new StringMemberValue("123456", cp));  
      attribute.setAnnotation(annotation2);  
      minInfo.addAttribute(attribute); */
      
      //鏁扮粍绫诲瀷娉ㄨВ
      ArrayMemberValue arrayMemberValue = new ArrayMemberValue(cp);
      
      List<MemberValue> list = new ArrayList<MemberValue>();
      //鍗曚釜娉ㄨВ
      Annotation annotationHystrixProperty = new Annotation("com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty", cp);  
      annotationHystrixProperty.addMemberValue("name",new StringMemberValue("execution.isolation.thread.timeoutInMilliseconds", cp));  
      annotationHystrixProperty.addMemberValue("value",new StringMemberValue("2000", cp));  
      AnnotationMemberValue annotationMemberValueHystrixProperty = new AnnotationMemberValue(cp);
      annotationMemberValueHystrixProperty.setValue(annotationHystrixProperty);
      list.add(annotationMemberValueHystrixProperty);
      
      annotationHystrixProperty = new Annotation("com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty", cp);  
      annotationHystrixProperty.addMemberValue("name",new StringMemberValue("execution.isolation.semaphore.maxConcurrentRequests", cp));  
      annotationHystrixProperty.addMemberValue("value",new StringMemberValue("200", cp));  
      annotationMemberValueHystrixProperty = new AnnotationMemberValue(cp);
      annotationMemberValueHystrixProperty.setValue(annotationHystrixProperty);
      list.add(annotationMemberValueHystrixProperty);
      
      
      annotationHystrixProperty = new Annotation("com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty", cp);  
      annotationHystrixProperty.addMemberValue("name",new StringMemberValue("execution.isolation.strategy", cp));  
      annotationHystrixProperty.addMemberValue("value",new StringMemberValue("SEMAPHORE", cp));  
      annotationMemberValueHystrixProperty = new AnnotationMemberValue(cp);
      annotationMemberValueHystrixProperty.setValue(annotationHystrixProperty);
      list.add(annotationMemberValueHystrixProperty);
      
      
      arrayMemberValue.setValue(list.toArray(new MemberValue[]{}));
      
      
      
      
      
      annotation2.addMemberValue("commandProperties",arrayMemberValue);  
      attribute.setAnnotation(annotation2);  
      minInfo.addAttribute(attribute); 
      
      com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand command= (HystrixCommand) annotation2.toAnnotationType(Thread.currentThread().getContextClassLoader(), pool);
      
      System.out.println(command);
      
      return command;
      
	}

}
