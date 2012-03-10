package iryrwarosh;

import java.awt.Color;

public class Creature {
	public Point position;
	
	private char glyph;
	public char glyph() { return glyph; }
	
	private Color color;
	public Color color(){ return color; }
	
	public Creature(char glyph, Color color){
		this.glyph = glyph;
		this.color = color;
	}

	public void moveBy(World world, int x, int y) {
		if (!world.tile(position.x+x, position.y+y).isGround())
			return;
		
		position.x += x;
		position.y += y;
	}
	
	public void update(){
		
	}
}
