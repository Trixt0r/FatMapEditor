package trixt0r.map.fat.core;

import trixt0r.map.fat.transform.FatBox;
import trixt0r.map.fat.widget.layer.nodes.ObjectNode;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class FatMapObject extends Actor{
	
	public static final Color DESELECTED = new Color(0f, .25f, .75f, 1f), SELECTED = new Color(.75f, .25f, .25f,1f), NOT_ON_CURRENT_LAYER = new Color(.75f, .75f, .75f, .25f);
	
	public FatMapLayer layer;
	public final int id;
	public boolean moveable;
	public float tempAngle, tempX, tempY, tempScaleX, tempScaleY, tempWidth, tempHeight;
	private float parentX, parentY;
	protected ObjectNode node;
	protected boolean selected;
	public boolean isOnSelectedLayer;
	protected Rectangle boundingBox;
	protected float originXRel, originYRel;
	public final FatBox parent;
	private static final Vector2 temp = new Vector2();
	
	public FatMapObject(FatMapLayer layer, int id, ObjectNode node){
		this.layer = layer;
		this.id = id;
		this.selected = false;
		this.moveable = true;
		this.node = node;
		node.object = this;
		this.boundingBox = new Rectangle();
		this.calcBBox();
		this.parent = new FatBox();
		this.parent.set(boundingBox);
		
	}
	
	protected void setUpRelPos(){
		this.parentX = this.getX()-this.parent.getX();
		this.parentY = this.getY()-this.parent.getY();
	}
	
	public abstract void draw(ShapeRenderer renderer);
	
	public abstract void update();
	
	public abstract void calcBBox();
	
	protected abstract void setObjectRotation(float angle);
	protected abstract void setObjectPosition(float x, float y);
	protected abstract void setObjectScale(float scaleX, float scaleY);
	
	public void select(boolean select){
		this.selected = select;
		if(!this.node.getParent().getTree().getSelection().contains(node, true) && this.selected && this.isOnSelectedLayer)
			this.node.getParent().getTree().getSelection().add(this.node);
		else if(this.node.getParent().getTree().getSelection().contains(node, true) && !this.selected)
			this.node.getParent().getTree().getSelection().removeValue(node, true);
	}
	
	public boolean isSelected(){
		return this.selected;
	}
	
	public void removeFromLayer(){
		node.getParent().remove(node);
		this.layer.removeObject(this);
	}
	
	protected void updateToParent() {
		this.setObjectRotation(this.parent.getAngle());
		temp.set(this.parentX*this.parent.getScaleX(),
				this.parentY*this.parent.getScaleY()).
				rotate(this.parent.getAngle()).
				add(this.parent.getX(), this.parent.getY());
		this.setObjectPosition(temp.x, temp.y);
		this.setObjectScale(this.parent.getScaleX(), this.parent.getScaleY());
	}
	
	@Override
	public void setX(float x){
		if(!this.moveable) return;
		this.parent.setX(x);
		this.updateToParent();
	}

	@Override
	public void setY(float y){
		if(!this.moveable) return;
		this.parent.setY(y);
		this.updateToParent();
	}
	
	@Override
	public void setWidth(float width){
		if(!this.moveable) return;
		this.parent.setWidth(width);
		this.updateToParent();
	}
	
	@Override
	public void setHeight(float height){
		if(!this.moveable) return;
		this.parent.setHeight(height);
		this.updateToParent();
	}
	
	@Override
	public void setPosition(float x, float y){
		this.setX(x);
		this.setY(y);
	}
	
	public Rectangle getBBox(){
		return this.boundingBox;
	}
	
	@Override
	public void setRotation(float degrees){
		if(!this.moveable) return;
		this.parent.setAngle(degrees);
		this.updateToParent();
	}
	
	@Override
	public void setScaleX(float scaleX){
		if(!this.moveable) return;
		this.parent.setScaleX(scaleX);
		this.updateToParent();
	}
	
	@Override
	public void setScaleY(float scaleY){
		if(!this.moveable) return;
		this.parent.setScaleY(scaleY);
		this.updateToParent();
	}
	
	public void setScale(float scaleX, float scaleY){
		if(!this.moveable) return;
		this.setScaleX(scaleX);
		this.setScaleY(scaleY);
	}
	
	@Override
	public void setScale(float scale){
		if(!this.moveable) return;
		this.setScaleX(scale);
		this.setScaleY(scale);
	}
	
	public void setUpTempValues(){
		this.tempScaleX = this.parent.getScaleX();
		this.tempScaleY = this.parent.getScaleY();
		this.tempAngle = this.parent.getAngle();
		this.tempWidth = this.parent.getWidth();
		this.tempHeight= this.parent.getHeight();
	}
	
	public void applyTempValues(){
		this.setX(this.tempX);
		this.setY(this.tempY);
		this.setRotation(this.tempAngle);
		this.setWidth(this.tempWidth);
		this.setHeight(this.tempHeight);
		this.setScale(this.tempScaleX, this.tempScaleY);
	}
	
	public void resetTemps(){
		this.tempAngle = 0;
		this.tempScaleX = 1;
		this.tempScaleY = 1;
	}
	
}
