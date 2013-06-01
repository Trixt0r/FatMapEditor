package map.fat;

import trixt0r.map.fat.FatMapEditor;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "FatMapEditor";
		cfg.useGL20 = true;
		cfg.width = 1280;
		cfg.height = 720;
		//cfg.resizable = false;

		//System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
		
		new LwjglApplication(new FatMapEditor(), cfg);
	}
}
