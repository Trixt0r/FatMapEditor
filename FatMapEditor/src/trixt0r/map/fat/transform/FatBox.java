package trixt0r.map.fat.transform;

import trixt0r.map.fat.FatTransformer;
import trixt0r.map.fat.utils.FatShapeDrawer;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class FatBox {
	
	protected float x, y, width, height, angle, scaleX, scaleY;
	public float tempAngle, tempWidth, tempHeight, tempScaleX, tempScaleY, tempX, tempY;
	public final Vector2 topRight, bottomRight, bottomLeft, topLeft;
	public final Vector2 center;
	protected float pivotX, pivotY;
	
	public FatBox(float x, float y, float width, float height){
		this.topRight = new Vector2();
		this.bottomRight = new Vector2();
		this.bottomLeft = new Vector2();
		this.topLeft = new Vector2();
		this.center = new Vector2();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.scaleX = 1f;
		this.scaleY = 1f;
		this.angle = 0;
		this.update();
	}
	
	public FatBox(){
		this(0,0,0,0);
	}
	
	protected void update(){
		float cos = MathUtils.cosDeg(angle);
		float sin = MathUtils.sinDeg(angle);
		this.topRight.set((width*scaleX/2) * cos - (height*scaleY/2) * sin, (width*scaleX/2) * sin + (height*scaleY/2) * cos).add(x, y);
		this.bottomRight.set((width*scaleX/2) * cos - (-height*scaleY/2) * sin, (width*scaleX/2) * sin + (-height*scaleY/2) * cos).add(x, y);
		this.bottomLeft.set((-width*scaleX/2) * cos - (-height*scaleY/2) * sin, (-width*scaleX/2) * sin + (-height*scaleY/2) * cos).add(x, y);
		this.topLeft.set((-width*scaleX/2) * cos - (height*scaleY/2) * sin, (-width*scaleX/2) * sin + (height*scaleY/2) * cos).add(x, y);
	}
	
	public void setAngle(float angle){
		this.angle = angle;
		this.update();
	}
	
	public void setWidth(float width){
		this.width = width;
		this.update();
	}
	
	public void setHeight(float height){
		this.height = height;
		this.update();
	}
	
	public void setX(float x){
		this.x = x;
		this.update();
	}
	
	public void setY(float y){
		this.y = y;
		this.update();
	}
	
	public void setScaleX(float scaleX){
		this.scaleX = scaleX;
		this.update();
	}
	
	public void setScaleY(float scaleY){
		this.scaleY = scaleY;
		this.update();
	}
	
	public float getX(){
		return this.x;
	}
	
	public float getY(){
		return this.y;
	}
	
	public float getWidth(){
		return this.width;
	}
	
	public float getHeight(){
		return this.height;
	}
	
	public float getAngle(){
		return this.angle;
	}
	
	public float getScaleX(){
		return this.scaleX;
	}
	
	public float getScaleY(){
		return this.scaleY;
	}
	
	public void set(float x, float y, float width, float height, float angle){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.angle = angle;
		this.update();
	}
	
	public void set(Rectangle rect){
		this.set(rect.x+rect.width/2, rect.y+rect.height/2, rect.width, rect.height, 0f);
	}
	
	public void reset(){
		this.x = 0;
		this.y = 0;
		this.width = 0;
		this.height = 0;
		this.angle = 0;
		this.scaleX = 1f;
		this.scaleY = 1f;
	}
	
	public void set(FatBox box){
		this.scaleX = box.scaleX;
		this.scaleY = box.scaleY;
		this.setPivot(box.pivotX, box.pivotY);
		this.set(box.x, box.y, box.width, box.height, box.angle);
		this.updateCenter();
		this.setUpTempValues();
	}
	
	public void setUpTempValues(){
		this.tempX = this.x;
		this.tempY = this.y;
		this.tempAngle = this.angle;
		this.tempWidth = this.width;
		this.tempHeight = this.height;
		this.tempScaleX = this.scaleX;
		this.tempScaleY = this.scaleY;
	}
	
	public void setPivot(float pivotX, float pivotY){
		this.pivotX = pivotX;
		this.pivotY = pivotY;
	}
	
	public void setCenter(float x, float y){
		Vector2 v = FatTransformer.getRelativePoint(this.x, this.y, this.angle, x, y);
		this.pivotX = v.x /((this.width*this.scaleX)/2);
		this.pivotY = v.y /((this.height*this.scaleY)/2);
	}
	
	public void updateCenter(){
		this.center.set(((this.width*this.scaleX)/2)*pivotX, ((this.height*this.scaleY)/2)*pivotY);
		this.center.rotate(this.angle);
		this.center.add(this.x, this.y);
	}
	
	public void draw(ShapeRenderer renderer){
		if(this.height == 0 || this.width == 0) return;
		FatShapeDrawer.drawBox(renderer, this);
	}

}
