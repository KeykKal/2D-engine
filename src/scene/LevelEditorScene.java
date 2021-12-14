package scene;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import components.*;

import engine.Window;
import imgui.ImGui;
import imgui.ImVec2;
import inputListener.KeyListener;
import inputListener.MouseListener;
import objects.Camera;
import objects.GameObject;
import objects.Prefabs;
import physic2d.primitives.Circle;
import physic2d.rigidbody.IntersectionDetector2D;
import renderer.DebugDraw;
import util.AssetPool;

public class LevelEditorScene extends Scene {
    
	private GameObject test;
	private GameObject tes2;
	private Spritesheet sprites;
	
	GameObject levelEditorConfig = new GameObject("Config");
	
    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();
        
        levelEditorConfig.addComponent(new MouseControls());
        levelEditorConfig.addComponent(new GridLines());
        
        this.camera = new Camera(new Vector2f(-250, 0));
        sprites = AssetPool.getSpritesheet("dungeon.png");
        DebugDraw.addLine2D(new Vector2f(0, 0), new Vector2f(800, 800), new Vector3f(1, 0, 0), 1200);
        
        if(levelLoaded) {
        	if(gameObjects.size() > 0)
        		this.activeGameObject = gameObjects.get(0);
        	return;
        }
        
        System.out.println(levelLoaded);
         
        test = new GameObject("teeee", 
        		new Transform(new Vector2f(100, 100), new Vector2f(64, 64)), 3);
        SpriteRenderer testSprite = new SpriteRenderer();
        testSprite.setSprite(sprites.getSprite(45));
        test.addComponent(testSprite);
        this.addGameObjectToScene(test);
        
        tes2 = new GameObject("test2", 
        		new Transform(new Vector2f(400, 100), new Vector2f(64, 64)), 1);
        
        SpriteRenderer tes2Sprite = new SpriteRenderer();
        tes2Sprite.setSprite(sprites.getSprite(20));
        tes2.addComponent(tes2Sprite);
        tes2.addComponent(new CameraMovement());
        this.addGameObjectToScene(tes2);
    }
    
    private void loadResources() {
    	AssetPool.getShader("default.glsl");
    	
    	AssetPool.addSpritesheet("dungeon.png", 
    			new Spritesheet(AssetPool.getTexturebyName("spritesheets/dungeon.png"), 16, 16, 256, 0));
   
    	for(GameObject g : gameObjects) {
    		if(g.getComponent(SpriteRenderer.class) != null) {
    			SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
    			if(spr.getTexture() != null) {
    				spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
    			}
    		}
    	}
    }
    
    
    @Override
    public void update(float dt) {
    	levelEditorConfig.update(dt);
  
    	Circle c1 = new Circle();
    	Circle c2 = new Circle();
    	
    	Vector3f color = new Vector3f(0,1,0);    		
    	
    	DebugDraw.addCirlce(new Vector2f(200, 200), 64, color, 1);
    	DebugDraw.addCirlce(new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY()),
    			64, color, 1);
    	
        for (GameObject g : gameObjects) {
			g.update(dt);
		}
        this.renderer.render();
        Window.get().changeTitel(Window.get().getTitle() + " |  FPS: " + (1.0f / dt));
    }
    
    @Override
    public void imgui() {	
    	ImGui.begin("Test Window");
    	
    	ImVec2 windowPos = new ImVec2();
    	ImGui.getWindowPos(windowPos);
    	ImVec2 windowSize = new ImVec2();
    	ImGui.getWindowSize(windowSize);
    	ImVec2 itemSpacing = new ImVec2();
    	ImGui.getStyle().getItemSpacing(itemSpacing);
    	
    	float windowX2 = windowPos.x + windowSize.x;
    	for(int i = 0; i < sprites.size(); i++) {
    		Sprite sprite = sprites.getSprite(i);
    		float spriteWidth = sprite.getWidth() * 4;
    		float spriteHeight = sprite.getHeight() * 4;
    		int id = sprite.getTexId();
    		Vector2f[] texCoords = sprite.getTexCoords();
    		
    		ImGui.pushID(i);
    		if(ImGui.imageButton(id, spriteWidth, spriteHeight, 
    				texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
    			GameObject object = Prefabs.generateSpriteObject(sprite, 32, 32);
    			levelEditorConfig.getComponent(MouseControls.class).pickupObject(object);
    		}
    		ImGui.popID();
    		
    		ImVec2 lastButtonPos = new ImVec2();
    		ImGui.getItemRectMax(lastButtonPos);
    		float lastButtonX2 = lastButtonPos.x;
    		float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
    		if(i + 1 < sprites.size() && nextButtonX2 < windowX2) {
    			ImGui.sameLine();
    		}
    		
    	}
    	
    	ImGui.end();
//    	ImGui.begin("Save and Load");
//    	
//    	if(ImGui.button("Load", 250, 45)) {
//    		load();
//    	}
//    	
//    	if(ImGui.button("Save", 250, 45)) {
//    		saveExit();
//    	}
//    	
//    	if(ImGui.button("Clear Scene", 250, 45)) {
//    		gameObjects.removeAll(gameObjects);
//    	}
//    	
//    	ImGui.end();
    	
    }
}


