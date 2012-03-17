package iryrwarosh.screens;

import iryrwarosh.Creature;
import iryrwarosh.FameHandler;
import iryrwarosh.GainedFame;
import iryrwarosh.SaidOutLoud;
import iryrwarosh.Trait;
import iryrwarosh.Factory;
import iryrwarosh.Handler;
import iryrwarosh.Item;
import iryrwarosh.Killed;
import iryrwarosh.Message;
import iryrwarosh.MessageBus;
import iryrwarosh.Moved;
import iryrwarosh.Note;
import iryrwarosh.Projectile;
import iryrwarosh.Tile;
import iryrwarosh.Common;
import iryrwarosh.World;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import asciiPanel.AsciiCharacterData;
import asciiPanel.AsciiPanel;
import asciiPanel.TileTransformer;

public class PlayScreen implements Screen, Handler {
	private FameHandler fameHandler;
	
	private World world;
	private Creature player;
	private Factory factory;
	
	private int screenWidth = 80;
	private int screenHeight = 23;
	
	private List<Message> messages = new ArrayList<Message>();
	
	private MessageLogScreen messageLogScreen;
	
	private int turnNumber = 0;
	
	public PlayScreen(World world, Factory factory, Creature player){
		MessageBus.subscribe(this);
		this.fameHandler = new FameHandler();
		this.world = world;
		this.factory = factory;
		this.player = player;
		this.messageLogScreen = new MessageLogScreen(this);
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.setDefaultForegroundColor(Common.playScreenForeground);
		terminal.setDefaultBackgroundColor(Common.playScreenBackground);
		displayTiles(terminal);
		displayHud(terminal);
		displayMessages(terminal);
	}

	private void displayHud(AsciiPanel terminal) {
		terminal.clear(' ', 0, 0, 80, 1);
		
		terminal.write("[z] ", 0, 0);
		terminal.write(player.leftHand().name(), player.leftHand().color(), null);
		
		terminal.write("[x] ", 22, 0);
		terminal.write(player.rightHand().name(), player.rightHand().color(), null);

		terminal.write("[?] help", 44, 0);

		terminal.write("evade: " + player.evadePercent(world) + "%", 54, 0, AsciiPanel.yellow, null);

		terminal.write(String.format("%3d" + (char)4, player.rupees()), 64, 0, Common.hsv(60, 25, 75), null);
		
		Item item = world.item(player.position.x, player.position.y);
		if (item != null && item.canBePickedUp()){
			terminal.write("[g] ", 0, 1);
			terminal.write(item.name(), item.color(), null);
			terminal.write(" (at your feet)");
		}

		terminal.setCursorPosition(69, 0);
		Color heartColor = player.isPoisoned() ? AsciiPanel.green : AsciiPanel.red;
		for (int i = 0; i < player.maxHearts(); i++){
			if (i > 0 && (i % 10) == 0)
				terminal.setCursorPosition(69, terminal.getCursorY() + 1);
			terminal.write((char)3, i < player.hearts() ? heartColor : AsciiPanel.brightBlack, null);
		}

		displayFame(terminal);
	}

