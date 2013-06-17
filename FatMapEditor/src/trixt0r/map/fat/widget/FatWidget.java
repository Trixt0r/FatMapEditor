package trixt0r.map.fat.widget;

import trixt0r.map.fat.FatMapEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public abstract class FatWidget extends WidgetGroup {
	
	public final Window window;
	public final Stage stage;
	public final Skin skin;
	public final ScrollPane scrollPane;
	public final Table table;
	public final FatMapEditor editor;
	
	public FatWidget(FatMapEditor editor){
		this.editor = editor;
		this.skin = this.editor.getSkin();
		this.stage = this.editor.getGui();
		this.window = new Window("", skin);
		this.table = new Table();
		this.scrollPane = new ScrollPane(table, skin);
		
		this.window.addActor(scrollPane);
		
		this.addActor(window);
		
		stage.addActor(this);
		
		scrollPane.setFlickScroll(false);
		
		table.align(Align.top | Align.left);
	}
	
	public abstract void layout();
	
	public boolean hasFocus(){
		Vector2 mouse = this.stage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		Vector2 v = this.screenToLocalCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		Actor hit = this.stage.hit(mouse.x, mouse.y, false);
		return hit == this.hit(v.x,v.y, false) && hit != null;
	}
}
