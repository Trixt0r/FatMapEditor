package trixt0r.map.fat.widget.control;

import trixt0r.map.fat.FatMapEditor;
import trixt0r.map.fat.FatTransformer;
import trixt0r.map.fat.widget.FatWidget;
import trixt0r.map.fat.widget.LabelTextField;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class ControllerWidget extends FatWidget {
	public final CheckBox snapToGrid;
	
	public final LabelTextField xOffset, yOffset;
	
	public ControllerWidget(Stage stage, Skin skin){
		super(stage, skin);
		
		this.xOffset = new LabelTextField("Grid - xoffset: ", skin, 48, ""+FatMapEditor.GRID_XOFFSET);
		this.xOffset.field.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
		this.xOffset.field.setMaxLength(4);
		this.yOffset = new LabelTextField("yoffset: ", skin, 48,""+FatMapEditor.GRID_YOFFSET);
		this.yOffset.field.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
		this.yOffset.field.setMaxLength(4);
		
		this.window.setMovable(false);
		this.window.setClip(true);
		
		this.snapToGrid = new CheckBox(" Snap to grid", skin);
		
		table.addActor(snapToGrid);
		table.addActor(xOffset);
		table.addActor(yOffset);
		
		layout();
	}
	
	public void layout(){
		this.window.setBounds(-2, this.stage.getCamera().viewportHeight-40, this.stage.getCamera().viewportWidth+4, 60);
		scrollPane.setBounds(0, 0, window.getWidth(), window.getHeight());
		table.setWidth(scrollPane.getWidth());
		table.align(Align.top | Align.left);
		
		this.snapToGrid.setX(this.window.getWidth() / 2);
		this.xOffset.setX(this.snapToGrid.getX() + this.snapToGrid.getPrefWidth()+20);
		this.xOffset.layout();
		this.yOffset.setX(this.xOffset.getX() + this.xOffset.getPrefWidth());
		this.yOffset.layout();
		
		this.snapToGrid.setY(5f);
		this.xOffset.setY(snapToGrid.getY());
		this.yOffset.setY(snapToGrid.getY());
	}
	
	@Override
	public void act(float delta){
		super.act(delta);
		try{
			this.xOffset.field.setColor(1, 1f, 1f, 1f);
			FatMapEditor.GRID_XOFFSET = Math.max(Integer.valueOf(this.xOffset.field.getText()),1);
		} catch(Exception e){
			this.xOffset.field.setColor(1, 0f, 0f, 1f);
		}
		try{
			this.yOffset.field.setColor(1, 1f, 1f, 1f);
			FatMapEditor.GRID_YOFFSET = Math.max(Integer.valueOf(this.yOffset.field.getText()),1);
		} catch(Exception e){
			this.yOffset.field.setColor(1, 0f, 0f, 1f);
		}
		FatTransformer.snapToGrid = this.snapToGrid.isChecked();
	}

}
