package components;

import org.lwjgl.glfw.GLFW;

import engine.Window;
import inputListener.MouseListener;
import objects.GameObject;
import util.Settings;

public class MouseControls extends Component {
	GameObject holdObject = null;
	
	public void pickupObject(GameObject g) {
		this.holdObject = g;
		Window.getScene().addGameObjectToScene(g);
	}
	
	public void place() {
		this.holdObject = null;
	}
	
	@Override
	public void update(float dt) {
		if(holdObject != null) {
			holdObject.transform.position.x = MouseListener.getOrthoX() - 16;
			holdObject.transform.position.y = MouseListener.getOrthoY() - 16;
			
			holdObject.transform.position.x = (int)(holdObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
			holdObject.transform.position.y = (int)(holdObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;
			
			if(MouseListener.mouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
				place();
			}
			
		}
	}
}
