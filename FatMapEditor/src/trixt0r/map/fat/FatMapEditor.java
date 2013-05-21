package trixt0r.map.fat;

import trixt0r.map.fat.widget.actions.LayerWidgetActions;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class FatMapEditor implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	public final static Skin skin = new Skin();
	Stage stage;
	BitmapFont font;
	Color color;
	NewLayerDialog diag;
	
	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		this.stage = new Stage();
		
		FileHandle skinFile = Gdx.files.internal("data/buttons.json");
		FileHandle atlasFile = skinFile.sibling(skinFile.nameWithoutExtension() + ".atlas");
		if (atlasFile.exists()) {
			TextureAtlas atlas = new TextureAtlas(atlasFile);
			skin.addRegions(atlas);
		}
	
		skin.load(skinFile);
		
		Gdx.input.setInputProcessor(stage);
		
		camera = new OrthographicCamera(w, h);
		camera.position.x = w/2; camera.position.y =h/2;
		this.stage.setCamera(camera);
		
		batch = new SpriteBatch();
		
		LayerWidgetActions.createLayerWidget(stage, skin);
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.act();
		LayerWidgetActions.act();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		stage.draw();
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		Gdx.graphics.setDisplayMode(Math.max(350, width), Math.max(100, height), Gdx.graphics.isFullscreen());
		this.stage.setViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.camera.update();
		LayerWidgetActions.reArangeLayout();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
