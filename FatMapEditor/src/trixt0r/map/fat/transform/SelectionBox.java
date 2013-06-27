package trixt0r.map.fat.transform;

import trixt0r.map.fat.core.FatMapObject;
import trixt0r.map.fat.utils.RectangleUtils;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class SelectionBox extends FatBox {
	
	public final Array<FatMapObject> objects;
	public float clickedAngle, xDiff, yDiff;
	private final Rectangle rect;
	private static final Vector2 temp = new Vector2();
	private boolean updateChildren = true;
	private FatBox tempBox;
	
	public SelectionBox(){
		super();
		this.scaleX = 1f;
		this.scaleY = 1f;
		this.objects = new Array<FatMapObject>();
		this.rect = new Rectangle();
	}
	
	public SelectionBox(Array<FatMapObject> objects){
		this();
		this.setObjects(objects);
	}
	
	public void setObjects(Array<FatMapObject> objects){
		RectangleUtils.calcBBox(this.rect, objects);
		this.updateChildren = false;
		this.reset();
		this.set(rect);
		this.objects.clear();
		this.objects.addAll(objects);
		
		for(FatMapObject object: this.objects){
			object.setUpTempValues();
			object.tempX = object.parent.getX() - this.x;
			object.tempY = object.parent.getY() - this.y;
		}
		if(objects.size == 1){
			FatMapObject object = objects.get(0);
			this.tempBox = object.parent;
			this.set(object.parent);
			object.resetTemps();
			object.tempX = object.parent.getX() - this.x;
			object.tempY = object.parent.getY() - this.y;
		}
		else{
			this.tempBox = null;
			this.setPivot(0, 0);
			this.updateCenter();
		}
		this.updateChildren = true;
	}
	
	@Override
	public void update(){
		super.update();
		if(this.objects == null || !this.updateChildren) return;
		for(FatMapObject object: this.objects){
			object.setRotation(object.tempAngle+this.angle);			
			temp.set(object.tempX*this.scaleX, object.tempY*this.scaleY).rotate(this.angle).add(this.x, this.y);
			object.setPosition(temp.x, temp.y);
			object.setScale(this.scaleX*object.tempScaleX, this.scaleY*object.tempScaleY);
			object.parent.updateCenter();
		}
	}
	
	@Override
	public void reset(){
		super.reset();
		this.objects.clear();
	}
	
	@Override
	public void setPivot(float pivotX, float pivotY){
		super.setPivot(pivotX, pivotY);
		if(this.tempBox != null) this.tempBox.setPivot(pivotX, pivotY);
	}
	
	@Override
	public void setCenter(float x, float y){
		super.setCenter(x, y);
		if(this.tempBox != null) this.tempBox.setCenter(x, y);
	}
	
	public void updateCenter(){
		super.updateCenter();
		for(FatMapObject object: this.objects)
			object.parent.updateCenter();
	}

}
