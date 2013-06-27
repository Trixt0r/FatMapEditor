package trixt0r.map.fat;

import trixt0r.map.fat.core.FatMapLayer;
import trixt0r.map.fat.core.FatMapObject;
import trixt0r.map.fat.transform.SelectionBox;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class FatTransformer {
	
	public static enum TOOL{
		TRANSLATION, SCALING, ROTATION, CREATION, ERASING, SELECTION, FLIP_HOR, FLIP_VER;
	}
	
	public static TOOL currentTool = TOOL.TRANSLATION;
	
	public static boolean snapToGrid = false;
	private static Vector2 tempVec = new Vector2(), tempVec2 = new Vector2();
	
	public static void translate(SelectionBox selection, float x, float y, boolean involveDiff){
		tempVec.set(x, y);
		if(involveDiff)
			tempVec.add(selection.xDiff, selection.yDiff);
		if(snapToGrid)
			snapToGrid(tempVec);
		selection.setX(tempVec.x);
		selection.setY(tempVec.y);
		selection.updateCenter();
	}
	
	public static void translateRelative(SelectionBox selection, float x, float y){
		tempVec.set(selection.getX() + x, selection.getY() + y);
		if(snapToGrid)
			snapToGrid(tempVec);
		
		selection.setX(tempVec.x);
		selection.setY(tempVec.y);
		selection.updateCenter();
	}
	
	public static void scale(SelectionBox selection, float x, float y, boolean involveDiff){
		tempVec2.set(selection.tempX - selection.xDiff, selection.tempY - selection.yDiff);
		tempVec.set(x, y);
		tempVec.sub(tempVec2);
		if(snapToGrid)
			snapToGrid(tempVec);
		selection.setScaleX(((selection.tempWidth+tempVec.x)/selection.tempWidth)*selection.tempScaleX);
		selection.setScaleY(((selection.tempHeight+tempVec.y)/selection.tempHeight)*selection.tempScaleY);
		selection.updateCenter();
	}
	
	public static void rotateRelative(SelectionBox selection, float targetX, float targetY){
		tempVec2.set(targetX, targetY).sub(selection.center);
		if(snapToGrid)	snapToGrid(tempVec2);
		float angle = tempVec2.angle();

		selection.setAngle(selection.tempAngle+angleDifference(angle, selection.clickedAngle)+180);
		
		tempVec.set(selection.center.x-(selection.tempX), selection.center.y-(selection.tempY));
		tempVec.rotate(angleDifference(angle,selection.clickedAngle));
		tempVec.add(selection.center);
		selection.setX(tempVec.x);
		selection.setY(tempVec.y);
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
	

	public static Vector2 getRelativePoint(float sourceX, float sourceY, float sourceAngle, float x, float y){
		Vector2 v = new Vector2(x - sourceX, y - sourceY);
		float cos = MathUtils.cosDeg(sourceAngle);
		float sin = MathUtils.sinDeg(sourceAngle);
		float xx = v.y * sin + v.x * cos;
		float yy = v.y * cos - v.x * sin;
		v.set(xx, yy);
		return v;
	}

}
