package map.fat;


import trixt0r.map.fat.FatMapEditor;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	
	public static LwjglApplication app;
	public final static LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
	static{
		cfg.title = "FatMapEditor";
		cfg.useGL20 = true;
		cfg.width = 1280;
		cfg.height = 720;
	}
	
	public static void main(String[] args) {
		/*LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "FatMapEditor";
		cfg.useGL20 = true;
		cfg.width = 1280;
		cfg.height = 720;*/
		
		app = new LwjglApplication(new FatMapEditor(), cfg);
	}
	
	/*public static void resize(int width, int height){
		Display.set
		Display.setLocation(new_x, new_y)
	}*/
}
