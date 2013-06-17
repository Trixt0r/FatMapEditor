package trixt0r.map.fat;

import trixt0r.map.fat.core.FatMapStage;
import trixt0r.map.fat.widget.WidgetStage;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class FatMapEditor implements ApplicationListener {
	public static int GRID_YOFFSET = 32;
	public static int GRID_XOFFSET = 32;
	
	public static final Color BOTTOM_LEFT_BG = new Color(.25f,.25f,.25f,0.5f),
			BOTTOM_RIGHT_BG =new Color(.25f,.25f,.25f,0.5f),
			TOP_LEFT_BG = new Color(.75f,.75f,.75f,0.5f),
			TOP_RIGHT_BG = new Color(.75f,.75f,.75f,0.5f),
			BG_COLOR =  new Color(.25f,.5f, .5f, 1);
	
	private Skin skin;
	public WidgetStage guiStage;
	private FatMapStage mapStage;
	FatInputHandler inputHandler;
	
	@Override
	public void create() {
		
		this.skin = new Skin(Gdx.files.internal("data/buttons.json"));

		this.mapStage = new FatMapStage();
		this.guiStage = new WidgetStage(this);
		
		this.inputHandler = new FatInputHandler(this.mapStage, this.guiStage);
		this.mapStage.setInputHandler(inputHandler);

		
		//Set up input
		FatInputMultiplexer inputHandling = new FatInputMultiplexer(guiStage, inputHandler);
		Gdx.input.setInputProcessor(inputHandling);
	}

	@Override
	public void dispose() {
		this.guiStage.dispose();
		this.mapStage.dispose();
		this.skin.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, BG_COLOR.r);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//update		
		this.mapStage.act();
		this.guiStage.act();
		
		this.inputHandler.setSelectedObjects(this.guiStage.getSelectedObjects());
		
		//draw
		this.mapStage.draw();
		this.guiStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		int maxWidth = Math.max(800, width), maxHeight = Math.max(600, height);
		Gdx.graphics.setDisplayMode(maxWidth, maxHeight, Gdx.graphics.isFullscreen());
		
		guiStage.setViewport(maxWidth, maxHeight, true);

		mapStage.setViewport(maxWidth, maxHeight, true);
	}
	
	public WidgetStage getGui(){
		return this.guiStage;
	}
	
	public FatMapStage getMap(){
		return this.mapStage;
	}
	
	public Skin getSkin(){
		return this.skin;
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	public boolean guiHasFocus(){
		return this.guiStage.hasFocus();
	}
}
