package trixt0r.map.fat.utils;

import trixt0r.map.fat.core.FatMapObject;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class RectangleUtils {
	
	public static void calcBBox(Rectangle bbox, Array<FatMapObject> objects){
		if(objects.size == 0){
			bbox.set(0, 0, 0, 0);
			return;
		}
		Rectangle rect = objects.get(0).getBBox();
		float minX=rect.x, minY=rect.y, maxX=rect.width+minX, maxY=rect.height+minY;
		for(FatMapObject actor: objects){
			rect = actor.getBBox();
			minX = Math.min(Math.min(rect.x, minX), rect.width+rect.x);
			minY = Math.min(Math.min(rect.y, minY), rect.height+rect.y);
			maxX = Math.max(Math.max(maxX, rect.x+rect.width), rect.x);
			maxY = Math.max(Math.max(maxY, rect.y+rect.height), rect.y);
		}
		bbox.set(minX, minY, maxX-minX, maxY-minY);
	}
	
	public static float getCenterX(Rectangle rect){
		return rect.x+rect.width/2;
	}
	
	public static float getCenterY(Rectangle rect){
		return rect.y+rect.height/2;
	}
	
	public static Vector2 getOrigin(Array<FatMapObject> objects){
		Vector2 origin = new Vector2();
		Rectangle rect;
		for(FatMapObject object: objects){
			rect = object.getBBox();
			origin.x += rect.x+object.getOriginX();
			origin.y += rect.y+object.getOriginY();
		}
		origin.scl((1/((float)objects.size)));
		return origin;
	}
	
	public static boolean overlapping(Rectangle rect1, Rectangle rect2){
		final float minX = Math.min(rect1.x,rect1.x+rect1.width), maxX =Math.max(rect1.x,rect1.x+rect1.width);
		final float minY = Math.min(rect1.y,rect1.y+rect1.height), maxY =Math.max(rect1.y,rect1.y+rect1.height);
		Rectangle over = new Rectangle(minX,minY,maxX - minX,maxY - minY);
		return rect2.overlaps(over);
	}

}
