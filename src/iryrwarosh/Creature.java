package iryrwarosh;

import java.awt.Color;

public class Creature {
	public Point position;
	
	private char glyph;
	public char glyph() { return glyph; }
	
	private Color color;
	public Color color(){ return color; }

	private int hp;
	public int hp() { return hp; }

	private int maxHp;
	public int maxHp() { return maxHp; }
	
	public Creature(char glyph, Color color, int maxHp){
		this.glyph = glyph;
		this.color = color;
		this.maxHp = maxHp;
		this.hp = maxHp;
	}

	public void moveBy(World world, int x, int y) {
		if (x==0 && y==0 || !world.tile(position.x+x, position.y+y).isGround())
			return;
		
		Creature other = world.creature(position.x+x, position.y+y);
		if (other == null) {
			position.x += x;
			position.y += y;
		} else {
			attack(other);
		}
	}
	
	public void attack(Creature other){
		other.hp--;
	}
	
	public void update(){
		
	}
}
