package iryrwarosh.screens;

import iryrwarosh.Creature;
import iryrwarosh.Tile;
import iryrwarosh.World;

import java.awt.Color;
import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class PlayScreen implements Screen {
	private World world;
	private Creature player;
	
	private int screenWidth = 80;
	private int screenHeight = 23;
	
	public PlayScreen(World world){
		this.world = world;
		this.player = new Creature('@', AsciiPanel.brightWhite, 10);
		world.add(player);
		
		addGoblins();
	}
	
	private void addGoblins(){
		for (int i = 0; i < 100; i++){
			int hue = 30 + (int)(Math.random() * 90);
			Creature goblin = new Creature('g', Tile.hsv(hue, 50, 50), 1){
				public void update(){
					moveBy(world, (int)(Math.random() * 3) - 1, (int)(Math.random() * 3) - 1);
				}
			};
			world.add(goblin);
		}
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		displayTiles(terminal);
		
		Color bg = Tile.hsv(30, 30, 15);
		terminal.clear(' ', 0, 0, 80, 1, Tile.hsv(0, 0, 15), bg);
		terminal.write("evade: " + player.evadePercent(world) + "%", 50, 0, AsciiPanel.yellow, bg);
		
		for (int i = 0; i < player.maxHp(); i++)
			terminal.write((char)3, 69+i, 0, i < player.hp() ? AsciiPanel.red : AsciiPanel.brightBlack, bg);
	}
	
	private void displayTiles(AsciiPanel terminal){
		for (int x = 0; x < screenWidth; x++)
		for (int y = 0; y < screenHeight; y++){
			Tile t = world.tile(x + getScrollX(), y + getScrollY());
			terminal.write(
					t.glyph(), 
					x, y+1, 
					t.color(),
					t.background());
		}
		
		for (Creature c : world.creatures()){
			int x = c.position.x - getScrollX();
			int y = c.position.y - getScrollY();
			
			if (x < 0 || x >= screenWidth || y < 0 || y >= screenHeight)
				continue;
			
			terminal.write(c.glyph(), 
					x, y+1, 
					c.color(), 
					world.tile(c.position.x, c.position.y).background());
		}
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
		
		if (player.hp() < 1)
			return new DeadScreen();
		
		return this;
	}
}
