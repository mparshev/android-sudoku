package my.example.sudoku;

import java.util.Random;

import android.util.Log;

public class SudokuGame {
	
	public final static int LEVEL = 1;

	Tile tiles[][];
	
	int selRow = -1, selCol = -1;
	
	boolean draftMode;
	
	public class Tile {
		
		int row;
		int col;
		
		int value;
		boolean empty;
		boolean error;
		boolean[] draft;
		
		public Tile() {
			this.row = 0;
			this.col = 0;
			this.value = 0;
			this.error = false;
			this.empty = true;
			this.draft = new boolean[] { true, true, true, true, true, true, true, true, true };
		}

		public String save() {
			String str = ""+value;
			str += empty ? "1" : "0";
			str += error ? "1" : "0";
			for(int i=0; i<9; i++)
				str += draft[i] ? "1" : "0";
			return str;
		}
		
		public String load(String str) {
			if(str.length()<12) return str;
			value = str.charAt(0)-'0';
			empty = (str.charAt(1)=='1');
			error = (str.charAt(2)=='1');
			for(int i=0; i<9; i++) {
				draft[i] = (str.charAt(3+i)=='1');
			}
			return str.substring(12);
		}
		
	}
	
	public SudokuGame() {
		tiles = new Tile[9][9];
	}

	public Tile getTile(int row, int col) {
		return tiles[row][col];
	}

	private class Solver {
		
		public Solver() {
		}

		boolean solve(Tile hint, int value) {
			Log.d("solve","row="+hint.row+" col="+hint.col+" value="+value);
			if(value == 0) return false;
			Tile[] row = getRow(hint.row);
			Tile[] col = getCol(hint.col);
			Tile[] qrt = getQrt(hint.row, hint.col);
			int v = value - 1;
			for( int i = 0; i < 9; i++) {
				row[i].draft[v] = false;
				col[i].draft[v] = false;
				qrt[i].draft[v] = false;
			}
			for( int i = 0; i < 9; i++) {
				solve(row[i], guess(row[i]));
				solve(col[i], guess(col[i]));
				solve(qrt[i], guess(qrt[i]));
			}

			return true;

		}

		public boolean solved() {
			for(int i=0; i<9; i++) {
				for(int j=1; j<9; j++) {
					Tile tile = tiles[i][j];
					if(!tile.empty) continue;
					int c = 0;
					for(int k=0; k<9; k++) {
						if(tile.draft[k]) ++c;
					}
					if(c>1) return false;
				}
			}
			return true;
		}
		
		private int guess(Tile tile) {
			if(!tile.empty) return 0;
			int v = 0;
			for(int i=0; i<9; i++) {
				if(tile.draft[i]) {
					if(v != 0) return 0;
					v = i+1;
				}
			}
			return v;
		}
		
		
		private Tile[] getRow(int row) {
			Tile[] arr = new Tile[9];
			for(int i=0; i<9; i++) 
				arr[i] = tiles[row][i];
			return arr;
		}
		
		private Tile[] getCol(int col) {
			Tile[] arr = new Tile[9];
			for(int i=0; i<9; i++)
				arr[i] = tiles[i][col];
			return arr;
		}

		private Tile[] getQrt(int row, int col) {
			Tile[] arr = new Tile[9];
			int r = (row/3)*3;
			int c = (col/3)*3;
			for(int i=0; i<9; i++) 
				arr[i] = tiles[r+i/3][c+i%3];
			return arr;
		}
		
	}
	
