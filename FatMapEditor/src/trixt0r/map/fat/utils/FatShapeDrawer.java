package trixt0r.map.fat.utils;

import trixt0r.map.fat.transform.FatBox;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;

public class FatShapeDrawer {
	
	private static float cos, sin, xTopRight, yTopRight, xBottomRight, yBottomRight, xBottomLeft, yBottomLeft, xTopLeft, yTopeft;

	public static void drawRectangle(ShapeRenderer renderer, Rectangle rect){
		renderer.rect(rect.x, rect.y, rect.width, rect.height);
	}
	
	public static void drawEllipse(ShapeRenderer renderer, Ellipse ellipse){
		renderer.ellipse(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
	}
	
	public static void drawPolygon(ShapeRenderer renderer, Polygon poly){
		renderer.polygon(poly.getTransformedVertices());
	}
	
	public static void drawPolyline(ShapeRenderer renderer, Polyline poly){
		renderer.polyline(poly.getTransformedVertices());
	}
	
	public static void drawBox(ShapeRenderer renderer, float x, float y, float width, float height, float angle){
		cos = MathUtils.cosDeg(angle);
		sin = MathUtils.sinDeg(angle);
		xTopRight = (width/2) * cos - (height/2) * sin;
		yTopRight = (width/2) * sin + (height/2) * cos;
		xBottomRight = (width/2) * cos - (-height/2) * sin;
		yBottomRight = (width/2) * sin + (-height/2) * cos;
		xBottomLeft = (-width/2) * cos - (-height/2) * sin;
		yBottomLeft = (-width/2) * sin + (-height/2) * cos;
		xTopLeft = (-width/2) * cos - (height/2) * sin;
		yTopeft = (-width/2) * sin + (height/2) * cos;
		
		renderer.line(x + xTopRight, y+yTopRight, x + xBottomRight, y+yBottomRight);
		renderer.line(x + xBottomRight, y+yBottomRight, x + xBottomLeft, y+yBottomLeft);
		renderer.line(x + xBottomLeft, y+yBottomLeft, x + xTopLeft, y+yTopeft);
		renderer.line(x + xTopLeft, y+yTopeft, x + xTopRight, y+yTopRight);
	}
	
	public static void drawBox(ShapeRenderer renderer, FatBox box){
		if(renderer.getCurrentType() != ShapeRenderer.ShapeType.Line) return;
		renderer.line(box.topRight.x, box.topRight.y, box.bottomRight.x, box.bottomRight.y);
		renderer.line(box.bottomRight.x, box.bottomRight.y, box.bottomLeft.x, box.bottomLeft.y);
		renderer.line(box.bottomLeft.x, box.bottomLeft.y, box.topLeft.x, box.topLeft.y);
		renderer.line(box.topLeft.x, box.topLeft.y, box.topRight.x, box.topRight.y);
		
		renderer.line(box.center.x-5, box.center.y, box.center.x+5, box.center.y);
		renderer.line(box.center.x, box.center.y-5, box.center.x, box.center.y+5);
	}
	
}
