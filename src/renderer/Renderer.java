package renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import components.SpriteRenderer;
import objects.GameObject;
import shader.Texture;

public class Renderer {

	private final int MAX_BATCH_SIZE = 1000;
	private List<RenderBatch> batches;
	
	public Renderer() {
		this.batches = new ArrayList<RenderBatch>();
	}
	
	
	public void add(GameObject g) {
		SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
		if(spr != null) {
			add(spr);
		}
	}
	
	public void add(SpriteRenderer sprite) {
		boolean added = false;
		for (RenderBatch batch : batches) {
			if(batch.hasRoom() && batch.getzIndex() == sprite.gameObject.getzIndex()) {
				Texture tex = sprite.getTexture();
				if(tex == null && (batch.hasTexture(tex) || batch.hasTextureRoom())) { 
					batch.addSprite(sprite);
					added = true;
					break;					
				}
			}
		}
		
		if(!added) {
			RenderBatch newbatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.getzIndex());
			newbatch.start();
			batches.add(newbatch);
			newbatch.addSprite(sprite);
			Collections.sort(batches, Collections.reverseOrder());
		}
	}
	
	public void render() {
		for (RenderBatch batch : batches) {
			batch.render();
		}
	}
	
}
