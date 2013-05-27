package trixt0r.map.fat.widget.layer.actions;

import trixt0r.map.fat.widget.layer.nodes.LayerNode;
import trixt0r.map.fat.widget.layer.nodes.ObjectNode;

import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.utils.Array;

/**
 * An action which is responsible for removing selected nodes.
 * @author Trixt0r
 */
public class LayerWidgetRemoveObject extends LayerWidgetObjectAction {

	public Array<Node> toRemove;
	
	public LayerWidgetRemoveObject(LayerNode root, Array<Node> toRemove) {
		super(root);
		this.toRemove = toRemove;
	}

	@Override
	public boolean act(float delta) {
		if(toRemove.size == 0) return false;
		
		for(Node node: toRemove)
			if(node.getParent() == root){
				root.getTree().remove(node);
				((ObjectNode)node).object.removeFromLayer();
				
			}
		return true;
	}

}
