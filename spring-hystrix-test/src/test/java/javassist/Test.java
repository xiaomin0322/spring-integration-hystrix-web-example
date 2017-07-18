package javassist;


import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;


public class Test {
	
	@org.junit.Test
	public void ReadTest() throws NotFoundException{  
	    ClassPool pool = ClassPool.getDefault();  
	    //获取要修改的类的所有信息  
	    CtClass ct = pool.get("javassist.CollectionBase");     
	    //获取类中的方法  
	    CtMethod[] cms = ct.getDeclaredMethods();         
	     //获取第一个方法（因为只有一个方法）  
	     CtMethod cm = cms[0];        
	     System.out.println("方法名称====" + cm.getName());        
	     //获取方法信息  
	     MethodInfo methodInfo = cm.getMethodInfo();       
	     //获取类里的em属性  
	     CtField cf = ct.getField("em");  
	     //获取属性信息  
	     FieldInfo fieldInfo = cf.getFieldInfo();          
	     System.out.println("属性名称===" + cf.getName());  
	       
	     //获取注解属性  
	     AnnotationsAttribute attribute = (AnnotationsAttribute) fieldInfo.getAttribute(AnnotationsAttribute.visibleTag);  
	     System.out.println(attribute);   
	     //获取注解  
	     Annotation annotation = attribute.getAnnotation("javax.annotation.Resource");         
	     System.out.println(annotation);  
	     //获取注解的值  
	     String text =((StringMemberValue) annotation.getMemberValue("name")).getValue() ;         
	     System.out.println("注解名称===" + text);  
	       
	}  
	
	@org.junit.Test
	public void ReadTest2() throws NotFoundException{  
	    ClassPool pool = ClassPool.getDefault();  
	    //获取要修改的类的所有信息  
	    CtClass ct = pool.get("javassist.CollectionBase");     
	    //获取类中的方法  
	    CtMethod[] cms = ct.getDeclaredMethods();         
	     //获取第一个方法（因为只有一个方法）  
	     CtMethod cm = cms[0];        
	     System.out.println("方法名称====" + cm.getName());        
	     //获取方法信息  
	     MethodInfo methodInfo = cm.getMethodInfo();       
	     //获取类里的em属性  
	     CtField cf = ct.getField("em");  
	     //获取属性信息  
	     FieldInfo fieldInfo = cf.getFieldInfo();          
	     System.out.println("属性名称===" + cf.getName());  
	       
	     //获取注解属性  
	     AnnotationsAttribute attribute = (AnnotationsAttribute) fieldInfo.getAttribute(AnnotationsAttribute.visibleTag);  
	     System.out.println(attribute);   
	     //获取注解  
	     Annotation annotation = attribute.getAnnotation("javax.annotation.Resource");         
	     System.out.println(annotation);  
	     //获取注解的值  
	     String text =((StringMemberValue) annotation.getMemberValue("name")).getValue() ;         
	     System.out.println("注解名称===" + text);  
	       
	}  
	
	@org.junit.Test
    public void UpdateTest() throws NotFoundException{  
           ClassPool pool = ClassPool.getDefault();      
           //获取需要修改的类  
           CtClass ct = pool.get("javassist.CollectionBase");   
             
           //获取类里的所有方法  
           CtMethod[] cms = ct.getDeclaredMethods();  
           CtMethod cm = cms[0];      
           System.out.println("方法名称====" + cm.getName());  
             
           MethodInfo minInfo = cm.getMethodInfo();  
           //获取类里的em属性  
           CtField cf = ct.getField("em");  
           FieldInfo fieldInfo = cf.getFieldInfo();    
             
           System.out.println("属性名称===" + cf.getName());  
             
           ConstPool cp = fieldInfo.getConstPool();  
           //获取注解信息  
           AnnotationsAttribute attribute2 = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);  
           Annotation annotation = new Annotation("javax.annotation.Resource", cp);  
           
           //修改名称为unitName的注解  
           annotation.addMemberValue("name", new StringMemberValue("456", cp));  
           attribute2.setAnnotation(annotation);  
           minInfo.addAttribute(attribute2);  
             
           //打印修改后方法  
           Annotation annotation2 = attribute2.getAnnotation("javax.annotation.Resource");  
           String text = ((StringMemberValue)annotation2.getMemberValue("name")).getValue();  
             
           System.out.println("修改后的注解名称===" + text);  
    }  

}
