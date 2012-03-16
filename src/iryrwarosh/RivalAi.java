package iryrwarosh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RivalAi {
	private int[][] baseValue;
	
	protected List<Trait> scaryTraits;
	private double explorationPercent;
	
	public RivalAi(double explorationPercent){
		scaryTraits = Arrays.asList( Trait.POISONOUS, Trait.AGGRESSIVE, Trait.REACH_ATTACK, 
			Trait.DOUBLE_ATTACK, Trait.DOUBLE_MOVE, Trait.HUNTER);
		
		this.explorationPercent = explorationPercent;
	}
	
	public void update(World world, Creature self) {
		if (baseValue == null)
			initBaseValues(world, self);
	
		self.registerAiForEvents(this);
		goToNearestInterestingThing(world, self);
	}

	private void initBaseValues(World world, Creature self) {
		baseValue = new int[80 / 3 * 19][24 / 3 * 9];
		for (int x = 0; x < world.width(); x++)
		for (int y = 0; y < world.height(); y++){
			if (self.canEnter(world.tile(x, y)))
				baseValue[x][y] = Math.random() < explorationPercent ? 1 : 0; // encourage exploration
			else
				baseValue[x][y] = -100;
		}
	}
	
	public int personalEvaluation(Creature creature){
		return 0;
	}
	
	public int personalEvaluation(Item item){
		return 0;
	}

	private void goToNearestInterestingThing(World world, Creature self) {
		if (self.isHidden())
			return;
		
		if (baseValue[self.position.x][self.position.y] == 1)
			baseValue[self.position.x][self.position.y] = 0;
		
		int[][] localValues = new int[80 / 3 * 19][24 / 3 * 9];
		
		for (Creature other : world.creaturesNear(self)){
			int value = -1;
			int radius = 2;
			
			for (Trait trait : scaryTraits){
				if (other.description().contains(trait.description())){
					value--;
					radius++;
				}
			}
			
			if (self.hearts() < 5)
				value--;
			
			if (other.glyph() == '@'){
				radius += 5;
				
				if (self.hearts() > other.hearts())
					value += 5;
			}
			
			if (other.description().contains(Trait.AGGRESSIVE.description()) 
					&& self.position.distanceTo(other.position) == 1)
				value = 9;

			value += personalEvaluation(other);
			
			add(localValues, value, radius, world, self, other.position);
		}
		
		for (Pair<Item,Point> pair : world.itemsNear(self)){
			int value = pair.first.collectableValue();
			
			if (self.hearts() == self.maxHearts() && pair.first.name().equals("heart"))
				value = 0;

			value += personalEvaluation(pair.first);
			
			add(localValues, value, value , world, self, pair.second);
		}
		
		Point next = adjacentInterstingThing(self, localValues);
		
		if (next == null)
			next = nextMove(self, baseValue, localValues);
		
		if (next == null){
			self.wander(world);
		} else {
			Point diff = next.minus(self.position);
			self.moveBy(world, diff.x, diff.y);
		}
	}

	private Point adjacentInterstingThing(Creature self, int[][] localValues) {
		Point next = null;
		int highestNumber = 0;
		for (Point p : self.position.neighbors()){
			if (p.x < 0 || p.y < 0 || p.x >= localValues.length || p.y >= localValues[0].length)
				continue;
				
			int value = baseValue[p.x][p.y] + localValues[p.x][p.y];
			if (value > highestNumber){
				highestNumber = value;
				next = p;
			}
		}
		return next;
	}
	
	int[][] done = new int[80 / 3 * 19][24 / 3 * 9];
	int doneCounter;
	private void add(int[][] map, int amount, int maxDistance, World world, Creature self, Point position) {
		doneCounter++;
		int step = amount > 0 ? -1 : 1;

		List<Point> frontiers = new ArrayList<Point>();
		List<Point> nextFrontiers = new ArrayList<Point>();
		
		frontiers.add(position);
		
		while (amount != 0){
			int loops = 0;
			while (frontiers.size() > 0){
				if (loops++ == 1000)
					break;
				
				Point current = frontiers.remove(0);
				
				if (done[current.x][current.y] == doneCounter)
					continue;
				
				if (current.distanceTo(position) >= maxDistance)
					continue;
				
				if (current.x < 0 || current.y < 0 || current.x >= map.length || current.y >= map[0].length)
					continue;
				
				map[current.x][current.y] += amount;
				done[current.x][current.y] = doneCounter;
				
				for (Point p : current.neighbors()){
					if (p.x < 0 || p.y < 0 || p.x >= map.length || p.y >= map[0].length)
						continue;
					
					if (done[p.x][p.y] != doneCounter && self.canEnter(world.tile(p.x, p.y)))
						nextFrontiers.add(p);
				}
			}
			amount += step;
			frontiers.clear();
			frontiers.addAll(nextFrontiers);
			nextFrontiers.clear();
		}
	}
	
	private Point nextMove(Creature self, int[][] map1, int[][] map2){
		Point[][] parent = new Point[80 / 3 * 19][24 / 3 * 9];
		
		List<Pair<Point,Point>> frontiers = new ArrayList<Pair<Point,Point>>();
		
		for (Point p : self.position.neighbors())
			frontiers.add(new Pair<Point,Point>(self.position, p));
		
		Point current = null;
		int counter = 0;
		while (frontiers.size() > 0){
			Pair<Point,Point> pair = frontiers.remove(0);
			current = pair.second;
			
			if (counter++ == 5000)
				break;
			
			if (current.x < 0 || current.y < 0 || current.x >= map1.length || current.y >= map1[0].length)
				continue;
			
			int value = map1[current.x][current.y] + map2[current.x][current.y];
			
			if (value < -90)
				continue;
			
			if (parent[current.x][current.y] != null)
				continue;
			
			parent[current.x][current.y] = pair.first;

			if (value > 0)
				break;
			
			for (Point p : current.neighbors()){
				frontiers.add(new Pair<Point,Point>(current, p));
			}
		}
		
		if (current == null || frontiers.size() == 0)
			return null;
		
		current = walkBackToStartPoint(self.position, parent, current);
		
		return current;
	}

	private Point walkBackToStartPoint(Point start, Point[][] parent, Point current) {
		Point next = parent[current.x][current.y];
		while (next != null){
			if (next.x == start.x && next.y == start.y)
				break;
			current = next;
			next = parent[current.x][current.y];
		}
		return current;
	}
	
	public void handle(Creature self, Message message){
		if (Attacked.class.isAssignableFrom(message.getClass())){
			MessageBus.publish(new SaidOutLoud(self, "Ouch!"));
		}
		if (Killed.class.isAssignableFrom(message.getClass())){
			if (((Killed)message).attacked.isMiniboss())
			MessageBus.publish(new SaidOutLoud(self, "Die " + ((Killed)message).attacked.name() + "!"));
		}
		if (DiscoveredLostArtifact.class.isAssignableFrom(message.getClass())){
			MessageBus.publish(new SaidOutLoud(self, "Shiny!"));
		}
	}
}
