package iryrwarosh;

import java.awt.Color;

import asciiPanel.AsciiPanel;

public enum Tile {
	GREEN_DIRT  (250, AsciiPanel.green),
	BROWN_DIRT  (250, AsciiPanel.yellow),
	WHITE_DIRT  (250, AsciiPanel.white),
	GREEN_TREE  ('*', AsciiPanel.green),
	BROWN_TREE  ('*', AsciiPanel.yellow),
	WHITE_TREE  ('*', AsciiPanel.white),
	GREEN_ROCK  (177, AsciiPanel.green),
	BROWN_ROCK  (177, AsciiPanel.yellow),
	WHITE_ROCK  (177, AsciiPanel.white),
	DESERT_SAND (250, AsciiPanel.brightYellow);
	
	private char glyph;
	public char glyph() { return glyph; }
	
	private Color color;
	public Color color() { return color; }
	
	Tile(int glyph, Color color){
		this.glyph = (char)glyph;
		this.color = color;
	}
}
