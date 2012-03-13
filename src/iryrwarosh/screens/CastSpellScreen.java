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
		terminal.write(" [1] Magic Missiles    cost 10", 1, 21)
				.write((char)4, Tile.hsv(60, 25, 75));
		terminal.write(" [2] Blink             cost  5", 1, 22)
				.write((char)4, Tile.hsv(60, 25, 75));
		terminal.write(" [3] Heart to rupees   cost  1", 1, 23)
				.write((char)3, AsciiPanel.red);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		switch (key.getKeyChar()){
		case '1': magicMissiles(); break;
		case '2': blink(); break;
		case '3': heartsToRupees(); break;
		}
		
		return previous;
	}

	private void magicMissiles() {
		world.add(new Projectile(player, 7, Tile.hsv(210, 33, 66), 2, player.position.plus(1, 0), new Point( 1, 0)));
		world.add(new Projectile(player, 7, Tile.hsv(210, 33, 66), 2, player.position.plus(-1, 0), new Point(-1, 0)));
		world.add(new Projectile(player, 7, Tile.hsv(210, 33, 66), 2, player.position.plus(0, 1), new Point( 0, 1)));
		world.add(new Projectile(player, 7, Tile.hsv(210, 33, 66), 2, player.position.plus(0,-1), new Point( 0,-1)));
		world.add(new Projectile(player, 7, Tile.hsv(210, 33, 66), 2, player.position.plus(-1,-1), new Point(-1,-1)));
		world.add(new Projectile(player, 7, Tile.hsv(210, 33, 66), 2, player.position.plus(-1, 1), new Point(-1, 1)));
		world.add(new Projectile(player, 7, Tile.hsv(210, 33, 66), 2, player.position.plus(1,-1), new Point( 1,-1)));
		world.add(new Projectile(player, 7, Tile.hsv(210, 33, 66), 2, player.position.plus(1, 1), new Point( 1, 1)));
		player.pay(world, 10);
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

		player.pay(world, 5);
		
		player.position.x = x;
		player.position.y = y;
	}

	private void heartsToRupees() {
		player.hurt(world, player, 1, "with magic");
		player.gainMoney(10);
	}
}
