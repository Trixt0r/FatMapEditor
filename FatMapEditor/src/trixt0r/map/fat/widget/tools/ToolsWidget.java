package trixt0r.map.fat.widget.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;

import trixt0r.map.fat.widget.FatWidget;

public class ToolsWidget extends FatWidget implements Disposable{
	
	public final ImageButton creationTool, selectionTool, translationTool, scalingTool, rotationTool, erasingTool, flipHorTool, flipVerTool;
	
	private Texture creationIcon, selectionIcon, translationIcon, scalingIcon, roationIcon, erasingIcon, flipHorIcon, flipVerIcon;

	public ToolsWidget(Stage stage, Skin skin) {
		super(stage, skin);
		this.window.setClip(true);
		this.window.setMovable(true);
		this.window.setTitleAlignment(Align.left);
		this.window.setTitle("Tools");
		
		creationIcon = new Texture(Gdx.files.internal("data/tool_icons/create.png"));
		TextureRegion createImage = new TextureRegion(creationIcon);
		
		selectionIcon = new Texture(Gdx.files.internal("data/tool_icons/select.png"));
		TextureRegion selectImage = new TextureRegion(selectionIcon);
		
		translationIcon = new Texture(Gdx.files.internal("data/tool_icons/translate.png"));
		TextureRegion translationImage = new TextureRegion(translationIcon);
		
		scalingIcon = new Texture(Gdx.files.internal("data/tool_icons/scale.png"));
		TextureRegion scalingImage = new TextureRegion(scalingIcon);
		
		roationIcon = new Texture(Gdx.files.internal("data/tool_icons/rotate.png"));
		TextureRegion rotationImage = new TextureRegion(roationIcon);
		
		erasingIcon = new Texture(Gdx.files.internal("data/tool_icons/erase.png"));
		TextureRegion erasingImage = new TextureRegion(erasingIcon);
		
		flipHorIcon = new Texture(Gdx.files.internal("data/tool_icons/flipHorizontal.png"));
		TextureRegion flipHorImage = new TextureRegion(flipHorIcon);
		
		flipVerIcon = new Texture(Gdx.files.internal("data/tool_icons/flipVertical.png"));
		TextureRegion flipVerImage = new TextureRegion(flipVerIcon);
		
		ImageButtonStyle creationStyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
		creationStyle.imageUp = new TextureRegionDrawable(createImage);
		
		ImageButtonStyle selectionStyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
		selectionStyle.imageUp = new TextureRegionDrawable(selectImage);
		
		ImageButtonStyle translationStyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
		translationStyle.imageUp = new TextureRegionDrawable(translationImage);
		
		ImageButtonStyle scalingStyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
		scalingStyle.imageUp = new TextureRegionDrawable(scalingImage);
		
		ImageButtonStyle rotationStyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
		rotationStyle.imageUp = new TextureRegionDrawable(rotationImage);
		
		ImageButtonStyle erasingStyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
		erasingStyle.imageUp = new TextureRegionDrawable(erasingImage);
		
		ImageButtonStyle flipHorStyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
		flipHorStyle.imageUp = new TextureRegionDrawable(flipHorImage);
		
		ImageButtonStyle flipVerStyle = new ImageButtonStyle(skin.get(ButtonStyle.class));
		flipVerStyle.imageUp = new TextureRegionDrawable(flipVerImage);
		
		this.creationTool = new ImageButton(creationStyle);
		this.selectionTool = new ImageButton(selectionStyle);
		this.translationTool = new ImageButton(translationStyle);
		this.scalingTool = new ImageButton(scalingStyle);
		this.rotationTool = new ImageButton(rotationStyle);
		this.erasingTool = new ImageButton(erasingStyle);
		this.flipHorTool = new ImageButton(flipHorStyle);
		this.flipVerTool = new ImageButton(flipVerStyle);
		
		this.window.setSize(creationTool.getWidth()*2+15, creationTool.getHeight()*5+10);
		
		this.table.addActor(creationTool);
		this.table.addActor(selectionTool);
		this.table.addActor(translationTool);
		this.table.addActor(scalingTool);
		this.table.addActor(rotationTool);
		this.table.addActor(erasingTool);
		this.table.addActor(flipHorTool);
		this.table.addActor(flipVerTool);
		
		
		this.table.align(Align.left|Align.top);
		
		this.layout();
		
	}

	@Override
	public void layout() {
		scrollPane.setBounds(0, 0, window.getWidth(), window.getHeight()-20);
		table.setBounds(0, 0, scrollPane.getWidth(), scrollPane.getHeight());
		
		this.creationTool.setPosition(5, this.table.getHeight()-this.creationTool.getHeight()-5);		
		this.erasingTool.setPosition(this.creationTool.getX()+this.creationIcon.getWidth()+12, this.creationTool.getY());
		
		this.selectionTool.setPosition(this.creationTool.getX(), this.creationTool.getY()-this.creationTool.getHeight()-5);		
		this.translationTool.setPosition(this.erasingTool.getX(), this.creationTool.getY()-this.creationTool.getHeight()-5);
		
		this.scalingTool.setPosition(this.selectionTool.getX(), this.selectionTool.getY()-this.selectionTool.getHeight()-5);		
		this.rotationTool.setPosition(this.translationTool.getX(), this.selectionTool.getY()-this.selectionTool.getHeight()-5);
		
		this.flipHorTool.setPosition(this.scalingTool.getX(), this.scalingTool.getY()-this.scalingTool.getHeight()-5);		
		this.flipVerTool.setPosition(this.rotationTool.getX(), this.scalingTool.getY()-this.scalingTool.getHeight()-5);
	}
	
	public void act(float delta){
		super.act(delta);
		if(Gdx.input.isKeyPressed(Keys.SPACE)) this.table.setY(this.table.getY()-5);
		if(this.window.getX()+this.window.getWidth() > this.stage.getWidth()) this.window.setX(this.stage.getWidth()-this.window.getWidth());
		if(this.window.getX() < 0) this.window.setX(0f);
		if(this.window.getY()+this.window.getHeight() > this.stage.getHeight()) this.window.setY(this.stage.getHeight()-this.window.getHeight());
		if(this.window.getY() < 0) this.window.setY(0f);
	}

	@Override
	public void dispose() {
		this.creationIcon.dispose();
		this.selectionIcon.dispose();
		this.erasingIcon.dispose();
		this.roationIcon.dispose();
		this.translationIcon.dispose();
		this.scalingIcon.dispose();
		this.flipHorIcon.dispose();
		this.flipVerIcon.dispose();
	}

}
