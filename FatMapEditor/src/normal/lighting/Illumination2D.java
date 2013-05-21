package normal.lighting;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Random;
 
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
 
/**
 * Simple illumination model with shaders in LibGDX.
 * @author davedes
 */
public class Illumination2D implements ApplicationListener {
 
        Texture texture, texture_n;
       
        boolean flipY;
        Texture normalBase;
        OrthographicCamera cam;
        SpriteBatch fxBatch, batch, normalBatch;
        
        float angle = 0;
       
        Matrix4 transform = new Matrix4();
 
        Random rnd = new Random();
 
        // position of our light
        final Vector3 DEFAULT_LIGHT_POS = new Vector3(0f, 0f, 0.00f);
        // the color of our light
        final Vector3 DEFAULT_LIGHT_COLOR = new Vector3(1f, 0.7f, 0.6f);
        // the ambient color (color to use when unlit)
        final Vector3 DEFAULT_AMBIENT_COLOR = new Vector3(1f, 1f, 1f);
        // the attenuation factor: x=constant, y=linear, z=quadratic
        final Vector3 DEFAULT_ATTENUATION = new Vector3(0.4f, 3f, 20f);
        // the ambient intensity (brightness to use when unlit)
        final float DEFAULT_AMBIENT_INTENSITY = 1f;
        final float DEFAULT_STRENGTH = 1f;
       
        final Color NORMAL_VCOLOR = new Color(1f,1f,1f,DEFAULT_STRENGTH);
       
        // the position of our light in 3D space
        Vector3[] lightPos = {new Vector3(DEFAULT_LIGHT_POS)};
        Vector3[] lightCol = {new Vector3(DEFAULT_LIGHT_COLOR)};
        // the resolution of our game/graphics
        Vector2 resolution = new Vector2();
        // the current attenuation
        Vector3 attenuation = new Vector3(DEFAULT_ATTENUATION);
        // the current ambient intensity
        float ambientIntensity = DEFAULT_AMBIENT_INTENSITY;
        float strength = DEFAULT_STRENGTH;
       
        // whether to use attenuation/shadows
        boolean useShadow = true;
 
        // whether to use lambert shading (with our normal map)
        boolean useNormals = true;
       
        DecimalFormat DEC_FMT = new DecimalFormat("0.00000");
 
        ShaderProgram programLighting, programNormalFix;
 
        BitmapFont font;
       
        private int texWidth, texHeight;
        
        TextureRegion reg, reg2, normalReg, normalReg2, colorRegion;
        
        FrameBuffer normalBuffer, colorBuffer;
       
        final String TEXT = "Use number keys to adjust parameters:\n" +
                        "1: Randomize Ambient Color\n" +
                        "2: Randomize Ambient Intensity {0}\n" +
                        "3: Randomize Light Color\n" +
                        "4/5: Increase/decrease constant attenuation: {1}\n" +
                        "6/7: Increase/decrease linear attenuation: {2}\n" +
                        "8/9: Increase/decrease quadratic attenuation: {3}\n" +
                        "0: Reset parameters\n" +
                        "RIGHT/LEFT: Increase/decrease normal map intensity: {4}\n" +
                        "UP/DOWN: Increase/decrease lightDir.z: {5}\n\n" +
                        "S toggles attenuation, N toggles normal shading\n" +
                        "T to toggle textures";
       
        private Texture joe, joe_n, teapot, teapot_n;
       
