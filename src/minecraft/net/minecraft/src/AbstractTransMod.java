package net.minecraft.src;

public abstract class AbstractTransMod {
	static RenderEngine renderEngine;//RenderEngine

	/**
	 * 文字处理//FontRenderer
	 * @param s
	 * @param i
	 * @param j
	 * @param k
	 * @param flag  
	 * @return 是否中断原方法
	 */
	public abstract boolean renderString(FontRenderer fr, String s, int i, int j, int k, boolean flag);
	
	
	/**
	 * 获取文本长度//FontRenderer
	 * @param s
	 * @return 文本长度， >=0则中断后续方法
	 */
	public abstract int getStringWidth(FontRenderer fr, String s);
	
	public void register(RenderEngine re) {//RenderEngine
		renderEngine = re;
	}
}
