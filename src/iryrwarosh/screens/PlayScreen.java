package iryrwarosh.screens;

import iryrwarosh.World;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class PlayScreen implements Screen {
	private World world;
	
	public PlayScreen(World world){
		this.world = world;
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.write("You are having fun.", 1, 1);
		terminal.write("Press [m] to see a map.", 1, 3);
		
		displayTiles(terminal);
	}
	
	
	private int scrollX = 0;
	private int scrollY = 0;
	private void displayTiles(AsciiPanel terminal){
		for (int x = 0; x < 80; x++)
		for (int y = 0; y < 24; y++)
			terminal.write(world.tile(x+scrollX, y+scrollY).glyph(), x, y, world.tile(x+scrollX, y+scrollY).color());
	}

	private void scrollBy(int x, int y){
		scrollX += x;
		scrollY += y;
		
		if (scrollX < 0)
			scrollX = 0;
		else if (scrollX > world.width() - 80)
			scrollX = world.width() - 80;

		if (scrollY < 0)
			scrollY = 0;
		else if (scrollY > world.height() - 24)
			scrollY = world.height() - 24;
	}
	
	@Override
	public Screen respondToUserInput(KeyEvent key) {
		switch (key.getKeyCode()){
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_H: scrollBy(-1, 0); break;
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_L: scrollBy( 1, 0); break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_K: scrollBy( 0,-1); break;
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_J: scrollBy( 0, 1); break;
        case KeyEvent.VK_Y: scrollBy(-1,-1); break;
        case KeyEvent.VK_U: scrollBy( 1,-1); break;
        case KeyEvent.VK_B: scrollBy(-1, 1); break;
        case KeyEvent.VK_N: scrollBy( 1, 1); break;
		case KeyEvent.VK_M: return new WorldMapScreen(this, world.map());
		}
		return this;
	}
}
