package trixt0r.map.fat;

import trixt0r.map.fat.core.FatMapLayer;
import trixt0r.map.fat.widget.layer.LayerWidget;
import trixt0r.map.fat.widget.layer.NewLayerDialog;
import trixt0r.map.fat.widget.layer.nodes.LayerNode;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;

public class FatMapEditor implements ApplicationListener {
	private OrthographicCamera guiCamera;
	private SpriteBatch batch;
	private ShapeRenderer renderer;
	
	public final static Skin skin = new Skin();
	public static Stage guiStage, mapStage;
	BitmapFont font;
	Color color;
	NewLayerDialog diag;
	LayerWidget layerWidget;
	FatInputHandler inputHandler;
	CameraMover mover;
	FatCamera mapCamera;
	
	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		guiStage = new Stage();
		mapStage = new Stage();
		
		FileHandle skinFile = Gdx.files.internal("data/buttons.json");
		FileHandle atlasFile = skinFile.sibling(skinFile.nameWithoutExtension() + ".atlas");
		if (atlasFile.exists()) {
			TextureAtlas atlas = new TextureAtlas(atlasFile);
			skin.addRegions(atlas);
		}
		skin.load(skinFile);
		
		this.guiCamera = new OrthographicCamera(w, h);
		this.guiCamera.position.x = w/2; this.guiCamera.position.y =h/2;
		guiStage.setCamera(guiCamera);
		
		this.mapCamera = new FatCamera(0.25f,0.25f);
		this.mover = new CameraMover();
		this.mapCamera.setToFollow(mover);
		this.mover.setX(w/2);
		this.mover.setY(h/2);
		
		this.mapCamera.setInterpolationX(Interpolation.linear);
		this.mapCamera.setInterpolationY(Interpolation.linear);
		this.mapCamera.setInterpolationZoom(Interpolation.linear);
		this.mapCamera.setZoomSpeed(0.05f);
		mapStage.setCamera(mapCamera);
		
		mapStage.addActor(mover);
		
		//this.mapCamera.position.x = w/2; //this.guiCamera.position.y =h/2;
		
		this.batch = new SpriteBatch();
		this.renderer = new ShapeRenderer();
		
		//Set up gui
		this.layerWidget = new LayerWidget(guiStage,skin);
		
		//Set up input
		this.inputHandler = new FatInputHandler(this);
		FatInputMultiplexer inputHandling = new FatInputMultiplexer(guiStage, inputHandler);
		Gdx.input.setInputProcessor(inputHandling);
	}

	@Override
	public void dispose() {
		this.batch.dispose();
		this.renderer.dispose();
		skin.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor((float)50/255,(float)99/255,(float)187/255, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//update
		this.mapCamera.update();
		
		this.renderer.setProjectionMatrix(this.mapCamera.combined);
		this.batch.setProjectionMatrix(this.guiCamera.combined);
		
		guiStage.act();
		
		mapStage.act();
		
		this.inputHandler.setSelectedObjects(this.layerWidget.getSelectedObjects());

		//draw
		Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			
			this.renderer.setColor(0.25f, 0.3f, 0.75f, 0.25f);
			this.renderer.begin(ShapeType.Filled);
				for(Node node: this.layerWidget.layerTree.getNodes()){
					FatMapLayer layer = ((LayerNode)node).layer;
					layer.draw(renderer);
				}
				//FatMapLayers.draw(this.renderer);
				this.inputHandler.drawSelectRegion(renderer);
			this.renderer.end();
	
			this.renderer.setColor(0.25f, 0.3f, 0.75f, 1.0f);
			this.renderer.begin(ShapeType.Line);
				for(Node node: this.layerWidget.layerTree.getNodes()){
					FatMapLayer layer = ((LayerNode)node).layer;
					layer.draw(renderer);
				}
				//FatMapLayers.draw(this.renderer);
				this.inputHandler.drawSelectRegion(renderer);
			this.renderer.end();
			this.renderer.setColor(new Color(0.25f, 0.3f, 0.75f, 0.25f));
		
			this.batch.begin();
			guiStage.draw();
			this.batch.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	@Override
	public void resize(int width, int height) {
		Gdx.graphics.setDisplayMode(Math.max(640, width), Math.max(480, height), Gdx.graphics.isFullscreen());
		guiStage.setViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		this.guiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.guiCamera.update();

		mapStage.setViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		this.mapCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
	
	public boolean guiHasFocus(){
		return this.layerWidget.hasFocus();
	}
}
