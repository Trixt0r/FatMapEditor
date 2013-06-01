package trixt0r.map.fat.core;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

public class FatMapLayers extends Group{
	
	private Array<FatMapLayer> LAYERS = new Array<FatMapLayer>();
	private static int LAYER_ID = 0;
	
	public FatMapLayers(){
		this.LAYERS = new Array<FatMapLayer>();
	}
	
	public FatMapLayer addLayer(String name){
		FatMapLayer layer = new FatMapLayer(LAYER_ID++, name);
		addLayer(layer);
		return layer;
	}
	
	private void addLayer(FatMapLayer layer){
		LAYERS.add(layer);
	}
	
	public void removeLayer(FatMapLayer layer){
		LAYERS.removeValue(layer, true);
	}
	
	public FatMapLayer getLayerById(int id){
		for(FatMapLayer layer: LAYERS)
			if(layer.id == id) return layer;
		return null;
	}
	
	public FatMapLayer getLayerByName(String name){
		for(FatMapLayer layer: LAYERS)
			if(layer.name.equals(name)) return layer;
		return null;
	}
	
	public  void draw(ShapeRenderer renderer){
		for(FatMapLayer layer: LAYERS)
			layer.draw(renderer);
	}

}
