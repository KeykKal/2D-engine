package scene;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import components.Component;
import deserializer.ComponentDeserializer;
import deserializer.GameObjectDeserializer;
import imgui.ImGui;
import objects.Camera;
import objects.GameObject;
import renderer.Renderer;

public abstract class Scene {

	protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
	protected List<GameObject> gameObjects = new ArrayList<>();
    protected GameObject activeGameObject = null;
	protected boolean levelLoaded = false;
    
    public Scene() {

    }

    public void init() {

    }

    public void start() {
    	for (GameObject g : gameObjects) {
			g.start();
			this.renderer.add(g);
		}
    	isRunning = true;
    }
    
    public void addGameObjectToScene(GameObject g) {
    	gameObjects.add(g);
		if(isRunning) {
    		g.start();
    		this.renderer.add(g);
    	}
    }
    
    public void removeGameObjectFromScene(GameObject g) {
    	gameObjects.remove(g);
    }
    
    public abstract void update(float dt);
    
    public Camera camera() {
    	return this.camera;
    }
    
    public void SceneImgui() {
    	if(activeGameObject != null) {
    		ImGui.begin("Inspector");
    		activeGameObject.Imgui();
    		ImGui.end();
    	}
    	imgui();
    }
    
    public void imgui() {
    	
    }
    
    public void saveExit() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();

        try {
            FileWriter writer = new FileWriter("level.cds");
            writer.write(gson.toJson(this.gameObjects));
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();

        String inFile = "";
        try {
            if(!Files.exists(Paths.get("level.cds")))
                Files.createFile(Paths.get("level.cds"));
            inFile = new String(Files.readAllBytes(Paths.get("level.cds")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inFile.equals("")) {
        	int maxGoId = -1;
        	int maxCompId = -1;
            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (int i=0; i < objs.length; i++) {
                addGameObjectToScene(objs[i]);
                
                for (Component c : objs[i].getAllComponents()) {
					if(c.getUid() > maxCompId) {
						maxCompId = c.getUid();
					}
				}
                if(objs[i].getUid() > maxGoId) {
                	maxGoId = objs[i].getUid();
                }
            }
            
            maxGoId++;
            maxCompId++;
            
            GameObject.init(maxGoId);
            Component.init(maxCompId);
            this.levelLoaded = true;
        }
    }
}