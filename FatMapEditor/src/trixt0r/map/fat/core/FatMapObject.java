package trixt0r.map.fat.core;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class FatMapObject extends Actor{
	
	public FatMapLayer layer;
	public final int id;
	public final MapObject mapObject;
	public boolean selected, moveable;
	public float xDiff, yDiff;
	
	public FatMapObject(FatMapLayer layer, int id, MapObject mapObject){
		this.layer = layer;
		this.id = id;
		this.mapObject = mapObject;
		this.selected = false;
		this.moveable = true;
	}
	
	public abstract void draw(ShapeRenderer renderer);
	
	public void removeFromLayer(){
		this.layer.removeObject(this);
	}
	
	@Override
	public void setX(float x){
		if(!this.moveable) return;
		super.setX(x);
	}
	
	@Override
	public void setY(float y){
		if(!this.moveable) return;
		super.setY(y);
	}
	
}
