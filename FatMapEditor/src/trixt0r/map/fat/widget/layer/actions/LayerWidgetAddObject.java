package trixt0r.map.fat.widget.layer.actions;

import java.util.Random;

import trixt0r.map.fat.FatMapEditor;
import trixt0r.map.fat.core.FatMapLayer;
import trixt0r.map.fat.core.FatMapShapeObject;
import trixt0r.map.fat.widget.layer.nodes.LayerNode;
import trixt0r.map.fat.widget.layer.nodes.ObjectNode;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
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
		ObjectNode node = new ObjectNode(label,root, null);
		FatMapLayer layer = root.layer;
		float width = 10f*xscale, height = 10f*yscale;
		MapObject[] objs = {new CircleMapObject(x,y, width/2), new EllipseMapObject(x,y,width,height), new RectangleMapObject(x-width/2,y-height/2, width, height)};
		int rand = new Random().nextInt(objs.length);
		FatMapShapeObject obj = new FatMapShapeObject(layer, layer.getObjectId(), objs[rand], node);
		obj.setX(x-width/2); obj.setY(y-height/2);
		
		layer.addObject(obj);
		root.add(node);
		return true;
	}

}
