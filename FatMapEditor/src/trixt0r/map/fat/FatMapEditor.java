package trixt0r.map.fat;

import trixt0r.map.fat.core.FatMapLayers;
import trixt0r.map.fat.widget.layer.LayerWidget;
import trixt0r.map.fat.widget.layer.NewLayerDialog;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class FatMapEditor implements ApplicationListener {
	private OrthographicCamera guiCamera;
	private FatCamera mapCamera;
	private SpriteBatch batch;
	private ShapeRenderer renderer;
	
	public final static Skin skin = new Skin();
	Stage guiStage, mapStage;
	BitmapFont font;
	Color color;
	NewLayerDialog diag;
	LayerWidget layerWidget;
	FatInputHandler inputHandler;
	CameraMover mover;
	
	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		this.guiStage = new Stage();
		this.mapStage = new Stage();
		
		FileHandle skinFile = Gdx.files.internal("data/buttons.json");
		FileHandle atlasFile = skinFile.sibling(skinFile.nameWithoutExtension() + ".atlas");
		if (atlasFile.exists()) {
			TextureAtlas atlas = new TextureAtlas(atlasFile);
			skin.addRegions(atlas);
		}
		skin.load(skinFile);
		
		this.guiCamera = new OrthographicCamera(w, h);
		this.guiCamera.position.x = w/2; this.guiCamera.position.y =h/2;
		this.guiStage.setCamera(guiCamera);
		
		this.mapCamera = new FatCamera(0.25f,0.25f);
		this.mover = new CameraMover();
		this.mapCamera.setToFollow(mover);
		this.mover.setX(w/2);
		this.mover.setY(h/2);
		
		this.mapCamera.setInterpolationX(Interpolation.linear);
		this.mapCamera.setInterpolationY(Interpolation.linear);
		this.mapCamera.setInterpolationZoom(Interpolation.swingOut);
		this.mapCamera.setZoomSpeed(0.05f);
		this.mapStage.setCamera(mapCamera);
		
		this.mapStage.addActor(mover);
		
		//this.mapCamera.position.x = w/2; //this.guiCamera.position.y =h/2;
		
		this.batch = new SpriteBatch();
		this.renderer = new ShapeRenderer();
		
		this.layerWidget = new LayerWidget(this.guiStage,skin);
		
		this.inputHandler = new FatInputHandler(this.layerWidget, this.mapCamera,this.mover);
		FatInputMultiplexer inputHandling = new FatInputMultiplexer(guiStage, inputHandler);
		
		Gdx.input.setInputProcessor(inputHandling);
	}

	@Override
	public void dispose() {
		this.batch.dispose();
		this.renderer.dispose();
	}

	@Override
	public void render() {
	//	Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		this.mapCamera.update();
		
		this.renderer.setProjectionMatrix(this.mapCamera.combined);
		this.batch.setProjectionMatrix(this.guiCamera.combined);
		
		this.guiStage.act();
		
		this.mapStage.act();

		this.inputHandler.setSelectedObjects(this.layerWidget.getSelectedObjects());
		
		this.renderer.begin(ShapeType.Filled);
			FatMapLayers.draw(this.renderer);
		this.renderer.end();
		
		this.renderer.begin(ShapeType.Line);
			FatMapLayers.draw(this.renderer);
		this.renderer.end();
		
		this.batch.begin();
		this.guiStage.draw();
		this.batch.end();
	}

	@Override
	public void resize(int width, int height) {
		Gdx.graphics.setDisplayMode(Math.max(350, width), Math.max(100, height), Gdx.graphics.isFullscreen());
		this.guiStage.setViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		this.guiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.guiCamera.update();

		this.mapStage.setViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		this.mapCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//this.mapCamera.setTarget(width/2, height/2);
		this.mapCamera.update();
		
		
		this.layerWidget.layout();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	public static class CameraMover extends Actor{
		
		public float hacc, vacc;
		public float hspeed, vspeed;
		private float friction = 2.5f;
		
		public float MAX_HSPEED = 40f, MAX_VSPEED = 40f;
		
		public boolean fastMode = false;
		
		@Override
		public void act(float delta){
			float maxHspeed = MAX_HSPEED, maxVspeed = MAX_VSPEED;
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
