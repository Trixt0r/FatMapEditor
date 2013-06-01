package trixt0r.map.fat;

import trixt0r.map.fat.FatMapEditor.CameraMover;
import trixt0r.map.fat.core.FatMapObject;
import trixt0r.map.fat.widget.layer.LayerWidget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class FatInputHandler implements InputProcessor {
	
	private LayerWidget layerWidget;
	private Array<FatMapObject> selectedObjects;
	private FatCamera cam;
	private CameraMover mover;
	private boolean dragAble = false;
	private boolean mapClicked = true;
	FatMapEditor editor;
	
	public static float HORIZONTAL_ACCELERATION = 10f, VERTICAL_ACCELERATION = 10f, ZOOM_SPEED = 0.25f;
	
	public FatInputHandler(FatMapEditor editor){
		this.editor = editor;
		this.cam = editor.mapCamera;
		this.mover = editor.mover;
		this.layerWidget = editor.layerWidget;
	}

	public boolean keyDown(int keycode) {
		if(keycode == Keys.ESCAPE)
			Gdx.app.exit();
		if(keycode == Keys.SPACE){
		}
		if(keycode == Keys.F1) {
			this.layerWidget.fadeOut = !this.layerWidget.fadeOut;
			return true;
		}
		if(!this.cam.followMouse && this.mapClicked){
			this.cam.setFollowSpeed(0.25f, 0.25f);
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
		if(keycode == Keys.A || keycode == Keys.D)
			mover.hacc = 0f;
		if(keycode == Keys.S || keycode == Keys.W)
			mover.vacc = 0f;
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer,
			int button) {
		Vector3 v = new Vector3(screenX, screenY,0f);
		this.cam.unproject(v);
		
		if(button == Buttons.MIDDLE && !this.editor.guiHasFocus()){
			this.mover.setX(this.cam.viewportWidth/2);
			this.mover.setY(this.cam.viewportHeight/2);
			return true;
		}
		
		this.dragAble = button == Buttons.LEFT;
		this.mapClicked = !this.editor.guiHasFocus();
		if(!this.mapClicked) return false;
		System.out.println(FatMapEditor.mapStage.hit(v.x, v.y, false));
		if(this.selectedObjects == null) return false;
		if(this.selectedObjects.size == 0) return false;
		for(FatMapObject object: this.selectedObjects){
			object.xDiff = object.getX() - v.x;
			object.yDiff = object.getY() - v.y;
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer,
			int button) {
		if(button == Buttons.RIGHT){
			mover.hacc = 0f;
			mover.vacc = 0f;
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Vector3 v = new Vector3(screenX, screenY,0f);
		this.cam.unproject(v);
		
		if(Gdx.input.isButtonPressed(Buttons.RIGHT)){
			this.mover.hacc = Gdx.input.getDeltaX()*this.cam.zoom;
			this.mover.vacc = -Gdx.input.getDeltaY()*this.cam.zoom;
			mover.maxHspeed = CameraMover.MAX_HSPEED*this.cam.zoom; 
			mover.maxVspeed = CameraMover.MAX_VSPEED*this.cam.zoom;
			return true;
		}
		
		if(!this.dragAble) return false;
		if(this.selectedObjects == null) return false;
		if(this.selectedObjects.size == 0) return false;
		for(FatMapObject object: this.selectedObjects){
			object.setX(v.x+object.xDiff);
			object.setY(v.y+object.yDiff);
		}
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if(!this.mapClicked) return false;
		FatMapEditor.guiStage.setScrollFocus(null);
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

}
