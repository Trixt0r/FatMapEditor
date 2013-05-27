package trixt0r.map.fat.widget.layer.actions;

import trixt0r.map.fat.FatMapEditor;
import trixt0r.map.fat.core.FatMapLayer;
import trixt0r.map.fat.core.FatMapShapeObject;
import trixt0r.map.fat.widget.layer.nodes.LayerNode;
import trixt0r.map.fat.widget.layer.nodes.ObjectNode;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * An action which is responsible for adding new objects to an existing layer.
 * @author Trixt0r
 */
public class LayerWidgetAddObject extends LayerWidgetObjectAction {

	public String name;
	public float x,y, xscale, yscale, alpha, angle;
	
	public LayerWidgetAddObject(LayerNode root, String name) {
		super(root);
		this.name = name;
	}

	@Override
	public boolean act(float delta) {
		if(this.name == null) return false;
		
		Label label = new Label(this.name, FatMapEditor.skin);
		
		FatMapLayer layer = root.layer;
		float width = 10f*xscale, height = 10f*yscale;
		FatMapShapeObject obj = new FatMapShapeObject(layer, layer.getObjectId(), new RectangleMapObject(x-width/2,y-height/2, width, height));
		obj.setX(x-width/2); obj.setY(y-height/2);
		layer.addObject(obj);
		root.add(new ObjectNode(label,root, obj));
		return true;
	}

}
