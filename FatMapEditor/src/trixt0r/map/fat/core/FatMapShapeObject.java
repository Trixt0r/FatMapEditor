package trixt0r.map.fat.core;

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
	
	public static final Color DESELECTED = new Color(0f, .75f, .25f, 1f), SELECTED = new Color(.75f, .25f, .25f,1f);
	
	private Color renderColor = new Color();

	public FatMapShapeObject(FatMapLayer layer, int id, MapObject mapObject) {
		super(layer, id, mapObject);
		if(!(mapObject instanceof CircleMapObject) && !(mapObject instanceof EllipseMapObject) && !(mapObject instanceof PolygonMapObject) &&
				!(mapObject instanceof PolylineMapObject) && !(mapObject instanceof RectangleMapObject))
			throw new GdxRuntimeException("The given object does not represent a shape!");
		if(mapObject instanceof RectangleMapObject){
			Rectangle rect = ((RectangleMapObject)this.mapObject).getRectangle();
			this.setBounds(rect.x, rect.y, rect.width, rect.height);
		}
	}
	
	@Override
	public void setX(float x){
		super.setX(x);
		if(!this.moveable) return;
		if(this.mapObject instanceof CircleMapObject){
			CircleMapObject obj = (CircleMapObject)this.mapObject;
			obj.getCircle().set(getX(), getY(), obj.getCircle().radius);
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
			obj.getCircle().set(getX(), getY(), obj.getCircle().radius);
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

}
