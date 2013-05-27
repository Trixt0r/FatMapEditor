package trixt0r.map.fat.widget.layer.nodes;

import trixt0r.map.fat.core.FatMapLayer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;

public class LayerNode extends Node {

	public final FatMapLayer layer;
	
	public LayerNode(Actor actor, FatMapLayer layer) {
		super(actor);
		this.layer = layer;
	}

}
