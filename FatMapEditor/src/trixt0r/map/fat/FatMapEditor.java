package trixt0r.map.fat;

import trixt0r.map.fat.core.FatMapLayer;
import trixt0r.map.fat.widget.control.ControllerWidget;
import trixt0r.map.fat.widget.layer.LayerWidget;
import trixt0r.map.fat.widget.layer.nodes.LayerNode;
import trixt0r.map.fat.widget.tools.ToolsWidget;

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
	public static int GRID_YOFFSET = 32;
	public static int GRID_XOFFSET = 32;
	private OrthographicCamera guiCamera;
	private SpriteBatch batch;
	private ShapeRenderer renderer;
	
	public static final Color BOTTOM_LEFT_BG = new Color(.25f,.25f,.25f,0.5f),
			BOTTOM_RIGHT_BG =new Color(.25f,.25f,.25f,0.5f),
			TOP_LEFT_BG = new Color(.75f,.75f,.75f,0.5f),
			TOP_RIGHT_BG = new Color(.75f,.75f,.75f,0.5f),
			BG_COLOR =  new Color(.25f,.5f, .5f, 1);
	
	public final static Skin skin = new Skin();
	public static Stage guiStage, mapStage;
	BitmapFont font;
	Color color;
	LayerWidget layerWidget;
	ControllerWidget controllerWidget;
	ToolsWidget toolsWidget;
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
		
		this.batch = new SpriteBatch();
		this.renderer = new ShapeRenderer();
		
		//Set up gui
		
		this.controllerWidget = new ControllerWidget(guiStage, skin);
		this.layerWidget = new LayerWidget(guiStage,skin);
		this.toolsWidget = new ToolsWidget(guiStage,skin);
		
		//Set up input
		this.inputHandler = new FatInputHandler(this);
		FatInputMultiplexer inputHandling = new FatInputMultiplexer(guiStage, inputHandler);
		Gdx.input.setInputProcessor(inputHandling);
	}

	@Override
	public void dispose() {
		this.batch.dispose();
		this.renderer.dispose();
		this.toolsWidget.dispose();
		skin.dispose();
	}

	@Override
	public void render() {
		//Gdx.gl.glClearColor((float)50/255,(float)99/255,(float)187/255, 1);
		Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, BG_COLOR.r);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//update
		this.mapCamera.update();
		
		this.renderer.setProjectionMatrix(this.mapCamera.combined);
		this.batch.setProjectionMatrix(this.guiCamera.combined);
		
		mapStage.act();
		
		guiStage.act();
		
		this.inputHandler.setSelectedObjects(this.layerWidget.getSelectedObjects());

		float gridWidth = this.mapCamera.viewportWidth*this.mapCamera.zoom, gridHeight = this.mapCamera.viewportHeight*this.mapCamera.zoom;
		
		//draw
		Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			
			this.renderer.begin(ShapeType.Filled);
			/*Color temp = new Color();
			for(int x = -1; x < gridWidth/GRID_XOFFSET && GRID_XOFFSET > 5; x++){
				for(int y = -1; y < gridHeight/GRID_YOFFSET; y++){
					if(x % 2 == 0)
						if(y % 2 == 0) temp.set(Color.BLACK);
						else temp.set(Color.WHITE);
					if(x % 2 == 1) 
						if(y % 2 == 0) temp.set(Color.WHITE);
						else temp.set(Color.BLACK);
					temp.mul(1, 1, 1, .5f);
					this.renderer.setColor(temp);
					this.renderer.rect(Math.round((this.mapCamera.position.x-gridWidth/2)/GRID_XOFFSET)*GRID_XOFFSET + x*GRID_XOFFSET, 
							Math.round((this.mapCamera.position.y-gridHeight/2)/GRID_YOFFSET)*GRID_YOFFSET + y*GRID_YOFFSET,
						GRID_XOFFSET,GRID_YOFFSET);
				}
			}*/
			
				this.renderer.rect(this.mapCamera.position.x-this.mapCamera.viewportWidth*this.mapCamera.zoom/2,
						this.mapCamera.position.y-this.mapCamera.viewportHeight*this.mapCamera.zoom/2, 
						this.mapCamera.viewportWidth*this.mapCamera.zoom, this.mapCamera.viewportHeight*this.mapCamera.zoom, 
						BOTTOM_LEFT_BG, BOTTOM_RIGHT_BG, TOP_LEFT_BG, TOP_RIGHT_BG);
				
				for(Node node: this.layerWidget.layerTree.getNodes()){
					FatMapLayer layer = ((LayerNode)node).layer;
					layer.draw(renderer);
				}
				this.inputHandler.drawSelectRegion(renderer);
			this.renderer.end();
			this.renderer.begin(ShapeType.Line);
			
				for(Node node: this.layerWidget.layerTree.getNodes()){
					FatMapLayer layer = ((LayerNode)node).layer;
					layer.draw(renderer);
				}
				this.inputHandler.drawSelectRegion(renderer);
	
				this.renderer.setColor(.25f, .25f, .25f, 0.75f);

				for(int x = 0; x <= this.mapCamera.viewportWidth*this.mapCamera.zoom && GRID_XOFFSET > 5; x+=GRID_XOFFSET)
					this.renderer.line(Math.round((this.mapCamera.position.x-gridWidth/2)/GRID_XOFFSET)*GRID_XOFFSET+x, 
							this.mapCamera.position.y-gridHeight/2,
							Math.round((this.mapCamera.position.x-gridWidth/2)/GRID_XOFFSET)*GRID_XOFFSET+x,
							this.mapCamera.position.y+gridHeight/2);
				for(int y = 0; y <= this.mapCamera.viewportHeight*this.mapCamera.zoom && GRID_YOFFSET > 5; y+=GRID_YOFFSET)
					this.renderer.line(this.mapCamera.position.x-gridWidth/2,
							Math.round((this.mapCamera.position.y-gridHeight/2)/GRID_YOFFSET)*GRID_YOFFSET+y,
							this.mapCamera.position.x+gridWidth/2,
							Math.round((this.mapCamera.position.y-gridHeight/2)/GRID_YOFFSET)*GRID_YOFFSET+y);

				/*this.renderer.setColor(.75f, .75f, .75f, 0.75f);
				this.renderer.line(this.mapCamera.position.x-10*this.mapCamera.zoom, this.mapCamera.position.y, this.mapCamera.position.x+10*this.mapCamera.zoom, this.mapCamera.position.y);
				this.renderer.line(this.mapCamera.position.x, this.mapCamera.position.y+10*this.mapCamera.zoom, this.mapCamera.position.x, this.mapCamera.position.y-10*this.mapCamera.zoom);*/
				
			this.renderer.end();
		
			this.batch.begin();
			guiStage.draw();
			this.batch.end();
			
		Gdx.gl.glDisable(GL20.GL_BLEND);
		//System.out.println("Keyboard: "+guiStage.getKeyboardFocus()+", Scroll: "+guiStage.getScrollFocus());
	}

	@Override
	public void resize(int width, int height) {
		int maxWidth = Math.max(800, width), maxHeight = Math.max(600, height);
		Gdx.graphics.setDisplayMode(maxWidth, maxHeight, Gdx.graphics.isFullscreen());
		
		//Gdx.gl.glViewport(0, 0, maxWidth, maxHeight);
		guiStage.setViewport(maxWidth, maxHeight, true);
		this.guiCamera.setToOrtho(false,maxWidth, maxHeight);
		this.guiCamera.update();

		mapStage.setViewport(maxWidth, maxHeight, true);
		this.mapCamera.setToOrtho(false, maxWidth, maxHeight);
		this.mapCamera.update();
		
		
		this.layerWidget.layout();
		this.controllerWidget.layout();
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
		return this.layerWidget.hasFocus() || this.controllerWidget.hasFocus() || this.toolsWidget.hasFocus();
	}
}
