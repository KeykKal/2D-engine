package components;

import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.*;

import engine.Window;
import inputListener.KeyListener;

public class CameraMovement extends Component {

	
	public float cameraSpeed = 900f;
	private Vector2f dir = new Vector2f();
	
	@Override
	public void start() {
		
	}
	
	
	@Override
	public void update(float dt) {
		
		inputSytem();
		Window.getScene().camera().position.add(new Vector2f(dir.x*cameraSpeed*dt, dir.y*cameraSpeed*dt));
		gameObject.transform.position.add(new Vector2f(dir.x*cameraSpeed*dt, dir.y*cameraSpeed*dt));
		dir = new Vector2f();
	}	
	
	private void inputSytem() {
	
		//Vertical
		if(KeyListener.isKeyPressed(GLFW_KEY_W)) {
			dir.y = 1;
		} else if(KeyListener.isKeyPressed(GLFW_KEY_S)) {
			dir.y = -1;
		}
		
		//Hotizontal
		if(KeyListener.isKeyPressed(GLFW_KEY_D)) {
			dir.x = 1;
		} else if(KeyListener.isKeyPressed(GLFW_KEY_A)) {
			dir.x = -1;
		}
		
		
	}
	
}
