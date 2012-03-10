package iryrwarosh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Worldgen {
	private WorldScreen[][] cells;
	private Tile[][] tiles;
	private int screenWidth = 19;
	private int screenHeight = 9;
	
	public Worldgen(int width, int height) {
		this.cells = new WorldScreen[width][height];
		this.tiles = new Tile[width * screenWidth][height * screenHeight];

		for (int x = 0; x < cells.length; x++)
		for (int y = 0; y < cells[0].length; y++){
			this.cells[x][y] = new WorldScreen();
		}
	}

	public World build(){
		makePerfectMaze();
		addExtraConnections();
		addThemes();
		addDesert();
		setTiles();
		addLake();
		addLake();
		return new World(tiles, new WorldMap(cells));
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
	
	private void addDesert(){
		int x = (int)(Math.random() * cells.length - 2) + 1;
		int y = (int)(Math.random() * cells[0].length - 2) + 1;
		
		cells[x][y].defaultGround = Tile.DESERT_SAND;
		cells[x][y].defaultWall = Tile.BROWN_ROCK;
		cells[x][y].sEdge = WorldScreen.CENTER;
		cells[x][y].eEdge = WorldScreen.CENTER;

		cells[x+1][y].defaultGround = Tile.DESERT_SAND;
		cells[x+1][y].defaultWall = Tile.BROWN_ROCK;
		cells[x+1][y].sEdge = WorldScreen.CENTER;
		cells[x+1][y].wEdge = WorldScreen.CENTER;

		cells[x][y+1].defaultGround = Tile.DESERT_SAND;
		cells[x][y+1].defaultWall = Tile.BROWN_ROCK;
		cells[x][y+1].nEdge = WorldScreen.CENTER;
		cells[x][y+1].eEdge = WorldScreen.CENTER;

		cells[x+1][y+1].defaultGround = Tile.DESERT_SAND;
		cells[x+1][y+1].defaultWall = Tile.BROWN_ROCK;
		cells[x+1][y+1].nEdge = WorldScreen.CENTER;
		cells[x+1][y+1].wEdge = WorldScreen.CENTER;
	}
	
	private void setTiles(){
		for (int x = 0; x < cells.length; x++)
		for (int y = 0; y < cells[0].length; y++)
			setTiles(x, y);

		for (int x = 0; x < cells.length; x++)
		for (int y = 0; y < cells[0].length; y++)
			addBorderOpenings(x, y);
	}

	private void setTiles(int sx, int sy){
		if (cells[sx][sy].defaultGround == Tile.DESERT_SAND){
			addMap(sx, sy, 
				    "###################"
				  + "#.................#"
				  + "#.................#"
				  + "#.................#"
				  + "#.................#"
				  + "#.................#"
				  + "#.................#"
				  + "#.................#"
				  + "###################");
			return;
		}
		
		switch ((int)(Math.random() * 7)){
		case 0:
			setTilesFull(sx, sy);
			break;
		case 1:
		case 2:
			setTilesOuter(sx, sy);
			setTilesInner(sx, sy);
			break;
		case 3:
		case 4:
			setTilesLeft(sx, sy);
			setTilesRight(sx, sy);
			break;
		case 5:
		case 6:
			setTilesTop(sx, sy);
			setTilesBottom(sx, sy);
			break;
		}

		switch ((int)(Math.random() * 60)){
		case 0: setTilesOuter(sx, sy); break;
		case 1: setTilesInner(sx, sy); break;
		case 2: setTilesLeft(sx, sy); break;
		case 3: setTilesRight(sx, sy); break;
		case 4: setTilesTop(sx, sy); break;
		case 5: setTilesBottom(sx, sy); break;
		}
	}
	
	private void setTilesFull(int sx, int sy){
		switch ((int)(Math.random() * 6)){
		case 0:
			addMap(sx, sy, 
				    "###################"
				  + "#.................#"
				  + "#.................#"
				  + "#.................#"
				  + "#.................#"
				  + "#.................#"
				  + "#.................#"
				  + "#.................#"
				  + "###################");
			break;
		case 1:
			addMap(sx, sy, 
				    "###################"
				  + "#.................#"
				  + "#.x.............x.#"
				  + "#.................#"
				  + "#.................#"
				  + "#.................#"
				  + "#.x.............x.#"
				  + "#.................#"
				  + "###################");
			break;
		case 2:
			addMap(sx, sy, 
				    "###################"
				  + "#.................#"
				  + "#.xxxxxxxxxxxxxxx.#"
				  + "#.xxxxxxxxxxxxxxx.#"
				  + "#.xxxxxxxxxxxxxxx.#"
				  + "#.xxxxxxxxxxxxxxx.#"
				  + "#.xxxxxxxxxxxxxxx.#"
				  + "#.................#"
				  + "###################");
			break;
		case 3:
			addMap(sx, sy, 
				    "###################"
				  + "#..x...x...x...x..#"
				  + "#x...x...x...x...x#"
				  + "#..x...x...x...x..#"
				  + "#x...x...x...x...x#"
				  + "#..x...x...x...x..#"
				  + "#x...x...x...x...x#"
				  + "#..x...x...x...x..#"
				  + "###################");
			break;
		case 4:
			addMap(sx, sy, 
				    "###################"
				  + "#.................#"
				  + "#.x.x.x.x.x.x.x.x.#"
				  + "#.................#"
				  + "#..x.x.x.x.x.x.x..#"
				  + "#.................#"
				  + "#.x.x.x.x.x.x.x.x.#"
				  + "#.................#"
				  + "###################");
			break;
		case 5:
			addMap(sx, sy, 
				    "###################"
				  + "#.................#"
				  + "#.~~~~~~~~~~~~~~~.#"
				  + "#.~~~~~~~~~~~~~~~.#"
				  + "#.~~~~~~~~~~~~~~~.#"
				  + "#.~~~~~~~~~~~~~~~.#"
				  + "#.~~~~~~~~~~~~~~~.#"
				  + "#.................#"
				  + "###################");
			break;
		}
	}
	
	private void setTilesInner(int sx, int sy){
		switch ((int)(Math.random() * 6)){
		case 0:
			addMap(sx, sy, 
					"                   "
				  + "                   "
				  + "                   "
				  + "   .............   "
				  + "   .............   "
				  + "   .............   "
				  + "                   "
				  + "                   "
				  + "                   ");
			break;
		case 1:
			addMap(sx, sy, 
					"                   "
				  + "                   "
				  + "                   "
				  + "   x...........x   "
				  + "   .............   "
				  + "   x...........x   "
				  + "                   "
				  + "                   "
				  + "                   ");
			break;
		case 2:
			addMap(sx, sy, 
					"                   "
				  + "                   "
				  + "                   "
				  + "   x...x...x...x   "
				  + "   ..x...x...x..   "
				  + "   x...x...x...x   "
				  + "                   "
				  + "                   "
				  + "                   ");
			break;
		case 3:
			addMap(sx, sy, 
					"                   "
				  + "                   "
				  + "                   "
				  + "   xxxxxxxxxxxxx   "
				  + "   xxxxxxxxxxxxx   "
				  + "   xxxxxxxxxxxxx   "
				  + "                   "
				  + "                   "
				  + "                   ");
			break;
		case 4:
			addMap(sx, sy, 
					"                   "
				  + "                   "
				  + "                   "
				  + "   .............   "
				  + "   .xxxxxxxxxxx.   "
				  + "   .............   "
				  + "                   "
				  + "                   "
				  + "                   ");
			break;
		case 5:
			addMap(sx, sy, 
					"                   "
				  + "                   "
				  + "   ~~~~~~~~~~~~~   "
				  + "   ~~~~~~~~~~~~~   "
				  + "   ~~~~~~~~~~~~~   "
				  + "   ~~~~~~~~~~~~~   "
				  + "   ~~~~~~~~~~~~~   "
				  + "                   "
				  + "                   ");
		}
	}
	
	private void setTilesOuter(int sx, int sy){
		switch ((int)(Math.random() * 5)){
		case 0:
			addMap(sx, sy, 
				    "###################"
				  + "#.................#"
				  + "#.................#"
				  + "#..             ..#"
				  + "#..             ..#"
				  + "#..             ..#"
				  + "#.................#"
				  + "#.................#"
				  + "###################");
			break;
		case 1:
			addMap(sx, sy, 
				    "###################"
				  + "#.................#"
				  + "#.x.x.x.x.x.x.x.x.#"
				  + "#..             ..#"
				  + "#.x             x.#"
				  + "#..             ..#"
				  + "#.x.x.x.x.x.x.x.x.#"
				  + "#.................#"
				  + "###################");
			break;
		case 2:
			addMap(sx, sy, 
				    "###################"
				  + "#xx.............xx#"
				  + "#x...............x#"
				  + "#..             ..#"
				  + "#..             ..#"
				  + "#..             ..#"
				  + "#x...............x#"
				  + "#xx.............xx#"
				  + "###################");
			break;
		case 3:
			addMap(sx, sy, 
				    "###################"
				  + "#.................#"
				  + "#.xxxx.......xxxx.#"
				  + "#.x             x.#"
				  + "#..             ..#"
				  + "#.x             x.#"
				  + "#.xxxx.......xxxx.#"
				  + "#.................#"
				  + "###################");
			break;
		case 4:
			addMap(sx, sy,  
				    "###################"
				  + "#.................#"
				  + "#...x.x.....x.x...#"
				  + "#.x             x.#"
				  + "#..             ..#"
				  + "#.x             x.#"
				  + "#...x.x.....x.x...#"
				  + "#.................#"
				  + "###################");
			break;
		}
	}

	private void setTilesLeft(int sx, int sy){
		switch ((int)(Math.random() * 10)){
		case 0:
			addMap(sx, sy, 
					"##########         "
			      + "#.........         "
			      + "#.........         "
			      + "#.........         "
			      + "#.........         "
			      + "#.........         "
			      + "#.........         "
			      + "#.........         "
			      + "##########         ");
			break;
		case 1:
			addMap(sx, sy, 
					"##########         "
			      + "##xx....xx         "
			      + "#x.......x         "
			      + "#.........         "
			      + "#.........         "
			      + "#.........         "
			      + "#x.......x         "
			      + "##xx....xx         "
			      + "##########         ");
			break;
		case 2:
			addMap(sx, sy, 
					"##########         "
			      + "#.........         "
			      + "#.xxx.....         "
			      + "#.xxx.....         "
			      + "#.........         "
			      + "#.xxx.....         "
			      + "#.xxx.....         "
			      + "#.........         "
			      + "##########         ");
			break;
		case 3:
			addMap(sx, sy, 
					"##########         "
			      + "#.........         "
			      + "#.........         "
			      + "#.x.......         "
			      + "#.........         "
			      + "#.x.......         "
			      + "#.........         "
			      + "#.........         "
			      + "##########         ");
			break;
		case 4:
			addMap(sx, sy, 
					"##########         "
			      + "#.........         "
			      + "#..xxxxx..         "
			      + "#.........         "
			      + "#..xxxxx..         "
			      + "#.........         "
			      + "#..xxxxx..         "
			      + "#.........         "
			      + "##########         ");
			break;
		case 5:
			addMap(sx, sy, 
					"##########         "
			      + "#.........         "
			      + "#.....xxx.         "
			      + "#.xxx.....         "
			      + "#.....xxx.         "
			      + "#.xxx.....         "
			      + "#.....xxx.         "
			      + "#.........         "
			      + "##########         ");
			break;
		case 6:
			addMap(sx, sy, 
					"##########         "
			      + "#.........         "
			      + "#.xxx.....         "
			      + "#.....xxx.         "
			      + "#.xxx.....         "
			      + "#.....xxx.         "
			      + "#.xxx.....         "
			      + "#.........         "
			      + "##########         ");
			break;
		case 7:
			addMap(sx, sy, 
					"##########         "
			      + "#.........         "
			      + "#.xxxxxx..         "
			      + "#.xxxxxx..         "
			      + "#.xxxxxx..         "
			      + "#.xxxxxx..         "
			      + "#.xxxxxx..         "
			      + "#.........         "
			      + "##########         ");
			break;
		case 8:
			addMap(sx, sy, 
					"##########         "
			      + "#.........         "
			      + "#.x.....x.         "
			      + "#...xxx...         "
			      + "#.x.....x.         "
			      + "#...xxx...         "
			      + "#.x.....x.         "
			      + "#.........         "
			      + "##########         ");
			break;
		case 9:
			addMap(sx, sy, 
					"##########         "
			      + "#..xxxxx..         "
			      + "#.........         "
			      + "#.........         "
			      + "#...xxx...         "
			      + "#.........         "
			      + "#.........         "
			      + "#..xxxxx..         "
			      + "##########         ");
			break;
		}
	}

	private void setTilesRight(int sx, int sy){
		switch ((int)(Math.random() * 10)){
		case 0:
			addMap(sx, sy,
					"          #########"
			      + "          ........#"
			      + "          ........#"
			      + "          ........#"
			      + "          ........#"
			      + "          ........#"
			      + "          ........#"
			      + "          ........#"
			      + "          #########");
			break;
		case 1:
			addMap(sx, sy, 
					"          #########"
			      + "          xx...xx##"
			      + "          x......x#"
			      + "          ........#"
			      + "          ........#"
			      + "          ........#"
			      + "          x......x#"
			      + "          xx...xx##"
			      + "          #########");
			break;
		case 2:
			addMap(sx, sy, 
					"          #########"
			      + "          ........#"
			      + "          ....xxx.#"
			      + "          ....xxx.#"
			      + "          ........#"
			      + "          ....xxx.#"
			      + "          ....xxx.#"
			      + "          ........#"
			      + "          #########");
			break;
		case 3:
			addMap(sx, sy, 
					"          #########"
			      + "          ........#"
			      + "          ........#"
			      + "          ......x.#"
			      + "          ........#"
			      + "          ......x.#"
			      + "          ........#"
			      + "          ........#"
			      + "          #########");
			break;
		case 4:
			addMap(sx, sy, 
					"          #########"
			      + "          ........#"
			      + "          ..xxxx..#"
			      + "          ........#"
			      + "          ..xxxx..#"
			      + "          ........#"
			      + "          ..xxxx..#"
			      + "          ........#"
			      + "          #########");
			break;
		case 5:
			addMap(sx, sy, 
					"          #########"
			      + "          ........#"
			      + "          ....xxx.#"
			      + "          xxx.....#"
			      + "          ....xxx.#"
			      + "          xxx.....#"
			      + "          ....xxx.#"
			      + "          ........#"
			      + "          #########");
			break;
		case 6:
			addMap(sx, sy, 
					"          #########"
			      + "          ........#"
			      + "          xxx.....#"
			      + "          ....xxx.#"
			      + "          xxx.....#"
			      + "          ....xxx.#"
			      + "          xxx.....#"
			      + "          ........#"
			      + "          #########");
			break;
		case 7:
			addMap(sx, sy, 
					"          #########"
			      + "          ........#"
			      + "          .xxxxxx.#"
			      + "          .xxxxxx.#"
			      + "          .xxxxxx.#"
			      + "          .xxxxxx.#"
			      + "          .xxxxxx.#"
			      + "          ........#"
			      + "          #########");
			break;
		case 8:
			addMap(sx, sy, 
					"          #########"
			      + "          ........#"
			      + "          x.....x.#"
			      + "          ..xxx...#"
			      + "          x.....x.#"
			      + "          ..xxx...#"
			      + "          x.....x.#"
			      + "          ........#"
			      + "          #########");
			break;
		case 9:
			addMap(sx, sy, 
					"          #########"
			      + "          .xxxxx..#"
			      + "          ........#"
			      + "          ........#"
			      + "          ..xxx...#"
			      + "          ........#"
			      + "          ........#"
			      + "          .xxxxx..#"
			      + "          #########");
			break;
		}
	}

	private void setTilesTop(int sx, int sy){
		switch ((int)(Math.random() * 5)){
		case 0:
			addMap(sx, sy,
					"###################"
			      + "#.................#"
			      + "#.................#"
			      + "#.................#"
			      + "#.................#"
			      + "                   "
			      + "                   "
			      + "                   "
			      + "                   ");
			break;
		case 1:
			addMap(sx, sy,
					"###################"
			      + "####...........####"
			      + "##...............##"
			      + "#.................#"
			      + "#.................#"
			      + "                   "
			      + "                   "
			      + "                   "
			      + "                   ");
			break;
		case 2:
			addMap(sx, sy,
					"###################"
			      + "#.................#"
			      + "#.x.............x.#"
			      + "#.................#"
			      + "#.................#"
			      + "                   "
			      + "                   "
			      + "                   "
			      + "                   ");
			break;
		case 3:
			addMap(sx, sy,
					"###################"
			      + "#.................#"
			      + "#.x.x.x.x.x.x.x.x.#"
			      + "#.................#"
			      + "#.................#"
			      + "                   "
			      + "                   "
			      + "                   "
			      + "                   ");
			break;
		case 4:
			addMap(sx, sy,
					"###################"
				  + "#.x.x.x.x.x.x.x.x.#"
				  + "#.x.x.x.x.x.x.x.x.#"
			      + "#.................#"
			      + "#.................#"
			      + "                   "
			      + "                   "
			      + "                   "
			      + "                   ");
			break;
		}
	}

	private void setTilesBottom(int sx, int sy){
		switch ((int)(Math.random() * 5)){
		case 0:
			addMap(sx, sy,
				    "                   "
				  + "                   "
				  + "                   "
				  + "                   "
			      + "#.................#"
			      + "#.................#"
			      + "#.................#"
			      + "#.................#"
				  + "###################");
			break;
		case 1:
			addMap(sx, sy,
				    "                   "
				  + "                   "
				  + "                   "
				  + "                   "
			      + "#.................#"
			      + "#.................#"
			      + "##...............##"
			      + "####...........####"
				  + "###################");
			break;
		case 2:
			addMap(sx, sy,
				    "                   "
				  + "                   "
				  + "                   "
				  + "                   "
			      + "#.................#"
			      + "#.................#"
			      + "#.x.............x.#"
			      + "#.................#"
				  + "###################");
			break;
		case 3:
			addMap(sx, sy,
				    "                   "
				  + "                   "
				  + "                   "
				  + "                   "
			      + "#.................#"
			      + "#.................#"
			      + "#.x.x.x.x.x.x.x.x.#"
			      + "#.................#"
				  + "###################");
			break;
		case 4:
			addMap(sx, sy,
				    "                   "
				  + "                   "
				  + "                   "
				  + "                   "
			      + "#.................#"
			      + "#.................#"
				  + "#.x.x.x.x.x.x.x.x.#"
				  + "#.x.x.x.x.x.x.x.x.#"
				  + "###################");
			break;
		}
	}
	
	private void addMap(int sx, int sy, String data) {
		int mx = sx * screenWidth;
		int my = sy * screenHeight;
		Tile floor = cells[sx][sy].defaultGround;
		Tile wall  = cells[sx][sy].defaultWall;
		Tile local  = Math.random() < 0.66 ? wall : getRandomWall();
		for (int x = 0; x < screenWidth; x++)
		for (int y = 0; y < screenHeight; y++) {
			switch (data.charAt(x + y * screenWidth)){
			case '.': tiles[mx+x][my+y] = floor; break;
			case '#': tiles[mx+x][my+y] = wall; break;
			case 'x': tiles[mx+x][my+y] = local; break;
			case '~': tiles[mx+x][my+y] = Tile.WATER; break;
			case ' ': break;
			}
		}
	}
	
	private Tile getRandomWall(){
		Tile[] tiles = { Tile.BROWN_ROCK, Tile.BROWN_TREE, 
						 Tile.BROWN_ROCK, Tile.BROWN_TREE, 
						 Tile.GREEN_ROCK, Tile.GREEN_TREE, 
						 Tile.GREEN_ROCK, Tile.GREEN_TREE, 
						 Tile.WHITE_ROCK, Tile.WHITE_TREE };
		
		return tiles[(int)(Math.random() * tiles.length)];
	}
	
	private void addBorderOpenings(int x, int y){
		if (cells[x][y].nEdge == WorldScreen.CENTER) clear(x * screenWidth + screenWidth/2, y * screenHeight, 1, 2, cells[x][y].defaultGround);
		if (cells[x][y].sEdge == WorldScreen.CENTER) clear(x * screenWidth + screenWidth/2, (y+1) * screenHeight - 2, 1, 2, cells[x][y].defaultGround);
		if (cells[x][y].wEdge == WorldScreen.CENTER) clear(x * screenWidth, y * screenHeight + screenHeight/2, 2, 1, cells[x][y].defaultGround);
		if (cells[x][y].eEdge == WorldScreen.CENTER) clear((x+1) * screenWidth - 2, y * screenHeight + screenHeight/2, 2, 1, cells[x][y].defaultGround);
	}

	private void clear(int x, int y, int w, int h, Tile tile) {
		for (int x2 = x; x2 < x + w; x2++)
		for (int y2 = y; y2 < y + h; y2++)
			tiles[x2][y2] = tile;
	}
	
	public void addLake(){
		int w = cells.length;
		int h = cells[0].length;
		int x = (int)(Math.random() * (w - 2) + 1);
		int y = (int)(Math.random() * (h - 2) + 1);
		
		clear(x * screenWidth - screenWidth/2, 
			  y * screenHeight - screenHeight/2, 
			  screenWidth - 2 + 1, 
			  screenHeight - 2 + 1, Tile.WATER);
		
		cells[x][y].seWater = true;
		cells[x+1][y].swWater = true;
		cells[x+1][y+1].nwWater = true;
		cells[x][y+1].neWater = true;
	}
}
