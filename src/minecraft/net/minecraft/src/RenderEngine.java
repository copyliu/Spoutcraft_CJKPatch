package net.minecraft.src;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.src.GLAllocation;
import net.minecraft.src.GameSettings;
import net.minecraft.src.ImageBuffer;
import net.minecraft.src.MCHash;
import net.minecraft.src.TextureFX;
import net.minecraft.src.TexturePackBase;
import net.minecraft.src.TexturePackList;
import net.minecraft.src.ThreadDownloadImageData;
import org.lwjgl.opengl.GL11;
//Spout HD Start
import net.minecraft.client.Minecraft;
import com.pclewis.mcpatcher.mod.TextureUtils;
import com.pclewis.mcpatcher.mod.TileSize;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Dimension;
import org.lwjgl.opengl.GLContext;
//Spout HD End

public class RenderEngine {

	public static boolean useMipmaps = false;
	private HashMap textureMap = new HashMap();
	private HashMap field_28151_c = new HashMap();
	private MCHash textureNameToImageMap = new MCHash();
	private IntBuffer singleIntBuffer = GLAllocation.createDirectIntBuffer(1);
    //Spout HD Start
	private ByteBuffer imageData = GLAllocation.createDirectByteBuffer(TileSize.int_glBufferSize);
	//Spout HD End
	public List textureList = new ArrayList(); //Spout private -> public
	private Map urlToImageDataMap = new HashMap();
	private GameSettings options;
	public boolean clampTexture = false;
	public boolean blurTexture = false;
	public TexturePackList texturePack; //Spout private -> public
	private BufferedImage missingTextureImage = new BufferedImage(64, 64, 2);
    //Spout Start
	public TexturePackBase oldPack = null;
	private ByteBuffer[] mipImageDatas;
	//Spout End

	public RenderEngine(TexturePackList var1, GameSettings var2) {
        allocateImageData(256); //Spout
		this.texturePack = var1;
		this.options = var2;
		Graphics var3 = this.missingTextureImage.getGraphics();
		var3.setColor(Color.WHITE);
		var3.fillRect(0, 0, 64, 64);
		var3.setColor(Color.BLACK);
		var3.drawString("missingtex", 1, 10);
		var3.dispose();
	}

	public int[] getTextureContents(String var1) {
		TexturePackBase var2 = this.texturePack.selectedTexturePack;
		int[] var3 = (int[])this.field_28151_c.get(var1);
		if(var3 != null) {
			return var3;
		} else {
			try {
				Object var6 = null;
				if(var1.startsWith("##")) {
					//Spout HD Start
					var3 = this.getImageContentsAndAllocate(this.unwrapImageByColumns(TextureUtils.getResourceAsBufferedImage(this, var2, var1.substring(2))));
                    //Spout HD End
				} else if(var1.startsWith("%clamp%")) {
					this.clampTexture = true;
					//Spout HD Start
					var3 = this.getImageContentsAndAllocate(TextureUtils.getResourceAsBufferedImage(this, var2, var1.substring(7)));
                    //Spout HD End
					this.clampTexture = false;
				} else if(var1.startsWith("%blur%")) {
					//Spout HD Start
					this.blurTexture = true;
					this.clampTexture = true;
					var3 = this.getImageContentsAndAllocate(TextureUtils.getResourceAsBufferedImage(this, var2, var1.substring(6)));
					this.clampTexture = false;
					//Spout HD end
					this.blurTexture = false;
				} else {
					InputStream var7 = var2.getResourceAsStream(var1);
					if(var7 == null) {
						var3 = this.getImageContentsAndAllocate(this.missingTextureImage);
					} else {
						var3 = this.getImageContentsAndAllocate(this.readTextureImage(var7));
					}
				}

				this.field_28151_c.put(var1, var3);
				return var3;
			} catch (IOException var5) {
				var5.printStackTrace();
				int[] var4 = this.getImageContentsAndAllocate(this.missingTextureImage);
				this.field_28151_c.put(var1, var4);
				return var4;
			}
		}
	}

