package iryrwarosh.screens;

import iryrwarosh.Creature;
import iryrwarosh.Item;
import iryrwarosh.Point;
import iryrwarosh.Projectile;
import iryrwarosh.Tile;
import iryrwarosh.World;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class ThrowItemScreen implements Screen {
	private Screen previous;
	private World world;
	private Creature player;
	private Item item;
	
	public ThrowItemScreen(Screen previous, World world, Creature player, Item item){
		this.previous = previous;
		this.world = world;
		this.player = player;
		this.item = item;
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		previous.displayOutput(terminal);
		
		terminal.write("Which of the 4 directions do you want to throw a " + item.name() + "?", 1, 20);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		switch (key.getKeyChar()){
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_4:
        case KeyEvent.VK_H: throwDirection(-1, 0); break;
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_6:
        case KeyEvent.VK_L: throwDirection( 1, 0); break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_8:
        case KeyEvent.VK_K: throwDirection( 0,-1); break;
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_2:
        case KeyEvent.VK_J: throwDirection( 0, 1); break;
		}
		
		return previous;
	}

	private void throwDirection(int dx, int dy) {
		char glyph = 9;
		
		if (dx == -1)
			glyph = 27;
		else if (dx == 1)
			glyph = 26;
		else if (dy == -1)
			glyph = 24;
		else if (dy == 1)
			glyph = 25;
		
		world.add(new Projectile(item.name(), player, glyph, AsciiPanel.brightWhite, 1, player.position.plus(dx, dy), new Point(dx, dy)){
			public boolean canEnter(Tile tile){
				return tile.isGround() || tile.isWater();
			}
		});
	}
}
