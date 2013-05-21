package trixt0r.map.fat.core;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class FatMapObject extends Actor{
	
	public final FatMapLayer layer;
	public final int id;
	public final MapObject mapObject;
	
	public FatMapObject(FatMapLayer layer, int id, MapObject mapObject){
		this.layer = layer;
		this.id = id;
		this.mapObject = mapObject;
	}
	
}
