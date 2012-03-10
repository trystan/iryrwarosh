package iryrwarosh;

import java.awt.Color;

public enum Tile {
	GREEN_DIRT  (250, hsv(110, 50, 33), hsv(110, 50,  5)),
	BROWN_DIRT  (250, hsv( 45, 50, 33), hsv( 45, 50,  5)),
	WHITE_DIRT  (250, hsv(  0,  0, 33), hsv(  0, 50,  5)),
	GREEN_TREE1 (  6, hsv(100, 40, 55), hsv(110, 50,  5)),
	GREEN_TREE2 (  6, hsv(115, 40, 50), hsv(110, 50,  5)),
	GREEN_TREE3 (  6, hsv(130, 40, 45), hsv(110, 50,  5)),
	GREEN_TREE4 ( 30, hsv(100, 55, 40), hsv(110, 50,  5)),
	GREEN_TREE5 ( 30, hsv(110, 50, 35), hsv(110, 50,  5)),
	GREEN_TREE6 ( 30, hsv(120, 45, 30), hsv(110, 50,  5)),
	BROWN_TREE1 (  6, hsv( 15, 55, 50), hsv( 30, 50,  5)),
	BROWN_TREE2 (  6, hsv( 20, 55, 50), hsv( 30, 50,  5)),
	BROWN_TREE3 (  6, hsv( 25, 55, 50), hsv( 30, 50,  5)),
	BROWN_TREE4 (  5, hsv( 20, 75, 45), hsv( 30, 50,  5)),
	BROWN_TREE5 (  5, hsv( 20, 70, 50), hsv( 30, 50,  5)),
	BROWN_TREE6 (  5, hsv( 20, 65, 55), hsv( 30, 50,  5)),
	WHITE_TREE1 (  6, hsv(  0,  0, 55), hsv(  0, 50,  5)),
	WHITE_TREE2 (  6, hsv(  0,  0, 50), hsv(  0, 50,  5)),
	WHITE_TREE3 (  6, hsv(  0,  0, 45), hsv(  0, 50,  5)),
	GREEN_ROCK  (177, hsv(110, 50, 50), hsv(110, 50,  5)),
	BROWN_ROCK  (177, hsv( 30, 50, 50), hsv( 30, 50,  5)),
	WHITE_ROCK  (177, hsv(  0,  0, 66), hsv(  0, 50,  5)),
	DESERT_SAND1(250, hsv( 60, 50, 55), hsv( 45, 50, 11)),
	DESERT_SAND2(250, hsv( 60, 50, 50), hsv( 45, 50, 10)),
	DESERT_SAND3(250, hsv( 60, 50, 45), hsv( 45, 50,  9)),
	WATER1      (247, hsv(210, 63, 80), hsv(210, 80, 21)),
	WATER2      (247, hsv(210, 66, 75), hsv(210, 80, 20)),
	WATER3      (247, hsv(210, 69, 70), hsv(210, 80, 19)),
    BRIDGE      (240, hsv( 30, 66, 66), hsv( 30, 90, 20)),
    STATUE      ('&', hsv( 20, 33, 90), hsv(  0,  0,  5)),;

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
		case GREEN_TREE4:
		case GREEN_TREE5:
		case GREEN_TREE6:
			varieties = new Tile[]{ GREEN_TREE4, GREEN_TREE5, GREEN_TREE6 };
			break;
		case BROWN_TREE1:
		case BROWN_TREE2:
		case BROWN_TREE3:
			varieties = new Tile[]{ BROWN_TREE1, BROWN_TREE2, BROWN_TREE3 };
			break;
		case BROWN_TREE4:
		case BROWN_TREE5:
		case BROWN_TREE6:
			varieties = new Tile[]{ BROWN_TREE4, BROWN_TREE5, BROWN_TREE6 };
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
		case DESERT_SAND1:
		case DESERT_SAND2:
		case DESERT_SAND3:
			varieties = new Tile[]{ DESERT_SAND1, DESERT_SAND2, DESERT_SAND3 };
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

	public boolean isGround() {
		return this == GREEN_DIRT || this == BROWN_DIRT || this == WHITE_DIRT 
		  || this == DESERT_SAND1 || this == DESERT_SAND2 || this == DESERT_SAND3
		  || this == BRIDGE;
	}
}
