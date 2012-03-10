package iryrwarosh;

import java.awt.Color;

public enum Tile {
	GREEN_DIRT  (250, hsv(110, 50, 33), hsv(110, 50,  5)),
	BROWN_DIRT  (250, hsv( 45, 50, 33), hsv( 45, 50,  5)),
	WHITE_DIRT  (250, hsv(  0,  0, 33), hsv(  0, 50,  5)),
	GREEN_TREE  ('*', hsv(110, 50, 66), hsv(110, 50,  5)),
	BROWN_TREE  ('*', hsv( 30, 50, 66), hsv( 30, 50,  5)),
	WHITE_TREE  ('*', hsv(  0,  0, 66), hsv(  0, 50,  5)),
	GREEN_ROCK  (177, hsv(110, 50, 50), hsv(110, 50,  5)),
	BROWN_ROCK  (177, hsv( 30, 50, 50), hsv( 30, 50,  5)),
	WHITE_ROCK  (177, hsv(  0,  0, 50), hsv(  0, 50,  5)),
	DESERT_SAND (250, hsv( 60, 50, 50), hsv( 45, 50, 10)),
	WATER       (247, hsv(210, 66, 75), hsv(210, 90, 20));

	public static Color hsv(int h, int s, int v){
		return Color.getHSBColor(h / 360f, s / 100f, v / 100f);
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
}
