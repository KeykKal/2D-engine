package renderer;

import components.SpriteRenderer;
import engine.Window;
import shader.Shader;
import shader.Texture;
import util.AssetPool;

import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.util.ArrayList;
import java.util.List;

public class RenderBatch implements Comparable<RenderBatch> {
	
	// Vertex
    // ======
    // Pos               Color
    // float, float,     float, float, float, float
    
	private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;
    
    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE *Float.BYTES;
    private final int VERTEX_SIZE = 9;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;
    
    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] texSlots = { 0, 1, 2, 3, 4, 5, 6, 7 };
    
    private List<Texture> textures;
    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;
    private int zIndex;
    
    
    public RenderBatch(int maxBatchSize, int zIndex) {
    	this.zIndex = zIndex;
        shader = AssetPool.getShader("default.glsl");
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        // 4 vertices quads
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
        
        textures = new ArrayList<Texture>();
    }
    
    public void start() {
    	vaoID = glGenVertexArrays();
    	glBindVertexArray(vaoID);
    
    	vboID = glGenBuffers();
    	glBindBuffer(GL_ARRAY_BUFFER, vboID);
    	glBufferData(GL_ARRAY_BUFFER, vertices.length*Float.BYTES, GL_DYNAMIC_DRAW);
    	
    	int eboID = glGenBuffers();
    	int[] indices = generateIndices();
    	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
    	glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    	
    	glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
    	glEnableVertexAttribArray(0);
    	
    	glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
    	glEnableVertexAttribArray(1);
    	
    	glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
    	glEnableVertexAttribArray(2);
    	
    	glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
    	glEnableVertexAttribArray(3);
    	
    }
    
    
    public void addSprite(SpriteRenderer spr) {
    	int index = this.numSprites;
    	this.sprites[index] = spr;
    	this.numSprites++;
    	
    	if(spr.getTexture() != null) {
    		if(!textures.contains(spr.getTexture())) {
    			textures.add(spr.getTexture());
    		}
    	}
    	
    	//add properties
    	loadVertexProperties(index);
    	
    	if(numSprites >= this.maxBatchSize) {
    		this.hasRoom = false;
    	}
    }
    boolean first = true;
    public void render() {
    	boolean rebufferData = false;
    	for(int i = 0; i < numSprites; i++) {
    		SpriteRenderer spr = sprites[i];
    		if(spr.isDirty()) {
    			loadVertexProperties(i);
    			spr.setClean();
    			rebufferData = true;
    		}
    	}
    	if(rebufferData) {
    		first = false;
    		glBindBuffer(GL_ARRAY_BUFFER, vboID);
    		glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
    	}
    	
    	//Shader thingis here
    	shader.use();
    	shader.uploadMat4f("projectionMat", Window.getScene().camera().getProjectionMatrix());
    	shader.uploadMat4f("viewMat", Window.getScene().camera().getViewMatrix());
    	
    	for(int i = 0; i < textures.size(); i++) {
    		glActiveTexture(GL_TEXTURE0 + i+1);
    		textures.get(i).bind();
    	}
    	shader.uploadintArray("textures", texSlots);	
    	
    	glBindVertexArray(vaoID);
    	glEnableVertexAttribArray(0);
    	glEnableVertexAttribArray(1);
    	
    	glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);
    	
    	glDisableVertexAttribArray(0);
    	glDisableVertexAttribArray(1);
    	glBindVertexArray(0);
    	
    	for(int i = 0; i < textures.size(); i++) {
    		textures.get(i).unbind();
    	}
    	
    	shader.detach();
    }
    
    private void loadVertexProperties(int index) {
    	SpriteRenderer sprite = this.sprites[index];
    	
    	int offset = index * 4 * VERTEX_SIZE;
    	
    	Vector4f color = sprite.getColor();
    	Vector2f[] texCoords = sprite.getTexCoords();
    	
    	int texID = 0;
    	if(sprite.getTexture()!=null) {
    		for(int i = 0; i < textures.size(); i++) {
    			if(textures.get(i).equals(sprite.getTexture())) {
    				texID = i + 1;
    				break;
    			}
    		}
    	}	
    	
    	
    	float xAdd = 1.0f;
    	float yAdd = 1.0f;
    	for(int i = 0; i < 4; i++) {
            if (i == 1) {
                yAdd = 0.0f;
            } else if (i == 2) {
                xAdd = 0.0f;
            } else if (i == 3) {
                yAdd = 1.0f;
            }
    		
            //Load The Position
            vertices[offset] = sprite.gameObject.transform.position.x + 
            		(xAdd*sprite.gameObject.transform.scale.x);
    		vertices[offset + 1] = sprite.gameObject.transform.position.y +
    				(yAdd * sprite.gameObject.transform.scale.y);
    	
    		//Load the Color
    		vertices[offset + 2] = color.x;
    		vertices[offset + 3] = color.y;
    		vertices[offset + 4] = color.z;
    		vertices[offset + 5] = color.w;
    	
    		//Load Texture coordinates
    		vertices[offset + 6] = texCoords[i].x;
    		vertices[offset + 7] = texCoords[i].y;
    		
    		//Load Texture ID
    		vertices[offset + 8] = texID;
    		
    		offset += VERTEX_SIZE;
    	} 
    }
    
    private int[] generateIndices() {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * maxBatchSize];
        for (int i=0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }
    
    
    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // 3, 2, 0, 0, 2, 1        7, 6, 4, 4, 6, 5
        // Triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        // Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }
    
    public boolean hasRoom() {
        return this.hasRoom;
    }
    
    public boolean hasTextureRoom() {
    	return this.textures.size() < 8;
    }
    
    public boolean hasTexture(Texture tex) {
    	return this.textures.contains(tex);
    }

	public int getzIndex() {
		return this.zIndex;
	}

	@Override
	public int compareTo(RenderBatch o) {
		return Integer.compare(this.zIndex, o.zIndex);
	}
	
    //Write an own Sorting function
    
}