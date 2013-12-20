package my.example.sudoku;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class SudokuView extends View {
	
	Context context;
	
	SudokuGame game = null;
	
	float cellSize, offsetX, offsetY;

	Paint black, white, blue, gray, red, micro;
	
	FontMetrics fm;
	
	Dialog mKeypad;
	WindowManager.LayoutParams mKeypadParams;
	
	public SudokuView(Context context) {
		super(context);
	}

	public SudokuView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SudokuView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void init(SudokuGame game) {

		this.game = game;
		black = new Paint();
		black.setColor(Color.BLACK);
		
		white = new Paint();
		white.setColor(Color.WHITE);

		gray = new Paint();
		gray.setColor(Color.GRAY);
		gray.setAlpha(64);
		
		blue = new Paint();
		blue.setColor(Color.CYAN);
		blue.setAlpha(64);

		red = new Paint();
		red.setColor(Color.RED);
		red.setAlpha(64);
		
		micro = new Paint();
		micro.setColor(Color.BLACK);
		
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		if(game==null) return;

		canvas.drawRect(0, 0, getWidth(), getHeight(), white);
		for(int row = 0; row < 9; row++) {
			for(int col = 0; col < 9; col++) {
				float cellX = offsetX+col*cellSize;
				float cellY = offsetY+row*cellSize;
				SudokuGame.Tile tile = game.getTile(row, col);
				if(tile.empty) {
					if(tile.error) 
						canvas.drawRect(cellX, cellY, cellX+cellSize, cellY+cellSize, red);
					else if(game.isSelected(row, col)) canvas.drawRect(cellX, cellY, cellX+cellSize, cellY+cellSize, blue);
					
				} else {
					canvas.drawRect(cellX, cellY, cellX+cellSize, cellY+cellSize, gray);
				}
				float x = cellX + cellSize/2f;
				float y = cellY + cellSize/2f - (fm.ascent + fm.descent)/2f;
				if(tile.value > 0)
					canvas.drawText(""+tile.value, x, y, black);
				else
					for(int v=0; v<9; v++) {
						if(tile.draft[v]) {
							canvas.drawText(""+(v+1), cellX+(v%3)*(cellSize/3), cellY+(v/3+1)*(cellSize/3), micro);
						}
					}
					
			}
		}
		for(int i = 0; i < 10; i++) {
			black.setStrokeWidth( (i%3==0) ? 3 : 0 );
			canvas.drawLine(offsetX + i*cellSize, offsetY, offsetX + i*cellSize, offsetY + cellSize*9, black);
			canvas.drawLine(offsetX, offsetY + i*cellSize, offsetX + cellSize*9, offsetY + i*cellSize, black);
		}
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction()!= MotionEvent.ACTION_DOWN) return super.onTouchEvent(event);
		game.select((int)((event.getY()-offsetY)/cellSize),(int)((event.getX()-offsetX)/cellSize));
		invalidate();
		return true;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if(game==null) return;
		
		cellSize = ( w>h ? h : w )/10f;
		offsetX = w / 2f - cellSize * 4.5f;
		offsetY = h / 2f - cellSize * 4.5f;

		black.setTextSize(cellSize * 0.75f);
		black.setTextAlign(Paint.Align.CENTER);

		micro.setTextSize(cellSize/3f);
		
		fm = black.getFontMetrics();

		super.onSizeChanged(w, h, oldw, oldh);
	}
	
}
