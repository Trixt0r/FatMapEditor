package trixt0r.map.fat.widget;

import trixt0r.map.fat.FatMapEditor;
import trixt0r.map.fat.core.FatMapObject;
import trixt0r.map.fat.widget.control.ControllerWidget;
import trixt0r.map.fat.widget.layer.LayerWidget;
import trixt0r.map.fat.widget.tools.ToolsWidget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class WidgetStage extends Stage{
	
	//private Skin skin;
	private OrthographicCamera camera;
	private ControllerWidget controllerWidget;
	public final LayerWidget layerWidget;
	public final ToolsWidget toolsWidget;
	public final FatMapEditor editor;
	
	public WidgetStage(FatMapEditor editor){
		super();
		this.editor = editor;
		//this.skin = this.editor.getSkin();

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		this.camera = new OrthographicCamera(w, h);
		this.camera.position.x = w/2; this.camera.position.y =h/2;
		this.setCamera(camera);
		
		editor.guiStage = this;
		
		//Set up gui		
		this.controllerWidget = new ControllerWidget(editor);
		this.layerWidget = new LayerWidget(editor);
		this.toolsWidget = new ToolsWidget(editor);
	}
	
	public FatMapEditor getEditor(){
		return this.editor;
	}
	
	public boolean hasFocus(){
		return this.layerWidget.hasFocus() || this.controllerWidget.hasFocus() || this.toolsWidget.hasFocus();
	}
	
	@Override
	public void dispose(){
		this.toolsWidget.dispose();
	}
	
	public Array<FatMapObject> getSelectedObjects(){
		return this.layerWidget.getSelectedObjects();
	}
	
	@Override
	public void setViewport(float width, float height, boolean keepAspectRatio){
		super.setViewport(width, height, keepAspectRatio);
		if(this.layerWidget != null) this.layerWidget.layout();
		if(this.controllerWidget != null) this.controllerWidget.layout();
	}

}
