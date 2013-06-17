package trixt0r.map.fat.widget.layer.nodes;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class ButtonNode extends Node{

	private Button buttonAdd;
	private Button buttonDel;
	
	private WidgetGroup buttonGroup;
	
	public ButtonNode(Skin skin) {
		super(new LayerButtonGroup());
		this.buttonAdd = new TextButton("Add", skin);
		this.buttonDel = new TextButton("Delete", skin);
		this.buttonGroup = (LayerButtonGroup)super.getActor();
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
