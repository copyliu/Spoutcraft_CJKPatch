// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class StringTranslate
{

	private StringTranslate() {
		translateTable = new Properties();
		try {//修改以支持UTF8编码
			InputStreamReader isr = new InputStreamReader(StringTranslate.class.getResourceAsStream("/lang/en_US.lang"), "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			translateTable.load(br);
			br.close();
			isr.close();
			isr = new InputStreamReader(StringTranslate.class.getResourceAsStream("/lang/stats_US.lang"), "UTF-8");
			br = new BufferedReader(isr);
			translateTable.load(br);
			br.close();
			isr.close();
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
  }
  
  
    public static StringTranslate getInstance()
    {
        return instance;
    }

    public String translateKey(String s)
    {
        return translateTable.getProperty(s, s);
    }

    public String translateKeyFormat(String s, Object aobj[])
    {
        String s1 = translateTable.getProperty(s, s);
        return String.format(s1, aobj);
    }

    public String translateNamedKey(String s)
    {
        return translateTable.getProperty((new StringBuilder()).append(s).append(".name").toString(), "");
    }

    private static StringTranslate instance = new StringTranslate();
    private Properties translateTable;

}
