package iryrwarosh.screens;

import iryrwarosh.Creature;
import iryrwarosh.World;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiCharacterData;
import asciiPanel.AsciiPanel;
import asciiPanel.TileTransformer;

public class DiveScreen implements Screen {
	private Screen previous;
	private World world;
	private Creature player;
	
	public DiveScreen(Screen previous, World world, Creature player){
		this.previous = previous;
		this.world = world;
		this.player = player;
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		previous.displayOutput(terminal);
		terminal.withEachTile(new TileTransformer(){

			@Override
			public void transformTile(int x, int y, AsciiCharacterData data) {
				if (y == 0)
					return;
				
				data.foregroundColor = data.foregroundColor.darker();
				data.backgroundColor = data.backgroundColor.darker();
			}
		});
		
		terminal.write("(underwater mode)", 1, 20);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		switch (key.getKeyCode()){
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_4:
        case KeyEvent.VK_H: swim(-1, 0); break;
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_6:
        case KeyEvent.VK_L: swim( 1, 0); break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_8:
        case KeyEvent.VK_K: swim( 0,-1); break;
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_2:
        case KeyEvent.VK_J: swim( 0, 1); break;
        case KeyEvent.VK_7:
        case KeyEvent.VK_Y: swim(-1,-1); break;
        case KeyEvent.VK_9:
        case KeyEvent.VK_U: swim( 1,-1); break;
        case KeyEvent.VK_1:
        case KeyEvent.VK_B: swim(-1, 1); break;
        case KeyEvent.VK_3:
        case KeyEvent.VK_N: swim( 1, 1); break;
        default: return previous;
		}
		
		if (player.rupees() < 1)
			return previous;
		
		return this;
	}

	private void swim(int dx, int dy) {
		if (!world.tile(player.position.x+dx, player.position.y+dy).isSwimmable())
			return;
		
		player.moveBy(world, dx, dy);
		player.update(world);
		player.loseRupees(world, 1);
	}
}
