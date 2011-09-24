package net.minecraft.src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Properties;


public class TransTool {

	private static final Properties config;

	private static boolean TRANS_MOD_IS_LOADED = false;

	private static AbstractTransMod tm = null;

	public static AbstractTransMod getTransMod() {
		return tm;
	}
	public static boolean addinTrans=false;
	public static String[] addinTransFiles=null;
	
	static {
		String modName = null;
		config = new Properties();
		try {
	    	InputStreamReader isr=new InputStreamReader(TransTool.class.getResourceAsStream("config.properties"), "UTF-8");
	    	BufferedReader br=new BufferedReader(isr);
	    	config.load(br);   
			br.close();
			isr.close();
			modName = config.getProperty("translate.mod.name");
		} catch (Exception e) {
			System.out.println("Loaded config file faild.");
		}
		if(config.getProperty("code").toLowerCase().equals("auto")){
			GuiScreen.code=java.nio.charset.Charset.defaultCharset().toString().toLowerCase();
		}else{
			GuiScreen.code=config.getProperty("code");
		}
		if("true".equalsIgnoreCase(config.getProperty("mod.trans"))){
			trySetValue("ModLoader","modTrans",true);
		}
		String filesStr = config.getProperty("addin.trans.files");
		if(filesStr != null) {
			addinTrans=true;
			addinTransFiles = filesStr.split("\\|");
		}
		if("true".equalsIgnoreCase(config.getProperty("mod.trans.out"))){
			trySetValue("ModLoader","modTransOut",true);
		}

		try {
			tm = (AbstractTransMod)Class.forName(modName).newInstance();
			TRANS_MOD_IS_LOADED  = true;
		} catch (Exception e) {
			System.out.println("Class<" + modName + "> Loaded faild!");
			e.printStackTrace();
		}
	}
	public static Properties getStringTranslateProps() {
		return (Properties) getPrivateValue(StringTranslate.class, StringTranslate.getInstance(), 1);//StringTranslate.a()=StringTranslate.getInstance()

	}
	
	/**利用反射获得私有对象*/
	public static Object getPrivateValue(Class instanceclass, Object instance,
			int fieldindex) {
		try {
			Field f = instanceclass.getDeclaredFields()[fieldindex];
			f.setAccessible(true);
			return f.get(instance);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**利用反射尝试设置可能存在的对象的值*/
	public static void trySetValue(String clsName, String fieldName,
			Object value) {
		try {
			Class cls = Class.forName(clsName);
			Field f = cls.getField(fieldName);
			f.setAccessible(true);
			f.set(cls, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isTrans() {
		return "true".equalsIgnoreCase(config.getProperty("is.translate")) && TRANS_MOD_IS_LOADED;//Boolean.parseBoolean()
	}

	public static String getValue(String key) {
		return config.getProperty(key);
	}


}
