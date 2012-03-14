package iryrwarosh;

import java.awt.Color;

public class Projectile {
	public Point position;
	public Point velocity;
	private Creature origin;
	
	private boolean isDone;
	public boolean isDone() { return isDone; }

	private String name;
	public String name() { return name; }
	
	private char glyph;
	public char glyph() { return glyph; }
	
	private Color color;
	public Color color(){ return color; }
	
	private int damage;
	private int airTime = 0;
	
	public Projectile(String name, Creature origin, int glyph, Color color, int damage, Point position, Point velocity){
		this.name = name;
		this.position = position;
		this.velocity = velocity;
		this.glyph = (char)glyph;
		this.color = color;
		this.origin = origin;
		this.damage = damage;
	}
	
	public void move(World world){
		position.x += velocity.x;
		position.y += velocity.y;
		
		if (airTime++ > 10)
			isDone = true;
		
		if (!canEnter(world.tile(position.x, position.y))) {
			isDone = true;
			return;
		}
		
		update(world);
	}
	
	public void update(World world){
		Creature creature = world.creature(position.x, position.y);
		
		if (creature == null || origin.isFriendlyTo(creature))
			return;
		
		if (creature.rightHand() != null && creature.hasTrait(Trait.DEFLECT_RANGED) && Math.random() < 0.5){
			MessageBus.publish(new DeflectRanged(world, creature, this));
		} else {
			creature.loseHearts(world, origin, damage, "from a distance", "You were slain by the " + name() + " from a " + origin.name());
		}
		isDone = true;
	}
	
	public boolean canEnter(Tile tile){
		return tile.isFlyable();
	}
}