	private int[] getImageContentsAndAllocate(BufferedImage var1) {
		int var2 = var1.getWidth();
		int var3 = var1.getHeight();
		int[] var4 = new int[var2 * var3];
		var1.getRGB(0, 0, var2, var3, var4, 0, var2);
		return var4;
	}

	private int[] getImageContents(BufferedImage var1, int[] var2) {
//Spout HD end
		if(var1 == null) {
			return var2;
		} else {
			int var3 = var1.getWidth();
			int var4 = var1.getHeight();
			var1.getRGB(0, 0, var3, var4, var2, 0, var3);
			return var2;
		}
//Spout HD start
	}

	public int getTexture(String var1) {
		TexturePackBase var2 = this.texturePack.selectedTexturePack;
		Integer var3 = (Integer)this.textureMap.get(var1);
		if(var3 != null) {
			return var3.intValue();
		} else {
			try {
				this.singleIntBuffer.clear();
				GLAllocation.generateTextureNames(this.singleIntBuffer);
				int var6 = this.singleIntBuffer.get(0);
				if(var1.startsWith("##")) {
					//Spout HD Start
					this.setupTexture(this.unwrapImageByColumns(TextureUtils.getResourceAsBufferedImage(this, var2, var1.substring(2))), var6);
					//Spout HD End
				} else if(var1.startsWith("%clamp%")) {
					this.clampTexture = true;
					//Spout HD Start
					this.setupTexture(TextureUtils.getResourceAsBufferedImage(this, var2, var1.substring(7)), var6);
					//Spout HD End
					this.clampTexture = false;
				} else if(var1.startsWith("%blur%")) {
					//Spout HD Start
					this.blurTexture = true;
					this.setupTexture(TextureUtils.getResourceAsBufferedImage(this, var2, var1.substring(6)), var6);
					this.blurTexture = false;
					//Spout HD end
				} else if(var1.startsWith("%blurclamp%")) {
					//Spout HD Start
					this.blurTexture = true;
					this.clampTexture = true;
					this.setupTexture(TextureUtils.getResourceAsBufferedImage(this, var2, var1.substring(11)), var6);
					this.blurTexture = false;
					this.clampTexture = false;
					//Spout HD end
				} else {
					//Spout HD Start
					this.setupTexture(TextureUtils.getResourceAsBufferedImage(var1), var6);
					//Spout HD End
				}

				this.textureMap.put(var1, Integer.valueOf(var6));
				return var6;
			} catch (Exception var5) {
				var5.printStackTrace();
				GLAllocation.generateTextureNames(this.singleIntBuffer);
				int var4 = this.singleIntBuffer.get(0);
				this.setupTexture(this.missingTextureImage, var4);
				this.textureMap.put(var1, Integer.valueOf(var4));
				return var4;
			}
		}
	}

	private BufferedImage unwrapImageByColumns(BufferedImage var1) {
		int var2 = var1.getWidth() / 16;
		BufferedImage var3 = new BufferedImage(16, var1.getHeight() * var2, 2);
		Graphics var4 = var3.getGraphics();

		for(int var5 = 0; var5 < var2; ++var5) {
			var4.drawImage(var1, -var5 * 16, var5 * var1.getHeight(), (ImageObserver)null);
		}

		var4.dispose();
		return var3;
	}

	public int allocateAndSetupTexture(BufferedImage var1) {
		this.singleIntBuffer.clear();
		GLAllocation.generateTextureNames(this.singleIntBuffer);
		int var2 = this.singleIntBuffer.get(0);
		this.setupTexture(var1, var2);
		this.textureNameToImageMap.addKey(var2, var1);
		return var2;
	}