	private void displayFame(AsciiPanel terminal) {
		List<Creature> people = fameHandler.getFamousPeople();
		int left = 68;
		
		if (people.size() == 0)
			return;
		
		if (getScrollX() == world.width() - screenWidth && getScrollY() == 0)
			terminal.setCursorPosition(left, terminal.getHeightInCharacters() - 7);
		else
			terminal.setCursorPosition(left, terminal.getCursorY() + 2);
		
		terminal.write("-- fame --", AsciiPanel.brightGreen, null);
		terminal.setCursorPosition(left, terminal.getCursorY() + 1);
		for (Creature famousPerson : people){
			terminal.write(" " + (famousPerson.isPlayer() ? "You " : famousPerson.name()), famousPerson.color(), null);
			terminal.write(String.format(" %3d%%", fameHandler.getFame(famousPerson)), AsciiPanel.white, null);
			terminal.setCursorPosition(left, terminal.getCursorY() + 1);
		}
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
			if (c.hasTrait(Trait.CAMOUFLAGED)){
				switch (player.position.distanceTo(c.position)){
				case 0:
				case 1:
				case 2:
				case 3:
					break;
				case 4:
					if (!player.hasTrait(Trait.DETECT_CAMOUFLAGED))
						color = color.darker();
					break;
				case 5:
					if (!player.hasTrait(Trait.DETECT_CAMOUFLAGED))
						color = color.darker().darker();
					break;
				default:
					if (!player.hasTrait(Trait.DETECT_CAMOUFLAGED))
						color = null;
					else
						color = color.darker();
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
		
		fadeIn(terminal);
		turnNumber++;
	}

	private void fadeIn(AsciiPanel terminal) {
		if (turnNumber == 0)
			terminal.withEachTile(new TileTransformer(){
				@Override
				public void transformTile(int x, int y, AsciiCharacterData data) {
					if (y== 0)
						return;
					if (data.character == '@' && data.foregroundColor == AsciiPanel.brightWhite)
						return;
					
					data.foregroundColor = data.foregroundColor.darker().darker();
					data.backgroundColor = data.backgroundColor.darker().darker();
				}
			});
		else if (turnNumber == 1)
			terminal.withEachTile(new TileTransformer(){
				@Override
				public void transformTile(int x, int y, AsciiCharacterData data) {
					if (y== 0)
						return;
					if (data.character == '@' && data.foregroundColor == AsciiPanel.brightWhite)
						return;
					
					data.foregroundColor = data.foregroundColor.darker();
					data.backgroundColor = data.backgroundColor.darker();
				}
			});
	}

	private void displayMessages(AsciiPanel terminal) {
		int i = terminal.getHeightInCharacters() - messages.size();
		for (Message m : messages){
			Color color = (GainedFame.class.isAssignableFrom(m.getClass())) ? AsciiPanel.brightYellow : null;

			if (SaidOutLoud.class.isAssignableFrom(m.getClass())) 
				color = ((SaidOutLoud)m).creature.color();
			
			terminal.writeCenter(clean(m.text()), i++, color, AsciiPanel.black);
		}
		messages.clear();
	}
	
	private String clean(String text){
		return text.replace("player has", "player have")
		           .replace("The player's", "Your").replace("the player's", "your").replace("player's", "your")
			       .replace("The player", "You").replace("the player", "you").replace("player", "you");
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
		if (player.hearts() < 1) {
			MessageBus.unsubscribe(this);
			return new DeadScreen(this, player.causeOfDeath());
		}
		
		switch (key.getKeyCode()){
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_NUMPAD4:
        case KeyEvent.VK_H: moveBy(-1, 0); break;
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_NUMPAD6:
        case KeyEvent.VK_L: moveBy( 1, 0); break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_NUMPAD8:
        case KeyEvent.VK_K: moveBy( 0,-1); break;
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_NUMPAD2:
        case KeyEvent.VK_J: moveBy( 0, 1); break;
        case KeyEvent.VK_NUMPAD7:
        case KeyEvent.VK_Y: moveBy(-1,-1); break;
        case KeyEvent.VK_NUMPAD9:
        case KeyEvent.VK_U: moveBy( 1,-1); break;
        case KeyEvent.VK_NUMPAD1:
        case KeyEvent.VK_B: moveBy(-1, 1); break;
        case KeyEvent.VK_NUMPAD3:
        case KeyEvent.VK_N: moveBy( 1, 1); break;
        case KeyEvent.VK_PERIOD: moveBy( 0, 0); break;
        case KeyEvent.VK_Z: 
        	Screen lhs = player.leftHand().use(this, world, player);
        	if (lhs != this) 
        		return lhs;
        	break;
        case KeyEvent.VK_X: 
        	Screen rhs = player.rightHand().use(this, world, player);
        	if (rhs != this) 
        		return rhs;
        	break;
        case KeyEvent.VK_G:
        case KeyEvent.VK_COMMA:
        	Item item = world.item(player.position.x, player.position.y);
            if (item == null || !item.canBePickedUp()) {
                MessageBus.publish(new Note(world, player, "Nothing to pick up here"));
                return this; //Don't spend an action when nothing to pick up
            } else {
                return new PickupItemScreen(this, world, player);
            }
		case KeyEvent.VK_M: return new WorldMapScreen(this, world.map(), player.position);
		case KeyEvent.VK_SPACE: return new LookAtScreen(this, world, player, getScrollX(), getScrollY());
		case KeyEvent.VK_Q:
		case KeyEvent.VK_ESCAPE:
			return new ConfirmationScreen(this, new ChooseStartingItemsScreen(), "Are you sure you'd like to quit?");
		case KeyEvent.VK_QUOTEDBL:
		case KeyEvent.VK_QUOTE:
			messageLogScreen.scrollToEnd();
			return messageLogScreen;
		default:
			if (key.getKeyChar() == '?')
				return new HelpScreen(this);
			
			return this;
		}
		
		world.update();
		
		if (player.hearts() < 1) {
			MessageBus.unsubscribe(this);
			return new DeadScreen(this, player.causeOfDeath());
		} else if (fameHandler.getFame(player) > 99){
			MessageBus.unsubscribe(this);
			return new VictoryScreen(this);
		} else {
			for (Creature creature : fameHandler.getFamousPeople()){
				if (fameHandler.getFame(creature) > 99)
					return new LostScreen(this);
			}
		}
		
		return this;
	}

	@Override
	public void handle(Message message) {
		fameHandler.handle(message);
		
		if (message.involves(player) && !Moved.class.isAssignableFrom(message.getClass())){
			messages.add(message);
			messageLogScreen.record(message);
		}
		
		if (Killed.class.isAssignableFrom(message.getClass()))
			addRandomBadGuy();
	}

	private void addRandomBadGuy() {
		if (Math.random() < 0.25){
			factory.goblin(world);
		} else {
			Tile[] biomes = { Tile.GREEN_TREE1, Tile.BROWN_TREE1, Tile.WHITE_TREE1, Tile.GREEN_TREE1, Tile.BROWN_TREE1, 
				Tile.GREEN_ROCK, Tile.BROWN_ROCK, Tile.WHITE_ROCK, Tile.DESERT_SAND1, Tile.WATER1 };
			Tile biome = biomes[(int)(Math.random() * biomes.length)];
			factory.monster(world, biome);
		}
	}
}
