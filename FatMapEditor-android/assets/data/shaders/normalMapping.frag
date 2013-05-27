#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
varying float angleOut;
uniform sampler2D u_texture;


vec3 rotate(vec3 normal){
	float rad = radians(360-angleOut);
	float c = cos(rad);
	float s = sin(rad);

	float newX = normal.x * c - normal.y * s;
	float newY = normal.x * s + normal.y * c;

	return vec3(newX, newY, normal.z);
}

void main() {
	vec4 nColor = texture2D(u_texture, v_texCoords.st).rgba;
	if(nColor.a != 0){
		vec3 normal = rotate(normalize(nColor.xyz * 2.0 - 1.0));
		gl_FragColor = (vec4(normal,1.0)+1.0)/2.0;
	}
	else
	 	gl_FragColor = vec4(0);
}