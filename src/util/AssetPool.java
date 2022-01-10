package util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import components.Spritesheet;
import shader.Shader;
import shader.Texture;

public class AssetPool {

	private static Map<String, Shader> shaders = new HashMap<>();
	private static Map<String, Texture> textures = new HashMap<>();
	private static Map<String, Spritesheet> spritesheets = new HashMap<>();
	
	/**
	 * this function saves the shader for resources saving purposes 
	 *
	 * @param shaderName: the name of the shader + the extension (.glsl)
	 * @return returns the shader inside of the res/shader folder
	 */
	public static Shader getShader(String shaderName) {
		File file = new File("res/shader/"+shaderName);
		if(AssetPool.shaders.containsKey(file.getAbsolutePath())) {
			return AssetPool.shaders.get(file.getAbsolutePath());
		} else {
			Shader shader = new Shader("res/shader/"+shaderName);
			shader.compile();
			AssetPool.shaders.put(file.getName(), shader);
			return shader;
		}
	}
	
	
	/**
	 * 
	 * @param textureName: the name of the texture + the extension (.png/.jpg)
	 * @return returns the Texture inside of the res/textures folder
	 */
	public static Texture getTextureByName(String textureName) {
		File file = new File("res/textures/"+ textureName);
		if(AssetPool.textures.containsKey(file.getAbsolutePath())) {
			return AssetPool.textures.get(file.getAbsolutePath());
		} else {
			Texture texture = new Texture();
			texture.init("res/textures/"+ textureName);
			AssetPool.textures.put(file.getName(), texture);
			return texture;
		}
	}
	
    public static Texture getTexture(String resourceName) {
        File file = new File(resourceName);
        if (AssetPool.textures.containsKey(file.getAbsolutePath())) {
            return AssetPool.textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture();
            texture.init(resourceName);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }
	
    public static void addSpritesheet(String resourceName, Spritesheet spritesheet) {
        File file = new File("res/textures/spritesheets/"+resourceName);
        if (!AssetPool.spritesheets.containsKey(file.getAbsolutePath())) {
            AssetPool.spritesheets.put(file.getAbsolutePath(), spritesheet);
        }
    }

    public static Spritesheet getSpritesheet(String resourceName) {
        File file = new File("res/textures/spritesheets/" + resourceName);
        if (!AssetPool.spritesheets.containsKey(file.getAbsolutePath())) {
            assert false : "Error: Tried to access spritesheet '" + resourceName + "' and it has not been added to asset pool.";
        }
        Texture noTexture = new Texture();
        noTexture.init("res/textures/spritesheets/NotLoading.png");
        return AssetPool.spritesheets.getOrDefault(file.getAbsolutePath(), new Spritesheet(noTexture, 16, 16, 256, 0));
    }
}

