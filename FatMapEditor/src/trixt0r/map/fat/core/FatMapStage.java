package trixt0r.map.fat.core;

import trixt0r.map.fat.FatCamera;
import trixt0r.map.fat.FatInputHandler;
import trixt0r.map.fat.FatMapEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class FatMapStage extends Stage{
	
	
	private FatCamera camera;
	private CameraMover mover;
	private ShapeRenderer renderer;
	private Array<FatMapLayer> layers;
	private FatInputHandler input;
	
	public FatMapStage(){
		super();
		this.mover = new CameraMover();
		this.mover.setX(this.getWidth()/2);
		this.mover.setY(this.getHeight()/2);
		this.addActor(mover);
		
		this.camera = new FatCamera(0.25f,0.25f);
		this.camera.setToFollow(mover);
		this.camera.setInterpolationX(Interpolation.linear);
		this.camera.setInterpolationY(Interpolation.linear);
		this.camera.setInterpolationZoom(Interpolation.linear);
		this.camera.setZoomSpeed(0.05f);
		this.setCamera(camera);
		
		this.layers = new Array<FatMapLayer>();
		this.renderer = new ShapeRenderer();
	}
	
	@Override
	public void draw(){
		this.renderer.setProjectionMatrix(this.camera.combined);
		super.draw();
		Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			this.renderer.begin(ShapeRenderer.ShapeType.Filled);
				this.renderer.rect(this.camera.position.x-this.camera.viewportWidth*this.camera.zoom/2,
					this.camera.position.y-this.camera.viewportHeight*this.camera.zoom/2, 
					this.camera.viewportWidth*this.camera.zoom, this.camera.viewportHeight*this.camera.zoom, 
					FatMapEditor.BOTTOM_LEFT_BG, FatMapEditor.BOTTOM_RIGHT_BG, FatMapEditor.TOP_LEFT_BG, FatMapEditor.TOP_RIGHT_BG);
				
				for(FatMapLayer layer: this.layers)
					layer.draw(this.renderer);
				this.input.drawSelectRegion(this.renderer);
			this.renderer.end();
			
			this.renderer.begin(ShapeRenderer.ShapeType.Line);
				for(FatMapLayer layer: this.layers)
					layer.draw(this.renderer);
				this.input.drawSelectRegion(this.renderer);
				this.drawGrid();
			this.renderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	@Override
	public void act(){
		super.act();
		this.camera.update();
	}
	
	@Override
	public void dispose(){
		super.dispose();
		this.renderer.dispose();
	}
	
	@Override
	public void addActor(Actor actor){
		super.addActor(actor);
		if(actor instanceof FatMapLayer) this.layers.add((FatMapLayer) actor);
	}
	
	public void removeLayer(FatMapLayer layer){
		super.getActors().removeValue(layer, true);
		this.layers.removeValue(layer, true);
	}
	
	public void removeAllLayers(Array<Actor> layers){
		Array<Actor> actors = super.getActors();
		actors.removeAll(layers, true);
		for(Actor actor: layers){
			this.layers.removeValue((FatMapLayer) actor, true);
		}
	}
	
	public CameraMover mover(){
		return this.mover;
	}

	private void drawGrid(){
		final float gridWidth = this.camera.viewportWidth*this.camera.zoom, gridHeight = this.camera.viewportHeight*this.camera.zoom;
		this.renderer.setColor(.25f, .25f, .25f, 0.75f);
		
		for(int x = 0; x <= this.camera.viewportWidth*this.camera.zoom && FatMapEditor.GRID_XOFFSET > 5; x+=FatMapEditor.GRID_XOFFSET)
			this.renderer.line(Math.round((this.camera.position.x-gridWidth/2)/FatMapEditor.GRID_XOFFSET)*FatMapEditor.GRID_XOFFSET+x, 
					this.camera.position.y-gridHeight/2,
					Math.round((this.camera.position.x-gridWidth/2)/FatMapEditor.GRID_XOFFSET)*FatMapEditor.GRID_XOFFSET+x,
					this.camera.position.y+gridHeight/2);
		for(int y = 0; y <= this.camera.viewportHeight*this.camera.zoom && FatMapEditor.GRID_YOFFSET > 5; y+=FatMapEditor.GRID_YOFFSET)
			this.renderer.line(this.camera.position.x-gridWidth/2,
					Math.round((this.camera.position.y-gridHeight/2)/FatMapEditor.GRID_YOFFSET)*FatMapEditor.GRID_YOFFSET+y,
					this.camera.position.x+gridWidth/2,
					Math.round((this.camera.position.y-gridHeight/2)/FatMapEditor.GRID_YOFFSET)*FatMapEditor.GRID_YOFFSET+y);
	}
	
	public void setInputHandler(FatInputHandler handler){
		this.input = handler;
	}
	
	public static class CameraMover extends Actor{
		
		public float hacc, vacc;
		public float hspeed, vspeed;
		private float friction = 10f;
		
		public static float MAX_HSPEED = 10f, MAX_VSPEED = 10f;
		
		public float maxHspeed, maxVspeed;
		
		public boolean fastMode = false;
		
		@Override
		public void act(float delta){
			if(fastMode){
				maxHspeed *= 2f;
				maxVspeed *= 2f;
			}
			hspeed = Math.min(Math.abs(hspeed+hacc), maxHspeed)*Math.signum(hacc);
			vspeed = Math.min(Math.abs(vspeed+vacc), maxVspeed)*Math.signum(vacc);
			if(hacc == 0f) hspeed -= Math.signum(hspeed)*Math.min(Math.abs(hspeed),friction);
			if(vacc == 0f) vspeed -= Math.signum(vspeed)*Math.min(Math.abs(vspeed),friction);
			this.setX(getX() + hspeed);
			this.setY(getY() + vspeed);
		}
		
	}

}
