package GameOfLifeCompute.main;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;

import GameOfLifeCompute.Input;
import GameOfLifeCompute.rendering.Program;
import GameOfLifeCompute.rendering.Shader;
import GameOfLifeCompute.rendering.Texture;
import GameOfLifeCompute.rendering.meshes.ScreenMesh;
import GameOfLifeCompute.rendering.meshes.TextureMesh;
import GameOfLifeCompute.utils.FileHandling;
import GameOfLifeCompute.utils.Timer;

public class Main {
	
	public Timer timer;
	public Input input;
	
	private Window window;
	
	private TextureMesh gameTexture;
	private ScreenMesh screen;

	private FrameBuffer frameBuffer;
	
	private Program screenProgram;
	private Program renderProgram;
	private Program computeProgram;
	
	private Texture texture0;
	private Texture texture1;
	private int screenWidth;
	private int screenHeight;
	
	private int pixelsPerSquare=4;
	private int textureWidth;
	private int textureHeight;
	private float fadeRate=0.01f;

	private boolean writeTexture0;
	private boolean renderTexture0;
	
	private boolean paused;

	
	public static void main(String[] args){
		new Main().gameLoop();
	}
	
	private void gameLoop(){
		try{
			init();
			loop();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			cleanup();
		}
	}
	
	private void init() throws Exception{
		window=new Window(-1,-1,"gameing");
		screenWidth=window.getWidth();
		screenHeight=window.getHeight();
		input=new Input();
		timer=new Timer(60,60);	//UPS,FPS
		frameBuffer=new FrameBuffer();

		textureWidth=screenWidth/pixelsPerSquare;
		textureHeight=screenHeight/pixelsPerSquare;
		
		window.init();
		input.init(window);
		frameBuffer.init(textureWidth,textureHeight);

		texture0=new Texture(textureWidth,textureHeight,GL46.GL_R32F,GL46.GL_RED,GL46.GL_FLOAT);
		texture1=new Texture(textureWidth,textureHeight,GL46.GL_R32F,GL46.GL_RED,GL46.GL_FLOAT);


		float[] vertices=new float[]{
				-1,1,
				1,1,
				1,-1,
				-1,-1
		};

		int[] indices=new int[]{
				0,1,2,
				0,2,3
		};

		float[] textCoords=new float[]{
				0,0,
				1,0,
				1,1,
				0,1
		};

		float[] colours=new float[]{
				1,0,1,
				0,1,1,
				0,0,1,
				0.5f,0,1
		};



		//Creating the screen
		gameTexture =new TextureMesh(vertices,indices,textCoords);
		screen=new ScreenMesh(vertices,indices,textCoords,colours);
		
		//Generating shaders and programs
		screenProgram=new Program("Screen program");
		renderProgram=new Program("Renderbuffer program");
		computeProgram=new Program("Compute program");
		
		
		screenProgram.attachShaders(new Shader[]{
				new Shader(FileHandling.loadResource("src/GameOfLifeCompute/rendering/screenGLSL/vertex.glsl"),GL46.GL_VERTEX_SHADER),
				new Shader(FileHandling.loadResource("src/GameOfLifeCompute/rendering/screenGLSL/fragment.glsl"),GL46.GL_FRAGMENT_SHADER)
		});

		renderProgram.attachShaders(new Shader[]{
				new Shader(FileHandling.loadResource("src/GameOfLifeCompute/rendering/renderGLSL/vertex.glsl"),GL46.GL_VERTEX_SHADER),
				new Shader(FileHandling.loadResource("src/GameOfLifeCompute/rendering/renderGLSL/fragment.glsl"),GL46.GL_FRAGMENT_SHADER)
		});
		
		computeProgram.attachShaders(new Shader[] {
				new Shader(FileHandling.loadResource("src/GameOfLifeCompute/main/gameOfLife.glsl"),GL46.GL_COMPUTE_SHADER)
		});
		
		screenProgram.link();
		renderProgram.link();
		computeProgram.link();
		
		screenProgram.createUniform("textureSampler");

		renderProgram.createUniform("gameTexture");
		renderProgram.createUniform("lastRender");
		renderProgram.createUniform("fadeRate");
		renderProgram.createUniform("paused");

		computeProgram.createUniform("paused");
		computeProgram.createUniform("texture0");
		computeProgram.createUniform("texture1");
		
		GL46.glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
		
		window.loop();
	}
	
	private void loop(){
		while(!window.shouldClose()){
			timer.update();
			if(timer.getUpdate()){
				update();
			}if(timer.getFrame()){
				render();
			}
		}
		
	}
	
