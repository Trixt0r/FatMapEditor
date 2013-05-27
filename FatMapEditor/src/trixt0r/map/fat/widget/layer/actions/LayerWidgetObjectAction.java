package trixt0r.map.fat.widget.layer.actions;

import trixt0r.map.fat.widget.layer.nodes.LayerNode;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.GdxRuntimeException;

public abstract class LayerWidgetObjectAction extends Action{
	
	protected LayerNode root;
	
	public LayerWidgetObjectAction(LayerNode root){
		if(root == null) throw new GdxRuntimeException("Root can't be null!");
		this.root = root;
	}

}
