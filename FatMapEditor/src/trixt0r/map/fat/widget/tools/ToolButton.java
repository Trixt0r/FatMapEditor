package trixt0r.map.fat.widget.tools;

import trixt0r.map.fat.FatTransformer;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

public class ToolButton extends ImageButton {
	
	public final FatTransformer.TOOL tool;

	public ToolButton(ImageButtonStyle style, FatTransformer.TOOL tool) {
		super(style);
		this.tool = tool;
	}

}
