package iryrwarosh.screens;

import iryrwarosh.Creature;
import iryrwarosh.CreatureTrait;
import iryrwarosh.Factory;
import iryrwarosh.Handler;
import iryrwarosh.Item;
import iryrwarosh.Killed;
import iryrwarosh.Message;
import iryrwarosh.MessageBus;
import iryrwarosh.Moved;
import iryrwarosh.Projectile;
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
	private Factory factory;
	
	private int screenWidth = 80;
	private int screenHeight = 23;
	
	private List<Message> messages = new ArrayList<Message>();
	
	public PlayScreen(World world, Factory factory, Creature player){
		MessageBus.subscribe(this);
		this.world = world;
		this.factory = factory;
		this.player = player;
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
		
		terminal.write("Weilding ", 1, 0, AsciiPanel.white, bg);
		terminal.write(player.weapon().name(), player.weapon().color(), bg);
		
		terminal.write(" and wearing ", AsciiPanel.white, bg);
		terminal.write(player.armor().name(), player.armor().color(), bg);
		
		terminal.write("evade: " + player.evadePercent(world) + "%", 45, 0, AsciiPanel.yellow, bg);

		terminal.write(player.money() + "" + (char)4, 65, 0, Tile.hsv(60, 25, 75), bg);
		
		Item item = world.item(player.position.x, player.position.y);
		if (item != null)
			terminal.write("(here: " + item.name() + ")", 29, 0, item.color(), bg);
		
		Color heartColor = player.isPoisoned() ? AsciiPanel.green : AsciiPanel.red;
		for (int i = 0; i < player.maxHp(); i++)
			terminal.write((char)3, 69+i, 0, i < player.hp() ? heartColor : AsciiPanel.brightBlack, bg);
	}
	
	private void displayTiles(AsciiPanel terminal){
		for (int x = 0; x < screenWidth; x++)
		for (int y = 0; y < screenHeight; y++){
			Tile t = world.tile(x + getScrollX(), y + getScrollY());
			Item item = world.item(x + getScrollX(), y + getScrollY());
			
			if (item == null) {
				terminal.write(
						t.glyph(), 
						x, y+1, 
						t.color(),
						t.background());
			} else {
				terminal.write(
						item.glyph(), 
						x, y+1, 
						item.color(),
						t.background());
			}
		}
		
		for (Creature c : world.creatures()){
			int x = c.position.x - getScrollX();
			int y = c.position.y - getScrollY();
			
			if (x < 0 || x >= screenWidth || y < 0 || y >= screenHeight)
				continue;
			
			Color color = c.color();
			if (c.hasTrait(CreatureTrait.CAMOUFLAGED)){
				switch (player.position.distanceTo(c.position)){
				case 0:
				case 1:
				case 2:
				case 3:
					break;
				case 4:
					color = color.darker();
					break;
				case 5:
					color = color.darker().darker();
					break;
				default:
					color = null;
				}
			}
			
			if (color != null)
				terminal.write(c.glyph(), 
					x, y+1, 
					color, 
					world.tile(c.position.x, c.position.y).background());
		}

		for (Projectile p : world.projectiles()){
			int x = p.position.x - getScrollX();
			int y = p.position.y - getScrollY();
			
			if (x < 0 || x >= screenWidth || y < 0 || y >= screenHeight)
				continue;
			
			terminal.write(p.glyph(), 
				x, y+1, 
				p.color(), 
				world.tile(p.position.x, p.position.y).background());
		}
	}

	private void displayMessages(AsciiPanel terminal) {
		int i = terminal.getHeightInCharacters() - messages.size();
		for (Message m : messages)
			terminal.writeCenter(m.text().replace("The player", "You").replace("the player", "you").replace("player", "you"), i++);
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
        	Item item = world.item(player.position.x, player.position.y);
        	if (item != null && Weapon.class.isInstance(item)){
        		player.equip(world, (Weapon)item);
        	}
        	break;
		case KeyEvent.VK_M: return new WorldMapScreen(this, world.map(), player.position);
		case KeyEvent.VK_X: return new LookAtScreen(this, world, player, getScrollX(), getScrollY());
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
		
		if (Killed.class.isAssignableFrom(message.getClass()))
			addRandomBadGuy();
	}

	private void addRandomBadGuy() {
		if (Math.random() < 0.25){
			factory.goblin(world);
		} else {
			Tile[] biomes = { Tile.GREEN_TREE1, Tile.PINE_TREE1, Tile.BROWN_TREE1, Tile.BROWN_TREE4, Tile.WHITE_TREE1, 
				Tile.GREEN_ROCK, Tile.BROWN_ROCK, Tile.WHITE_ROCK, Tile.DESERT_SAND1, Tile.WATER1 };
			Tile biome = biomes[(int)(Math.random() * biomes.length)];
			factory.monster(world, biome);
		}
	}
}
