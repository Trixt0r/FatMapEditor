package trixt0r.map.fat;

import trixt0r.map.fat.FatMapEditor.CameraMover;
import trixt0r.map.fat.core.FatMapLayer;
import trixt0r.map.fat.core.FatMapObject;
import trixt0r.map.fat.utils.RectangleUtils;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

public class FatInputHandler implements InputProcessor {
	
	private LayerWidget layerWidget;
	private Array<FatMapObject> selectedObjects;
	private FatCamera cam;
	private CameraMover mover;
	private boolean dragAble = false, scaleAble = false, rotateAble = false;
	private boolean mapClicked = true;
	private float clickedAngle = 0f;
	FatMapEditor editor;
	private Rectangle selectionRect, selectionBBox;
	private Vector2 center, centerOrigin;
	private Label toolTip;
	
	public static float HORIZONTAL_ACCELERATION = 10f, VERTICAL_ACCELERATION = 10f, ZOOM_SPEED = 0.25f;
	
	public FatInputHandler(FatMapEditor editor){
		this.editor = editor;
		this.cam = editor.mapCamera;
		this.mover = editor.mover;
		this.layerWidget = editor.layerWidget;
		this.selectionRect = new Rectangle();;
		this.selectionBBox = new Rectangle();
		this.center = new Vector2();
		this.centerOrigin = new Vector2();
		this.toolTip = new Label("Tooltip", FatMapEditor.skin);
		this.toolTip.setVisible(false);
		FatMapEditor.guiStage.addActor(toolTip);
	}

	public boolean keyDown(int keycode) {
		if(keycode == Keys.ESCAPE)
			Gdx.app.exit();
		if(!this.mapClicked) return false;
		if(keycode == Keys.SPACE) FatTransformer.snapToGrid = true;
		if(keycode == Keys.LEFT) FatTransformer.translateRelative(selectedObjects, (FatTransformer.snapToGrid) ? -FatMapEditor.GRID_XOFFSET: -1f, 0f);
		if(keycode == Keys.RIGHT) FatTransformer.translateRelative(selectedObjects, (FatTransformer.snapToGrid) ? FatMapEditor.GRID_XOFFSET : 1f, 0f);
		if(keycode == Keys.UP) FatTransformer.translateRelative(selectedObjects, 0, (FatTransformer.snapToGrid) ? FatMapEditor.GRID_YOFFSET : 1f);
		if(keycode == Keys.DOWN) FatTransformer.translateRelative(selectedObjects, 0, (FatTransformer.snapToGrid) ? -FatMapEditor.GRID_YOFFSET: -1f);
		
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
		boolean prevClicked = this.mapClicked;
		this.mapClicked = false;
		if(this.editor.guiHasFocus()) return false;
		FatMapEditor.guiStage.unfocusAll();
		this.mapClicked = true;
		Vector3 v = new Vector3(screenX, screenY,0f);
		this.cam.unproject(v);
		System.out.println(v);
		
		this.centerOrigin.set(RectangleUtils.getOrigin(selectedObjects));
		
		if(button == Buttons.MIDDLE){
			if(this.selectedObjects == null){
				this.mover.setX(this.cam.viewportWidth/2);
				this.mover.setY(this.cam.viewportWidth/2);
			}
			else if(this.selectedObjects.size == 0){
				this.mover.setX(this.cam.viewportWidth/2);
				this.mover.setY(this.cam.viewportWidth/2);
			}
			else{
				this.mover.setX(this.centerOrigin.x);//this.cam.viewportWidth/2);
				this.mover.setY(this.centerOrigin.y);//this.cam.viewportHeight/2);
			}
			return true;
		}
		
		if(button == Buttons.LEFT && FatTransformer.currentTool == FatTransformer.TOOL.SELECTION){
			this.selectionRect.set(v.x, v.y, 0f, 0f);
			if(this.selectedObjects != null && prevClicked && !Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)){
				for(FatMapObject object: this.selectedObjects)
					object.select(false);
				this.selectedObjects = null;
			}
		}
		
