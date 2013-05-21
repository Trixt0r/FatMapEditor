package normal.lighting;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.Vector3;

public class TextureConverter {
	
	private final static float GAUS_FIRST = 0.0162162162f, GAUS_SECOND = 0.0540540541f, GAUS_THIRD = 0.1216216216f, GAUS_FOURTH = 0.1945945946f, GAUS_MID = 0.2270270270f;
	
	public static Pixmap getHeightMap(Pixmap map){
		Pixmap gray = new Pixmap(map.getWidth(), map.getHeight(), Format.RGBA8888);
		for(int x = 0; x < map.getWidth(); x++){
			for(int y = 0; y < map.getHeight(); y++){
				Color color = new Color();
				Color.rgba8888ToColor(color, map.getPixel(x, y));
				float r = color.r;
				float g = color.g;
				float b = color.b;
				float a = color.a;
				float avg = (r+g+b)/3;
				gray.setColor(avg,avg,avg,a);
				gray.drawPixel(x, y);
			}
		}
		return gray;
	}
	
	public static Pixmap getFilledWhiteMap(Pixmap map){
		Pixmap white = new Pixmap(map.getWidth(), map.getHeight(), Format.RGBA8888);
		for(int x = 0; x < map.getWidth(); x++){
			for(int y = 0; y < map.getHeight(); y++){
				Color color = new Color();
				Color.rgba8888ToColor(color, map.getPixel(x, y));
				if(color.a < 1f) white.setColor(0f, 0f, 0f, 0f);
				else white.setColor(1,1,1,1);
				white.drawPixel(x, y);
			}
		}
		return white;
	}

	
	public static Pixmap getBluredMap(Pixmap map, float blur, int times){
		if(times <= 0) return null;
		Pixmap[] maps = new Pixmap[times];
		maps[0] = new Pixmap(map.getWidth(), map.getHeight(), Format.RGBA8888);
		maps[0].drawPixmap(map, 0, 0);
		for(int i = 1; i < maps.length; i++)
			maps[i] = getBluredMap(maps[i-1], blur);
		for(int i = 1; i < maps.length-1; i++)
			maps[i].dispose();
		return maps[maps.length-1];
	}
	
	public static Pixmap getBluredMap(Pixmap map, float blur){
		Pixmap buffer = applyBlurFrom(map, 1, 0, blur);
		Pixmap newMap = applyBlurFrom(buffer, 0, 1, blur);
		buffer.dispose();
		return newMap;
	}
	
	private static Pixmap applyBlurFrom(Pixmap from, int hstep, int vstep, float blur){
		Pixmap buffer = new Pixmap(from.getWidth(), from.getHeight(), Format.RGBA8888);
		buffer.setColor(0,0,0,0);
		buffer.fill();
		Color[] colors = new Color[9];
		for(int x = 0; x < from.getWidth(); x++){
			for(int y = 0; y < from.getHeight(); y++){
				float a = getColor(getPixel(from, x,y)).a;
				int i = 4;
				colors[4-i] = getColor(getPixel(from, x-(int)(i*blur*hstep), y-(int)(i*blur*vstep)));
				colors[4-i].mul(GAUS_FIRST); i--;
				colors[4-i] = getColor(getPixel(from, x-(int)(i*blur*hstep), y-(int)(i*blur*vstep)));
				colors[4-i].mul(GAUS_SECOND);i--;
				colors[4-i] = getColor(getPixel(from, x-(int)(i*blur*hstep), y-(int)(i*blur*vstep)));
				colors[4-i].mul(GAUS_THIRD);i--;
				colors[4-i] = getColor(getPixel(from, x-(int)(i*blur*hstep), y-(int)(i*blur*vstep)));
				colors[4-i].mul(GAUS_FOURTH);i--;
				
				colors[4] = getColor((from.getPixel(x, y)));
				colors[4].mul(GAUS_MID);
				i = 4;
				colors[5+4-i] = getColor(getPixel(from, x+(int)(i*blur*hstep), y+(int)(i*blur*vstep)));
				colors[5+4-i].mul(GAUS_FIRST); i--;
				colors[5+4-i] = getColor(getPixel(from, x+(int)(i*blur*hstep), y+(int)(i*blur*vstep)));
				colors[5+4-i].mul(GAUS_SECOND);i--;
				colors[5+4-i] = getColor(getPixel(from, x+(int)(i*blur*hstep), y+(int)(i*blur*vstep)));
				colors[5+4-i].mul(GAUS_THIRD);i--;
				colors[5+4-i] = getColor(getPixel(from, x+(int)(i*blur*hstep), y+(int)(i*blur*vstep)));
				colors[5+4-i].mul(GAUS_FOURTH);
				
				Color color = new Color(0,0,0,0);
				for(i = 0; i < colors.length; i++)
					color.add(colors[i]);
				color.a = a;
				buffer.setColor(color);
				buffer.drawPixel(x, y);
			}
		}
		return buffer;
	}
	
	public static Pixmap getNormalPixmap(Pixmap map){
		Pixmap normal = new Pixmap(map.getWidth(), map.getHeight(), Format.RGBA8888);
		for(int x = 0; x < map.getWidth(); x++){
			for(int y = 0; y < map.getHeight(); y++){
				int rgba = map.getPixel(x, y);
				Color color = new Color();
				Color.rgba8888ToColor(color, rgba);
				Color colorRight = new Color() , colorDown = new Color();
				if(x+1 < map.getWidth()) Color.rgba8888ToColor(colorRight, map.getPixel(x+1, y));
				else Color.rgba8888ToColor(colorRight, rgba);
				if(y+1 < map.getHeight()) Color.rgba8888ToColor(colorDown, map.getPixel(x, y+1));
				else Color.rgba8888ToColor(colorDown, rgba);
				
				Vector3 v1 = new Vector3(x,y,rgba);
				Vector3 v2 = new Vector3(x+1,y,Color.rgba8888(colorRight));
				Vector3 v3 = new Vector3(x,y+1,Color.rgba8888(colorDown));
				
				v2.sub(v1);
				v3.sub(v1);
				Vector3 diff1 = new Vector3(v2);
				Vector3 diff2 = new Vector3(v3);
				Vector3 n = diff1.crs(diff2);
				n.nor();
				
				//Convert into rgb values
				n.add(1f);
				n.mul(0.5f);
				
				//if(color.a == 0f) n.set(0.0f,0.0f,0f);
				
				normal.setColor(n.x,n.y,n.z,color.a);
				normal.drawPixel(x, y);
			}
		}
		return normal;
	}
	
	
	
	public static void savePixMap(Pixmap map, String filename){
		PixmapIO.writePNG(Gdx.files.absolute(filename), map);
	}
	
	private static Color getColor(int rgba){
		Color color = new Color();
		Color.rgba8888ToColor(color, rgba);
		return color;
	}
	
	private static int getPixel(Pixmap source, int x, int y){
		int xx = x, yy = y;
		if(xx >= source.getWidth()) xx = source.getWidth()-1;
		if(xx < 0) xx = 0;
		if(yy >= source.getHeight()) yy = source.getHeight()-1;
		if(yy < 0) yy = 0;
		return source.getPixel(xx, yy);
	}

}
