package trixt0r.map.fat.core;

import trixt0r.map.fat.transform.TransformBox;
import trixt0r.map.fat.utils.FatShapeDrawer;
import trixt0r.map.fat.utils.RectangleUtils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

public class FatMapLayer extends Group {
	public static final Color BOX_COLOR = new Color(1,1,0,1);

	private static int LAYER_ID;
	
	public final int id;
	public String name;
	public final Rectangle bbox, selectionBBox;
	public final TransformBox selectionBox;
	public boolean isCurrentLayer = false;
	
	private int objectId;
	
	public final Array<FatMapObject> objects;
	private Array<FatMapObject> selectedObjects;
	
	
	public FatMapLayer(int id, String name){
		super.setVisible(false);
		this.id = id;
		this.name = name;
		this.objects = new Array<FatMapObject>();
		this.selectedObjects = new Array<FatMapObject>();
		this.objectId = 0;
		this.bbox = new Rectangle();
		this.selectionBBox = new Rectangle();
		this.selectionBox = new TransformBox();
	}
	
	public void draw(ShapeRenderer renderer){
		for(FatMapObject object: this.objects)
			object.draw(renderer);
			if(this.isCurrentLayer){
				if(renderer.getCurrentType() == ShapeRenderer.ShapeType.Line){
					renderer.setColor(FatMapObject.DESELECTED);
					FatShapeDrawer.drawRectangle(renderer, bbox);
					renderer.setColor(FatMapObject.SELECTED);
					FatShapeDrawer.drawRectangle(renderer, selectionBBox);
				}
				renderer.setColor(BOX_COLOR);
				this.selectionBox.draw(renderer);
			}
	}
	
	public void addObject(FatMapObject object){
		object.layer = this;
		this.objects.add(object);
		this.addActor(object);
	}
	
	public void removeObject(FatMapObject object){
		this.objects.removeValue(object, true);
		super.removeActor(object);
	}
	
	public int getObjectId(){
		return this.objectId++;
	}
	
	public static int getLayerId(){
		return LAYER_ID++;
	}
	
	public void setTransformable(boolean selected){
		this.isCurrentLayer = selected;
		for(FatMapObject obj: this.objects)
			obj.isOnSelectedLayer = selected;
	}
	
	@Override
	public void act(float delta){
		super.act(delta);
		if(!this.isCurrentLayer)
			for(FatMapObject obj: this.objects)
				obj.select(false);
		if(this.isCurrentLayer){
			this.calcBBox();
			this.calcSelectionBBox();
		}
	}
	
	private void calcBBox(){
		RectangleUtils.calcBBox(this.bbox, this.objects);
	}
	
	private void calcSelectionBBox(){
		RectangleUtils.calcBBox(this.selectionBBox, this.getSelectedObjects());
		if(!this.getSelectedObjects().equals(this.selectionBox.objects))
			this.selectionBox.setObjects(this.getSelectedObjects());
	}
	
	public Array<FatMapObject> getSelectedObjects(){
		this.selectedObjects.clear();
		for(FatMapObject actor: this.objects)
			if(actor.isSelected())
				this.selectedObjects.add(actor);
		return this.selectedObjects;
	}
}
