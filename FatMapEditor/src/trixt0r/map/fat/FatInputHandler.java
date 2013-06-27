package trixt0r.map.fat;

import trixt0r.map.fat.core.FatMapStage;
import trixt0r.map.fat.core.FatMapStage.CameraMover;
import trixt0r.map.fat.core.FatMapLayer;
import trixt0r.map.fat.core.FatMapObject;
import trixt0r.map.fat.transform.TransformBox;
import trixt0r.map.fat.transform.TransformerPoint;
import trixt0r.map.fat.utils.RectangleUtils;
import trixt0r.map.fat.widget.WidgetStage;
import trixt0r.map.fat.widget.layer.LayerWidget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class FatInputHandler implements InputProcessor {
	
	private LayerWidget layerWidget;
	private Array<FatMapObject> selectedObjects;
	private FatCamera cam;
	private CameraMover mover;
	private boolean dragAble = false, scaleAble = false, rotateAble = false, prevClicked = false;
	private boolean mapClicked = true, dragOrigin = false;
	private float originDiffX = 0f, originDiffY =0f;
	private Rectangle selectionRect;
	private TransformBox selectionBox;
	private FatMapStage map;
	private WidgetStage gui;
	private TransformerPoint clickedPoint;
	
	public static float HORIZONTAL_ACCELERATION = 10f, VERTICAL_ACCELERATION = 10f, ZOOM_SPEED = 0.25f;
	
	public FatInputHandler(FatMapStage map, WidgetStage gui){
		this.map = map;
		this.gui = gui;
		this.cam = (FatCamera) map.getCamera();
		this.mover = map.mover();
		this.layerWidget = gui.layerWidget;
		this.selectionRect = new Rectangle();
		this.selectedObjects = new Array<FatMapObject>();
	}

	public boolean keyDown(int keycode) {
		if(keycode == Keys.ESCAPE)
			Gdx.app.exit();
		if(!this.mapClicked) return false;
		if(keycode == Keys.SPACE) FatTransformer.snapToGrid = true;
		if(keycode == Keys.LEFT) FatTransformer.translateRelative(this.selectionBox, (FatTransformer.snapToGrid) ? -FatMapEditor.GRID_XOFFSET: -1f, 0f);
		if(keycode == Keys.RIGHT) FatTransformer.translateRelative(this.selectionBox, (FatTransformer.snapToGrid) ? FatMapEditor.GRID_XOFFSET : 1f, 0f);
		if(keycode == Keys.UP) FatTransformer.translateRelative(this.selectionBox, 0, (FatTransformer.snapToGrid) ? FatMapEditor.GRID_YOFFSET : 1f);
		if(keycode == Keys.DOWN) FatTransformer.translateRelative(this.selectionBox, 0, (FatTransformer.snapToGrid) ? -FatMapEditor.GRID_YOFFSET: -1f);
		
		if(keycode == Keys.F1) {
			this.layerWidget.fadeOut = !this.layerWidget.fadeOut;
			return true;
		}
		if(!this.cam.followMouse && this.mapClicked){
			this.cam.setFollowSpeed(.25f, .25f);
			float hacc = HORIZONTAL_ACCELERATION, vacc = VERTICAL_ACCELERATION;
			this.mover.fastMode = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT);
			if(this.mover.fastMode){
				hacc *= 2f; vacc *= 2f;
			}
			if(keycode == Keys.A) mover.hacc = -hacc*this.cam.zoom;
			if(keycode == Keys.D) mover.hacc = hacc*this.cam.zoom;
			if(keycode == Keys.S) mover.vacc = -vacc*this.cam.zoom;
			if(keycode == Keys.W) mover.vacc = vacc*this.cam.zoom;
			mover.maxHspeed = CameraMover.MAX_HSPEED*this.cam.zoom; 
			mover.maxVspeed = CameraMover.MAX_VSPEED*this.cam.zoom;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.SPACE) FatTransformer.snapToGrid = false;
		if(keycode == Keys.A || keycode == Keys.D)
			mover.hacc = 0f;
		if(keycode == Keys.S || keycode == Keys.W)
			mover.vacc = 0f;
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer,
			int button) {
		this.dragAble = false;
		this.rotateAble = false;
		this.scaleAble = false;
		this.prevClicked = this.mapClicked;
		this.mapClicked = false;
		if(this.gui.hasFocus()) return false;
		this.gui.unfocusAll();
		this.mapClicked = true;
		Vector3 v = new Vector3(screenX, screenY,0f);
		this.cam.unproject(v);
		
		if(button == Buttons.RIGHT && this.selectedObjects != null && this.selectionBox != null){
			this.dragOrigin = this.selectedObjects.size > 0;
			this.originDiffX = this.selectionBox.center.x - v.x;
			this.originDiffY = this.selectionBox.center.y - v.y;
			return true;
		}
		
		
		if(button == Buttons.MIDDLE && this.selectionBox != null){
			this.mover.setX(this.selectionBox.center.x);
			this.mover.setY(this.selectionBox.center.y);
			return true;
		}
		
		if(button == Buttons.LEFT && FatTransformer.currentTool == FatTransformer.TOOL.SELECTION){
			this.selectionRect.set(v.x, v.y, 0f, 0f);
			if(this.selectedObjects != null && !Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && this.prevClicked)
				for(FatMapObject object: this.selectedObjects)
					object.select(false);
		}
		
		this.dragAble = FatTransformer.currentTool == FatTransformer.TOOL.TRANSLATION && this.prevClicked;
		this.scaleAble = FatTransformer.currentTool == FatTransformer.TOOL.SCALING && this.prevClicked;
		this.rotateAble = FatTransformer.currentTool == FatTransformer.TOOL.ROTATION && this.prevClicked;
		
		if(button == Buttons.LEFT && FatTransformer.currentTool == FatTransformer.TOOL.ERASING && this.selectionBox != null){
			this.clickedPoint = this.selectionBox.getNearestPoint(v.x, v.y);
			this.selectionBox.setUpTempValues();
			this.selectionBox.xDiff = this.selectionBox.getX() - v.x;
			this.selectionBox.yDiff = this.selectionBox.getY() - v.y;
			if(this.clickedPoint != null){
				this.clickedPoint.setClicked(true);
				this.clickedPoint.click(v.x, v.y);
				this.rotateAble = this.clickedPoint.equals(this.selectionBox.rotation);
			} else this.dragAble = true;
		}
		if(!this.mapClicked) return false;
		if(this.selectedObjects.size == 0) return false;
		this.selectionBox = this.selectedObjects.get(0).layer.selectionBox;
		this.selectionBox.setUpTempValues();
		this.selectionBox.xDiff = this.selectionBox.getX() - v.x;
		this.selectionBox.yDiff = this.selectionBox.getY() - v.y;
		
		Vector2 temp = new Vector2(this.selectionBox.center);
		this.selectionBox.clickedAngle = temp.sub(v.x, v.y).angle();
		
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		this.dragOrigin = false;
		if(this.clickedPoint != null) this.clickedPoint.setClicked(false);
		this.clickedPoint = null;
		/*for(FatMapObject object: this.selectedObjects)
			object.update();*/
		if(button == Buttons.LEFT){
			if(FatTransformer.currentTool == FatTransformer.TOOL.SELECTION && this.prevClicked){
				Vector3 v = new Vector3(screenX, screenY,0f);
				this.cam.unproject(v);
				this.selectionRect.width = v.x-this.selectionRect.x;
				this.selectionRect.height = v.y-this.selectionRect.y;
				
				float left = Math.min(this.selectionRect.x+this.selectionRect.width, this.selectionRect.x);
				float bottom = Math.min(this.selectionRect.y+this.selectionRect.height, this.selectionRect.y);
				float right = Math.max(this.selectionRect.x+this.selectionRect.width, this.selectionRect.x);
				float top = Math.max(this.selectionRect.y+this.selectionRect.height, this.selectionRect.y);
				this.selectionRect.set(left, bottom, right-left, top-bottom);
				
				this.selectFromRect();
				
				this.setSelectedObjects(this.gui.getSelectedObjects());
				
				this.selectionRect.set(0f, 0f, 0f, 0f);
				if(this.selectedObjects.size != 0){
					this.selectedObjects.get(0).layer.act(0);
					this.selectedObjects.get(0).layer.selectionBox.update();
					this.selectedObjects.get(0).layer.selectionBox.updateCenter();
				}
				
				//this.center.set(this.selectionBox.center);
			}
		}
		
		if(!this.prevClicked || this.gui.hasFocus()){
			this.selectedObjects.clear();
			return false;
		}
		
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(!this.mapClicked) return false;
		Vector3 v = new Vector3(screenX, screenY,0f);
		this.cam.unproject(v);
		
		if(this.clickedPoint != null){
			this.clickedPoint.drag(v.x, v.y);
			this.selectionBox.applyTransformPoints();
		}
		
		if(this.dragOrigin){
			this.selectionBox.setCenter(v.x + this.originDiffX, v.y + this.originDiffY);
			this.selectionBox.updateCenter();
			return true;
		}
		
		if(!Gdx.input.isButtonPressed(Buttons.LEFT)) return false;
		
		if(FatTransformer.currentTool == FatTransformer.TOOL.SELECTION){
			this.selectionRect.width = v.x-this.selectionRect.x;
			this.selectionRect.height = v.y-this.selectionRect.y;
			return true;
		}
		
		if(!this.dragAble && !this.scaleAble && !this.rotateAble) return false;
		if(this.selectedObjects.size == 0) return false;
		if(this.dragAble) FatTransformer.translate(this.selectionBox, v.x, v.y, true);
		if(this.scaleAble) FatTransformer.scale(this.selectionBox, v.x, v.y, true);
		if(this.rotateAble)	FatTransformer.rotateRelative(this.selectionBox, v.x,v.y);
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if(!this.mapClicked) return false;
		this.gui.unfocusAll();
		
		this.cam.setFollowSpeed(0.1f, 0.1f);
		Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
		this.cam.unproject(v);
		this.mover.setPosition(v.x, v.y);
		
		this.cam.setZoomOut(amount > 0);
		
		float am = ZOOM_SPEED*amount;
		
		if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) am*=2f;
		this.cam.resetZoom();
		this.cam.setZoomFar(this.cam.zoom+am);
		this.cam.setZoomNear(this.cam.zoom+am);
		return true;
	}
	
	public void setSelectedObjects(Array<FatMapObject> objects){
		this.selectedObjects.clear();
		this.selectedObjects.addAll(objects);
	}
	
	public void drawSelectRegion(ShapeRenderer renderer){
		float alpha = 1f;
		if(renderer.getCurrentType() == ShapeRenderer.ShapeType.Filled) alpha = .25f;
		if(this.prevClicked){
			renderer.setColor(1f, .75f, .75f, alpha);
			renderer.rect(this.selectionRect.x, this.selectionRect.y, this.selectionRect.width, this.selectionRect.height);
		}
	}
	
	private void selectFromRect(){
		for(Actor actor: this.map.getActors()){
			if(!(actor instanceof FatMapLayer)) continue;
			FatMapLayer layer = (FatMapLayer) actor;
			for(FatMapObject obj: layer.objects){
				if(RectangleUtils.overlapping(obj.getBBox(), this.selectionRect)){
					if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) obj.select(!obj.isSelected());
					else obj.select(true);
				}
				/*else
					obj.select(false);*/
			}
		}
	}

}