		this.dragAble = FatTransformer.currentTool == FatTransformer.TOOL.TRANSLATION;
		this.scaleAble = FatTransformer.currentTool == FatTransformer.TOOL.SCALING;
		this.rotateAble = FatTransformer.currentTool == FatTransformer.TOOL.ROTATION;
		if(!this.mapClicked) return false;
		if(this.selectedObjects == null) return false;
		if(this.selectedObjects.size == 0) return false;
		
		//this.centerOrigin.set(RectangleUtils.getOrigin(selectedObjects));
		RectangleUtils.calcBBox(selectionBBox, selectedObjects);
		this.center.set(selectionBBox.x+selectionBBox.width/2,selectionBBox.y+selectionBBox.height/2);
		Vector2 temp = new Vector2(this.centerOrigin);
		this.clickedAngle = temp.sub(v.x,v.y).angle();
		
		for(FatMapObject object: this.selectedObjects){
			object.xDiff = object.getX() - v.x;
			object.yDiff = object.getY() - v.y;
			object.tempX = object.getX();
			object.tempY = object.getY();
			object.tempWidth = object.getWidth();
			object.tempHeight = object.getHeight();
			object.tempAngle = object.getRotation();
		}
		
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer,
			int button) {
		if(!this.mapClicked) return false;
		this.dragAble = false;
		this.rotateAble = false;
		this.scaleAble = false;
		
		for(FatMapObject object: this.selectedObjects){
			object.xDiff = 0;
			object.yDiff = 0;
			object.tempX = object.getX();
			object.tempY = object.getY();
			object.tempWidth = object.getWidth();
			object.tempHeight = object.getHeight();
			object.tempAngle = 0;
		}
		
		this.selectionBBox.set(0, 0, 0, 0);
		
		if(button == Buttons.LEFT && FatTransformer.currentTool == FatTransformer.TOOL.SELECTION){
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
			
			this.selectedObjects = this.editor.layerWidget.getSelectedObjects();
			
			this.selectionRect.set(0f, 0f, 0f, 0f);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(!this.mapClicked) return false;
		Vector3 v = new Vector3(screenX, screenY,0f);
		this.cam.unproject(v);
		
		if(!Gdx.input.isButtonPressed(Buttons.LEFT)) return false;
		
		if(FatTransformer.currentTool == FatTransformer.TOOL.SELECTION){
			this.selectionRect.width = v.x-this.selectionRect.x;
			this.selectionRect.height = v.y-this.selectionRect.y;
			return true;
		}
		
		if((!this.dragAble && !this.scaleAble && !this.rotateAble) || this.selectedObjects == null) return false;
		if(this.selectedObjects.size == 0) return false;
		if(this.dragAble) FatTransformer.translate(this.selectedObjects, v.x, v.y, true);
		if(this.scaleAble) FatTransformer.scale(this.selectedObjects, v.x, v.y, true);
		if(this.rotateAble){
			FatTransformer.rotateRelative(this.selectedObjects, v.x,v.y, centerOrigin.x, centerOrigin.y, clickedAngle);
		}
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if(this.cam.followMouse && !this.cam.isZoomOut()){
			this.cam.setFollowSpeed(0.1f, 0.1f);
			Vector3 v = new Vector3(screenX, screenY, 0f);
			this.cam.unproject(v);
			this.mover.setPosition(v.x, v.y);
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if(!this.mapClicked) return false;
		FatMapEditor.guiStage.unfocusAll();
		
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
		this.selectedObjects = objects;
	}
	
	public void drawSelectRegion(ShapeRenderer renderer){
		float alpha = 1f;
		if(renderer.getCurrentType() == ShapeRenderer.ShapeType.Filled) alpha = .25f;
		else if(this.rotateAble){
			renderer.setColor(1f, .75f, .75f, 1f);
			renderer.line(centerOrigin.x-5,centerOrigin.y, centerOrigin.x+5, centerOrigin.y);
			renderer.line(centerOrigin.x,centerOrigin.y-5, centerOrigin.x, centerOrigin.y+5);
		}
		renderer.setColor(1f, .75f, .75f, alpha);
		renderer.rect(this.selectionRect.x, this.selectionRect.y, this.selectionRect.width, this.selectionRect.height);
	}
	
	private void selectFromRect(){
		for(Actor actor: FatMapEditor.mapStage.getActors()){
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
