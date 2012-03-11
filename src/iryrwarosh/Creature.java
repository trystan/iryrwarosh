package iryrwarosh;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	public int attack  = 2;
	public int defense = 1;
	public int evade   = 5;
	
	public int evadePercent(World world){
		int moveable = 0;
		
		for (int ox = -1; ox < 2; ox++)
		for (int oy = -1; oy < 2; oy++){
			if (ox==0 && oy==0)
				continue;
			
			if (world.tile(position.x+ox, position.y+oy).isGround()
					&& world.creature(position.x+ox, position.y+oy) == null)
				moveable++;
		}
		
		return moveable * evade;
	}
	
	public boolean evadeCheck(World world){
		return Math.random() * 100 < evadePercent(world);
	}
	
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
		} else if (other.evadeCheck(world)){
			MessageBus.publish(new Evaded(other, this));
			other.evade(world);
		} else {
			MessageBus.publish(new Attacked(this, other));
			attack(other);
		}
	}
	
	public void attack(Creature other){
		other.hp--;
		if (other.hp < 1)
			MessageBus.publish(new Killed(this, other));
	}

	public void evade(World world){
		List<Point> evadeTo = new ArrayList<Point>();
		
		for (int ox = -1; ox < 2; ox++)
		for (int oy = -1; oy < 2; oy++){
			if (ox==0 && oy==0)
				continue;
			
			if (world.tile(position.x+ox, position.y+oy).isGround()
					&& world.creature(position.x+ox, position.y+oy) == null)
				evadeTo.add(new Point(position.x+ox, position.y+oy));
		}
		
		Collections.shuffle(evadeTo);
		position = evadeTo.get(0);
	}
	
	public void update(){
		
	}
}
