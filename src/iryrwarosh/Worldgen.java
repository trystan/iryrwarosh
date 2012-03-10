package iryrwarosh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
		addThemes();
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
	
	public void addThemes(){
		Tile[][] themes = new Tile[cells.length][cells[0].length];
		for (Tile theme : new Tile[]{ 
				Tile.BROWN_ROCK, Tile.BROWN_TREE, 
				Tile.BROWN_ROCK, Tile.BROWN_TREE, 
				Tile.GREEN_ROCK, Tile.GREEN_TREE, 
				Tile.GREEN_ROCK, Tile.GREEN_TREE, 
				Tile.WHITE_ROCK, Tile.WHITE_TREE}){
		
			while (true){
				int x = (int)(Math.random() * themes.length);
				int y = (int)(Math.random() * themes[0].length);
				
				if (themes[x][y] == null){
					themes[x][y] = theme;
					break;
				}
			}
		}
		
		spreadThemesUntilComplete(themes);
		setThemeGround();
	}

	private void spreadThemesUntilComplete(Tile[][] themes) {
		int unthemedCount = 0;

		do {
			themes = spreadThemesOnce(themes);

			unthemedCount = 0;
			for (int x = 0; x < themes.length; x++)
			for (int y = 0; y < themes[0].length; y++){
				if (themes[x][y] == null)
					unthemedCount++;
			}
			
		} while (unthemedCount > 0);
		
		placeThemes(themes);
	}
	
	private Tile[][] spreadThemesOnce(Tile[][] themes) {
		Tile[][] themes2 = new Tile[themes.length][themes[0].length];
		List<Character> directions = Arrays.asList('N', 'S', 'W', 'E');
		
		for (int x = 0; x < themes.length; x++)
		for (int y = 0; y < themes[0].length; y++){
			if (themes[x][y] != null) {
				themes2[x][y] = themes[x][y];
			} else {
				Collections.shuffle(directions);
				
				for (Character direction : directions){
					switch (direction){
					case 'N':
						if (cells[x][y].nEdge != WorldScreen.WALL && themes[x][y-1] != null)
							themes2[x][y] = themes[x][y-1];
						break;
					case 'S':
						if (cells[x][y].sEdge != WorldScreen.WALL && themes[x][y+1] != null)
							themes2[x][y] = themes[x][y+1];
						break;
					case 'W':
						if (cells[x][y].wEdge != WorldScreen.WALL && themes[x-1][y] != null)
							themes2[x][y] = themes[x-1][y];
						break;
					case 'E':
						if (cells[x][y].eEdge != WorldScreen.WALL && themes[x+1][y] != null)
							themes2[x][y] = themes[x+1][y];
						break;
					}
				}
			}
		}
	
		return themes2;
	}

	private void placeThemes(Tile[][] themes) {
		for (int x = 0; x < themes.length; x++)
		for (int y = 0; y < themes[0].length; y++){
			if (themes[x][y] != null){
				cells[x][y].defaultWall = themes[x][y];
			}
		}
	}
	
	private void setThemeGround(){
		for (int x = 0; x < cells.length; x++)
		for (int y = 0; y < cells[0].length; y++){
			switch (cells[x][y].defaultWall){
			case BROWN_ROCK:
			case BROWN_TREE:
				cells[x][y].defaultGround = Tile.BROWN_DIRT;
				break;
			case GREEN_ROCK:
			case GREEN_TREE:
				cells[x][y].defaultGround = Tile.GREEN_DIRT;
				break;
			case WHITE_ROCK:
			case WHITE_TREE:
				cells[x][y].defaultGround = Tile.WHITE_DIRT;
				break;
			}
		}
	}
}
