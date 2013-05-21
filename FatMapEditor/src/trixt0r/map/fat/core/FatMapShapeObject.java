package trixt0r.map.fat.core;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class FatMapShapeObject extends FatMapObject {

	public FatMapShapeObject(FatMapLayer layer, int id, MapObject mapObject) {
		super(layer, id, mapObject);
		if(!(mapObject instanceof CircleMapObject) && !(mapObject instanceof EllipseMapObject) && !(mapObject instanceof PolygonMapObject) &&
				!(mapObject instanceof PolylineMapObject) && !(mapObject instanceof RectangleMapObject))
			throw new GdxRuntimeException("The given object does not represent a shape!");
	}
	
	@Override
	public void setX(float x){
		super.setX(x);
		if(this.mapObject instanceof CircleMapObject){
			CircleMapObject obj = (CircleMapObject)this.mapObject;
			obj.getCircle().x = x;
		} else if(this.mapObject instanceof EllipseMapObject){
			EllipseMapObject obj = (EllipseMapObject)this.mapObject;
			obj.getEllipse().x = x;
		} else if(this.mapObject instanceof PolygonMapObject){
			PolygonMapObject obj = (PolygonMapObject)this.mapObject;
			obj.getPolygon().setPosition(x, obj.getPolygon().getY());
		} else if(this.mapObject instanceof PolylineMapObject){
			PolylineMapObject obj = (PolylineMapObject)this.mapObject;
			obj.getPolygon().setPosition(x, obj.getPolygon().getY());
		} else if(this.mapObject instanceof RectangleMapObject){
			RectangleMapObject obj = (RectangleMapObject)this.mapObject;
			obj.getRectangle().x = x;
		}
	}
	@Override
	public void setY(float y){
		super.setY(y);
		if(this.mapObject instanceof CircleMapObject){
			CircleMapObject obj = (CircleMapObject)this.mapObject;
			obj.getCircle().y = y;
		} else if(this.mapObject instanceof EllipseMapObject){
			EllipseMapObject obj = (EllipseMapObject)this.mapObject;
			obj.getEllipse().y = y;
		} else if(this.mapObject instanceof PolygonMapObject){
			PolygonMapObject obj = (PolygonMapObject)this.mapObject;
			obj.getPolygon().setPosition(obj.getPolygon().getX(), y);
		} else if(this.mapObject instanceof PolylineMapObject){
			PolylineMapObject obj = (PolylineMapObject)this.mapObject;
			obj.getPolygon().setPosition(obj.getPolygon().getX(), y);
		} else if(this.mapObject instanceof RectangleMapObject){
			RectangleMapObject obj = (RectangleMapObject)this.mapObject;
			obj.getRectangle().y = y;
		}
	}

}
