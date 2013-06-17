package trixt0r.map.fat.widget.layer.actions;

import trixt0r.map.fat.FatMapEditor;
import trixt0r.map.fat.core.FatMapLayer;
import trixt0r.map.fat.widget.layer.nodes.ButtonNode;
import trixt0r.map.fat.widget.layer.nodes.LayerNode;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;

/**
 * An action which is responsible for adding a new layer into the map.
 * @author Trixt0r
 */
public class LayerWidgetAddLayer extends LayerWidgetAction{

	public String name;
	private final FatMapEditor editor;
	
	public LayerWidgetAddLayer(Tree layerTree, String name, FatMapEditor editor) {
		super(layerTree);
		this.name = name;
		this.editor = editor;
	}

	/**
	 * Has to be called after setting {@link #name}, {@link #layerTree} and {@link #layerNodes}.
	 */
	@Override
	public boolean act(float delta) {
		if(this.name == null) return false;
		Label label = new Label(this.name, this.editor.getSkin());
		FatMapLayer layer = new FatMapLayer(FatMapLayer.getLayerId(), this.name);
		this.editor.getMap().addActor(layer);
		LayerNode node = new LayerNode(label, layer);
		this.layerTree.add(node);
		node.add(new ButtonNode(this.editor.getSkin()));
		this.layerTree.getSelection().clear();
		this.layerTree.getSelection().add(node);
		return true;
	}

}
