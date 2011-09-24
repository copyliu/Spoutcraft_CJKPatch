package net.minecraft.src;



import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.lwjgl.opengl.GL11;


public class TransCN extends AbstractTransMod {

	private static final int TT_IMAGE_SIZE = 512;
	
	// 字体
	private static final Font font;

	// 字体对象
	private static final FontMetrics fm;

	// 字体大小
	private static final int FONT_SIZE;
	
	private final static int GAME_SIZE = 8;//游戏默认字号

	// 某纯粹用于计算的字体对象
	private static final FontMetrics fm_game;
	
	final int NORMALWIDTH = getCharWidth('张');//与字号相关，改动以修复部分内容显示的异常：如省略号
	
	private final int textImageId[] = new int[256];

	int ttMaxWidth = TT_IMAGE_SIZE / NORMALWIDTH;//getCharWidth('张')

	int ttMaxHeight = TT_IMAGE_SIZE / fm.getHeight();

	int ttPageMax = ttMaxWidth * ttMaxHeight;

	ExImage exImage;
	
	public static String Version() {
		return "2.2 for MC 1.8";
	}

	public static String ModName() {
		return "汉化组件" + Version();
	}

	
	static {
		// 初始化
		System.out.println(ModName()+"加载中...");
		System.out.println(TransResource.chineseStr);
		FONT_SIZE=Integer.parseInt(TransTool.getValue("font.size"));
		BufferedImage imgTemp = new BufferedImage(1, 1,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) imgTemp.getGraphics();
		g.setFont(new Font(TransTool.getValue("font.name"), Font.BOLD, GAME_SIZE));
		fm_game= g.getFontMetrics();
		font = new Font(TransTool.getValue("font.name"), Font.BOLD, FONT_SIZE);//调整为粗体
		g.setFont(font);
		fm = g.getFontMetrics();
	}
	
	@Override
	public void register(RenderEngine re) {
		super.register(re);
		createImage(TransResource.chineseStr.toString());
		exImage = new ExImage();
	}

	private void createImage(String string) {
		for(int page = 0; string.length() > ttPageMax*page; page++) {
			String str = string.substring(ttPageMax*page);
			BufferedImage imgTemp = new BufferedImage(TT_IMAGE_SIZE, TT_IMAGE_SIZE,
					BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g = (Graphics2D) imgTemp.getGraphics();
			g = imgTemp.createGraphics();
	        g.setColor(Color.WHITE);
			g.setFont(font);
			for (int i = 0, j = 0;; i++, j++) {
				if (j >= ttMaxHeight) {
					break;
				}
				int length = ttMaxWidth * (i + 1);
				if (length > str.length()) {
					//按字渲染，修正字符中间出现不等宽字符时的渲染错位
					String substr=str.substring(ttMaxWidth * i, str.length());
					int sublen=substr.length();
					for(int k=0;k<ttMaxWidth&&k<sublen;k++){
						g.drawString(substr.substring(k,k+1), k * NORMALWIDTH,
								fm.getHeight() * j + fm.getAscent());
					}
					/*g.drawString(str.substring(ttMaxWidth * i, str.length()), 0,
								fm.getHeight() * j + fm.getAscent());*/
					break;
				} else {
					//按字渲染，修正字符中间出现不等宽字符时的渲染错位
					String substr=str.substring(ttMaxWidth * i, length);
					for(int k=0;k<ttMaxWidth;k++){
						g.drawString(substr.substring(k,k+1), k * NORMALWIDTH,
								fm.getHeight() * j + fm.getAscent());
					}
					/*g.drawString(str.substring(ttMaxWidth * i, length), 0, 
							fm.getHeight() * j + fm.getAscent());*/
				}
			}
			textImageId[page] = renderEngine.allocateAndSetupTexture(imgTemp);//allocateAndSetupTexture
		}
	}

	
	/**
	 * 
	 * @param fr
	 * @param s
	 *            文字内容
	 * @param i
	 *            x坐标
	 * @param j
	 *            y坐标
	 * @param k
	 *            颜色？？
	 * @param flag
	 *            是否阴影
	 */
	public boolean renderString(FontRenderer fr, String s, int i, int j, int k,boolean flag) {
		if (s == null || s.trim().length() <= 0) {
			return true;
		}
		if (flag) {
			int l = k & 0xff000000;
			k = (k & 0xfcfcfc) >> 2;
			k += l;
		}
		GL11.glBindTexture(3553 /* GL_TEXTURE_2D */, fr.fontTextureName);
		float f = (float) (k >> 16 & 0xff) / 255F;
		float f1 = (float) (k >> 8 & 0xff) / 255F;
		float f2 = (float) (k & 0xff) / 255F;
		float f3 = (float) (k >> 24 & 0xff) / 255F;
		if (f3 == 0.0F) {
			f3 = 1.0F;
		}
		GL11.glColor4f(f, f1, f2, f3);

		fr.buffer.clear();
		GL11.glPushMatrix();
		GL11.glTranslatef(i, j, 0.0F); // 移动到指定位置
		for (int index = 0; index < s.length(); index++) {
			Character ch = s.charAt(index);//此处及下k1与原文件不同，因此下文为++而非+=2
			if (CharacterUtils.isAddin(ch)) {
				//TODO:修复对省略号处理的异常
				if(fr.buffer.position() > 0) {
            		fr.buffer.flip();
                    GL11.glCallLists(fr.buffer);
                    fr.buffer.clear();
        		}
				draw(ch);
				GL11.glTranslatef(fm_game.charWidth(ch), 0.0F, 0.0F);
				continue;
			}
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, fr.fontTextureName);

			for (; s.length() > index + 1 && s.charAt(index) == '\247'; index ++) {//见ch
				int j1 = "0123456789abcdef".indexOf(s.toLowerCase().charAt(
						index + 1));
				if (j1 < 0 || j1 > 15) {
					j1 = 15;
				}
				fr.buffer.put(fr.fontDisplayLists + 256 + j1 + (flag ? 16 : 0));
				if (fr.buffer.remaining() == 0) {
					fr.buffer.flip();
					GL11.glCallLists(fr.buffer);
					fr.buffer.clear();
				}
			}
			if (index < s.length()) {
				int k1 = ChatAllowedCharacters.originalCharacters.indexOf(ch);
				if (k1 >= 0) {
					fr.buffer.put(fr.fontDisplayLists + k1 + 32);
				}
			}
			if (fr.buffer.remaining() == 0) {
				fr.buffer.flip();
				GL11.glCallLists(fr.buffer);
				fr.buffer.clear();
			}
		}

		fr.buffer.flip();
		GL11.glCallLists(fr.buffer);
		GL11.glPopMatrix();
		return true;
	}