        public void create() {
            // load our textures
            teapot = new Texture(Gdx.files.internal("data/teapot.png"));
            teapot_n = new Texture(Gdx.files.internal("data/teapot_n.png"));
            joe = new Texture(Gdx.files.internal("data/joe.png"));
            joe_n = new Texture(Gdx.files.internal("data/joe_normal.png"));
            
            Pixmap base = new Pixmap(Gdx.files.internal("data/joe.png"));
            Pixmap white = TextureConverter.getFilledWhiteMap(base);
            Pixmap blur = TextureConverter.getBluredMap(white, 0.5f, 3);
            Pixmap normalMap = TextureConverter.getNormalPixmap(blur);
            blur.dispose();
            blur = TextureConverter.getBluredMap(normalMap, 1f, 3);
            TextureConverter.savePixMap(normalMap, "normal.png");
            TextureConverter.savePixMap(blur, "blur.png");
            TextureConverter.savePixMap(white, "white.png");
            joe_n = new Texture(blur);
            normalMap.dispose();
            base.dispose();
            blur.dispose();
            white.dispose();
           
            texture = teapot;
            texture_n = teapot_n;
            flipY = true;
           
            //we only use this to show what the strength-adjusted normal map looks like on screen
            Pixmap pix = new Pixmap(1, 1, Format.RGB565);
            pix.setColor(0.5f, 0.5f, 1.0f, 1.0f);
            pix.fill();
            normalBase = new Texture(pix);
           
            texWidth = texture.getWidth();
            texHeight = texture.getHeight();
           
            // a simple 2D orthographic camera
            cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            cam.setToOrtho(false);
 
                // create our shader program...
            programLighting = createLightingShader();
            programNormalFix = createNormalFixShader();
 
                // now we create our sprite batch for our shader
            fxBatch = new SpriteBatch(100, programLighting);
            // setShader is needed; perhaps this is a LibGDX bug?
            fxBatch.setShader(programLighting);
            fxBatch.setProjectionMatrix(cam.combined);
            fxBatch.setTransformMatrix(transform);
            
            // usually we would just use a single batch for our application,
            // but for demonstration let's also show the un-affected image
            batch = new SpriteBatch(100);
            batch.setProjectionMatrix(cam.combined);
            batch.setTransformMatrix(transform);
            
            this.normalBatch = new SpriteBatch(100, this.programNormalFix);
            normalBatch.setShader(this.programNormalFix);
            normalBatch.setProjectionMatrix(cam.combined);
            normalBatch.setTransformMatrix(transform);
 
            // quick little input for debugging -- press S to toggle shadows, N to
            // toggle normals
            Gdx.input.setInputProcessor(new InputAdapter() {
                    public boolean keyDown(int key) {
                            if (key == Keys.S) {
                                    useShadow = !useShadow;
                                    return true;
                            } else if (key == Keys.N) {
                                    useNormals = !useNormals;
                                    return true;
                            } else if (key == Keys.NUM_1) {
                                    programLighting.begin();
                                    programLighting.setUniformf("ambientColor", rndColor());
                                    programLighting.end();
                                    return true;
                            } else if (key == Keys.NUM_2) {
                                    ambientIntensity = rnd.nextFloat();
                                    return true;
                            } else if (key == Keys.NUM_3) {
                                    programLighting.begin();
                                    /*for(int i = 0; i< Illumination2D.this.lightCol.length; i++)
                                    	Illumination2D.this.lightCol[i] = rndColor();
                                    Illumination2D.this.setUniform("lightColor", Illumination2D.this.lightCol);*/
                                    programLighting.setUniformf("lightColor", rndColor());
                                    programLighting.end();
                                    return true;
                            } else if (key == Keys.NUM_0) {
                                    attenuation.set(DEFAULT_ATTENUATION);
                                    ambientIntensity = DEFAULT_AMBIENT_INTENSITY;
                                    for(int i = 0; i< Illumination2D.this.lightCol.length; i++)
                                    	Illumination2D.this.lightCol[i].set(DEFAULT_LIGHT_POS);
                                    strength = DEFAULT_STRENGTH;
                                    programLighting.begin();
                                    //Illumination2D.this.lightCol[0] = DEFAULT_LIGHT_COLOR;
                                    //Illumination2D.this.setUniform("lightColor", Illumination2D.this.lightCol);
                                    programLighting.setUniformf("lightColor", DEFAULT_LIGHT_COLOR);
                                    programLighting.setUniformf("ambientColor", DEFAULT_AMBIENT_COLOR);
                                    programLighting.setUniformf("ambientIntensity", ambientIntensity);
                                    programLighting.setUniformf("attenuation", attenuation);
                                    Illumination2D.this.setUniform(programLighting,"light", Illumination2D.this.lightPos);
                                    programLighting.setUniformf("strength", strength);
                                    programLighting.end();
                            } else if (key == Keys.T) {
                                    texture = texture==teapot ? joe : teapot;
                                    texture_n = texture_n==teapot_n ? joe_n : teapot_n;
                                    Illumination2D.this.reg.setRegion(texture);
                                    Illumination2D.this.normalReg.setRegion(texture_n);
                                    //flipY = texture==rock;
                                    texWidth = texture.getWidth();
                                    texHeight = texture.getHeight();
                                    programLighting.begin();
                                   // program.setUniformi("yInvert", flipY ? 1 : 0);
                                    programLighting.end();
                            }
                            return false;
                    }
            });
           
            font = new BitmapFont();
            this.reg = new TextureRegion(teapot);
            this.normalReg = new TextureRegion(teapot_n);
            this.reg2 = new TextureRegion(joe);
            this.normalReg2 = new TextureRegion(joe_n);
            this.normalBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
            this.colorBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
            this.colorRegion = new TextureRegion(this.colorBuffer.getColorBufferTexture());
        }
       
