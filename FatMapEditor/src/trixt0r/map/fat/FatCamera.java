package trixt0r.map.fat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * A camera which follows a given FatWorldObject with various interpolation techniques. 
 * See {@link #com.badlogic.gdx.math.Interpolation} for more information.
 * @author Trixt0r
 *
 */
public class FatCamera extends OrthographicCamera{

	private Actor toFollow;
	private Vector2 tempPosition;
	private Interpolation interpolationX = Interpolation.linear, interpolationY = Interpolation.linear, interpolationZoom = Interpolation.linear;
	private float alphaX = 0f, alphaY = 0f, x = 0f,y = 0f, followSpeedX, followSpeedY, zoomNear, zoomFar, alphaZoomFar = 0f, alphaZoomNear = 0f, zoomSpeed = 0.005f, tempZoom = 1f;
	private int zoomThreshold = 0, zoomCount = 0;
	private boolean zoomOut = false;
	public boolean followMouse;
	public static float MAX_ZOOM = 0.0625f, MIN_ZOOM = 10f;
	
	/**
	 * @param toFollow instance which this camera has to follow
	 * @param followSpeedX indicates the horizontal following speed, between 0 and 1
	 * @param followSpeedY indicates the vertical following speed, between 0 and 1
	 */
	public FatCamera(Actor toFollow, float followSpeedX, float followSpeedY){
		this(toFollow);
		this.followSpeedX = followSpeedX;
		this.followSpeedY = followSpeedY;
	}
	
	public FatCamera(Actor toFollow){
		this();
		this.toFollow = toFollow;
	}
	
	public FatCamera(){
		this.tempPosition = new Vector2();
		this.zoomNear = 1f;
		this.zoomFar = 2f;
	}
	
	public FatCamera(float followSpeedX, float followSpeedY){
		this.tempPosition = new Vector2();
		this.followSpeedX = followSpeedX;
		this.followSpeedY = followSpeedY;
		this.zoomNear = 1f;
		this.zoomFar = 2f;
	}
	
	/**
	 * Updates the position of this camera with respect to the given FatWorldObject.
	 */
	@Override
	public void update(boolean updateFrustum){
		if(this.tempPosition.x != this.toFollow.getX()){
			
			this.alphaX = this.followSpeedX;
			this.tempPosition.x = this.toFollow.getX();
			this.x = this.position.x;
		}
		else if(alphaX < 1)	alphaX += this.followSpeedX; 
		if(this.tempPosition.y != this.toFollow.getY()){
			
			this.alphaY = this.followSpeedY;
			this.tempPosition.y = this.toFollow.getY();
			this.y = this.position.y;
		}
		else if(alphaY < 1)	alphaY += this.followSpeedY;
		this.move();
		if(this.alphaZoomFar < 1 && this.zoomOut && this.zoomCount == this.zoomThreshold) this.alphaZoomFar = Math.min(this.alphaZoomFar+this.zoomSpeed,1f);
		if(this.alphaZoomNear < 1 && !this.zoomOut && this.zoomCount == this.zoomThreshold)	this.alphaZoomNear = Math.min(this.alphaZoomNear+this.zoomSpeed,1f);
		
		if(this.followMouse && !zoomOut){
			this.setFollowSpeed(0.1f, 0.1f);
			Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
			this.unproject(v);
			this.toFollow.setPosition(v.x, v.y);
		}
		this.followMouse = !(this.zoom == this.zoomFar || this.zoom == this.zoomNear);
		
		this.zoom();
		super.update(updateFrustum);
		if(this.zoomCount < this.zoomThreshold) this.zoomCount++;
	}
	
	/**
	 * @param toFollow set the object which the camera has follow to.
	 */
	public void setToFollow(Actor toFollow){
		this.toFollow = toFollow;
		this.tempPosition = new Vector2(toFollow.getX(),toFollow.getY());
	}
	
	/**
	 * @param interpolation set the interpolation technique in hor. direction. See {@link #com.badlogic.gdx.math.Interpolation} for more information.
	 */
	public void setInterpolationX(Interpolation interpolation){
		this.interpolationX = interpolation;
	}
	
	/**
	 * @param interpolation et the interpolation technique in vert. direction. See {@link #com.badlogic.gdx.math.Interpolation} for more information.
	 */
	public void setInterpolationY(Interpolation interpolation){
		this.interpolationY = interpolation;
	}
	
	public Interpolation getInterpolationX(){
		return this.interpolationX;
	}
	
	public Interpolation getInterpolationY(){
		return this.interpolationY;
	}
	
	private void move(){
		if(this.toFollow == null) return;
		this.position.x = this.interpolationX.apply(x, this.toFollow.getX(), this.alphaX);
		this.position.y = this.interpolationY.apply(y, this.toFollow.getY(), this.alphaY);
	}
	
	private void zoom(){
		if(this.zoomOut) this.zoom = this.interpolationZoom.apply(this.tempZoom, this.zoomFar, this.alphaZoomFar);
		else this.zoom = this.interpolationZoom.apply(this.tempZoom, this.zoomNear, this.alphaZoomNear);
	}
	
	/**
	 * @param hSpeed horizontal interpolation speed.
	 * @param vSpeed vertical interpolation speed.
	 */
	public void setFollowSpeed(float hSpeed, float vSpeed){
		this.followSpeedX = hSpeed;
		this.followSpeedY = vSpeed;
	}

	/**
	 * @return the followSpeedX
	 */
	public float getFollowSpeedX() {
		return followSpeedX;
	}

	/**
	 * @param followSpeedX the followSpeedX to set, between 0 and 1
	 */
	public void setFollowSpeedX(float followSpeedX) {
		this.followSpeedX = followSpeedX;
	}

	/**
	 * @return the followSpeedY
	 */
	public float getFollowSpeedY() {
		return followSpeedY;
	}

	/**
	 * @param followSpeedY the followSpeedY to set, between 0 and 1
	 */
	public void setFollowSpeedY(float followSpeedY) {
		this.followSpeedY = followSpeedY;
	}

	/**
	 * @return the zoomNear
	 */
	public float getZoomNear() {
		return zoomNear;
	}

	/**
	 * @param zoomNear the zoomNear to set
	 */
	public void setZoomNear(float zoomNear) {
		this.zoomNear = zoomNear;
		this.zoomNear = Math.max(MAX_ZOOM, this.zoomNear);
		this.zoomNear = Math.min(MIN_ZOOM, this.zoomNear);
	}

	/**
	 * @return the zoomFar
	 */
	public float getZoomFar() {
		return zoomFar;
	}

	/**
	 * @param zoomFar the zoomFar to set
	 */
	public void setZoomFar(float zoomFar) {
		this.zoomFar = zoomFar;
		this.zoomFar = Math.max(MAX_ZOOM, this.zoomFar);
		this.zoomFar = Math.min(MIN_ZOOM, this.zoomFar);
	}

	/**
	 * @return the zoomSpeed
	 */
	public float getZoomSpeed() {
		return zoomSpeed;
	}

	/**
	 * @param zoomSpeed the zoomSpeed to set
	 */
	public void setZoomSpeed(float zoomSpeed) {
		this.zoomSpeed = zoomSpeed;
	}

	/**
	 * @return the zoomThreshold
	 */
	public int getZoomThreshold() {
		return zoomThreshold;
	}

	/**
	 * @param zoomThreshold the zoomThreshold to set
	 */
	public void setZoomThreshold(int zoomThreshold) {
		this.zoomThreshold = zoomThreshold;
	}

	/**
	 * @return the zoomOut
	 */
	public boolean isZoomOut() {
		return zoomOut;
	}

	/**
	 * @param zoomOut the zoomOut to set
	 */
	public void setZoomOut(boolean zoomOut) {
		this.zoomOut = zoomOut;
	}

	/**
	 * @return the interpolationZoom
	 */
	public Interpolation getInterpolationZoom() {
		return interpolationZoom;
	}

	/**
	 * @param interpolationZoom the interpolationZoom to set
	 */
	public void setInterpolationZoom(Interpolation interpolationZoom) {
		this.interpolationZoom = interpolationZoom;
	}
	
	public void resetZoom(){
		this.alphaZoomFar = 0f;
		this.alphaZoomNear = 0f;
		this.tempZoom = this.zoom;
	}
}
