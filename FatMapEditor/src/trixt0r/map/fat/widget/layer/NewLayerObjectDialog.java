package trixt0r.map.fat.widget.layer;

import trixt0r.map.fat.FatMapEditor;
import trixt0r.map.fat.widget.LabelTextField;
import trixt0r.map.fat.widget.layer.actions.LayerWidgetAddObject;
import trixt0r.map.fat.widget.layer.nodes.LayerNode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class NewLayerObjectDialog extends Dialog {
	
	TextButton buttonOk, buttonCancel;
	LabelTextField name, posX, posY, scaleX, scaleY, angle, alpha;
	Label lblPosition, lblScale;
	LayerNode layerNode;

	public NewLayerObjectDialog(Skin skin) {
		super("New freeform object", skin);

		this.name = new LabelTextField("Name: ", skin, "newObject");
		this.posX = new LabelTextField("X: ", skin, 50, ""+(Gdx.graphics.getWidth()/2-100));
		this.posY = new LabelTextField("Y: ", skin, 50, ""+Gdx.graphics.getHeight()/2);
		this.scaleX = new LabelTextField("X: ", skin, 50, "10");
		this.scaleY = new LabelTextField("Y: ", skin, 50, "10");
		this.angle = new LabelTextField("Angle: ", skin, 50, "0");
		this.alpha = new LabelTextField("Alpha: ", skin, 50, "100");
		
		this.lblPosition = new Label("Position: ", skin);
		this.lblScale = new Label("Scale: ", skin);
		this.buttonOk = new TextButton("Create", skin);
		this.buttonCancel = new TextButton("Cancel", skin);
		
		this.key(Keys.ENTER, name);
		this.button(buttonOk, name);
		this.button(buttonCancel);
		this.add(name);
		this.add(posX);
		this.add(posY);
		this.add(scaleX);
		this.add(scaleY);
		this.add(angle);
		this.add(alpha);
		this.add(lblPosition);
		this.add(lblScale);
	}
	
	@Override
	public void result(Object object){
		if(this.getColor().a != 1) cancel();
		if(object == this.name && this.getColor().a == 1){
			LayerWidgetAddObject dialog = new LayerWidgetAddObject(this.layerNode, this.name.getText());
			dialog.x = Float.parseFloat(this.posX.getText());
			dialog.y = Float.parseFloat(this.posY.getText());
			dialog.xscale = Float.parseFloat(this.scaleX.getText());
			dialog.yscale = Float.parseFloat(this.scaleY.getText());
			dialog.alpha = Float.parseFloat(this.alpha.getText()) / 100;
			dialog.angle = Float.parseFloat(this.angle.getText()) % 360;
			dialog.act(0);
			this.getStage().unfocus(this);
		}
	}
	
	public void setLayerNode(LayerNode layerNode){
		this.layerNode = layerNode;
	}
	
	@Override
	public float getPrefWidth () {
		return 300;
	}
	
	@Override
	public float getPrefHeight () {
		return 225;
	}
	
	@Override
	public Dialog show(Stage stage){
		super.show(stage);
		this.name.field.setText("new Object"+(this.layerNode.getChildren().size));
		stage.setKeyboardFocus(this.name.field);
		this.posX.field.setText(""+(FatMapEditor.mapStage.getCamera().position.x));
		this.posY.field.setText(""+(FatMapEditor.mapStage.getCamera().position.y));
		this.scaleX.field.setText("10");
		this.scaleY.field.setText("10");
		this.angle.field.setText("0");
		this.alpha.field.setText("100");
		
		this.name.field.selectAll();
		this.posX.field.selectAll();
		this.posY.field.selectAll();
		this.scaleX.field.selectAll();
		this.scaleY.field.selectAll();
		this.angle.field.selectAll();
		this.alpha.field.selectAll();
		return this;
	}
	
	@Override
	public void layout(){
		this.name.setPosition(5,this.getPrefHeight()-this.name.getPrefHeight()-15);
		this.name.layout();
		this.lblPosition.setPosition(5, this.name.getY()-this.lblPosition.getPrefHeight()-10);
		this.posX.setPosition(this.lblPosition.getX()+this.lblPosition.getPrefWidth()+10,this.lblPosition.getY());
		this.posX.layout();
		this.posY.setPosition(this.posX.getX()+this.posX.getPrefWidth()+10,this.lblPosition.getY());
		this.posY.layout();
		
		this.lblScale.setPosition(5, this.lblPosition.getY()-this.lblScale.getPrefHeight()-10);
		this.scaleX.setPosition(this.lblScale.getX()+this.lblScale.getPrefWidth()+10,this.lblScale.getY());
		this.scaleX.layout();
		this.scaleY.setPosition(this.scaleX.getX()+this.scaleX.getPrefWidth()+10,this.lblScale.getY());
		this.scaleY.layout();
		
		this.angle.setPosition(5,this.lblScale.getY()-this.lblScale.getPrefHeight()-10);
		this.angle.layout();
		this.alpha.setPosition(this.angle.getX()+this.alpha.getPrefWidth()+10,this.angle.getY());
		this.alpha.layout();
		
		this.buttonCancel.setPosition(this.getPrefWidth()-this.buttonCancel.getPrefWidth()-10, this.buttonCancel.getPrefHeight()/2);
		this.buttonOk.setPosition(this.buttonCancel.getX()-this.buttonOk.getPrefWidth()-10, this.buttonOk.getPrefHeight()/2);
		super.invalidate();
	}

}