	private void render(){
		int renderTexture=renderTexture0?0:1;
		window.loop();

		frameBuffer.bindFrameBuffer();
		frameBuffer.bindTexture(renderTexture);

		GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
		GL46.glViewport(0, 0, textureWidth, textureHeight);

		gameTexture.render(renderProgram, writeTexture0?texture0:texture1,frameBuffer.getTexture(1-renderTexture),fadeRate,paused?1:0);

		frameBuffer.unbindFrameBuffer();

		GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);

		GL46.glViewport(0, 0, window.getWidth(), window.getHeight());
		
		screen.render(screenProgram,frameBuffer.getTexture(writeTexture0?0:1));
		
		renderTexture0=!renderTexture0;
	}
	
	private void update(){
		if(input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)){
    		window.close();
    	}
		
		//Pointers to read-write textures
		Texture readTexture=writeTexture0?texture1:texture0;
		Texture writeTexture=writeTexture0?texture0:texture1;
		
		computeProgram.useProgram();
		
		computeProgram.setUniform("paused",paused?1:0);
		computeProgram.setUniform("texture0", 0);
		computeProgram.setUniform("texture1", 1);
		
		//Binding textures
		GL46.glBindImageTexture(0, readTexture.getId(), 0, false, 0, GL46.GL_READ_ONLY, GL46.GL_R32F);
		GL46.glBindImageTexture(1, writeTexture.getId(), 0, false, 0, GL46.GL_WRITE_ONLY, GL46.GL_R32F);
		
		//Running compute shader
		GL46.glDispatchCompute(textureWidth,textureHeight,1);
		
		//Waits until all accesses to a texture are done to continue
		GL46.glMemoryBarrier(GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
		
		//Unbinding textures
		GL46.glBindImageTexture(0, 0, 0, false, 0, GL46.GL_READ_ONLY, GL46.GL_R32F);
		GL46.glBindImageTexture(1, 0, 0, false, 0, GL46.GL_WRITE_ONLY, GL46.GL_R32F);
		
		computeProgram.unlinkProgram();
		
		int[] mousePos=input.getMousePos();
		mousePos[0]/=pixelsPerSquare;
		mousePos[1]/=pixelsPerSquare;
		
		//User inputs to draw on
		if(input.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
			writeTexture.writeToTexture(mousePos[0], (mousePos[1]),1,1,new float[]{1});
		}else if(input.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
			writeTexture.writeToTexture(mousePos[0], (mousePos[1]),1,1,new float[]{0});
		}else if(input.isKeyPressed(GLFW.GLFW_KEY_1)){
			writeTexture.writeToTexture(mousePos[0], (mousePos[1]), Presets.glider);
		}else if(input.isKeyPressed(GLFW.GLFW_KEY_2)){
			writeTexture.writeToTexture(mousePos[0], (mousePos[1]), Presets.gosperGun);
		}else if(input.isKeyPressed(GLFW.GLFW_KEY_3)){
			writeTexture.writeToTexture(mousePos[0], (mousePos[1]), Presets.Pentadecathlon);
		}else if(input.isKeyPressed(GLFW.GLFW_KEY_4)){
			writeTexture.writeToTexture(mousePos[0], (mousePos[1]), Presets.RPentomino);
		}else if(input.isKeyPressed(GLFW.GLFW_KEY_5)){
			writeTexture.writeToTexture(mousePos[0], (mousePos[1]), Presets.DieHard);
		}else if(input.isKeyPressed(GLFW.GLFW_KEY_6)){
			writeTexture.writeToTexture(mousePos[0], (mousePos[1]), Presets.Acorn);
		}else if(input.isKeyPressed(GLFW.GLFW_KEY_7)){
			writeTexture.writeToTexture(mousePos[0], (mousePos[1]), Presets.Infinite);
		}else if(input.isKeyPressed(GLFW.GLFW_KEY_DELETE)){
			writeTexture.writeToTexture(0,0,Presets.genEmptyScreen(textureWidth,textureHeight));
		}

		writeTexture0=!writeTexture0;
		
		if(input.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
			paused=!paused;
		}
		
		input.updateInputs();
	}
	
	private void cleanup(){
		gameTexture.cleanup();
		screen.cleanup();
		window.cleanup();
		
		screenProgram.cleanup();
		renderProgram.cleanup();
		computeProgram.cleanup();

		frameBuffer.cleanup();
		
		texture0.cleanup();
		texture1.cleanup();
	}
}