	public int getStringWidth(FontRenderer fr, String s) {
		int width = 0;
		if (s == null)
			return width;
		for (Character ch : s.toCharArray()) {
			if (CharacterUtils.isAddin(ch)) {
				width += fm_game.charWidth(ch);
			} else {
				if(ch == '\247') {//修复彩色字
	                continue;
	            }
				int k = ChatAllowedCharacters.originalCharacters.indexOf(ch);
				if (k >= 0) {
					width += fr.charWidth[k + 32];
				}
			}
		}
		return width;
	}
	
	public static int getCharWidth(Character ch) {
		return fm.charWidth(ch);//缩放
	}

	class ExImage {
		int page = 0;
		int[] exTextId = new int[256];
		int x=0;
		int y=0;
		BufferedImage imgTemp;
		Graphics2D g;
		public ExImage() {
			createImage();
		}
		private void createImage() {
			imgTemp = new BufferedImage(TT_IMAGE_SIZE, TT_IMAGE_SIZE,
					BufferedImage.TYPE_4BYTE_ABGR);
			g = (Graphics2D) imgTemp.getGraphics();
			g = imgTemp.createGraphics();
	        g.setColor(Color.WHITE);
			g.setFont(font);
			exTextId[page] = renderEngine.allocateAndSetupTexture(imgTemp);//allocateAndSetupTexture
		}
		
		void add(Character ch) {
			TransResource.exStr.append(ch);
			//改动以修复部分内容显示的异常：如省略号
			g.drawString(ch.toString(), x * NORMALWIDTH, y * fm.getHeight() + fm.getAscent());//getCharWidth(ch)
			renderEngine.setupTexture(imgTemp, exTextId[page]);//setupTexture
			if(x<ttMaxWidth - 1) {
				x++;
			} else if(y<ttMaxHeight -1 ) {
				x=0;
				y++;
			} else {
				x=0;
				y=0;
				page ++;
				createImage();
			}
		}
	}
	
	public void draw(Character ch) {
		int index = TransResource.chineseStr.indexOf(ch.toString());
		int page = -1;
		if(index >= 0) {
			page = index / ttPageMax;
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textImageId[page]);
		} else {
			index = TransResource.exStr.indexOf(ch.toString());
			if(index >= 0) {
				page = index / ttPageMax;
			} else {
				exImage.add(ch);
				page = exImage.page;
			}
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, exImage.exTextId[page]);
		}
		GL11.glBegin(5);
		float height = fm.getHeight();
		float width = getCharWidth(ch);
		float height_game = fm_game.getHeight();
		float width_game = fm_game.charWidth(ch);
		
		float offsetX = (index % ttPageMax % ttMaxWidth) * NORMALWIDTH;//动以修复部分内容显示的异常：如省略号,width
		float offsetY = (index % ttPageMax/ ttMaxWidth ) * height;
		GL11.glTexCoord2f(offsetX / TT_IMAGE_SIZE, offsetY / TT_IMAGE_SIZE);
        GL11.glVertex2f(0,  0 - fm_game.getDescent());//四角坐标,缩放
        GL11.glTexCoord2f(offsetX / TT_IMAGE_SIZE, (offsetY + height) / TT_IMAGE_SIZE);
        GL11.glVertex2f(0, height_game - fm_game.getDescent());//四角坐标,缩放
        GL11.glTexCoord2f((offsetX + width) / TT_IMAGE_SIZE, offsetY / TT_IMAGE_SIZE);
        GL11.glVertex2f(width_game, 0- fm_game.getDescent());//四角坐标,缩放
        GL11.glTexCoord2f((offsetX + width) / TT_IMAGE_SIZE, (offsetY + height) / TT_IMAGE_SIZE);
        GL11.glVertex2f(width_game, height_game - fm_game.getDescent());//四角坐标,缩放
        GL11.glEnd();
	}

}
