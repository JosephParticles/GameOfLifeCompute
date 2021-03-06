package GameOfLifeCompute.main;

public class Presets {

	public static Preset genEmptyScreen(int width, int height){
		float[] values=new float[width*height];
		return new Preset(width,height,values);
	}

	public static Preset glider=new Preset(3,3,new float[]{
			0, 1, 0,
			0, 0, 1,
			1, 1, 1
	});

	public static Preset gosperGun=new Preset(36,9,new float[]{
			0,0,0,0,0,0,0,0,0,0 ,0,0,0,0,0,0,0,0,0,0 ,0,0,0,0,1,0,0,0,0,0 ,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0 ,0,0,0,0,0,0,0,0,0,0 ,0,0,1,0,1,0,0,0,0,0 ,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0 ,0,0,1,1,0,0,0,0,0,0 ,1,1,0,0,0,0,0,0,0,0 ,0,0,0,0,1,1,
			0,0,0,0,0,0,0,0,0,0 ,0,1,0,0,0,1,0,0,0,0 ,1,1,0,0,0,0,0,0,0,0 ,0,0,0,0,1,1,
			1,1,0,0,0,0,0,0,0,0 ,1,0,0,0,0,0,1,0,0,0 ,1,1,0,0,0,0,0,0,0,0 ,0,0,0,0,0,0,
			1,1,0,0,0,0,0,0,0,0 ,1,0,0,0,1,0,1,1,0,0 ,0,0,1,0,1,0,0,0,0,0 ,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0 ,1,0,0,0,0,0,1,0,0,0 ,0,0,0,0,1,0,0,0,0,0 ,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0 ,0,1,0,0,0,1,0,0,0,0 ,0,0,0,0,0,0,0,0,0,0 ,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0 ,0,0,1,1,0,0,0,0,0,0 ,0,0,0,0,0,0,0,0,0,0 ,0,0,0,0,0,0,
	});
	
	public static Preset Pentadecathlon=new Preset(3,8,new float[]{
			1,1,1,
			1,0,1,
			1,1,1,
			1,1,1,
			1,1,1,
			1,1,1,
			1,0,1,
			1,1,1,
	});
	
	public static Preset RPentomino=new Preset(3,3,new float[]{
		0,1,1,
		1,1,0,
		0,1,0
	});
	
	public static Preset DieHard=new Preset(8,3,new float[]{
			0,0,0,0,0,0,1,0,
			1,1,0,0,0,0,0,0,
			0,1,0,0,0,1,1,1
	});
	
	public static Preset Acorn=new Preset(7,3,new float[]{
			0,1,0,0,0,0,0,
			0,0,0,1,0,0,0,
			1,1,0,0,1,1,1
	});
	
	public static Preset Infinite=new Preset(5,5,new float[] {
			1,1,1,0,1,
			1,0,0,0,0,
			0,0,0,1,1,
			0,1,1,0,1,
			1,0,1,0,1
	});
}
