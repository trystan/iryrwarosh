package iryrwarosh.screens;

import iryrwarosh.Creature;
import iryrwarosh.Factory;
import iryrwarosh.Handler;
import iryrwarosh.Message;
import iryrwarosh.MessageBus;
import iryrwarosh.Moved;
import iryrwarosh.Tile;
import iryrwarosh.Weapon;
import iryrwarosh.World;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import asciiPanel.AsciiPanel;

public class PlayScreen implements Screen, Handler {
	private World world;
	private Creature player;
	
	private int screenWidth = 80;
	private int screenHeight = 23;
	
	private List<Message> messages = new ArrayList<Message>();
	
	public PlayScreen(World world){
		MessageBus.subscribe(this);
		
		Factory factory = new Factory();
		
		this.world = world;
		this.player = new Creature('@', AsciiPanel.brightWhite, 10);
		
		world.add(player);
		
		addGoblins(factory);
		addWeapons(factory);

		this.player.equip(world, factory.weapon());
	}
	
	private void addGoblins(Factory factory){
		for (int i = 0; i < 100; i++){
			int hue = 30 + (int)(Math.random() * 90);
			Creature goblin = new Creature('g', Tile.hsv(hue, 50, 50), 2){
				public void update(){
					moveBy(world, (int)(Math.random() * 3) - 1, (int)(Math.random() * 3) - 1);
				}
			};
			world.add(goblin);
			goblin.equip(world, factory.weapon());
		}
	}
	
	private void addWeapons(Factory factory){
		for (int i = 0; i < 20; i++)
			world.add(factory.weapon());
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		displayHud(terminal);
		displayTiles(terminal);
		displayMessages(terminal);
	}

	private void displayHud(AsciiPanel terminal) {
		Color bg = Tile.hsv(30, 30, 15);
		terminal.clear(' ', 0, 0, 80, 1, Tile.hsv(0, 0, 15), bg);
		
		terminal.write("Holding a " + player.weapon().name(), 1, 0, player.weapon().color(), bg);
		
		terminal.write("evade: " + player.evadePercent(world) + "%", 50, 0, AsciiPanel.yellow, bg);
		
		Weapon w = world.item(player.position.x, player.position.y);
		if (w != null)
			terminal.write("(here: " + w.name() + ")", 34, 0, w.color(), bg);
		
		for (int i = 0; i < player.maxHp(); i++)
			terminal.write((char)3, 69+i, 0, i < player.hp() ? AsciiPanel.red : AsciiPanel.brightBlack, bg);
	}
	
	private void displayTiles(AsciiPanel terminal){
		for (int x = 0; x < screenWidth; x++)
		for (int y = 0; y < screenHeight; y++){
			Tile t = world.tile(x + getScrollX(), y + getScrollY());
			Weapon w = world.item(x + getScrollX(), y + getScrollY());
			
			if (w == null) {
				terminal.write(
						t.glyph(), 
						x, y+1, 
						t.color(),
						t.background());
			} else {
				terminal.write(
						w.glyph(), 
						x, y+1, 
						w.color(),
						t.background());
			}
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

	private void displayMessages(AsciiPanel terminal) {
		int i = terminal.getHeightInCharacters() - messages.size();
		for (Message m : messages)
			terminal.writeCenter(m.text(), i++);
		messages.clear();
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
        case KeyEvent.VK_COMMA:
        case KeyEvent.VK_G:
        	Weapon w = world.item(player.position.x, player.position.y);
        	if (w != null){
        		player.equip(world, w);
        	}
        	break;
		case KeyEvent.VK_M: return new WorldMapScreen(this, world.map(), player.position);
		}
		
		world.update();
		
		if (player.hp() < 1) {
			MessageBus.unsubscribe(this);
			return new DeadScreen();
		}
		
		return this;
	}

	@Override
	public void handle(Message message) {
		if (message.involves(player) && !Moved.class.isAssignableFrom(message.getClass()))
			messages.add(message);
	}
}
