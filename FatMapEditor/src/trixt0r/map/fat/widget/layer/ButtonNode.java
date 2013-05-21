package trixt0r.map.fat.widget.layer;

import trixt0r.map.fat.FatMapEditor;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class ButtonNode extends Node{

	private Button buttonAdd;
	private Button buttonDel;
	
	private WidgetGroup buttonGroup;
	
	public ButtonNode(WidgetGroup group, Skin skin) {
		super(group);
		this.buttonAdd = new TextButton("Add", FatMapEditor.skin);
		this.buttonDel = new TextButton("Delete", FatMapEditor.skin);
		this.buttonGroup = group;
		this.buttonGroup.addActor(this.buttonAdd);
		this.buttonDel.setPosition(0, 0);
		this.buttonDel.setPosition(this.buttonAdd.getWidth()+5, 0);
		this.buttonGroup.addActor(this.buttonDel);
		this.setSelectable(false);
	}
	
	public WidgetGroup getGroup(){
		return this.buttonGroup;
	}
	
	public Button getAddButton(){
		return this.buttonAdd;
	}
	
	public Button getDelButton(){
		return this.buttonDel;
	}
	
	public static class LayerButtonGroup extends WidgetGroup{
		public float getPrefWidth () {
			return 100;
		}

		public float getPrefHeight () {
			return 24;
		}
	}

}
