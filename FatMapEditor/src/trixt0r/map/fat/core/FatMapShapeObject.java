package trixt0r.map.fat.core;

import trixt0r.map.fat.widget.layer.nodes.ObjectNode;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class FatMapShapeObject extends FatMapObject {
	
	private Color renderColor = new Color();

	public FatMapShapeObject(FatMapLayer layer, int id, MapObject mapObject, ObjectNode node) {
		super(layer, id, mapObject, node);
		if(!(mapObject instanceof CircleMapObject) && !(mapObject instanceof EllipseMapObject) && !(mapObject instanceof PolygonMapObject) &&
				!(mapObject instanceof PolylineMapObject) && !(mapObject instanceof RectangleMapObject))
			throw new GdxRuntimeException("The given object does not represent a shape!");
		if(mapObject instanceof RectangleMapObject){
			Rectangle rect = ((RectangleMapObject)this.mapObject).getRectangle();
			this.setBounds(rect.x, rect.y, rect.width, rect.height);
			this.boundingBox = rect;
		} else if(this.mapObject instanceof EllipseMapObject){
			Ellipse ellipse = ((EllipseMapObject)this.mapObject).getEllipse();
			this.setBounds(ellipse.x-ellipse.width/2, ellipse.y-ellipse.height/2, ellipse.width, ellipse.height);
		} else if(this.mapObject instanceof CircleMapObject){
			Circle circle = ((CircleMapObject)this.mapObject).getCircle();
			this.setBounds(circle.x-circle.radius, circle.y-circle.radius, circle.radius*2, circle.radius*2);
		}
		this.calcBBox();
	}
	
	@Override
	public void setX(float x){
		super.setX(x);
		if(!this.moveable) return;
		if(this.mapObject instanceof CircleMapObject){
			CircleMapObject obj = (CircleMapObject)this.mapObject;
			obj.getCircle().set(getX()+this.getWidth()/2, obj.getCircle().y, obj.getCircle().radius);
		} else if(this.mapObject instanceof EllipseMapObject){
			EllipseMapObject obj = (EllipseMapObject)this.mapObject;
			obj.getEllipse().set(getX(), getY(), obj.getEllipse().width, obj.getEllipse().height);
		} else if(this.mapObject instanceof PolygonMapObject){
			PolygonMapObject obj = (PolygonMapObject)this.mapObject;
			obj.getPolygon().setPosition(getX(), obj.getPolygon().getY());
		} else if(this.mapObject instanceof PolylineMapObject){
			PolylineMapObject obj = (PolylineMapObject)this.mapObject;
			obj.getPolyline().setPosition(getX(), obj.getPolyline().getY());
		} else if(this.mapObject instanceof RectangleMapObject){
			RectangleMapObject obj = (RectangleMapObject)this.mapObject;
			obj.getRectangle().setX(getX());
		}
	}
	@Override
	public void setY(float y){
		super.setY(y);
		if(!this.moveable) return;
		if(this.mapObject instanceof CircleMapObject){
			CircleMapObject obj = (CircleMapObject)this.mapObject;
			obj.getCircle().set(obj.getCircle().x, getY()+this.getWidth()/2, obj.getCircle().radius);
		} else if(this.mapObject instanceof EllipseMapObject){
			EllipseMapObject obj = (EllipseMapObject)this.mapObject;
			obj.getEllipse().set(getX(), getY(), obj.getEllipse().width, obj.getEllipse().height);
		} else if(this.mapObject instanceof PolygonMapObject){
			PolygonMapObject obj = (PolygonMapObject)this.mapObject;
			obj.getPolygon().setPosition(obj.getPolygon().getX(), getY());
		} else if(this.mapObject instanceof PolylineMapObject){
			PolylineMapObject obj = (PolylineMapObject)this.mapObject;
			obj.getPolyline().setPosition(obj.getPolyline().getX(), getY());
		} else if(this.mapObject instanceof RectangleMapObject){
			RectangleMapObject obj = (RectangleMapObject)this.mapObject;
			obj.getRectangle().setY(getY());
		}
	}
	
	public void draw(ShapeRenderer renderer){
		float alpha = 1f;
		if(renderer.getCurrentType() == ShapeRenderer.ShapeType.Filled) alpha = 0.25f;
		if(this.selected) this.renderColor.set(SELECTED).mul(1f, 1f, 1f, alpha);
		else if(!this.isOnSelectedLayer) this.renderColor.set(NOT_ON_CURRENT_LAYER);
		else this.renderColor.set(DESELECTED).mul(1f, 1f, 1f, alpha);
		renderer.setColor(renderColor);
		if(this.mapObject instanceof CircleMapObject){
			Circle circle = ((CircleMapObject)this.mapObject).getCircle();
			renderer.circle(circle.x, circle.y, circle.radius);
		} else if(this.mapObject instanceof EllipseMapObject){
			Ellipse ellipse = ((EllipseMapObject)this.mapObject).getEllipse();
			renderer.ellipse(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
		} else if(this.mapObject instanceof PolygonMapObject){
			Polygon polygon = ((PolygonMapObject)this.mapObject).getPolygon();
			renderer.polygon(polygon.getTransformedVertices());
		} else if(this.mapObject instanceof PolylineMapObject && renderer.getCurrentType() == ShapeRenderer.ShapeType.Line){
			Polyline polyline = ((PolylineMapObject)this.mapObject).getPolyline();
			renderer.polyline(polyline.getTransformedVertices());
		} else if(this.mapObject instanceof RectangleMapObject ){
			Rectangle rect = ((RectangleMapObject)this.mapObject).getRectangle();
			renderer.rect(rect.x, rect.y, rect.width, rect.height);
		}
	}

	@Override
	protected void calcBBox() {
		if(this.mapObject instanceof CircleMapObject){
			Circle circle = ((CircleMapObject)this.mapObject).getCircle();
			this.boundingBox.set(this.getX(), this.getY(), circle.radius*2, circle.radius*2);
		} else if(this.mapObject instanceof EllipseMapObject){
			Ellipse ellipse = ((EllipseMapObject)this.mapObject).getEllipse();
			this.boundingBox.set(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
		} else if(this.mapObject instanceof PolygonMapObject){
			Polygon polygon = ((PolygonMapObject)this.mapObject).getPolygon();
			this.boundingBox = polygon.getBoundingRectangle();
		} else if(this.mapObject instanceof PolylineMapObject){
			Polyline polyline = ((PolylineMapObject)this.mapObject).getPolyline();
			this.calcBBoxForPolyline(polyline);
		}
	}
	
	private void calcBBoxForPolyline(Polyline line) {
		float[] vertices = line.getTransformedVertices();

		float minX = vertices[0];
		float minY = vertices[1];
		float maxX = vertices[0];
		float maxY = vertices[1];

		final int numFloats = vertices.length;
		for (int i = 2; i < numFloats; i += 2) {
			minX = minX > vertices[i] ? vertices[i] : minX;
			minY = minY > vertices[i + 1] ? vertices[i + 1] : minY;
			maxX = maxX < vertices[i] ? vertices[i] : maxX;
			maxY = maxY < vertices[i + 1] ? vertices[i + 1] : maxY;
		}

		this.boundingBox = new Rectangle();
		this.boundingBox.x = minX;
		this.boundingBox.y = minY;
		this.boundingBox.width = maxX - minX;
		this.boundingBox.height = maxY - minY;
	}

}
