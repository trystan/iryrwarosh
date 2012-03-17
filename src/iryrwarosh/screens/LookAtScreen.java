package iryrwarosh.screens;

import iryrwarosh.Creature;
import iryrwarosh.Item;
import iryrwarosh.Tile;
import iryrwarosh.World;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiCharacterData;
import asciiPanel.AsciiPanel;
import asciiPanel.TileTransformer;

public class LookAtScreen implements Screen {
	private Screen previous;
	private World world;
	private Creature player;
	private int lookX = 40;
	private int lookY = 11;
	private int startX;
	private int startY;
	
	public LookAtScreen(Screen previous, World world, Creature player, int startX, int startY){
		this.previous = previous;
		this.world = world;
		this.player = player;
		this.startX = startX;
		this.startY = startY;
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		previous.displayOutput(terminal);
		
		terminal.withEachTile(lookX-1, lookY, 3, 3, new TileTransformer(){

			@Override
			public void transformTile(int x, int y, AsciiCharacterData data) {
				if (y ==0)
					return;
				
				data.foregroundColor = data.foregroundColor.brighter().brighter();
				data.backgroundColor = data.backgroundColor.brighter().brighter().brighter();
				
				if (x == lookX && y == lookY+1){
					data.foregroundColor = data.foregroundColor.brighter().brighter();
					data.backgroundColor = data.backgroundColor.brighter().brighter().brighter();
				}
			}
			
		});

		Creature creature = world.creature(startX + lookX, startY + lookY);
		Item item = world.item(startX + lookX, startY + lookY);
		Tile tile = world.tile(startX + lookX, startY + lookY);
		
		String text = null;
		
		if (creature != null && player.canSee(creature))
			text = creature.description();
		else if (item != null)
			text = item.name();
		else
			text = tile.description();

		if (text.length() > 78)
			text = text.substring(0, 75) + "...";
		
		terminal.write(text, 1, 23);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		switch (key.getKeyCode()){
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_4:
        case KeyEvent.VK_H: moveBy(-1, 0); break;
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_6:
        case KeyEvent.VK_L: moveBy( 1, 0); break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_8:
        case KeyEvent.VK_K: moveBy( 0,-1); break;
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_2:
        case KeyEvent.VK_J: moveBy( 0, 1); break;
        case KeyEvent.VK_7:
        case KeyEvent.VK_Y: moveBy(-1,-1); break;
        case KeyEvent.VK_9:
        case KeyEvent.VK_U: moveBy( 1,-1); break;
        case KeyEvent.VK_1:
        case KeyEvent.VK_B: moveBy(-1, 1); break;
        case KeyEvent.VK_3:
        case KeyEvent.VK_N: moveBy( 1, 1); break;
        default: return previous;
		}
		
		return this;
	}

	private void moveBy(int x, int y) {
		lookX = Math.max(0, Math.min(lookX + x, 79));
		lookY = Math.max(0, Math.min(lookY + y, 23));
	}

}