	public void generate() {
		int[] values = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		int[] offset = new int[] { 0, 3, 6, 8, 2, 5, 7, 1, 4 };
		
		Random random = new Random();
		
		for(int k=0; k<80; k++) {
			int i = random.nextInt(9);
			int j = random.nextInt(9);
			int v = values[i];
			values[i] = values[j];
			values[j] = v;
		}
		
		for(int row=0; row<9; row++) {
			int v = offset[row];
			for(int col=0; col<9; col++) {
				Tile tile = new Tile();
				tile.value = values[v];
				tiles[row][col] = tile;
				v = (++v)%9;
			}
		}
		
		for(int i=0; i<333; i++) {
			if(random.nextInt(1)>0) {
				int row0 = random.nextInt(3);
				int row1 = row0*3 + random.nextInt(3);
				int row2 = row0*3 + random.nextInt(3);
				for(int col=0; col<9; col++) {
					swapTiles(row1, col, row2, col);
				}
			} else {
				int col0 = random.nextInt(3);
				int col1 = col0*3 + random.nextInt(3);
				int col2 = col0*3 + random.nextInt(3);
				for(int row=0; row<9; row++) {
					swapTiles(row, col1, row, col2);
				}
			}
		}

		for(int row=0; row<9; row++) {
			for(int col=0; col<9; col++) {
				Tile tile = tiles[row][col];
				tile.row = row;
				tile.col = col;
			}
		}
		
		Solver solver = new Solver();
		while(!solver.solved()) {
		//for(int i=0; i<21; i++) {
			int row = random.nextInt(9);
			int col = random.nextInt(9);
			Tile tile = tiles[row][col];
			tile.empty = false;
			solver.solve(tile, tile.value);
		}

		for(int i=0; i<9; i++) {
			for(int j=0; j<9; j++) {
				Tile tile = tiles[i][j];
				if(tile.empty) {
					tile.value = 0;
				}
			}
		}
		
	}
	
	private void swapTiles(int row1, int col1, int row2, int col2) {
		Tile tile = tiles[row1][col1];
		tiles[row1][col1] = tiles[row2][col2];
		tiles[row2][col2] = tile;
	}
	
	public void select(int row, int col) {
		if(col>=0 && col < 9 && row>=0 && row < 9) {
			selCol = col;
			selRow = row;
		} else {
			selCol = -1;
			selRow = -1;
		}
	}
	
	public boolean isSelected(int row, int col) {
		return row == selRow && col == selCol; 
	}

	public void setTile(int value) {
		if(selRow == -1 || selCol == -1) return;
		Tile tile = tiles[selRow][selCol];
		if(!tile.empty) return;
		tile.error = false;
		tile.value = 0;
		if(value == 0) return;
		if(draftMode) {
			tile.draft[value-1] = !tile.draft[value-1];
			return;
		}
		int quadRow = (selRow/3) * 3;
		int quadCol = (selCol/3) * 3;
		for(int i=0; i<9; i++) {
			if(value == tiles[selRow][i].value) 
				tile.error = true;
			else
				tiles[selRow][i].draft[value-1] = false;
			if(value == tiles[i][selCol].value) 
				tile.error = true;
			else
				tiles[i][selCol].draft[value-1] = false;
			if(value == tiles[quadRow+i/3][quadCol+i%3].value) 
				tile.error = true;
			else
				tiles[quadRow+i/3][quadCol+i%3].draft[value-1] = false;
		}
		tile.value = value;
		
	}
	
	public void setDraftMode(boolean value) {
		draftMode = value;
		//Log.d("game", "Draft mode is now "+draftMode);
	}
	
	public void reset() {
		for(int i=0; i<9; i++) {
			for(int j=0; j<9; j++) {
				Tile tile = tiles[i][j];
				if(tile.empty) {
					tile.value = 0;
					tile.error = false;
					for(int v=0; v<9; v++) tile.draft[v] = false;
				}
			}
		}
	}
	
	public boolean solved() {
		for(int i=0; i<9; i++) {
			for(int j=0; j<9; j++) {
				Tile tile = tiles[i][j];
				if(tile.error) return false;
				if(tile.value == 0) return false;
			}
		}
		return true;
	}
	
	public String save() {
		String str = "";
		for(int row=0; row<9; row++)
			for(int col=0; col<9; col++)
				str += tiles[row][col].save();
		return str;
	}
	
	public void load(String str) {
		if(str==null) { generate(); return; } 
		for(int row=0; row<9; row++)
			for(int col=0; col<9; col++) {
				Tile tile = new Tile();
				tile.row = row;
				tile.col = col;
				tiles[row][col] = tile;
				
				str = tiles[row][col].load(str);
			}
	}
	
}
