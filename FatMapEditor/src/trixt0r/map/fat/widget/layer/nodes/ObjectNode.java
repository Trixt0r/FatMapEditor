package trixt0r.map.fat.widget.layer.nodes;

import trixt0r.map.fat.core.FatMapObject;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;

public class ObjectNode extends Node {

	public LayerNode layer;
	public FatMapObject object;
	
	public ObjectNode(Actor actor, LayerNode layer, FatMapObject object) {
		super(actor);
		this.layer = layer;
		this.object = object;
	}

}
