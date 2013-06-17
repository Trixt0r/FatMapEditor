package trixt0r.map.fat.widget.layer.actions;

import java.util.Random;

import trixt0r.map.fat.core.FatMapLayer;
import trixt0r.map.fat.core.FatMapShapeObject;
import trixt0r.map.fat.widget.layer.nodes.LayerNode;
import trixt0r.map.fat.widget.layer.nodes.ObjectNode;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;;

/**
 * An action which is responsible for adding new objects to an existing layer.
 * @author Trixt0r
 */
public class LayerWidgetAddObject extends LayerWidgetObjectAction {

	public String name;
	public float x,y, xscale, yscale, alpha, angle;
	private final Skin skin;
	
	public LayerWidgetAddObject(LayerNode root, String name, Skin skin) {
		super(root);
		this.name = name;
		this.skin = skin;
	}

	@Override
	public boolean act(float delta) {
		if(this.name == null) return false;
		
		Label label = new Label(this.name, this.skin);
		ObjectNode node = new ObjectNode(label,root, null);
		FatMapLayer layer = root.layer;
		float width = 10f*xscale, height = 10f*yscale;
		MapObject[] objs = {new EllipseMapObject(x,y,width,height), new RectangleMapObject(x-width/2,y-height/2, width, height), getPolyline(x,y), getPolygon(x,y)};
		int rand = new Random().nextInt(objs.length);
		FatMapShapeObject obj = new FatMapShapeObject(layer, layer.getObjectId(), objs[rand], node);
		obj.setX(x-width/2); obj.setY(y-height/2);
		
		layer.addObject(obj);
		
		for(Node n: root.getChildren())
			root.getTree().getSelection().removeValue(n, true);
		
		root.add(node);
		root.getTree().getSelection().add(node);
		return true;
	}
	
	private static float[] getSinusVertices(){
		float[] vertices = new float[60];
		for(int i = 0; i< vertices.length/2; i++){
			vertices[i*2] = i*5;
			vertices[i*2+1] = MathUtils.sinDeg(i*(360/(vertices.length/2)))*20;
		}
		return vertices;
	}
	
	private static float[] getHexVertices(){
		float[] vertices = new float[12];
		for(int i = 0; i< vertices.length/2; i++){
			vertices[i*2] = MathUtils.cosDeg(i*(360/(vertices.length/2)))*50;
			vertices[i*2+1] = MathUtils.sinDeg(i*(360/(vertices.length/2)))*50;
		}
		return vertices;
	}
	
	private static PolygonMapObject getPolygon(float x, float y){
		Polygon poly = new Polygon(getHexVertices());
		poly.setPosition(x, y);
		return new PolygonMapObject(poly);
	}
	private static PolylineMapObject getPolyline(float x, float y){
		Polyline poly = new Polyline(getSinusVertices());
		poly.setPosition(x, y);
		return new PolylineMapObject(poly);
	}

}
