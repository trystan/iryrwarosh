package iryrwarosh.screens;

import iryrwarosh.Creature;
import iryrwarosh.Tile;
import iryrwarosh.World;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class PlayScreen implements Screen {
	private World world;
	private Creature player;
	
	private int screenWidth = 80;
	private int screenHeight = 24;
	
	public PlayScreen(World world){
		this.world = world;
		this.player = new Creature('@', AsciiPanel.brightWhite);
		world.add(player);
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		displayTiles(terminal);
	}
	
	private void displayTiles(AsciiPanel terminal){
		for (int x = 0; x < screenWidth; x++)
		for (int y = 0; y < screenHeight; y++){
			Tile t = world.tile(x + getScrollX(), y + getScrollY());
			terminal.write(
					t.glyph(), 
					x, 
					y, 
					t.color(),
					t.background());
		}
		
		terminal.write(player.glyph(), 
				player.position.x - getScrollX(), 
				player.position.y - getScrollY(), 
				player.color(), 
				world.tile(player.position.x, player.position.y).background());
	}
	
	public int getScrollX() {
        return Math.max(0, Math.min(player.position.x - screenWidth / 2, world.width() - screenWidth));
    }
    
    public int getScrollY() {
        return Math.max(0, Math.min(player.position.y - screenHeight / 2, world.height() - screenHeight));
    }
    
	private void moveBy(int x, int y){
		player.moveBy(world, x, y);
	}
	
	@Override
	public Screen respondToUserInput(KeyEvent key) {
		switch (key.getKeyCode()){
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_H: moveBy(-1, 0); break;
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_L: moveBy( 1, 0); break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_K: moveBy( 0,-1); break;
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_J: moveBy( 0, 1); break;
        case KeyEvent.VK_Y: moveBy(-1,-1); break;
        case KeyEvent.VK_U: moveBy( 1,-1); break;
        case KeyEvent.VK_B: moveBy(-1, 1); break;
        case KeyEvent.VK_N: moveBy( 1, 1); break;
		case KeyEvent.VK_M: return new WorldMapScreen(this, world.map(), player.position);
		}
		
		world.update();
		return this;
	}
}
