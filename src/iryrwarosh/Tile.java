package iryrwarosh;

import java.awt.Color;

public enum Tile {
	GREEN_DIRT  (250, hsv(110, 50, 33), hsv(110, 50,  5)),
	BROWN_DIRT  (250, hsv( 45, 50, 33), hsv( 45, 50,  5)),
	WHITE_DIRT  (250, hsv(  0,  0, 33), hsv(  0, 50,  5)),
	GREEN_TREE1 (  6, hsv(100, 66, 50), hsv(110, 50,  5)),
	GREEN_TREE2 (  6, hsv(110, 66, 50), hsv(110, 50,  5)),
	GREEN_TREE3 (  6, hsv(120, 66, 50), hsv(110, 50,  5)),
	BROWN_TREE1 ('*', hsv( 20, 50, 66), hsv( 30, 50,  5)),
	BROWN_TREE2 ('*', hsv( 30, 50, 66), hsv( 30, 50,  5)),
	BROWN_TREE3 ('*', hsv( 40, 50, 66), hsv( 30, 50,  5)),
	WHITE_TREE1 ('*', hsv(  0,  0, 55), hsv(  0, 50,  5)),
	WHITE_TREE2 ('*', hsv(  0,  0, 66), hsv(  0, 50,  5)),
	WHITE_TREE3 ('*', hsv(  0,  0, 77), hsv(  0, 50,  5)),
	GREEN_ROCK  (177, hsv(110, 50, 50), hsv(110, 50,  5)),
	BROWN_ROCK  (177, hsv( 30, 50, 50), hsv( 30, 50,  5)),
	WHITE_ROCK  (177, hsv(  0,  0, 50), hsv(  0, 50,  5)),
	DESERT_SAND (250, hsv( 60, 50, 50), hsv( 45, 50, 10)),
	WATER1      (247, hsv(210, 63, 80), hsv(210, 80, 22)),
	WATER2      (247, hsv(210, 66, 75), hsv(210, 80, 20)),
	WATER3      (247, hsv(210, 69, 70), hsv(210, 80, 18));

	public static Color hsv(int h, int s, int v){
		return Color.getHSBColor(h / 360f, s / 100f, v / 100f);
	}
	
	public Tile variation(){
		Tile[] varieties = null;
		
		switch (this){
		case GREEN_TREE1:
		case GREEN_TREE2:
		case GREEN_TREE3:
			varieties = new Tile[]{ GREEN_TREE1, GREEN_TREE2, GREEN_TREE3 };
			break;
		case BROWN_TREE1:
		case BROWN_TREE2:
		case BROWN_TREE3:
			varieties = new Tile[]{ BROWN_TREE1, BROWN_TREE2, BROWN_TREE3 };
			break;
		case WHITE_TREE1:
		case WHITE_TREE2:
		case WHITE_TREE3:
			varieties = new Tile[]{ WHITE_TREE1, WHITE_TREE2, WHITE_TREE3 };
			break;
		case WATER1:
		case WATER2:
		case WATER3:
			varieties = new Tile[]{ WATER1, WATER2, WATER3 };
			break;
		}
		
		if (varieties == null)
			return this;
		else 
			return varieties[(int)(Math.random() * varieties.length)];
	}
	
	private char glyph;
	public char glyph() { return glyph; }
	
	private Color color;
	public Color color() { return color; }
	
	private Color background;
	public Color background() { return background; }
	
	Tile(int glyph, Color color, Color background){
		this.glyph = (char)glyph;
		this.color = color;
		this.background = background;
	}

	public boolean isWater() {
		return this == WATER1 || this == WATER2 || this == WATER3;
	}
}
