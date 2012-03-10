package iryrwarosh;

import java.util.ArrayList;
import java.util.List;

public class Worldgen {
	private WorldScreen[][] cells;
	
	public Worldgen(int width, int height) {
		this.cells = new WorldScreen[width][height];

		for (int x = 0; x < cells.length; x++)
		for (int y = 0; y < cells[0].length; y++){
			this.cells[x][y] = new WorldScreen();
		}
	}

	public World build(){
		makePerfectMaze();
		addExtraConnections();
		return new World(new WorldMap(cells));
	}
	
	private void makePerfectMaze(){
		int width = cells.length;
		int height = cells[0].length;
		boolean[][] connected = new boolean[cells.length][cells[0].length];
		
		List<Point> path = new ArrayList<Point>();
		path.add(new Point((int)(Math.random() * cells.length), (int)(Math.random() * cells[0].length)));
		
		while (!path.isEmpty()) {
			Point p = path.remove((int) (Math.random() * path.size()));

			String possibleDirections = "";

			if (p.x + 1 < width && !connected[p.x + 1][p.y]) 
				possibleDirections += 'E';

			if (p.x - 1 >= 0 && !connected[p.x - 1][p.y])
				possibleDirections += 'W';

			if (p.y + 1 < height && !connected[p.x][p.y + 1])
				possibleDirections += 'S';

			if (p.y - 1 >= 0 && !connected[p.x][p.y - 1])
				possibleDirections += 'N';

			if (possibleDirections.length() > 0) {
				char direction = possibleDirections.charAt((int) (Math.random() * possibleDirections.length()));

				if (possibleDirections.length() > 1)
					path.add(p.copy());

				connected[p.x][p.y] = true;

				connectScreens(p, direction);

				connected[p.x][p.y] = true;
				path.add(p.copy());
			}
		}
	}

	private void addExtraConnections(){
		int width = cells.length;
		int height = cells[0].length;
		int total = Math.max(width, height);
		
		while (total-- > 0) {
			Point p = new Point((int)(Math.random() * width),
					            (int)(Math.random() * height));

			String possibleDirections = "";

			if (p.x + 1 < width && cells[p.x][p.y].eEdge == WorldScreen.WALL) 
				possibleDirections += 'E';

			if (p.x - 1 >= 0 && cells[p.x][p.y].wEdge == WorldScreen.WALL)
				possibleDirections += 'W';

			if (p.y + 1 < height && cells[p.x][p.y].sEdge == WorldScreen.WALL)
				possibleDirections += 'S';

			if (p.y - 1 >= 0 && cells[p.x][p.y].nEdge == WorldScreen.WALL)
				possibleDirections += 'N';

			if (possibleDirections.length() > 0) {
				char direction = possibleDirections.charAt((int) (Math.random() * possibleDirections.length()));
				
				connectScreens(p, direction);
			}
		}
	}

	private void connectScreens(Point p, char direction) {
		int pathType = WorldScreen.CENTER;
		switch (direction){
		case 'N':
			cells[p.x][p.y-1].sEdge = pathType;
			cells[p.x][p.y].nEdge = pathType;
			p.y--;
			break;
		case 'S':
			cells[p.x][p.y+1].nEdge = pathType;
			cells[p.x][p.y].sEdge = pathType;
			p.y++;
			break;
		case 'W':
			cells[p.x-1][p.y].eEdge = pathType;
			cells[p.x][p.y].wEdge = pathType;
			p.x--;
			break;
		case 'E':
			cells[p.x+1][p.y].wEdge = pathType;
			cells[p.x][p.y].eEdge = pathType;
			p.x++;
			break;
		}
	}
}
