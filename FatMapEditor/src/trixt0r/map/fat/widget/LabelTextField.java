package trixt0r.map.fat.widget;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class LabelTextField extends Table {
	
	public final Label title;
	public final TextField field;
	
	public LabelTextField(String title, Skin skin){
		this(title, skin, 150, "");
	}
	
	public LabelTextField(String title, Skin skin, float fieldWidth){
		this(title, skin, fieldWidth, "");
	}
	
	public LabelTextField(String title, Skin skin, String content){
		this(title, skin, 150, content);
	}
	
	public LabelTextField(String title, Skin skin, float fieldWidth, String content){
		super(skin);
		this.title = new Label(title, skin);
		this.field = new TextField(content,skin);
		
		this.add(this.field);
		this.add(this.title);
		this.field.setWidth(fieldWidth);
		this.field.selectAll();
	}
	
	public String getText(){
		return this.field.getText();
	}
	
	public String getTitle(){
		return this.title.getText().toString();
	}
	
	@Override
	public float getPrefWidth(){
		return this.title.getWidth()+this.field.getWidth()+20;
	}
	
	@Override
	public float getPrefHeight(){
		return Math.max(this.title.getHeight(),this.field.getHeight())+10;
	}
	
	@Override
	public void layout(){
		this.title.setPosition(0, 0);
		this.field.setPosition(this.title.getX()+this.title.getWidth()+5, 0);
	}

}
