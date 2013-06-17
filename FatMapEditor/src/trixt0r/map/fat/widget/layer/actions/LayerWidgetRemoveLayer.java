package trixt0r.map.fat.widget.layer.actions;

import trixt0r.map.fat.core.FatMapStage;
import trixt0r.map.fat.widget.layer.nodes.LayerNode;

import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.utils.Array;

/**
 * An action which is responsible for removing selected layer(s) from the map.
 * @author Trixt0r
 */
public class LayerWidgetRemoveLayer extends LayerWidgetAction{

	public Array<Node> toRemove;
	private FatMapStage stage;
	
	public LayerWidgetRemoveLayer(Tree layerTree, Array<Node> toRemove, FatMapStage stage) {
		super(layerTree);
		this.toRemove = toRemove;
		this.stage = stage;
	}

	/**
	 * Before calling this method you have to set {@link #toRemove}!
	 */
	@Override
	public boolean act(float delta) {
		if(this.toRemove.size == 0) return false;
		
		for(Node node: this.toRemove){
			this.layerTree.remove(node);
			stage.removeLayer(((LayerNode)node).layer);
		}
		return true;
	}

}
