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
		update2();
	}
	
	
	public static void update()throws Exception{
		  ClassPool pool = ClassPool.getDefault();      
          //获取需要修改的类  
          CtClass ct = pool.get("org.springframework.integration.hystrix.HystrixCommandServiceImpl");   
            
          //获取类里的所有方法  
          CtMethod[] cms = ct.getDeclaredMethods();  
          CtMethod cm = cms[1];      
          System.out.println("方法名称====" + cm.getName());  
            
          MethodInfo minInfo = cm.getMethodInfo();  
          
         /* CtMethod ctMethod= ct.getMethod("get2", "java.lang.String");
          
          minInfo = ctMethod.getMethodInfo();*/
            
            
          ConstPool cp = minInfo.getConstPool();  
          //获取注解信息  
          AnnotationsAttribute attribute2 = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);  
          Annotation annotation = new Annotation("com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand", cp);  
          
          //修改名称为unitName的注解  
          annotation.addMemberValue("groupKey", new StringMemberValue("123", cp));  
          attribute2.setAnnotation(annotation);  
          minInfo.addAttribute(attribute2);  
            
          //打印修改后方法  
          Annotation annotation2 = attribute2.getAnnotation("com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand");  
          String text = ((StringMemberValue)annotation2.getMemberValue("groupKey")).getValue();  
          System.out.println("修改后的注解名称===" + text);  
          
          com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand command= (HystrixCommand) annotation2.toAnnotationType(Thread.currentThread().getContextClassLoader(), pool);
          
          System.out.println(command.groupKey());
          
	}
	
	
	public static HystrixCommand update2()throws Exception{
		  ClassPool pool = ClassPool.getDefault();      
        //获取需要修改的类  
        CtClass ct = pool.get("org.springframework.integration.hystrix.HystrixCommandServiceImpl");   
          
        //获取类里的所有方法  
        CtMethod[] cms = ct.getDeclaredMethods();  
        CtMethod cm = cms[1];      
        System.out.println("方法名称====" + cm.getName());  
          
        MethodInfo minInfo = cm.getMethodInfo();  
        
        
        AnnotationsAttribute attribute = (AnnotationsAttribute) minInfo.getAttribute(AnnotationsAttribute.visibleTag);  
        
        /*System.out.println(attribute.getAnnotations().length);
        
        for(Annotation a:attribute.getAnnotations()){
        	System.out.println(a.getClass());
        }*/
        ConstPool cp = minInfo.getConstPool();  
          
        //打印修改后方法  
        Annotation annotation2 = attribute.getAnnotation("com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand");  
       /* String text = ((StringMemberValue)annotation2.getMemberValue("groupKey")).getValue();  
        System.out.println("修改后的注解名称===" + text);  */
        System.out.println(annotation2);
        
        
      //修改名称为unitName的注解  
      /*  annotation2.addMemberValue("groupKey", new StringMemberValue("123456", cp));  
        attribute.setAnnotation(annotation2);  
        minInfo.addAttribute(attribute); */
        
        //数组类型注解
        ArrayMemberValue arrayMemberValue = new ArrayMemberValue(cp);
        
        List<MemberValue> list = new ArrayList<MemberValue>();
        //单个注解
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

}
