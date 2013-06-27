package trixt0r.map.fat.transform;

import trixt0r.map.fat.FatTransformer;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class TransformBox extends SelectionBox {
	
	public final TransformerPoint topRight, topLeft, bottomRight, bottomLeft, top, right, bottom, left, rotation;
	private final TransformerPoint[] points;
	private final Vector2 temp = new Vector2();
	private boolean initialized;
	
	public TransformBox(){
		super();
		topRight = new TransformerPoint(super.topRight,0,0,10,10);
		topLeft = new TransformerPoint(super.topLeft,0,0,10,10);
		bottomRight = new TransformerPoint(super.bottomRight,0,0,10,10);
		bottomLeft = new TransformerPoint(super.bottomLeft,0,0,10,10);
		top = new TransformerPoint(null, 0,0,10,10);
		right = new TransformerPoint(null, 0,0,10,10);
		bottom = new TransformerPoint(null, 0,0,10,10);
		left = new TransformerPoint(null, 0,0,10,10);
		rotation = new TransformerPoint(null, 0,0,10,10);
		this.points = new TransformerPoint[]{topRight, topLeft, bottomRight, bottomLeft, top, right, bottom, left, rotation};
		initialized = true;
	}
	
	@Override
	public void update(){
		super.update();
		if(this.initialized) this.updateTransformPoints();
	}
	
	public void updateTransformPoints(){
		if(!this.topRight.isClicked()) this.topRight.bbox.x = super.topRight.x - this.topRight.bbox.width/2; this.topRight.bbox.y = super.topRight.y - this.topRight.bbox.height/2;
		if(!this.topLeft.isClicked()) this.topLeft.bbox.x = super.topLeft.x- this.topLeft.bbox.width/2; this.topLeft.bbox.y = super.topLeft.y- this.topLeft.bbox.height/2;
		if(!this.bottomRight.isClicked()) this.bottomRight.bbox.x = super.bottomRight.x - this.bottomRight.bbox.width/2; this.bottomRight.bbox.y = super.bottomRight.y - this.topRight.bbox.height/2;
		if(!this.bottomLeft.isClicked()) this.bottomLeft.bbox.x = super.bottomLeft.x- this.bottomLeft.bbox.width/2; this.bottomLeft.bbox.y = super.bottomLeft.y- this.bottomLeft.bbox.height/2;
		
		this.updateMiddlePoints();
		this.updateRotationPoint();
	}
	
	public void applyTransformPoints(){
		if(!this.initialized) return;
		if(this.topRight.isClicked())
			this.performTransform(this.topRight, this.topLeft, this.bottomRight, 1,1,180,270);
		else if(this.topLeft.isClicked())
			this.performTransform(this.topLeft, this.topRight, this.bottomLeft, -1, 1, 0, 270);
		else if(this.bottomRight.isClicked())
			this.performTransform(this.bottomRight, this.bottomLeft, this.topRight, 1, -1, 180, 90);
		else if(this.bottomLeft.isClicked())
			this.performTransform(this.bottomLeft, this.bottomRight, this.topLeft, -1, -1, 0, 90);
		else if(this.right.isClicked())
			this.performMiddleTransform(this.right, this.left, this.topRight, this.bottomRight, 1, 90, 270, true, 0);
		else if(this.left.isClicked())
			this.performMiddleTransform(this.left, this.right, this.topLeft, this.bottomLeft, -1, 90, 270, true, 180);
		else if(this.top.isClicked())
			this.performMiddleTransform(this.top, this.bottom, this.topRight, this.topLeft, 1, 0, 180, false, 90);
		else if(this.bottom.isClicked())
			this.performMiddleTransform(this.bottom, this.top, this.bottomRight, this.bottomLeft, -1, 0, 180, false, 270);
		
		this.updateMiddlePoints();
		this.x = (this.topRight.bbox.x + this.topLeft.bbox.x + this.bottomLeft.bbox.x + this.bottomRight.bbox.x + 20)/4;
		this.y = (this.topRight.bbox.y + this.topLeft.bbox.y + this.bottomLeft.bbox.y + this.bottomRight.bbox.y + 20)/4;
		this.updateRotationPoint();
		super.update();
		if(!this.rotation.isClicked()) this.updateCenter();
	}
	
	@Override
	public void draw(ShapeRenderer renderer){
		super.draw(renderer);
		if(this.height == 0 || this.width == 0) return;
		if(renderer.getCurrentType() == ShapeRenderer.ShapeType.Line){
			renderer.line(this.right.bbox.x+this.right.bbox.width/2, this.right.bbox.y+this.right.bbox.height/2,
					this.rotation.bbox.x+this.rotation.bbox.width/2, this.rotation.bbox.y+this.rotation.bbox.height/2);
			rotation.draw(renderer);
		}
		if(renderer.getCurrentType() != ShapeRenderer.ShapeType.Filled) return;
		topRight.draw(renderer);
		topLeft.draw(renderer);
		bottomRight.draw(renderer);
		bottomLeft.draw(renderer);
		top.draw(renderer);
		right.draw(renderer);
		bottom.draw(renderer);
		left.draw(renderer);
	}
	
	public TransformerPoint getNearestPoint(float x, float y){
		for(int i = 0; i< this.points.length; i++)
			if(this.points[i].inside(x, y)) return this.points[i];
		return null;
	}
	
	public void updateMiddlePoints(){		
		this.top.bbox.x = (this.topRight.bbox.x+this.topLeft.bbox.x)/2;
		this.top.bbox.y = (this.topRight.bbox.y+this.topLeft.bbox.y)/2;
		
		this.bottom.bbox.x = (this.bottomRight.bbox.x+this.bottomLeft.bbox.x)/2;
		this.bottom.bbox.y = (this.bottomRight.bbox.y+this.bottomLeft.bbox.y)/2;
		
		this.right.bbox.x = (this.bottomRight.bbox.x+this.topRight.bbox.x)/2;
		this.right.bbox.y = (this.bottomRight.bbox.y+this.topRight.bbox.y)/2;
		
		this.left.bbox.x = (this.bottomLeft.bbox.x+this.topLeft.bbox.x)/2;
		this.left.bbox.y = (this.bottomLeft.bbox.y+this.topLeft.bbox.y)/2;
	}
	
	private void performTransform(TransformerPoint clicked, TransformerPoint oppositeHor, TransformerPoint oppositeVer, int reverseHor, int reverseVer, float angleHor, float angleVer){
		temp.set(FatTransformer.getRelativePoint(this.x, this.y, this.angle, clicked.bbox.x+clicked.bbox.width/2, clicked.bbox.y+clicked.bbox.height/2));
		float clickedX = temp.x, clickedY = temp.y;
		temp.set(FatTransformer.getRelativePoint(this.x, this.y, this.angle, oppositeHor.attachedPoint.x, oppositeHor.attachedPoint.y));
		float followerX = temp.x;
		temp.set(FatTransformer.getRelativePoint(this.x, this.y, this.angle, oppositeVer.attachedPoint.x, oppositeVer.attachedPoint.y));
		float followerY = temp.y;
		
		oppositeHor.bbox.x = reverseHor*MathUtils.cosDeg(this.angle+angleHor)*(clickedX-followerX)+clicked.bbox.x;
		oppositeHor.bbox.y = reverseHor*MathUtils.sinDeg(this.angle+angleHor)*(clickedX-followerX)+clicked.bbox.y;
		oppositeVer.bbox.x = reverseVer*MathUtils.cosDeg(this.angle+angleVer)*(clickedY-followerY)+clicked.bbox.x;
		oppositeVer.bbox.y = reverseVer*MathUtils.sinDeg(this.angle+angleVer)*(clickedY-followerY)+clicked.bbox.y;
		
		this.scaleX = (reverseHor*(clickedX-followerX)/this.tempWidth);
		this.scaleY = (reverseVer*(clickedY-followerY)/this.tempHeight);
	}
	
	private void performMiddleTransform(TransformerPoint clicked, TransformerPoint opposite, TransformerPoint first, TransformerPoint second,
			int reverseDir, float angleToFirst, float angleToSecond, boolean horizontal, float angle){
		temp.set(FatTransformer.getRelativePoint(this.x, this.y, this.angle, clicked.bbox.x,clicked.bbox.y));
		float clickedCoord = (horizontal) ? temp.x: temp.y;
		temp.set(FatTransformer.getRelativePoint(this.x, this.y, this.angle, opposite.bbox.x, opposite.bbox.y));
		float opCoord = (horizontal) ? temp.x: temp.y;
		float length;
		if(horizontal){
			this.scaleX = reverseDir*(clickedCoord-opCoord)/this.tempWidth;
			length = (this.scaleX*this.width)/2;
		}
		else{
			this.scaleY = reverseDir*(clickedCoord-opCoord)/this.tempHeight;
			length = (this.scaleY*this.height)/2;
		}
		clicked.bbox.x = MathUtils.cosDeg(this.angle+angle)*length + this.x - clicked.bbox.width/2;
		clicked.bbox.y = MathUtils.sinDeg(this.angle+angle)*length + this.y - clicked.bbox.height/2;
		
		length = (!horizontal) ? (this.scaleX*this.width)/2: (this.scaleY*this.height)/2;
		
		first.bbox.x = MathUtils.cosDeg(this.angle+angleToFirst) * length + clicked.bbox.x;
		first.bbox.y = MathUtils.sinDeg(this.angle+angleToFirst) * length + clicked.bbox.y;
		second.bbox.x = MathUtils.cosDeg(this.angle+angleToSecond) * length + clicked.bbox.x;
		second.bbox.y = MathUtils.sinDeg(this.angle+angleToSecond) * length + clicked.bbox.y;
	}
	
	private void updateRotationPoint(){
		this.rotation.bbox.x = this.right.bbox.x + MathUtils.cosDeg(this.angle)*50*this.scaleX;
		this.rotation.bbox.y = this.right.bbox.y + MathUtils.sinDeg(this.angle)*50*this.scaleX;
	}

}
