package trixt0r.map.fat;

import trixt0r.map.fat.core.FatMapObject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class FatTransformer {
	
	public static boolean snapToGrid = false;
	private static Vector2 tempVec = new Vector2();
	
	public static void translate(Array<FatMapObject> objects, float x, float y, boolean involveDiff){
		for(FatMapObject object: objects){
			tempVec.set(x, y);
			if(involveDiff)
				tempVec.add(object.xDiff, object.yDiff);
			if(snapToGrid)
				snapToGrid(tempVec);
			object.setX(tempVec.x);
			object.setY(tempVec.y);
		}
	}
	
	public static void translateRelative(Array<FatMapObject> objects, float x, float y){
		for(FatMapObject object: objects){
			tempVec.set(object.getX() + x, object.getY() + y);
			if(snapToGrid)
				snapToGrid(tempVec);
			
			object.setX(tempVec.x);
			object.setY(tempVec.y);
		}
	}
	
	public static void snapToGrid(Vector2 position){
		position.x = Math.round(position.x / FatMapEditor.GRID_XOFFSET) * FatMapEditor.GRID_XOFFSET;
		position.y = Math.round(position.y / FatMapEditor.GRID_YOFFSET) * FatMapEditor.GRID_YOFFSET;
	}

}
