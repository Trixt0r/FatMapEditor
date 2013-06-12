package trixt0r.map.fat.core;

import trixt0r.map.fat.widget.layer.nodes.ObjectNode;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class FatMapObject extends Actor{
	
	public static final Color DESELECTED = new Color(0f, .25f, .75f, 1f), SELECTED = new Color(.75f, .25f, .25f,1f), NOT_ON_CURRENT_LAYER = new Color(.75f, .75f, .75f, .25f);
	
	public FatMapLayer layer;
	public final int id;
	public final MapObject mapObject;
	public boolean moveable;
	public float xDiff, yDiff, tempWidth, tempHeight, tempAngle, tempX, tempY, tempScaleX, tempScaleY;
	protected ObjectNode node;
	protected boolean selected;
	public boolean isOnSelectedLayer;
	protected Rectangle boundingBox;
	protected float originXRel, originYRel;
	private boolean dirty = false;
	
	public FatMapObject(FatMapLayer layer, int id, MapObject mapObject, ObjectNode node){
		this.layer = layer;
		this.id = id;
		this.mapObject = mapObject;
		this.selected = false;
		this.moveable = true;
		this.node = node;
		node.object = this;
		this.boundingBox = new Rectangle();
	}
	
	public abstract void draw(ShapeRenderer renderer);
	
	//public abstract void update();
	
	protected abstract void calcBBox();
	
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
	
	@Override
	public void setX(float x){
		if(!this.moveable) return;
		super.setX(x);
		this.dirty = true;
	}
	
	@Override
	public void setY(float y){
		if(!this.moveable) return;
		super.setY(y);
		this.dirty = true;
	}
	
	@Override
	public void setWidth(float width){
		if(!this.moveable) return;
		super.setWidth(width);
		this.dirty = true;
	}
	
	@Override
	public void setHeight(float height){
		if(!this.moveable) return;
		super.setHeight(height);
		this.dirty = true;
	}
	
	@Override
	public void setPosition(float x, float y){
		this.setX(x);
		this.setY(y);
	}
	
	public void setOriginRel(float x, float y){
		this.originXRel = x;
		this.originYRel = y;
	}
	
	public Rectangle getBBox(){
		if(this.dirty){
			this.dirty = false;
			this.calcBBox();
		}
		return this.boundingBox;
	}
	
	@Override
	public void setRotation(float degrees){
		super.setRotation(degrees);
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
	
	@Override
	public void setScaleX(float scaleX){
		if(!this.moveable) return;
		super.setScaleX(scaleX);
	}
	
	@Override
	public void setScaleY(float scaleY){
		if(!this.moveable) return;
		super.setScaleY(scaleY);
	}
	
}
