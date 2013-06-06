package trixt0r.map.fat;

import trixt0r.map.fat.FatMapEditor.CameraMover;
import trixt0r.map.fat.core.FatMapLayer;
import trixt0r.map.fat.core.FatMapObject;
import trixt0r.map.fat.widget.layer.LayerWidget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class FatInputHandler implements InputProcessor {
	
	private LayerWidget layerWidget;
	private Array<FatMapObject> selectedObjects;
	private FatCamera cam;
	private CameraMover mover;
	private boolean dragAble = false;
	private boolean mapClicked = true;
	FatMapEditor editor;
	private Rectangle selectionRect;
	
	public static float HORIZONTAL_ACCELERATION = 10f, VERTICAL_ACCELERATION = 10f, ZOOM_SPEED = 0.25f;
	
	public FatInputHandler(FatMapEditor editor){
		this.editor = editor;
		this.cam = editor.mapCamera;
		this.mover = editor.mover;
		this.layerWidget = editor.layerWidget;
		this.selectionRect = new Rectangle();
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
		this.mapClicked = false;
		if(this.editor.guiHasFocus()) return false;
		this.mapClicked = true;
		Vector3 v = new Vector3(screenX, screenY,0f);
		this.cam.unproject(v);
		
		if(button == Buttons.MIDDLE){
			this.mover.setX(this.cam.viewportWidth/2);
			this.mover.setY(this.cam.viewportHeight/2);
			return true;
		}
		
		if(button == Buttons.LEFT ){
			this.selectionRect.set(v.x, v.y, 0f, 0f);
			if(this.selectedObjects != null){
				for(FatMapObject object: this.selectedObjects)
					object.select(false);
				this.selectedObjects = null;
			}
		}
		
		this.dragAble = button == Buttons.RIGHT;
		if(!this.mapClicked) return false;
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
		if(!this.mapClicked) return false;
		
		if(button == Buttons.LEFT){
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
		
		if(Gdx.input.isButtonPressed(Buttons.LEFT)){
			this.selectionRect.width = v.x-this.selectionRect.x;
			this.selectionRect.height = v.y-this.selectionRect.y;
			return true;
		}
		
		if(!this.dragAble || this.selectedObjects == null) return false;
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
	
	public void drawSelectRegion(ShapeRenderer renderer){
		float alpha = 1f;
		if(renderer.getCurrentType() == ShapeRenderer.ShapeType.Filled) alpha = 0.25f;
		renderer.setColor(1f, 0.75f, 0.75f, alpha);
		renderer.rect(this.selectionRect.x, this.selectionRect.y, this.selectionRect.width, this.selectionRect.height);
	}
	
	private void selectFromRect(){
		/*float minX = Math.min(this.selectionRect.x, this.selectionRect.width), maxX = Math.max(this.selectionRect.x, this.selectionRect.width);
		float minY = Math.min(this.selectionRect.y, this.selectionRect.height), maxY = Math.max(this.selectionRect.y, this.selectionRect.height);
		int xSign = sign((this.selectionRect.x+this.selectionRect.width)-this.selectionRect.x);
		int ySign = sign((this.selectionRect.y+this.selectionRect.height)-this.selectionRect.y);*/
		for(Actor actor: FatMapEditor.mapStage.getActors()){
			if(!(actor instanceof FatMapLayer)) continue;
			FatMapLayer layer = (FatMapLayer) actor;
			for(FatMapObject obj: layer.objects){
				if(Intersector.overlaps(selectionRect, obj.getBBox()))
					obj.select(true);
				else
					obj.select(false);
			}
		}
	}
	
	/*private int sign(float x){
		if(x >= 0) return 1;
		else return -1;
	}*/

}
