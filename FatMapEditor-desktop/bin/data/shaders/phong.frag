#version 150
// GLSL version 1.50
// Fragment shader for diffuse shading in combination with a texture map

#define MAX_LIGHTS 5

// Uniform variables passed in from host program
uniform vec3 kd;
uniform vec3 ka;
uniform vec3 ca;
uniform vec3 ks;
uniform float phong;
uniform vec4 radiance[MAX_LIGHTS];

// Variables passed in from the vertex shader
varying float ndotl[MAX_LIGHTS];
varying vec4 lightDir[MAX_LIGHTS];
varying vec2 frag_texcoord;
varying vec4 frag_normal;
varying vec4 eye;

void main()
{	
	//Diffuse
	vec4 diffuse[MAX_LIGHTS];
	for (int i = 0; i < MAX_LIGHTS; i++) {
		diffuse[i] = radiance[i] * ndotl[i] * vec4(kd,0);
	}
	vec4 dif = diffuse[0];
	for (int i = 1; i < MAX_LIGHTS; i++) {
		dif = dif + diffuse[i];
	}
	
	//Specular
	vec4 specular[MAX_LIGHTS];
	for (int i = 0; i < MAX_LIGHTS; i++) {
		vec4 ref = normalize(reflect(-lightDir[i], frag_normal));
		specular[i] = radiance[i] * vec4(ks,0) * pow(max(dot(ref,eye),0),phong);
	}
	vec4 spec = specular[0];
	for (int i = 1; i < MAX_LIGHTS; i++) {
		spec = spec + specular[i];
	}
	
	//Ambient
	vec4 ambient = vec4(ka,0) * vec4(ca,0);
	
	gl_FragColor = dif + spec + ambient;
}