        private Vector3 rndColor() {
                return new Vector3(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat());
        }
        
        private static ShaderProgram loadShaderProgram(String vertexShader, String fragmentShader){
            ShaderProgram program = new ShaderProgram(Gdx.files.internal(vertexShader), Gdx.files.internal(fragmentShader));
            ShaderProgram.pedantic = false;
            if (program.isCompiled() == false)
                    throw new IllegalArgumentException("couldn't compile shader: "
                                    + program.getLog());
            return program;
        }
 
        private ShaderProgram createLightingShader() {
                ShaderProgram program = loadShaderProgram("data/shaders/shader.vert","data/shaders/shader.frag");
                // set resolution vector
                resolution.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
 
                // we are only using this many uniforms for testing purposes...!!
                program.begin();
                program.setUniformi("u_texture", 0);
                program.setUniformi("u_normals", 1);
                //program.setUniformf("light", lightPos);
                this.setUniform(program, "light",this.lightPos);
                program.setUniformf("strength", strength);
                program.setUniformf("ambientIntensity", ambientIntensity);
                program.setUniformf("ambientColor", DEFAULT_AMBIENT_COLOR);
                program.setUniformf("resolution", resolution);
                program.setUniformf("lightColor", DEFAULT_LIGHT_COLOR);
                program.setUniformf("attenuation", attenuation);
                program.setUniformi("useShadow", useShadow ? 1 : 0);
                program.setUniformi("useNormals", useNormals ? 1 : 0);
                program.setUniformi("yInvert", flipY ? 1 : 0);
                program.end();
 
                return program;
        }
 
        private ShaderProgram createNormalFixShader() {
                ShaderProgram program = loadShaderProgram("data/shaders/normalMapping.vert", "data/shaders/normalMapping.frag");
                
                program.begin();
                program.setAttributef("angle", 0, 0, 0, 0);
                program.end();
 
                return program;
        }
 
        public void dispose() {
        		this.fxBatch.dispose();
        		this.normalBatch.dispose();
                this.batch.dispose();
                this.texture.dispose();
                this.texture_n.dispose();
            	this.normalBuffer.dispose();
            	this.colorBuffer.dispose();
            	this.programLighting.dispose();
            	this.programNormalFix.dispose();
        }
        
        private void setUniform(ShaderProgram program, String name, Vector3[] array){
        	float[] f = new float[array.length*3];
        	for(int i = 0; i < array.length; i++){
        		f[i] = array[i].x;
        		f[i+1] = array[i].y;
        		f[i+2] = array[i].z;
        	}
        	program.setUniform3fv(name, f, 0, f.length);
        }
       
