#ifdef GL_ES
precision mediump float;
#endif

#define MAX_LIGHTS 10

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_normals;
uniform vec3 light[MAX_LIGHTS];
uniform vec3 ambientColor;
uniform float ambientIntensity; 
uniform vec2 resolution;
uniform vec3 lightColor;
uniform bool useNormals;
uniform bool useShadow;
uniform vec3 attenuation;
uniform float strength;
uniform bool yInvert;

void main() {
	vec3 sum = vec3(0.0);
	   
	//sample color & normals from our textures
	vec4 color = texture2D(u_texture, v_texCoords.st);
	vec3 nColor = texture2D(u_normals, v_texCoords.st).rgb;
	
	//some bump map programs will need the Y value flipped..
	nColor.g = yInvert ? 1.0 - nColor.g : nColor.g;
	
	//this is for debugging purposes, allowing us to lower the intensity of our bump map
	vec3 nBase = vec3(0.5, 0.5, 1.0);
	nColor = mix(nBase, nColor, strength);
	
	//normals need to be converted to [-1.0, 1.0] range and normalized
	vec3 normal = normalize(nColor * 2.0 - 1.0);
	vec3 deltaPos;
	vec3 lightDir;
	float lambert;
	float d;
	float att;
	vec3 result;
	for(int i = 0; i < MAX_LIGHTS; i++){
		//here we do a simple distance calculation
		deltaPos = vec3( ((light[0].xy) - gl_FragCoord.xy) / resolution.xy, light[0].z);
	
		lightDir = normalize(deltaPos);
		lambert = useNormals ? clamp(dot(normal, lightDir), 0.0, 1.0) : 1.0;
	   
		//now let's get a nice little falloff
		d = sqrt(dot(deltaPos, deltaPos));
		att = useShadow ? 1.0 / ( attenuation.x + (attenuation.y*d) + (attenuation.z*d*d) ) : 1.0;
		if(i == 0)result = (ambientColor * ambientIntensity) + (lightColor.rgb * lambert) * att;
		else result = vec3(0.0);
		result *= color.rgb;
		sum += result;
	}
	
	gl_FragColor = v_color * vec4(sum, color.a);
}