	public void setupTexture(BufferedImage var1, int var2) {
//Spout HD Start
		if(var1 != null) {
			GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, var2);
			if(useMipmaps) {
				GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10241 /*GL_TEXTURE_MIN_FILTER*/, 9986 /*GL_NEAREST_MIPMAP_LINEAR*/);
				GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10240 /*GL_TEXTURE_MAG_FILTER*/, 9728 /*GL_NEAREST*/);
			} else {
				GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10241 /*GL_TEXTURE_MIN_FILTER*/, 9728 /*GL_NEAREST*/);
				GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10240 /*GL_TEXTURE_MAG_FILTER*/, 9728 /*GL_NEAREST*/);
			}

			if(this.blurTexture) {
				GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10241 /*GL_TEXTURE_MIN_FILTER*/, 9729 /*GL_LINEAR*/);
				GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10240 /*GL_TEXTURE_MAG_FILTER*/, 9729 /*GL_LINEAR*/);
			}

			if(this.clampTexture) {
				GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10242 /*GL_TEXTURE_WRAP_S*/, 10496 /*GL_CLAMP*/);
				GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10243 /*GL_TEXTURE_WRAP_T*/, 10496 /*GL_CLAMP*/);
			} else {
				GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10242 /*GL_TEXTURE_WRAP_S*/, 10497 /*GL_REPEAT*/);
				GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10243 /*GL_TEXTURE_WRAP_T*/, 10497 /*GL_REPEAT*/);
			}

			int var3 = var1.getWidth();
			int var4 = var1.getHeight();
			int[] var5 = new int[var3 * var4];
			byte[] var6 = new byte[var3 * var4 * 4];
			var1.getRGB(0, 0, var3, var4, var5, 0, var3);

			int var7;
			int var8;
			int var9;
			int var10;
			int var11;
			int var12;
			int var13;
			int var14;
			for(var7 = 0; var7 < var5.length; ++var7) {
				var8 = var5[var7] >> 24 & 255;
				var9 = var5[var7] >> 16 & 255;
				var10 = var5[var7] >> 8 & 255;
				var11 = var5[var7] & 255;
				if(this.options != null && this.options.anaglyph) {
					var12 = (var9 * 30 + var10 * 59 + var11 * 11) / 100;
					var13 = (var9 * 30 + var10 * 70) / 100;
					var14 = (var9 * 30 + var11 * 70) / 100;
					var9 = var12;
					var10 = var13;
					var11 = var14;
				}

				var6[var7 * 4 + 0] = (byte)var9;
				var6[var7 * 4 + 1] = (byte)var10;
				var6[var7 * 4 + 2] = (byte)var11;
				var6[var7 * 4 + 3] = (byte)var8;
			}

			this.imageData = TextureUtils.getByteBuffer(this.imageData, var6);
			GL11.glTexImage2D(3553 /*GL_TEXTURE_2D*/, 0, 6408 /*GL_RGBA*/, var3, var4, 0, 6408 /*GL_RGBA*/, 5121 /*GL_UNSIGNED_BYTE*/, this.imageData);
			if(useMipmaps) {
				for(var7 = 1; var7 <= 4; ++var7) {
					var8 = var3 >> var7 - 1;
					var9 = var3 >> var7;
					var10 = var4 >> var7;

					for(var11 = 0; var11 < var9; ++var11) {
						for(var12 = 0; var12 < var10; ++var12) {
							var13 = this.imageData.getInt((var11 * 2 + 0 + (var12 * 2 + 0) * var8) * 4);
							var14 = this.imageData.getInt((var11 * 2 + 1 + (var12 * 2 + 0) * var8) * 4);
							int var15 = this.imageData.getInt((var11 * 2 + 1 + (var12 * 2 + 1) * var8) * 4);
							int var16 = this.imageData.getInt((var11 * 2 + 0 + (var12 * 2 + 1) * var8) * 4);
							int var17 = this.alphaBlend(this.alphaBlend(var13, var14), this.alphaBlend(var15, var16));
							this.imageData.putInt((var11 + var12 * var9) * 4, var17);
						}
					}

					GL11.glTexImage2D(3553 /*GL_TEXTURE_2D*/, var7, 6408 /*GL_RGBA*/, var9, var10, 0, 6408 /*GL_RGBA*/, 5121 /*GL_UNSIGNED_BYTE*/, this.imageData);
				}
			}

		}
	}
