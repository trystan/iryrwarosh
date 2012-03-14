package iryrwarosh.screens;

import iryrwarosh.Creature;
import iryrwarosh.World;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiCharacterData;
import asciiPanel.AsciiPanel;
import asciiPanel.TileTransformer;

public class SneakScreen implements Screen {
	private Screen previous;
	private World world;
	private Creature player;
	
	public SneakScreen(Screen previous, World world, Creature player){
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
		
		terminal.write("(sneak mode)", 1, 20);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		switch (key.getKeyCode()){
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_4:
        case KeyEvent.VK_H: sneak(-1, 0); break;
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_6:
        case KeyEvent.VK_L: sneak( 1, 0); break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_8:
        case KeyEvent.VK_K: sneak( 0,-1); break;
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_2:
        case KeyEvent.VK_J: sneak( 0, 1); break;
        case KeyEvent.VK_7:
        case KeyEvent.VK_Y: sneak(-1,-1); break;
        case KeyEvent.VK_9:
        case KeyEvent.VK_U: sneak( 1,-1); break;
        case KeyEvent.VK_1:
        case KeyEvent.VK_B: sneak(-1, 1); break;
        case KeyEvent.VK_3:
        case KeyEvent.VK_N: sneak( 1, 1); break;
        default: return previous;
		}
		
		if (player.rupees() < 5)
			return previous;
		
		return this;
	}

	private void sneak(int dx, int dy) {
		player.moveBy(world, dx, dy);
		player.update(world);
		player.loseRupees(world, 5);
	}
}
