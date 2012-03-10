package iryrwarosh;

import java.awt.Color;

import asciiPanel.AsciiPanel;

public enum Tile {
	GROUND (250, AsciiPanel.yellow),
	TREE   ('*', AsciiPanel.green),
	ROCK   (177, AsciiPanel.yellow);
	
	private char glyph;
	public char glyph() { return glyph; }
	
	private Color color;
	public Color color() { return color; }
	
	Tile(int glyph, Color color){
		this.glyph = (char)glyph;
		this.color = color;
	}
}
