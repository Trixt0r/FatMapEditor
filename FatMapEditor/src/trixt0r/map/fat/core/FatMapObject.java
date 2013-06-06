package trixt0r.map.fat.core;

import trixt0r.map.fat.widget.layer.nodes.ObjectNode;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class FatMapObject extends Actor{
	
	public FatMapLayer layer;
	public final int id;
	public final MapObject mapObject;
	public boolean moveable;
	public float xDiff, yDiff;
	protected ObjectNode node;
	protected boolean selected;
	protected Rectangle boundingBox;
	
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
	
	protected abstract void calcBBox();
	
	public void select(boolean select){
		this.selected = select;
		if(!this.node.getParent().getTree().getSelection().contains(node, true) && this.selected)
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
		this.calcBBox();
	}
	
	@Override
	public void setY(float y){
		if(!this.moveable) return;
		super.setY(y);
		this.calcBBox();
	}
	
	public Rectangle getBBox(){
		return this.boundingBox;
	}
	
}
