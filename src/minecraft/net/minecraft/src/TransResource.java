package net.minecraft.src;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Properties;

public class TransResource {
	
	// 中文
	public static StringBuffer chineseStr = new StringBuffer();
	
	// 额外
	public static StringBuffer exStr = new StringBuffer();
	static{
		 try
	        {//缓存汉化用字符
			   Properties props = TransTool.getStringTranslateProps();
			   
			   HashSet<Character> set = new HashSet<Character>(1024);
			   if(TransTool.addinTrans){
				   for(String file:TransTool.addinTransFiles) {
					   try
					   {
						   InputStreamReader isr=new InputStreamReader(TransTool.class.getResourceAsStream("lang/" + file), "UTF-8");
						   BufferedReader br=new BufferedReader(isr);
						   props.load(br);
						   br.close();
						   isr.close();
					   }
					   catch(IOException ioexception) {
							System.out.println("Loaded addin trans file: \"" + file + "\" faild.");
					   }
				   }
			   }
			   for(Object str:props.values()) {
				   if(str != null) {
					   for(Character ch:((String) str).toCharArray()) {
						   if(CharacterUtils.isAddin(ch)) {
							   set.add(ch);
						   }
					   }
				   }
			   }
			   
			   for(Character ch:set) {
				   chineseStr.append(ch);
			   }
	        }
	        catch(Exception e)
	        {
	            System.out.println("加载本地化配置异常。");
	            e.printStackTrace();
	        }
	}
}
