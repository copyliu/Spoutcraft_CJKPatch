package net.minecraft.src;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CharacterUtils {

	public static final char COLOR_PREX = '\247';
	public static final String COLOR_BLACK = COLOR_PREX + "0";
	public static final String COLOR_BLUE = COLOR_PREX + "1";
	public static final String COLOR_GREEN = COLOR_PREX + "2";
	public static final String COLOR_RED = COLOR_PREX + "4";
	public static final String COLOR_WITE = COLOR_PREX + "7";

	public static final char ch[] = {
		'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', 
		'<', '>', '|', '"', ':'
	};

	public static boolean isAddin(char ch) {
		//因为GBK中非汉字部分对应的unicode编码过于散乱，因此切换为白名单模式
		if(ch<0x80||ch==0x192||ch==0x2302||ch=='\247')//修复彩色字
			return false;
		if(ch<=0xff) {
			if(ChatAllowedCharacters.originalCharacters.indexOf(ch) >= 0) {
				return false;
			}
		}

		return true;
	}

	/*//暂未使用,仍需修改
	public static boolean isAllowed(char ch) {
		if(isNew(ch)) {
			return true;
		} else if(ChatAllowedCharacters.allowedCharacters.indexOf(ch) >= 0) {
			return true;
		}
		return false;
	} */
}
