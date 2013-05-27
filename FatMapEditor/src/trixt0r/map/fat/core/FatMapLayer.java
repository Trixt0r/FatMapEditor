package trixt0r.map.fat.core;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.utils.Array;

public class FatMapLayer extends MapLayer {
	public final int id;
	public String name;
	
	private int objectId;
	
	public final Array<FatMapObject> objects;
	
	
	public FatMapLayer(int id, String name){
		this.id = id;
		this.name = name;
		this.objects = new Array<FatMapObject>();
		this.objectId = 0;
	}
	
	public void draw(ShapeRenderer renderer){
		for(FatMapObject object: this.objects)
			object.draw(renderer);
	}
	
	public void addObject(FatMapObject object){
		object.layer = this;
		this.objects.add(object);
	}
	
	public void removeObject(FatMapObject object){
		object.layer = null;
		this.objects.removeIndex(this.objects.indexOf(object, true));
	}
	
	public int getObjectId(){
		return this.objectId++;
	}
}
