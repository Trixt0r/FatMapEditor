package trixt0r.map.fat;

import trixt0r.map.fat.core.FatMapLayer;
import trixt0r.map.fat.core.FatMapObject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class FatTransformer {
	
	public static enum TOOL{
		TRANSLATION, SCALING, ROTATION, CREATION, ERASING, SELECTION, FLIP_HOR, FLIP_VER;
	}
	
	public static TOOL currentTool = TOOL.TRANSLATION;
	
	public static boolean snapToGrid = false;
	private static Vector2 tempVec = new Vector2(), tempVec2 = new Vector2();
	
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
	
	public static void scale(Array<FatMapObject> objects, float x, float y, boolean involveDiff){
		tempVec2.set(objects.get(0).getX()-objects.get(0).xDiff, objects.get(0).getY()-objects.get(0).yDiff);
		for(FatMapObject object: objects){
			tempVec.set(x, y);
			tempVec.sub(tempVec2);
			if(snapToGrid)	snapToGrid(tempVec);
			object.setWidth(object.tempWidth + tempVec.x);
			object.setHeight(object.tempHeight + tempVec.y);
		}
	}
	
	public static void rotateRelative(Array<FatMapObject> objects, float targetX, float targetY, float centerX, float centerY, float startingAngle){
		Vector2 center = new Vector2(centerX, centerY);
		Vector2 temp = new Vector2();
		tempVec2.set(targetX, targetY);
		float angle;
		tempVec2.sub(center);
		if(snapToGrid)	snapToGrid(tempVec2);
		angle = tempVec2.angle();
		for(FatMapObject object: objects){
			
			object.setRotation(object.tempAngle+angleDifference(angle, startingAngle)+180);
			
			temp.set(center.x-(object.tempX), center.y-(object.tempY));
			temp.rotate(angleDifference(angle,startingAngle));
			temp.add(center);
			object.setX(temp.x);
			object.setY(temp.y);
		}
	}
		
	public static void create(float x, float y, FatMapLayer layer, FatMapObject preview){
		//TODO
	}
	
	public static void snapToGrid(Vector2 position){
		position.x = Math.round(position.x / FatMapEditor.GRID_XOFFSET) * FatMapEditor.GRID_XOFFSET;
		position.y = Math.round(position.y / FatMapEditor.GRID_YOFFSET) * FatMapEditor.GRID_YOFFSET;
	}
	
	public static float angleDifference(float a, float b){
		return ((((a - b) % 360) + 540) % 360) - 180;
	}

}
