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
	
	public static float HORIZONTAL_ACCELERATION = 2f, VERTICAL_ACCELERATION = 2f, ZOOM_SPEED = 0.25f;
	
	public FatInputHandler(LayerWidget layerWidget, FatCamera cam, CameraMover mover){
		this.layerWidget = layerWidget;
		this.cam = cam;
		this.mover = mover;
	}

	public boolean keyDown(int keycode) {
		if(keycode == Keys.F1) {
			this.layerWidget.fadeOut = !this.layerWidget.fadeOut;
			return true;
		}
		float hacc = HORIZONTAL_ACCELERATION, vacc = VERTICAL_ACCELERATION;
		this.mover.fastMode = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT);
		if(this.mover.fastMode){
			hacc *= 2f; vacc *= 2f;
		}
		if(keycode == Keys.A) mover.hacc = -hacc;
		if(keycode == Keys.D) mover.hacc = hacc;
		if(keycode == Keys.S) mover.vacc = -vacc;
		if(keycode == Keys.W) mover.vacc = vacc;
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
		
		if(button == Buttons.MIDDLE){
			this.mover.setX(this.cam.viewportWidth/2);
			this.mover.setY(this.cam.viewportHeight/2);
		}
		
		this.dragAble = button == Buttons.LEFT;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Vector3 v = new Vector3(screenX, screenY,0f);
		this.cam.unproject(v);
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
		this.cam.setZoomOut(amount > 0);
		float am = ZOOM_SPEED*amount;
		if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) am*=2f;
		this.cam.resetZoom();
		this.cam.setZoomFar(this.cam.zoom+am);
		this.cam.setZoomNear(this.cam.zoom+am);
		System.out.println((100f/this.cam.zoom)+" %");
		return true;
	}
	
	public void setSelectedObjects(Array<FatMapObject> objects){
		this.selectedObjects = objects;
	}

}
