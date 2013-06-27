package trixt0r.map.fat.utils;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class VerticesUtils {
	
	public static void calcBBoxForVertices(float[] vertices, Rectangle target) {
		float minX = vertices[0];
		float minY = vertices[1];
		float maxX = vertices[0];
		float maxY = vertices[1];

		final int numFloats = vertices.length;
		for (int i = 2; i < numFloats; i += 2) {
			minX = minX > vertices[i] ? vertices[i] : minX;
			minY = minY > vertices[i + 1] ? vertices[i + 1] : minY;
			maxX = maxX < vertices[i] ? vertices[i] : maxX;
			maxY = maxY < vertices[i + 1] ? vertices[i + 1] : maxY;
		}
		
		target.x = minX;
		target.y = minY;
		target.width = maxX - minX;
		target.height = maxY - minY;
	}
	
	public static Vector2 getCenterForVertices(float[] vertices){
		Rectangle rect = new Rectangle();
		calcBBoxForVertices(vertices, rect);
		return new Vector2(rect.x+rect.width/2, rect.y+rect.height/2);
	}
	
	public static Vector2 getCenterForMapObject(MapObject mapObject){
		if(mapObject instanceof PolygonMapObject)
			return getCenterForVertices(((PolygonMapObject)mapObject).getPolygon().getTransformedVertices());
		else if(mapObject instanceof PolylineMapObject)
			return getCenterForVertices(((PolylineMapObject)mapObject).getPolyline().getTransformedVertices());
		else return null;
	}

}
