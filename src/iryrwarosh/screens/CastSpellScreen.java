package iryrwarosh.screens;

import iryrwarosh.Creature;
import iryrwarosh.Point;
import iryrwarosh.Projectile;
import iryrwarosh.Tile;
import iryrwarosh.World;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class CastSpellScreen implements Screen {
	private Screen previous;
	private World world;
	private Creature player;
	
	public CastSpellScreen(Screen previous, World world, Creature player){
		this.previous = previous;
		this.world = world;
		this.player = player;
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		previous.displayOutput(terminal);
		
		terminal.clear(' ', 1, 20, 31, 4);
		terminal.write("What do you want to cast?", 1, 20);
		terminal.write(" [1] Fireball      cost 10", 1, 21)
				.write((char)4, Tile.hsv(60, 25, 75));
		terminal.write(" [2] Blink         cost  5", 1, 22)
				.write((char)4, Tile.hsv(60, 25, 75));
		terminal.write(" [3] Fireblast     cost 25", 1, 23)
				.write((char)4, Tile.hsv(60, 25, 75));
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		switch (key.getKeyChar()){
		case '1': fireball(); break;
		case '2': blink(); break;
		case '3': fireblast(); break;
		}
		
		return previous;
	}

	private void fireball() {
		Point dir = player.lastMovedDirection();
		world.add(new Projectile("fireball", player, 7, Tile.LAVA1.color(), 5, player.position.plus(dir.x, dir.y), dir));
		world.add(new Projectile("fireball", player, 250, Tile.LAVA2.color(), 2, player.position.copy(), dir));
		player.loseRupees(world, 10);
	}
	
	private void blink() {
		int tries = 0;
		int x = -1;
		int y = -1;
		
		while (tries++ < 30 
				&& !(player.canEnter(world.tile(x,y))
				  && world.creature(x,y) == null)){
			x = player.position.x + (int)(Math.random() * 19 - 9);
			y = player.position.y + (int)(Math.random() * 19 - 9); 
		}
		
		if (tries == 30)
			return;

		player.loseRupees(world, 5);
		
		player.position.x = x;
		player.position.y = y;
	}

	private void fireblast() {
		Point dir = player.lastMovedDirection();
		Point side = new Point(dir.y, dir.x);
		
		world.add(new Projectile("fireball", player, 7, Tile.LAVA1.color(), 5, player.position.plus(dir), dir));
		world.add(new Projectile("fireball", player, 250, Tile.LAVA2.color(), 2, player.position.copy(), dir));
		
		world.add(new Projectile("fireball", player, 7, Tile.LAVA2.color(), 5, player.position.plus(dir).plus(side), dir));
		world.add(new Projectile("fireball", player, 250, Tile.LAVA3.color(), 2, player.position.plus(side), dir));
		
		world.add(new Projectile("fireball", player, 7, Tile.LAVA2.color(), 5, player.position.plus(dir).minus(side), dir));
		world.add(new Projectile("fireball", player, 250, Tile.LAVA3.color(), 2, player.position.minus(side), dir));
		
		player.loseRupees(world, 10);
	}
}
