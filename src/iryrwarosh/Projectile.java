package iryrwarosh;

import java.awt.Color;

public class Projectile {
	public Point position;
	public Point velocity;
	private Creature origin;
	
	private boolean isDone;
	public boolean isDone() { return isDone; }
	
	private char glyph;
	public char glyph() { return glyph; }
	
	private Color color;
	public Color color(){ return color; }
	
	private int damage;
	private int airTime = 0;
	
	public Projectile(Creature origin, int glyph, Color color, int damage, Point position, Point velocity){
		this.position = position;
		this.velocity = velocity;
		this.glyph = (char)glyph;
		this.color = color;
		this.origin = origin;
		this.damage = damage;
	}
	
	public void update(World world){
		position.x += velocity.x;
		position.y += velocity.y;
		
		if (airTime++ > 20)
			isDone = true;
		
		if (!world.tile(position.x, position.y).isFlyable()) {
			isDone = true;
			return;
		}
			
		Creature c = world.creature(position.x, position.y);
		
		if (c == null || origin.isFriend(c))
			return;
		
		if (c.armor() != null && c.hasTrait(CreatureTrait.DEFLECT_RANGED) && Math.random() < 0.5){
			MessageBus.publish(new DeflectRanged(world, c, this));
		} else {
			c.hurt(world, origin, damage, "from a distance");
		}
		isDone = true;
	}
}
