package iryrwarosh;

import java.awt.Color;

import asciiPanel.AsciiPanel;

public enum Tile {
	WHITE_WALL  (' ', Common.hsv(  0,  0,  0), Common.hsv(  0,  0, 40), "wall"),
	WHITE_TILE1 (' ', Common.hsv(  0,  0,  0), Common.hsv(  0,  0,  8), "tile"),
	WHITE_TILE2 (' ', Common.hsv(  0,  0,  0), Common.hsv(  0,  0, 10), "tile"),
	GREEN_DIRT  (250, Common.hsv(110, 50, 33), Common.hsv(110, 50,  5), "dirt"),
	BROWN_DIRT  (250, Common.hsv( 45, 50, 33), Common.hsv( 45, 50,  5), "dirt"),
	WHITE_DIRT  (250, Common.hsv(  0,  0, 33), Common.hsv(  0,  0,  5), "dirt"),
	GREEN_TREE1 (  6, Common.hsv(100, 40, 55), Common.hsv(110, 50,  5), "green tree"),
	GREEN_TREE2 (  6, Common.hsv(115, 40, 50), Common.hsv(110, 50,  5), "green tree"),
	GREEN_TREE3 (  6, Common.hsv(130, 40, 45), Common.hsv(110, 50,  5), "green tree"),
	PINE_TREE1  ( 30, Common.hsv(100, 55, 40), Common.hsv(110, 50,  5), "pine tree"),
	PINE_TREE2  ( 30, Common.hsv(110, 50, 35), Common.hsv(110, 50,  5), "pine tree"),
	PINE_TREE3  ( 30, Common.hsv(120, 45, 30), Common.hsv(110, 50,  5), "pine tree"),
	BROWN_TREE1 (  6, Common.hsv( 15, 55, 50), Common.hsv( 30, 50,  5), "brown tree"),
	BROWN_TREE2 (  6, Common.hsv( 20, 55, 50), Common.hsv( 30, 50,  5), "brown tree"),
	BROWN_TREE3 (  6, Common.hsv( 25, 55, 50), Common.hsv( 30, 50,  5), "brown tree"),
	BROWN_TREE4 (  5, Common.hsv( 20, 75, 45), Common.hsv( 30, 50,  5), "brown tree"),
	BROWN_TREE5 (  5, Common.hsv( 20, 70, 50), Common.hsv( 30, 50,  5), "brown tree"),
	BROWN_TREE6 (  5, Common.hsv( 20, 65, 55), Common.hsv( 30, 50,  5), "brown tree"),
	WHITE_TREE1 (  6, Common.hsv(  0,  0, 55), Common.hsv(  0,  0,  5), "white tree"),
	WHITE_TREE2 (  6, Common.hsv(  0,  0, 50), Common.hsv(  0,  0,  5), "white tree"),
	WHITE_TREE3 (  6, Common.hsv(  0,  0, 45), Common.hsv(  0,  0,  5), "white tree"),
	GREEN_ROCK  (177, Common.hsv(110, 50, 15), Common.hsv(110, 50, 50), "hill rock"),
	BROWN_ROCK  (177, Common.hsv( 30, 50, 15), Common.hsv( 30, 50, 50), "mountain rock"),
	WHITE_ROCK  (177, Common.hsv(  0,  0, 15), Common.hsv(  0,  0, 50), "white rock"),
	DESERT_SAND1(250, Common.hsv( 60, 50, 55), Common.hsv( 45, 50, 11), "desert sand"),
	DESERT_SAND2(250, Common.hsv( 60, 50, 50), Common.hsv( 45, 50, 10), "desert sand"),
	DESERT_SAND3(250, Common.hsv( 60, 50, 45), Common.hsv( 45, 50,  9), "desert sand"),
	WATER1      (247, Common.hsv(210, 63, 80), Common.hsv(210, 80, 21), "water"),
	WATER2      (247, Common.hsv(210, 66, 75), Common.hsv(210, 80, 20), "water"),
	WATER3      (247, Common.hsv(210, 69, 70), Common.hsv(210, 80, 19), "water"),
    BRIDGE      (240, Common.hsv( 30, 66, 66), Common.hsv( 30, 90, 20), "bridge"),
    STATUE      ('&', Common.hsv( 20, 33, 90), Common.hsv(  0,  0,  5), "statue"),
    STATUE_WHITE('&', Common.hsv(  0,  0, 66), Common.hsv(  0,  0,  5), "statue"),
    LAVA1       (247, Common.hsv(  0, 64, 79), Common.hsv(  0, 75, 22), "lava"),
    LAVA2       (247, Common.hsv(  0, 66, 75), Common.hsv(  0, 75, 20), "lava"),
    LAVA3       (247, Common.hsv(  0, 68, 71), Common.hsv(  0, 75, 18), "lava"),
    OUT_OF_BOUNDS ('x', AsciiPanel.brightBlack, AsciiPanel.black, "OUT OF BOUNDS");

	public Tile variation(int x, int y){
		Tile[] varieties = null;
		
		switch (this){
		case GREEN_TREE1:
		case GREEN_TREE2:
		case GREEN_TREE3:
			varieties = new Tile[]{ GREEN_TREE1, GREEN_TREE2, GREEN_TREE3 };
			break;
		case PINE_TREE1:
		case PINE_TREE2:
		case PINE_TREE3:
			varieties = new Tile[]{ PINE_TREE1, PINE_TREE2, PINE_TREE3 };
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
		case LAVA1:
		case LAVA2:
		case LAVA3:
			varieties = new Tile[]{ LAVA1, LAVA2, LAVA3 };
			break;
		case DESERT_SAND1:
		case DESERT_SAND2:
		case DESERT_SAND3:
			varieties = new Tile[]{ DESERT_SAND1, DESERT_SAND2, DESERT_SAND3 };
			break;
		case WHITE_TILE1:
		case WHITE_TILE2:
			if ((x+y) % 2 == 0)
				return WHITE_TILE1;
			else
				return WHITE_TILE2;
		}
		
		if (varieties == null)
			return this;
		else 
			return varieties[(int)(Math.random() * varieties.length)];
	}
	

	private String description;
	public String description() { return description; }
	
	private char glyph;
	public char glyph() { return glyph; }
	
	private Color color;
	public Color color() { return color; }
	
	private Color background;
	public Color background() { return background; }
	
	Tile(int glyph, Color color, Color background, String description){
		this.glyph = (char)glyph;
		this.color = color;
		this.background = background;
		this.description = description;
	}

	public boolean isWater() {
		return this == WATER1 || this == WATER2 || this == WATER3;
	}

	public boolean isGround() {
		return this == GREEN_DIRT || this == BROWN_DIRT || this == WHITE_DIRT 
		  || this == DESERT_SAND1 || this == DESERT_SAND2 || this == DESERT_SAND3
		  || this == BRIDGE || this == WHITE_TILE1 || this == WHITE_TILE2;
	}

	public boolean isSwimmable() {
		return isWater() || this == BRIDGE;
	}
	
	public boolean isFlyable(){
		return this != OUT_OF_BOUNDS && this != WHITE_WALL;
	}

	public boolean isLava() {
		return this == LAVA1 || this == LAVA2 || this == LAVA3;
	}

	public boolean canHideIn() {
		return this.isFlyable() && this != WHITE_TILE1 && this != WHITE_TILE2; 
	}
}
