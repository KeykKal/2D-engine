package editor;

import org.joml.Vector2f;

import engine.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import inputListener.MouseListener;

public class GameViewWindow {
	
	private static float leftX, rightX, topY, bottomY;
	
	public static void imgui() {
		ImGui.begin("Game", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
		
		ImVec2 windowSize = getLargestSizeForViewport();
		ImVec2 windowPos = getCenterPosForViewport(windowSize);
		
		
		ImGui.setCursorPos(windowPos.x, windowPos.y);
		
		ImVec2 topLeft = new ImVec2();
		ImGui.getCursorScreenPos(topLeft);
		topLeft.x -= ImGui.getScrollX();
		topLeft.y -= ImGui.getScrollY();
		
		leftX = topLeft.x;
		bottomY = topLeft.y;
		rightX = topLeft.x + windowSize.x;
		topY = topLeft.y + windowSize.y;
		
		int texId = Window.getFrameBuffer().getTextureId();
		ImGui.image(texId, windowSize.x, windowSize.y, 0, 1, 1, 0);
		
		MouseListener.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y));
		MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));
		
		ImGui.end();
	}
	
	private static ImVec2 getLargestSizeForViewport() {
		ImVec2 windowSize = new ImVec2();
		
		ImGui.getContentRegionAvail(windowSize);
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();
		
		float aspectWidth = windowSize.x;
		float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
		
		if(aspectHeight > windowSize.y) {
			//black bars
			
			aspectHeight = windowSize.y;
			aspectWidth = aspectHeight * Window.getTargetAspectRatio();
		}
		
		return new ImVec2(aspectWidth, aspectHeight);
	}
	
	private static ImVec2 getCenterPosForViewport(ImVec2 aspectSize) {
		ImVec2 windowSize = new ImVec2();
		
		ImGui.getContentRegionAvail(windowSize);
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();
		
		float viewPortX = (windowSize.x / 2f) - (aspectSize.x / 2f);
		float viewPortY = (windowSize.y / 2f) - (aspectSize.y / 2f);
		
		return new ImVec2(viewPortX + ImGui.getCursorPosX(), 
				viewPortY + ImGui.getCursorPosY());
	}

	public static boolean getWantCaptureMouse() {
		return MouseListener.getX() >= leftX && 
			   MouseListener.getX() <= rightX &&
			   MouseListener.getY() >= bottomY &&
			   MouseListener.getY() <= topY;
	}
}


