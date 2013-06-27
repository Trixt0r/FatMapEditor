package trixt0r.map.fat.core;
import trixt0r.map.fat.widget.layer.nodes.ObjectNode;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;

public class FatMapShapeObject extends FatMapObject {
	
	private Color renderColor = new Color();
	private Polygon shape;
	public boolean closed = false;

	public FatMapShapeObject(FatMapLayer layer, int id, Polygon polygon, ObjectNode node) {
		super(layer, id, node);
		this.shape = polygon;
		this.calcBBox();
		this.setBounds(this.boundingBox.x, this.boundingBox.y, this.boundingBox.width, this.boundingBox.height);
		this.parent.set(boundingBox);
		this.setUpRelPos();
	}
	
	public void draw(ShapeRenderer renderer){
		float alpha = 1f;
		if(renderer.getCurrentType() == ShapeRenderer.ShapeType.Filled) alpha = 0.15f;
		if(this.selected) this.renderColor.set(SELECTED).mul(1f, 1f, 1f, alpha);
		else if(!this.isOnSelectedLayer) this.renderColor.set(NOT_ON_CURRENT_LAYER);
		else this.renderColor.set(DESELECTED).mul(1f, 1f, 1f, alpha);
		renderer.setColor(renderColor);
		renderer.rect(this.boundingBox.x, this.boundingBox.y, this.boundingBox.width, this.boundingBox.height);
		
		if(renderer.getCurrentType() == ShapeRenderer.ShapeType.Line){
			if(this.closed) renderer.polygon(this.shape.getTransformedVertices());
			else renderer.polyline(this.shape.getTransformedVertices());
			if(this.selected){
				renderer.setColor(0,0,0,1);
				parent.draw(renderer);
			}
		}
		
	}

	@Override
	public void calcBBox() {
		if(this.shape != null)
			this.boundingBox = this.shape.getBoundingRectangle();
	}

	@Override
	public void update() {
		/*if(this.mapObject instanceof PolygonMapObject && this.getRotation() != this.tempAngle){
			PolygonMapObject obj = (PolygonMapObject)this.mapObject;
			obj.getPolygon().setPosition(0, 0);
			obj.setPolygon(new Polygon(obj.getPolygon().getTransformedVertices()));
			obj.getPolygon().setPosition(this.getX(), this.getY());
			super.setScale(1);
			super.setRotation(0);
		}
		else if(this.mapObject instanceof PolylineMapObject && this.getRotation() != this.tempAngle){
			PolylineMapObject obj = (PolylineMapObject)this.mapObject;
			obj.getPolyline().setPosition(0, 0);
			obj.setPolyline(new Polyline(obj.getPolyline().getTransformedVertices()));
			obj.getPolyline().setPosition(this.getX(), this.getY());
			super.setScale(1);
			super.setRotation(0);
		}
		this.calcBBox();*/
	}

	@Override
	protected void setObjectRotation(float angle) {
		this.shape.setRotation(angle);
		this.calcBBox();
	}

	@Override
	protected void setObjectPosition(float x, float y) {
		this.shape.setPosition(x, y);
		this.calcBBox();
	}

	@Override
	protected void setObjectScale(float scaleX, float scaleY) {
		this.shape.setScale(scaleX, scaleY);
		this.calcBBox();
	}
	
	@Override
	public float getX(){
		return this.shape.getX();
	}
	
	@Override
	public float getY(){
		return this.shape.getY();
	}
}
