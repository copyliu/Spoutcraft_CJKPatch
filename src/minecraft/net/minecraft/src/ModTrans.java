package net.minecraft.src;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import net.minecraft.client.Minecraft;


public class ModTrans {
	static PrintStream printstream;
	static PrintStream printstreamEn;
	public static void initModOut(){
		File f = Minecraft.getMinecraftDir();
		String filenameEn="ModTransTextEn.txt";
		String filename="ModTransText.txt";
        String pathEn = f.getPath()+"/"+filenameEn;
        String path = f.getPath()+"/"+filename;
        File fileEn = new File(pathEn);
        File file = new File(path);
        try
        {
            fileEn.delete();
            fileEn.createNewFile();
        }
        catch(IOException ioexception)
        {
            System.out.println("[ModTrans] Unable to load "+pathEn+"!");
        }
        FileOutputStream fileoutputstreamEn = null;
        try
        {
            fileoutputstreamEn = new FileOutputStream(pathEn);
        }
        catch(FileNotFoundException filenotfoundexception)
        {
            System.out.println("[ModTrans] Unable to open "+pathEn+"!");
        }
        try
        {
            printstreamEn = new PrintStream(fileoutputstreamEn, true, "UTF-8");
        }
        catch(UnsupportedEncodingException unsupportedencodingexception)
        {
            System.out.println("[ModTrans] Unable to write to "+pathEn+"!");
        }
        
        try
        {
            file.delete();
            file.createNewFile();
        }
        catch(IOException ioexception)
        {
            System.out.println("[ModTrans] Unable to load "+path+"!");
        }
        FileOutputStream fileoutputstream = null;
        try
        {
            fileoutputstream = new FileOutputStream(path);
        }
        catch(FileNotFoundException filenotfoundexception)
        {
            System.out.println("[ModTrans] Unable to open "+path+"!");
        }
        try
        {
            printstream = new PrintStream(fileoutputstream, true, "UTF-8");
        }
        catch(UnsupportedEncodingException unsupportedencodingexception)
        {
            System.out.println("[ModTrans] Unable to write to "+path+"!");
        }
	}
	public static void TransOut(String name,String valueEn,String value){
		printstreamEn.println(name + " = " + valueEn);
		printstream.println(name + " = " + value);
	}
	public static void unInitModOut(){
        printstreamEn.close();
        printstream.close();
	}

}
