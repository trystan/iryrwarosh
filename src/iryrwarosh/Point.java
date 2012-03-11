package iryrwarosh;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Point {
	public int x;
	public int y;
	
	public Point(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Point copy() {
		return new Point(x, y);
	}
	
	public int distanceTo(Point other){
		return (int)Math.sqrt((x-other.x)*(x-other.x) + (y-other.y)*(y-other.y)); 
	}

	public Collection<? extends Point> neighbors() {
		List<Point> neighbors = Arrays.asList(
				new Point(x+1,y-1), new Point(x+0,y-1), new Point(x+1,y-1), 
				new Point(x+1,y), new Point(x-1,y),
				new Point(x-1,y+1), new Point(x+0,y+1), new Point(x+1,y+1));
				
		Collections.shuffle(neighbors);
		return neighbors;
	}
}
