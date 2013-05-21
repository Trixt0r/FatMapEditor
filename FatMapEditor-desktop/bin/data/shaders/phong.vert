#version 150
// GLSL version 1.50 
// Vertex shader for diffuse shading in combination with a texture map

#define MAX_LIGHTS 5

// Uniform variables, passed in from host program via suitable 
// variants of glUniform*
uniform mat4 projection;
uniform mat4 modelview;
uniform vec4 lightDirection[MAX_LIGHTS];
uniform vec4 radiance[MAX_LIGHTS];
uniform vec4 posLight[MAX_LIGHTS];
uniform int type[MAX_LIGHTS];
uniform vec3 cam;

// Input vertex attributes; passed in from host program to shader
// via vertex buffer objects
attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

// Output variables for fragment shader
varying float ndotl[MAX_LIGHTS];
varying vec4 lightDir[MAX_LIGHTS];
varying vec2 frag_texcoord;
varying vec4 frag_normal;
varying vec4 eye;

void main()
{		
	// Compute dot product of normal and light direction
	// and pass color to fragment shader
	// Note: here we assume "lightDirection" is specified in camera coordinates,
	// so we transform the normal to camera coordinates, and we don't transform
	// the light direction, i.e., it stays in camera coordinates
	float ldotn[MAX_LIGHTS];
	for (int i = 0; i< MAX_LIGHTS; i++) {
		if (type[i] == 1){
			ndotl[i] = max(dot(modelview * vec4(normal,0), lightDirection[i]),0);
			lightDir[i] = normalize(lightDirection[i]);
		}
		if (type[i] == 2){
			ndotl[i] = max(dot(modelview*vec4(normal,0), (posLight[i]-modelview*position)/length(posLight[i]-modelview*position)),0);
			lightDir[i] = normalize(posLight[i] - modelview*position);
		}
	}
	frag_normal = normalize(modelview * vec4(normal,0));
	eye = normalize(-(modelview*position));

	// Pass texture coordiantes to fragment shader, OpenGL automatically
	// interpolates them to each pixel  (in a perspectively correct manner) 
	frag_texcoord = texcoord;

	// Transform position, including projection matrix
	// Note: gl_Position is a default output variable containing
	// the transformed vertex position
	gl_Position = projection * modelview * position;
}