        public void render() {
        	angle = (angle +1) % 360;
            Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
            batch.setProjectionMatrix(cam.combined);
            fxBatch.setProjectionMatrix(cam.combined);
            normalBatch.setProjectionMatrix(cam.combined);
           
            final int IMG_Y = texHeight/2;


            this.normalBuffer.begin();
            Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

            this.normalBatch.begin();
            
            this.programNormalFix.setAttributef("angle", 0,0,0,0);
            this.normalBatch.draw(this.normalReg, this.reg.getRegionWidth()*2 + 20, IMG_Y);
            this.normalBatch.flush();
            
            this.programNormalFix.setAttributef("angle", angle,angle,angle,angle);
            this.normalBatch.draw(this.normalReg2, this.reg.getRegionWidth()*2 + 500, IMG_Y, this.reg2.getRegionWidth()/2, this.reg2.getRegionHeight()/2, this.reg2.getRegionWidth(), this.reg2.getRegionHeight(), 1, 1, angle);
            this.normalBatch.flush();
            
            this.normalBatch.end();
            
            this.normalBuffer.end();

            this.colorBuffer.begin();
	            Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	            batch.begin();
		            this.batch.draw(this.reg, this.reg.getRegionWidth()*2 + 20, IMG_Y);
		            this.batch.draw(this.reg2, this.reg.getRegionWidth()*2 + 500, IMG_Y, this.reg2.getRegionWidth()/2, this.reg2.getRegionHeight()/2, 
		            		this.reg2.getRegionWidth(), this.reg2.getRegionHeight(), 1, 1, angle);
	            batch.end();
            this.colorBuffer.end();
            

            
            // get y-down light position based on mouse/touch
            //for(int i = 0; i< this.lightPos.length; i++){
            	lightPos[0].x = Gdx.input.getX();
            	lightPos[0].y = Gdx.graphics.getHeight() - Gdx.input.getY();
            //}
           
           
            performKeyboardHandling();
            
           
            // start our FX batch, which will bind our shader program
            fxBatch.begin();
           
            // update our uniforms
            programLighting.setUniformf("ambientIntensity", ambientIntensity);
            programLighting.setUniformf("attenuation", attenuation);
            //program.setUniformf("light[0]", lightPos);
            this.setUniform(programLighting,"light",this.lightPos);
            programLighting.setUniformi("useNormals", useNormals ? 1 : 0);
            programLighting.setUniformi("useShadow", useShadow ? 1 : 0);
            programLighting.setUniformf("strength", strength);
           
            this.normalBuffer.getColorBufferTexture().bind(1);
            this.colorBuffer.getColorBufferTexture().bind(0);
            fxBatch.draw(this.colorRegion, 0, 0);
            fxBatch.end();

            // draw our sprites without any effects
            batch.begin();
           
            //let's first simulate our resulting normal map by blending a blue square atop it
            //we also could have achieved this with glTexEnv in the fixed function pipeline
            NORMAL_VCOLOR.a = 1.0f - strength;
            batch.draw(texture_n, texWidth + 10, IMG_Y);
            batch.setColor(NORMAL_VCOLOR);
            batch.draw(normalBase, texWidth + 10, IMG_Y, texWidth, texHeight);
            batch.setColor(Color.WHITE);
            batch.draw(texture, 0, IMG_Y);
            //now let's simulate how our normal map will be sampled using strength
            //we can do this simply by blending a blue fill overtop
           
            String str = MessageFormat.format(TEXT, ambientIntensity,
                            attenuation.x, attenuation.y, DEC_FMT.format(attenuation.z),
                            strength, lightPos[0].z);
            font.drawMultiLine(batch, str, 10, Gdx.graphics.getHeight()-10);
           
            font.draw(batch, "Diffuse Color", 10, IMG_Y+texHeight + 20);
            font.draw(batch, "Normal Map", texWidth+20, IMG_Y+texHeight + 20);
            font.draw(batch, "Final Color", texWidth*2+30, IMG_Y+texHeight + 20);
            
            //batch.draw(this.normalBuffer.getColorBufferTexture(), 0, this.normalBuffer.getColorBufferTexture().getHeight(), this.normalBuffer.getColorBufferTexture().getWidth(), -this.normalBuffer.getColorBufferTexture().getHeight());
            batch.end();
            
        }
 
        public void resize(int width, int height) {
        	this.normalBuffer.dispose();
        	this.colorBuffer.dispose();
        	this.normalBuffer = new FrameBuffer(Format.RGBA8888, width, height, false);
        	this.colorBuffer = new FrameBuffer(Format.RGBA8888, width, height, false);
            this.colorRegion = new TextureRegion(this.colorBuffer.getColorBufferTexture());
            this.colorRegion.flip(false, true);
            cam.setToOrtho(false, width, height);
            resolution.set(width, height);
            programLighting.setUniformf("resolution", resolution);
        }
 
        public void pause() {
        }
 
        public void resume() {
        }
        
        private void performKeyboardHandling(){ // handle attenuation input
            if (Gdx.input.isKeyPressed(Keys.NUM_4)) {
                attenuation.x += 0.025f;
	        } else if (Gdx.input.isKeyPressed(Keys.NUM_5)) {
	                attenuation.x -= 0.025f;
	                if (attenuation.x < 0)
	                        attenuation.x = 0;
	        } else if (Gdx.input.isKeyPressed(Keys.NUM_6)) {
	                attenuation.y += 0.25f;
	        } else if (Gdx.input.isKeyPressed(Keys.NUM_7)) {
	                attenuation.y -= 0.25f;
	                if (attenuation.y < 0)
	                        attenuation.y = 0;
	        } else if (Gdx.input.isKeyPressed(Keys.NUM_8)) {
	                attenuation.z += 0.25f;
	        } else if (Gdx.input.isKeyPressed(Keys.NUM_9)) {
	                attenuation.z -= 0.25f;
	                if (attenuation.z < 0)
	                        attenuation.z = 0;
	        } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
	                strength += 0.025f;
	                if (strength > 1f)
	                        strength = 1f;
	        } else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
	                strength -= 0.025f;
	                if (strength < 0)
	                        strength = 0;
	        } else if (Gdx.input.isKeyPressed(Keys.UP)) {
	                lightPos[0].z += 0.0025f;
	        } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
	                lightPos[0].z -= 0.0025f;
	        }
        }
}
