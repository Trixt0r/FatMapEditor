package trixt0r.map.fat.transform;

import trixt0r.map.fat.FatTransformer;
import trixt0r.map.fat.utils.FatShapeDrawer;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class TransformerPoint {
	
	public final Rectangle bbox;
	private float xDiff, yDiff;
	private boolean clicked;
	public final Vector2 attachedPoint;
	private final Vector2 temp = new Vector2();
	
	public TransformerPoint(Vector2 attached){
		this(attached, 0,0,0,0);
	}
	
	public TransformerPoint(Vector2 attached,float x, float y, float width, float height){
		this.bbox = new Rectangle(x,y, width, height);
		this.attachedPoint = attached;
	}
	
	public void click(float x, float y){
		this.xDiff = (this.bbox.x + this.bbox.width/2) - x;
		this.yDiff = (this.bbox.y + this.bbox.height/2) - y;
	}
	
	public boolean inside(float x, float y){
		return this.bbox.contains(x, y);
	}
	
	public void setClicked(boolean clicked){
		this.clicked = clicked;
	}
	
	public void drag(float x, float y){
		if(!this.clicked) return;
		this.temp.set(x+this.xDiff, y+this.yDiff);
		if(FatTransformer.snapToGrid)
			FatTransformer.snapToGrid(temp);
		this.bbox.x = temp.x - this.bbox.width/2;
		this.bbox.y = temp.y - this.bbox.width/2;
	}
	
	public boolean isClicked(){
		return this.clicked;
	}
	
	public void draw(ShapeRenderer renderer){
		FatShapeDrawer.drawRectangle(renderer, bbox);
	}
	
}
