package trixt0r.map.fat.widget.layer;

import trixt0r.map.fat.FatMapEditor;
import trixt0r.map.fat.widget.layer.actions.LayerWidgetAddLayer;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
/**
 * A dialog which let's the user create a new layer.
 * @author Trixt0r
 */
public class NewLayerDialog extends Dialog{
	
	public final TextField field;
	public final TextButton buttonOk, buttonCancel;
	public Tree tree;
	private final Skin skin;
	private final FatMapEditor editor;

	public NewLayerDialog(String title, FatMapEditor editor, Tree tree) {
		super(title, editor.getSkin());
		this.editor = editor;
		this.skin = editor.getSkin();
		this.tree = tree;
		this.field = new TextField("Layer "+(tree.getNodes().size+1), skin);
		this.buttonOk = new TextButton("Create", skin);
		this.buttonCancel = new TextButton("Cancel", skin);

		this.key(Keys.ENTER, field);
		this.button(buttonOk, field);
		this.button(buttonCancel);
		this.add(field);
	}
	
	@Override
	public void result(Object object){
		if(this.getColor().a != 1) cancel();
		if(object == this.field && this.getColor().a == 1){
			new LayerWidgetAddLayer(tree, field.getText(), this.editor).act(0);
			this.getStage().unfocus(this);
		}
	}
	
	@Override
	public float getPrefWidth () {
		return this.field.getWidth()+10;
	}
	
	@Override
	public float getPrefHeight () {
		return 125;
	}
	
	public Dialog show(Stage stage){
		super.show(stage);
		this.field.setText("Layer "+(tree.getNodes().size+1));
		this.field.selectAll();
		stage.setKeyboardFocus(field);
		return this;
	}
	
	@Override
	public void layout(){
		this.field.setPosition(Math.round((this.getPrefWidth() - field.getPrefWidth()) / 2), Math.round((this.getPrefHeight() - field.getPrefHeight()) / 2)+this.buttonOk.getPrefHeight()/4);
		this.buttonCancel.setPosition(this.getPrefWidth()-this.buttonCancel.getPrefWidth()-10, this.buttonCancel.getPrefHeight()/2);
		this.buttonOk.setPosition(this.buttonCancel.getX()-this.buttonOk.getPrefWidth()-10, this.buttonOk.getPrefHeight()/2);
		super.invalidate();
	}
}
