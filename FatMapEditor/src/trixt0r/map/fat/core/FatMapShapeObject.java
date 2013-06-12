package trixt0r.map.fat.core;
import trixt0r.map.fat.utils.VerticesUtils;
import trixt0r.map.fat.widget.layer.nodes.ObjectNode;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class FatMapShapeObject extends FatMapObject {
	
	private Color renderColor = new Color();

	public FatMapShapeObject(FatMapLayer layer, int id, MapObject mapObject, ObjectNode node) {
		super(layer, id, mapObject, node);
		if(!(mapObject instanceof EllipseMapObject) && !(mapObject instanceof PolygonMapObject) &&
				!(mapObject instanceof PolylineMapObject) && !(mapObject instanceof RectangleMapObject))
			throw new GdxRuntimeException("The given object does not represent a shape!");
		this.calcBBox();
		this.setBounds(this.boundingBox.x, this.boundingBox.y, this.boundingBox.width, this.boundingBox.height);
		if(mapObject instanceof PolygonMapObject || mapObject instanceof PolylineMapObject) this.setOriginRel(.5f, .5f);
	}
	
	@Override
	public void setX(float x){
		super.setX(x);
		if(!this.moveable) return;
		if(this.mapObject instanceof EllipseMapObject){
			EllipseMapObject obj = (EllipseMapObject)this.mapObject;
			obj.getEllipse().set(getX(), getY(), obj.getEllipse().width, obj.getEllipse().height);
		} else if(this.mapObject instanceof PolygonMapObject){
			PolygonMapObject obj = (PolygonMapObject)this.mapObject;
			obj.getPolygon().setPosition(getX(), getY());
		} else if(this.mapObject instanceof PolylineMapObject){
			PolylineMapObject obj = (PolylineMapObject)this.mapObject;
			obj.getPolyline().setPosition(getX(), obj.getPolyline().getY());
		} else if(this.mapObject instanceof RectangleMapObject){
			RectangleMapObject obj = (RectangleMapObject)this.mapObject;
			obj.getRectangle().setX(getX());
		}
		this.calcBBox();
	}
	@Override
	public void setY(float y){
		super.setY(y);
		if(!this.moveable) return;
		if(this.mapObject instanceof EllipseMapObject){
			EllipseMapObject obj = (EllipseMapObject)this.mapObject;
			obj.getEllipse().set(getX(), getY(), obj.getEllipse().width, obj.getEllipse().height);
		} else if(this.mapObject instanceof PolygonMapObject){
			PolygonMapObject obj = (PolygonMapObject)this.mapObject;
			obj.getPolygon().setPosition(getX(), getY());
		} else if(this.mapObject instanceof PolylineMapObject){
			PolylineMapObject obj = (PolylineMapObject)this.mapObject;
			obj.getPolyline().setPosition(obj.getPolyline().getX(), getY());
		} else if(this.mapObject instanceof RectangleMapObject){
			RectangleMapObject obj = (RectangleMapObject)this.mapObject;
			obj.getRectangle().setY(getY());
		}
		this.calcBBox();
	}
	
	@Override 
	public void setScaleX(float scaleX){
		super.setScaleX(scaleX);
		if(!this.moveable) return;
		if(this.mapObject instanceof EllipseMapObject){
			Ellipse ellipse = ((EllipseMapObject)this.mapObject).getEllipse();
			ellipse.width = this.tempWidth*(this.getScaleX()/this.tempScaleX);
		} else if(this.mapObject instanceof PolygonMapObject){
			PolygonMapObject obj = (PolygonMapObject)this.mapObject;
			obj.getPolygon().setScale(this.getScaleX(), this.getScaleY());
		} else if(this.mapObject instanceof PolylineMapObject){
			PolylineMapObject obj = (PolylineMapObject)this.mapObject;
			obj.getPolyline().setScale(this.getScaleX(), this.getScaleY());
		} else if(this.mapObject instanceof RectangleMapObject){
			RectangleMapObject obj = (RectangleMapObject)this.mapObject;
			obj.getRectangle().setWidth((this.getScaleX()/this.tempScaleX)*this.tempWidth);
		}
		this.calcBBox();
		this.setSize(this.tempWidth*(this.getScaleX()/this.tempScaleX), this.getHeight());
	}
	
	@Override 
	public void setScaleY(float scaleY){
		super.setScaleY(scaleY);
		if(!this.moveable) return;
		if(this.mapObject instanceof EllipseMapObject){
			Ellipse ellipse = ((EllipseMapObject)this.mapObject).getEllipse();
			ellipse.height = this.tempHeight*(this.getScaleY()/this.tempScaleY);
		} else if(this.mapObject instanceof PolygonMapObject){
			PolygonMapObject obj = (PolygonMapObject)this.mapObject;
			obj.getPolygon().setScale(this.getScaleX(), this.getScaleY());
		} else if(this.mapObject instanceof PolylineMapObject){
			PolylineMapObject obj = (PolylineMapObject)this.mapObject;
			obj.getPolyline().setScale(this.getScaleX(), this.getScaleY());
		} else if(this.mapObject instanceof RectangleMapObject){
			RectangleMapObject obj = (RectangleMapObject)this.mapObject;
			obj.getRectangle().setHeight((this.getScaleY()/this.tempScaleY)*this.tempHeight);
		}
		this.calcBBox();
		this.setSize(this.getWidth(), this.tempHeight*(this.getScaleY()/this.tempScaleY));
	}
	
	public void draw(ShapeRenderer renderer){
		float alpha = 1f;
		if(renderer.getCurrentType() == ShapeRenderer.ShapeType.Filled) alpha = 0.25f;
		if(this.selected) this.renderColor.set(SELECTED).mul(1f, 1f, 1f, alpha);
		else if(!this.isOnSelectedLayer) this.renderColor.set(NOT_ON_CURRENT_LAYER);
		else this.renderColor.set(DESELECTED).mul(1f, 1f, 1f, alpha);
		renderer.setColor(renderColor);
		renderer.rect(this.boundingBox.x, this.boundingBox.y, this.boundingBox.width, this.boundingBox.height);
		if(this.mapObject instanceof EllipseMapObject){
			Ellipse ellipse = ((EllipseMapObject)this.mapObject).getEllipse();
			renderer.ellipse(ellipse.x, ellipse.y, ellipse.width, ellipse.height, (int)(12 * (float)Math.cbrt(Math.max(Math.abs(ellipse.width * 0.5f),  Math.abs(ellipse.height * 0.5f)))));
		} else if(this.mapObject instanceof PolygonMapObject && renderer.getCurrentType() == ShapeRenderer.ShapeType.Line){
			Polygon polygon = ((PolygonMapObject)this.mapObject).getPolygon();
			renderer.polygon(polygon.getTransformedVertices());
		} else if(this.mapObject instanceof PolylineMapObject && renderer.getCurrentType() == ShapeRenderer.ShapeType.Line){
			Polyline polyline = ((PolylineMapObject)this.mapObject).getPolyline();
			renderer.polyline(polyline.getTransformedVertices());
		} else if(this.mapObject instanceof RectangleMapObject ){
			Rectangle rect = ((RectangleMapObject)this.mapObject).getRectangle();
			renderer.rect(rect.x, rect.y, rect.width, rect.height);
		}
		if(renderer.getCurrentType() == ShapeRenderer.ShapeType.Line){
			renderer.setColor(0,1,1,1);
			renderer.line(this.boundingBox.x+getOriginX()-5, this.boundingBox.y+getOriginY(), this.boundingBox.x+getOriginX()+5, this.boundingBox.y+getOriginY());
			renderer.line(this.boundingBox.x+getOriginX(), this.boundingBox.y+getOriginY()-5, this.boundingBox.x+getOriginX(), this.boundingBox.y+getOriginY()+5);
		}
		
	}

	@Override
	protected void calcBBox() {
		if(this.mapObject instanceof EllipseMapObject){
			Ellipse ellipse = ((EllipseMapObject)this.mapObject).getEllipse();
			this.boundingBox.set(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
		} else if(this.mapObject instanceof PolygonMapObject)
			this.boundingBox = ((PolygonMapObject)this.mapObject).getPolygon().getBoundingRectangle();
		else if(this.mapObject instanceof PolylineMapObject)
			this.calcBBoxForPolyline(((PolylineMapObject)this.mapObject).getPolyline());
		else if(this.mapObject instanceof RectangleMapObject)
			this.boundingBox = ((RectangleMapObject)this.mapObject).getRectangle();
		this.setOrigin(this.boundingBox.width*this.originXRel, this.boundingBox.height*this.originYRel);
		//super.setSize(this.boundingBox.width, this.boundingBox.height);
	}
	
	@Override
	public void setRotation(float degrees){
		super.setRotation(degrees);
		if(this.mapObject instanceof PolygonMapObject)
			((PolygonMapObject)this.mapObject).getPolygon().setRotation(this.getRotation());
		else if(this.mapObject instanceof PolylineMapObject)
			((PolylineMapObject)this.mapObject).getPolyline().setRotation(this.getRotation());
		this.calcBBox();
	}
	
	private void calcBBoxForPolyline(Polyline line) {
		VerticesUtils.calcBBoxForVertices(line.getTransformedVertices(), this.boundingBox);
	}

	/*@Override
	public void update() {
		if(this.mapObject instanceof PolygonMapObject){
			PolygonMapObject obj = (PolygonMapObject)this.mapObject;
			obj.getPolygon().setPosition(0, 0);
			//obj.getPolygon().setScale(1, 1);
			obj.setPolygon(new Polygon(obj.getPolygon().getTransformedVertices()));
			obj.getPolygon().setPosition(this.getX(), this.getY());
			super.setScale(1);
			//obj.getPolygon().setScale(this.getScaleX(), this.getScaleY());
		}
		else if(this.mapObject instanceof PolylineMapObject){
			PolylineMapObject obj = (PolylineMapObject)this.mapObject;
			obj.getPolyline().setPosition(0, 0);
			//obj.getPolyline().setScale(1, 1);
			obj.setPolyline(new Polyline(obj.getPolyline().getTransformedVertices()));
			obj.getPolyline().setPosition(this.getX(), this.getY());
			super.setScale(1);
			//obj.getPolyline().setScale(this.getScaleX(), this.getScaleY());
		}
	}*/

}
