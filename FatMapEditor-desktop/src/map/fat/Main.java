package map.fat;

//import java.lang.reflect.Constructor;

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
		
		/*try {
			Class<?> c = Class.forName("com.badlogic.gdx.backends.lwjgl.LwjglApplication");
			try {
				Constructor<?> cons = c.getConstructor(com.badlogic.gdx.ApplicationListener.class, com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration.class);
				cons.newInstance(editor, cfg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}*/
		
		new LwjglApplication(new FatMapEditor(), cfg);
	}
}
