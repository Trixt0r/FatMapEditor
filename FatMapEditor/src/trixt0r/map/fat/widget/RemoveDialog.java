package trixt0r.map.fat.widget;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
/**
 * A dialog which let's the user to create a new layer.
 * @author Trixt0r
 */
public class RemoveDialog extends Dialog{
	
	Label question;
	TextButton buttonOk, buttonCancel;
	Action removeAction;

	public RemoveDialog(String title, Skin skin, Tree tree, String question) {
		super(title, skin);
		this.question = new Label(question, skin);
		this.buttonOk = new TextButton("Ok", skin);
		this.buttonCancel = new TextButton("Cancel", skin);
		this.addActor(this.question);

		this.key(Keys.ENTER, this.question);
		this.button(buttonOk, this.question);
		this.button(buttonCancel);
	}
	
	@Override
	public void result(Object object){
		if(this.getColor().a != 1) cancel();
		if(object == this.question && this.getColor().a == 1)
			this.removeAction.act(0);
	}
	
	@Override
	public float getPrefWidth () {
		return this.question.getPrefWidth()+25;
	}
	
	@Override
	public float getPrefHeight () {
		return 125;
	}
	
	@Override
	public void layout(){
		this.question.setPosition((this.getPrefWidth() - question.getPrefWidth()) / 2, (this.getPrefHeight() - question.getPrefHeight()) / 2+this.buttonOk.getPrefHeight()/4);
		this.buttonCancel.setPosition(this.getPrefWidth()-this.buttonCancel.getPrefWidth()-10, this.buttonCancel.getPrefHeight()/2);
		this.buttonOk.setPosition(this.buttonCancel.getX()-this.buttonOk.getPrefWidth()-10, this.buttonOk.getPrefHeight()/2);
		super.invalidate();
	}
	
	public void setQuestion(String question){
		this.question.setText(question);
		this.question.invalidate();
		this.question.validate();
	}
	
	public void setRemoveAction(Action action){
		this.removeAction = action;
	}
}