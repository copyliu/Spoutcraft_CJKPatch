// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 
package net.minecraft.src;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ChatAllowedCharacters {

	public ChatAllowedCharacters() {
	}
	//cnmode start
	private static String getOriginalCharacters() {
		String s = "";
		try {
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(ChatAllowedCharacters.class.getResourceAsStream("/font.txt"), "UTF-8"));
			String s1 = "";
			do {
				String s2;
				if ((s2 = bufferedreader.readLine()) == null) {
					break;
				}
				if (!s2.startsWith("#")) {
					s = (new StringBuilder()).append(s).append(s2).toString();
				}
			} while (true);
			bufferedreader.close();
		} catch (Exception exception) {
		}
		return s;
	}

	//添加字符
	private static String getAddinCharacters() {


		String s = "";
		try {
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader((ChatAllowedCharacters.class).getResourceAsStream("/addin.txt"), "UTF-8"));
			do {
				String s2;
				if ((s2 = bufferedreader.readLine()) == null) {
					break;
				}
				if (!s2.startsWith("#")) {
					s = (new StringBuilder()).append(s).append(s2).toString();
				}
			} while (true);
			bufferedreader.close();

		} catch (Exception exception) {
		}
		return s;
	}

	private static String getAllowedCharacters() {
		String s = "";
		s = originalCharacters + getAddinCharacters();
		return s;
	}
	public static final String originalCharacters = getOriginalCharacters();
	//cnmode end
	public static final String allowedCharacters = getAllowedCharacters();
	public static final char allowedCharactersArray[] = {
		'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\',
		'<', '>', '|', '"', ':'
	};
}