//Spout HD end
	public void createTextureFromBytes(int[] var1, int var2, int var3, int var4) {
		GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, var4);
		if(useMipmaps) {
			GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10241 /*GL_TEXTURE_MIN_FILTER*/, 9986 /*GL_NEAREST_MIPMAP_LINEAR*/);
			GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10240 /*GL_TEXTURE_MAG_FILTER*/, 9728 /*GL_NEAREST*/);
		} else {
			GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10241 /*GL_TEXTURE_MIN_FILTER*/, 9728 /*GL_NEAREST*/);
			GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10240 /*GL_TEXTURE_MAG_FILTER*/, 9728 /*GL_NEAREST*/);
		}

		if(this.blurTexture) {
			GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10241 /*GL_TEXTURE_MIN_FILTER*/, 9729 /*GL_LINEAR*/);
			GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10240 /*GL_TEXTURE_MAG_FILTER*/, 9729 /*GL_LINEAR*/);
		}

		if(this.clampTexture) {
			GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10242 /*GL_TEXTURE_WRAP_S*/, 10496 /*GL_CLAMP*/);
			GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10243 /*GL_TEXTURE_WRAP_T*/, 10496 /*GL_CLAMP*/);
		} else {
			GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10242 /*GL_TEXTURE_WRAP_S*/, 10497 /*GL_REPEAT*/);
			GL11.glTexParameteri(3553 /*GL_TEXTURE_2D*/, 10243 /*GL_TEXTURE_WRAP_T*/, 10497 /*GL_REPEAT*/);
		}

		byte[] var5 = new byte[var2 * var3 * 4];

		for(int var6 = 0; var6 < var1.length; ++var6) {
			int var7 = var1[var6] >> 24 & 255;
			int var8 = var1[var6] >> 16 & 255;
			int var9 = var1[var6] >> 8 & 255;
			int var10 = var1[var6] & 255;
			if(this.options != null && this.options.anaglyph) {
				int var11 = (var8 * 30 + var9 * 59 + var10 * 11) / 100;
				int var12 = (var8 * 30 + var9 * 70) / 100;
				int var13 = (var8 * 30 + var10 * 70) / 100;
				var8 = var11;
				var9 = var12;
				var10 = var13;
			}

			var5[var6 * 4 + 0] = (byte)var8;
			var5[var6 * 4 + 1] = (byte)var9;
			var5[var6 * 4 + 2] = (byte)var10;
			var5[var6 * 4 + 3] = (byte)var7;
		}

		//Spout HD Start
		this.imageData = TextureUtils.getByteBuffer(this.imageData, var5);
        //Spout HD End
		GL11.glTexSubImage2D(3553 /*GL_TEXTURE_2D*/, 0, 0, 0, var2, var3, 6408 /*GL_RGBA*/, 5121 /*GL_UNSIGNED_BYTE*/, this.imageData);
	}

	public void deleteTexture(int var1) {
		this.textureNameToImageMap.removeObject(var1);
		this.singleIntBuffer.clear();
		this.singleIntBuffer.put(var1);
		this.singleIntBuffer.flip();
		GL11.glDeleteTextures(this.singleIntBuffer);
	}

	public int getTextureForDownloadableImage(String var1, String var2) {
		ThreadDownloadImageData var3 = (ThreadDownloadImageData)this.urlToImageDataMap.get(var1);
		if(var3 != null && var3.image != null && !var3.textureSetupComplete) {
			if(var3.textureName < 0) {
				var3.textureName = this.allocateAndSetupTexture(var3.image);
			} else {
				this.setupTexture(var3.image, var3.textureName);
			}

			var3.textureSetupComplete = true;
		}

		return var3 != null && var3.textureName >= 0?var3.textureName:(var2 == null?-1:this.getTexture(var2));
	}

	public ThreadDownloadImageData obtainImageData(String var1, ImageBuffer var2) {
		ThreadDownloadImageData var3 = (ThreadDownloadImageData)this.urlToImageDataMap.get(var1);
		if(var3 == null) {
			this.urlToImageDataMap.put(var1, new ThreadDownloadImageData(var1, var2));
		} else {
			++var3.referenceCount;
		}

		return var3;
	}

	public void releaseImageData(String var1) {
		ThreadDownloadImageData var2 = (ThreadDownloadImageData)this.urlToImageDataMap.get(var1);
		if(var2 != null) {
			--var2.referenceCount;
			if(var2.referenceCount == 0) {
                //Spout Start
				if (Minecraft.theMinecraft.theWorld != null) {
					List<EntityPlayer> players = Minecraft.theMinecraft.theWorld.playerEntities;
					for (EntityPlayer player : players) {
						if (player.skinUrl != null && player.skinUrl.equals(var1)) {
							var2.referenceCount++;
						}
						if (player.playerCloakUrl != null && player.playerCloakUrl.equals(var1)) {
							var2.referenceCount++;
						}
					}
				}
				if (var2.referenceCount > 0) {
					return;
				}
				//Spout End
				if(var2.textureName >= 0) {
					this.deleteTexture(var2.textureName);
				}

				this.urlToImageDataMap.remove(var1);
			}
		}

	}

	public void registerTextureFX(TextureFX var1) {
		//Spout HD start
		TextureUtils.registerTextureFX(this.textureList, var1);
		//Spout HD end
	}

	public void updateDynamicTextures() {
		int var1;
		TextureFX var2;
		int var3;
		int var4;
		int var5;
		int var6;
		int var7;
		int var8;
		int var9;
		int var10;
		int var11;
		int var12;
		for(var1 = 0; var1 < this.textureList.size(); ++var1) {
			var2 = (TextureFX)this.textureList.get(var1);
			var2.anaglyphEnabled = this.options.anaglyph;
			var2.onTick();
			//Spout HD Start
			this.imageData = TextureUtils.getByteBuffer(this.imageData, var2.imageData);
            //Spout HD End
			var2.bindImage(this);

			for(var3 = 0; var3 < var2.tileSize; ++var3) {
				for(var4 = 0; var4 < var2.tileSize; ++var4) {
					//Spout HD Start
					GL11.glTexSubImage2D(3553 /*GL_TEXTURE_2D*/, 0, var2.iconIndex % 16 * TileSize.int_size + var3 * TileSize.int_size, var2.iconIndex / 16 * TileSize.int_size + var4 * TileSize.int_size, TileSize.int_size, TileSize.int_size, 6408 /*GL_RGBA*/, 5121 /*GL_UNSIGNED_BYTE*/, this.imageData);
                    //Spout HD End
					if(useMipmaps) {
						for(var5 = 1; var5 <= 4; ++var5) {
							var6 = 16 >> var5 - 1;
							var7 = 16 >> var5;

							for(var8 = 0; var8 < var7; ++var8) {
								for(var9 = 0; var9 < var7; ++var9) {
									var10 = this.imageData.getInt((var8 * 2 + 0 + (var9 * 2 + 0) * var6) * 4);
									var11 = this.imageData.getInt((var8 * 2 + 1 + (var9 * 2 + 0) * var6) * 4);
									var12 = this.imageData.getInt((var8 * 2 + 1 + (var9 * 2 + 1) * var6) * 4);
									int var13 = this.imageData.getInt((var8 * 2 + 0 + (var9 * 2 + 1) * var6) * 4);
									int var14 = this.averageColor(this.averageColor(var10, var11), this.averageColor(var12, var13));
									this.imageData.putInt((var8 + var9 * var7) * 4, var14);
								}
							}

							GL11.glTexSubImage2D(3553 /*GL_TEXTURE_2D*/, var5, var2.iconIndex % 16 * var7, var2.iconIndex / 16 * var7, var7, var7, 6408 /*GL_RGBA*/, 5121 /*GL_UNSIGNED_BYTE*/, this.imageData);
						}
					}
				}
			}
		}

		for(var1 = 0; var1 < this.textureList.size(); ++var1) {
			var2 = (TextureFX)this.textureList.get(var1);
			if(var2.textureId > 0) {
				//Spout HD Start
				this.imageData = TextureUtils.getByteBuffer(this.imageData, var2.imageData);
				GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, var2.textureId);
				GL11.glTexSubImage2D(3553 /*GL_TEXTURE_2D*/, 0, 0, 0, TileSize.int_size, TileSize.int_size, 6408 /*GL_RGBA*/, 5121 /*GL_UNSIGNED_BYTE*/, this.imageData);
                //Spout HD End
				if(useMipmaps) {
					for(var3 = 1; var3 <= 4; ++var3) {
						var4 = 16 >> var3 - 1;
						var5 = 16 >> var3;

						for(var6 = 0; var6 < var5; ++var6) {
							for(var7 = 0; var7 < var5; ++var7) {
								var8 = this.imageData.getInt((var6 * 2 + 0 + (var7 * 2 + 0) * var4) * 4);
								var9 = this.imageData.getInt((var6 * 2 + 1 + (var7 * 2 + 0) * var4) * 4);
								var10 = this.imageData.getInt((var6 * 2 + 1 + (var7 * 2 + 1) * var4) * 4);
								var11 = this.imageData.getInt((var6 * 2 + 0 + (var7 * 2 + 1) * var4) * 4);
								var12 = this.averageColor(this.averageColor(var8, var9), this.averageColor(var10, var11));
								this.imageData.putInt((var6 + var7 * var5) * 4, var12);
							}
						}

						GL11.glTexSubImage2D(3553 /*GL_TEXTURE_2D*/, var3, 0, 0, var5, var5, 6408 /*GL_RGBA*/, 5121 /*GL_UNSIGNED_BYTE*/, this.imageData);
					}
				}
			}
		}

	}

	private int averageColor(int var1, int var2) {
		int var3 = (var1 & -16777216) >> 24 & 255;
		int var4 = (var2 & -16777216) >> 24 & 255;
		return (var3 + var4 >> 1 << 24) + ((var1 & 16711422) + (var2 & 16711422) >> 1);
	}

	private int alphaBlend(int var1, int var2) {
		int var3 = (var1 & -16777216) >> 24 & 255;
		int var4 = (var2 & -16777216) >> 24 & 255;
		short var5 = 255;
		if(var3 + var4 == 0) {
			var3 = 1;
			var4 = 1;
			var5 = 0;
		}

		int var6 = (var1 >> 16 & 255) * var3;
		int var7 = (var1 >> 8 & 255) * var3;
		int var8 = (var1 & 255) * var3;
		int var9 = (var2 >> 16 & 255) * var4;
		int var10 = (var2 >> 8 & 255) * var4;
		int var11 = (var2 & 255) * var4;
		int var12 = (var6 + var9) / (var3 + var4);
		int var13 = (var7 + var10) / (var3 + var4);
		int var14 = (var8 + var11) / (var3 + var4);
		return var5 << 24 | var12 << 16 | var13 << 8 | var14;
	}

	public void refreshTextures() {
		TexturePackBase var1 = this.texturePack.selectedTexturePack;
		Iterator var2 = this.textureNameToImageMap.func_35860_b().iterator();

		BufferedImage var4;
		while(var2.hasNext()) {
			int var3 = ((Integer)var2.next()).intValue();
			var4 = (BufferedImage)this.textureNameToImageMap.lookup(var3);
			this.setupTexture(var4, var3);
		}

		ThreadDownloadImageData var8;
		for(var2 = this.urlToImageDataMap.values().iterator(); var2.hasNext(); var8.textureSetupComplete = false) {
			var8 = (ThreadDownloadImageData)var2.next();
		}

		var2 = this.textureMap.keySet().iterator();

		String var9;
		while(var2.hasNext()) {
			var9 = (String)var2.next();

			try {
				//Spout HD Start
				if(var9.startsWith("##")) {
					var4 = this.unwrapImageByColumns(TextureUtils.getResourceAsBufferedImage(this, var1, var9.substring(2)));
				} else if(var9.startsWith("%clamp%")) {
					this.clampTexture = true;
					var4 = TextureUtils.getResourceAsBufferedImage(this, var1, var9.substring(7));
				} else if(var9.startsWith("%blur%")) {
					this.blurTexture = true;
					var4 = TextureUtils.getResourceAsBufferedImage(this, var1, var9.substring(6));
				} else if(var9.startsWith("%blurclamp%")) {
					this.blurTexture = true;
					this.clampTexture = true;
					var4 = TextureUtils.getResourceAsBufferedImage(this, var1, var9.substring(11));
				} else {
					var4 = TextureUtils.getResourceAsBufferedImage(this, var1, var9);
				}
				if (var4 == null) {
					var2.remove();
					continue;
				}
                //Spout HD End

				int var5 = ((Integer)this.textureMap.get(var9)).intValue();
				this.setupTexture(var4, var5);
				this.blurTexture = false;
				this.clampTexture = false;
			} catch (IOException var7) {
				//Spout HD Start
				//Gracefully handle errors
				var2.remove();
				//var6.printStackTrace();
				//Spout HD End
			}
		}

		var2 = this.field_28151_c.keySet().iterator();

		while(var2.hasNext()) {
			var9 = (String)var2.next();

			try {
				//Spout HD Start
				if(var9.startsWith("##")) {
					var4 = this.unwrapImageByColumns(TextureUtils.getResourceAsBufferedImage(this, var1, var9.substring(2)));
				} else if(var9.startsWith("%clamp%")) {
					this.clampTexture = true;
					var4 = TextureUtils.getResourceAsBufferedImage(this, var1, var9.substring(7));
				} else if(var9.startsWith("%blur%")) {
					this.blurTexture = true;
					var4 = TextureUtils.getResourceAsBufferedImage(this, var1, var9.substring(6));
				} else {
					var4 = TextureUtils.getResourceAsBufferedImage(this, var1, var9);
				}
                if (var4 == null) {
                    var2.remove();
                    continue;
                }
                //Spout HD End

				this.getImageContents(var4, (int[])this.field_28151_c.get(var9));
				this.blurTexture = false;
				this.clampTexture = false;
			} catch (IOException var6) {
				//Spout HD Start
				//Gracefully handle errors
				var2.remove();
				//var6.printStackTrace();
				//Spout HD End
			}
		}

	}

	private BufferedImage readTextureImage(InputStream var1) throws IOException {
		BufferedImage var2 = ImageIO.read(var1);
		var1.close();
		return var2;
	}

	public void bindTexture(int var1) {
		if(var1 >= 0) {
			GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, var1);
		}
	}

	//Spout HD Start
	public void setTileSize(Minecraft var1) {
		this.imageData = GLAllocation.createDirectByteBuffer(TileSize.int_glBufferSize);
		this.refreshTextures();
		TextureUtils.refreshTextureFX(this.textureList);
	}

	private void checkImageDataSize(int var1) {
		if(this.imageData != null) {
			int var2 = var1 * var1 * 4;
			if(this.imageData.capacity() >= var2) {
				return;
			}
		}
		
		this.allocateImageData(var1);
	}

	private void allocateImageData(int var1) {
		int var2 = var1 * var1 * 4;
		this.imageData = GLAllocation.createDirectByteBuffer(var2);
		ArrayList var3 = new ArrayList();
		//System.out.println("Allocating Image Data: " + var1);
		for(int var4 = var1 / 2; var4 > 0; var4 /= 2) {
			int var5 = var4 * var4 * 4;
			//System.out.println("Allocating MipMap Data: " + var5);
			ByteBuffer var6 = GLAllocation.createDirectByteBuffer(var5);
			var3.add(var6);
		}

		this.mipImageDatas = (ByteBuffer[])((ByteBuffer[])var3.toArray(new ByteBuffer[var3.size()]));
	}

	private int getMaxMipmapLevel(int var1) {
		int var2;
		for(var2 = 0; var1 > 0; ++var2) {
			var1 /= 2;
		}

		return var2 - 1;
	}

	//Spout HD End

}
