package my.example.sudoku;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Sudoku extends Activity {
	
	public final String CURRENT_GAME = "current_game";
	public final String STORED_GAME = "stored_game";

	SudokuGame game;
	SudokuView view;
	
	Button[] buttons = new Button[9];
	
	String safe = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		game = new SudokuGame();
		
		if(savedInstanceState!=null) {
			game.load(savedInstanceState.getString(CURRENT_GAME));
		} else {
			game.load(getPreferences(MODE_PRIVATE).getString(CURRENT_GAME, null));
		}
		
		if(game.solved()) game.generate();
		
		setContentView(R.layout.sudoku);

		view = (SudokuView)findViewById(R.id.sudokuView1);
		
		view.init(game);
		
		buttons[0] = (Button)findViewById(R.id.button1);
		buttons[1] = (Button)findViewById(R.id.button2);
		buttons[2] = (Button)findViewById(R.id.button3);
		buttons[3] = (Button)findViewById(R.id.button4);
		buttons[4] = (Button)findViewById(R.id.button5);
		buttons[5] = (Button)findViewById(R.id.button6);
		buttons[6] = (Button)findViewById(R.id.button7);
		buttons[7] = (Button)findViewById(R.id.button8);
		buttons[8] = (Button)findViewById(R.id.button9);
		for(int i = 0; i < buttons.length; i++) {
			final int t = i + 1;
			buttons[i].setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					game.setTile(t);
					view.invalidate();
					if(game.solved()) 
						Toast.makeText(getApplicationContext(), R.string.alert_message, Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sudoky, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_new_game: game.generate(); break;
			case R.id.action_reset: game.reset(); break;
			default:
				return super.onOptionsItemSelected(item);
		}
		view.invalidate();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
		return true;
	}

	

	@Override
	protected void onPause() {
		super.onPause();
		getPreferences(MODE_PRIVATE).edit().putString(CURRENT_GAME, game.save()).commit();
	}
/*
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
*/
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//outState.putSerializable(CURRENT_GAME, game);
		outState.putString(CURRENT_GAME, game.save());
		super.onSaveInstanceState(outState);
	}

	public void clearTile(View v) {
		game.setTile(0);
		view.invalidate();
	}
	
	public void draftMode(View v) {
		game.setDraftMode(((ToggleButton)v).isChecked());
	}
	
	public void saveClick(View v) {
		String text;
		if(safe == null) {
			safe = game.save();
			text = "L";
		} else {
			game.load(safe);
			safe = null;
			text = "M";
		}
		((Button)v).setText(text);
		view.invalidate();
	}
}
