package trixt0r.map.fat.widget.layer.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.utils.GdxRuntimeException;

public abstract class LayerWidgetAction extends Action {
	

	protected Tree layerTree;
	
	public LayerWidgetAction(Tree layerTree){
		if(layerTree == null) throw new GdxRuntimeException("layerTree or layerNodes can't be null!");
		this.layerTree = layerTree;
	}

}
