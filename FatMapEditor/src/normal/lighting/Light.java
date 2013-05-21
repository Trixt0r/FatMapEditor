package normal.lighting;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Light {
	
	public Vector3 position, attenuation;
	public Color color;
	
	public static Vector3 getVectorColor(Color color){
		return new Vector3(color.r, color.g, color.b);
	}

}
