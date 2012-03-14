package iryrwarosh.screens;

import iryrwarosh.Creature;
import iryrwarosh.Point;
import iryrwarosh.Projectile;
import iryrwarosh.Tile;
import iryrwarosh.Trait;
import iryrwarosh.World;

import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;

public class CastAdvancedSpellScreen implements Screen {
	private Screen previous;
	private World world;
	private Creature player;
	
	public CastAdvancedSpellScreen(Screen previous, World world, Creature player){
		this.previous = previous;
		this.world = world;
		this.player = player;
	}
	
	@Override
	public void displayOutput(AsciiPanel terminal) {
		previous.displayOutput(terminal);
		
		terminal.clear(' ', 1, 20, 31, 4);
		terminal.write("What do you want to cast?", 1, 20);
		terminal.write(" [1] Magic Missiles    cost 10", 1, 21)
				.write((char)4, Tile.hsv(60, 25, 75));
		terminal.write(" [2] Summon imp        cost 15", 1, 22)
				.write((char)4, Tile.hsv(60, 25, 75));
		terminal.write(" [3] Mutate self       cost 20", 1, 23)
				.write((char)4, AsciiPanel.red);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key) {
		switch (key.getKeyChar()){
		case '1': magicMissiles(); break;
		case '2': summonImp(); break;
		case '3': mutateSelf(); break;
		}
		
		return previous;
	}

	private void magicMissiles() {
		world.add(new Projectile(player, 7, Tile.hsv(210, 33, 66), 2, player.position.plus(1, 0), new Point( 1, 0)));
		world.add(new Projectile(player, 7, Tile.hsv(210, 33, 66), 2, player.position.plus(-1, 0), new Point(-1, 0)));
		world.add(new Projectile(player, 7, Tile.hsv(210, 33, 66), 2, player.position.plus(0, 1), new Point( 0, 1)));
		world.add(new Projectile(player, 7, Tile.hsv(210, 33, 66), 2, player.position.plus(0,-1), new Point( 0,-1)));
		world.add(new Projectile(player, 7, Tile.hsv(210, 33, 66), 2, player.position.plus(-1,-1), new Point(-1,-1)));
		world.add(new Projectile(player, 7, Tile.hsv(210, 33, 66), 2, player.position.plus(-1, 1), new Point(-1, 1)));
		world.add(new Projectile(player, 7, Tile.hsv(210, 33, 66), 2, player.position.plus(1,-1), new Point( 1,-1)));
		world.add(new Projectile(player, 7, Tile.hsv(210, 33, 66), 2, player.position.plus(1, 1), new Point( 1, 1)));
		player.loseRupees(world, 10);
	}
	
	private void summonImp() {
		Creature imp = new Creature("imp", (char)139, Tile.hsv(350, 90, 75), 10){
			public void update(World world){
				super.update(world);
				wander(world);
			}
		};
		imp.addTrait(Trait.FLIER);
		imp.addTrait(Trait.LOOTLESS);
		imp.addTrait(Trait.REGENERATES);
		world.add(imp);
		Point dir = player.lastMovedDirection();
		imp.position = player.position.plus(dir.x, dir.y).plus(dir.x, dir.y);
		
		player.loseRupees(world, 15);
	}

	private void mutateSelf() {
		Trait trait = Trait.getRandom();
		while (player.hasTrait(trait))
			trait = Trait.getRandom();
		
		player.addTrait(trait);
		player.loseRupees(world, 20);
	}
}
