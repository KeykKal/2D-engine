#type vertex
#version 330 core
	layout (location=0) in vec3 aPos;
	layout (location=1) in vec4 aColor;
	layout (location=2) in vec2 aTextCoords;
	layout (location=3) in float aTexID;

	uniform mat4 projectionMat;
	uniform mat4 viewMat;

	out vec4 fColor;
	out vec2 uvPass;
	out float texIDPass;


	void main()
	{
    	fColor = aColor;
    	uvPass = aTextCoords;
    	texIDPass = aTexID;

    	gl_Position = projectionMat * viewMat * vec4(aPos, 1.0);
	}

#type fragment
#version 330 core

	uniform sampler2D textures[8];

	in vec4 fColor;
	in vec2 uvPass;
	in float texIDPass;

	out vec4 color;

	void main()
	{
		if(texIDPass > 0){
			int id = int(texIDPass);
			color = fColor * texture(textures[id], uvPass);
		} else {
			color = fColor;
		}

	}
