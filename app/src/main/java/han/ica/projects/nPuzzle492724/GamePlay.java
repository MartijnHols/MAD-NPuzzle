package han.ica.projects.nPuzzle492724;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.GridLayoutAnimationController;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class GamePlay extends ActionBarActivity implements AdapterView.OnItemClickListener {
	public static final int DIFFICULTY_EASY = 8;
	public static final int DIFFICULTY_MEDIUM = 15;
	public static final int DIFFICULTY_HARD = 24;

	private Image image;
	private int difficulty;
	private Tile[] tiles;
	private int emptyTilePosition;

	protected GridView gvPuzzle;
    protected View vRectangle;

	private Toast toast;

	public int playerMoves;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_play);

		Intent i = getIntent();
		image = new Image(this, i.getIntExtra("resourceId", 0));
		difficulty = i.getIntExtra("difficulty", 1);

		gvPuzzle = (GridView) findViewById(R.id.gvPuzzle);

        vRectangle = (View) findViewById(R.id.vRectangle);


		prepareGame();
	}

	private Timer tmrDisplaySolution;
	private Timer tmrShuffler;

	public void prepareGame() {
		tiles = getTiles();

		gvPuzzle.setNumColumns(getColumns());
		gvPuzzle.setAdapter(new ImageTilesAdapter(this, tiles));
		gvPuzzle.setOnItemClickListener(this);

		playerMoves = 0;

		// Wacht 3 seconden om het verwachte resultaat te tonen
		isActive = false;

		cancelTimers();
		tmrDisplaySolution = new Timer();
		tmrDisplaySolution.schedule(new TimerTask() {
			@Override
			public void run() {
				shuffle();
			}
		}, 3 * 1000);
	}

	public void shuffle() {
		final int shuffleMoves = 10 + difficulty * 5;
		Log.d("NPuzzle", "Shuffle " + shuffleMoves + " keer");

		cancelTimers();
		tmrShuffler = new Timer();
		tmrShuffler.scheduleAtFixedRate(new TimerTask() {
			private int counter = 0;

			@Override
			public void run() {
				if (counter++ == shuffleMoves) {
					cancelTimers();
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							begin();
						}
					});
					return;
				}

				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!isActive) {
							//Log.d("NPuzzle", "Shuffling...");
							doPseudoRandomMove();
						}
					}
				});
			}
		}, 0, 1000 / 35);
	}



	public void cancelTimers() {
		if (tmrDisplaySolution != null) {
			tmrDisplaySolution.cancel();
			tmrDisplaySolution = null;
		}
		if (tmrShuffler != null) {
			tmrShuffler.cancel();
			tmrShuffler = null;
		}
	}

	public void begin() {
		Log.d("NPuzzle", "Start!");
		isActive = true;
		toast("Succes!");
	}

	protected final Activity getActivity() {
		return this;
	}

	protected int getColumns() {
		return (int) Math.sqrt(difficulty + 1);
	}

	protected Tile[] getTiles() {
		int columns = getColumns();
		int width = image.getBitmap().getWidth();
		int tileWidth = width / columns;
		int tileHeight = tileWidth;

		Tile[] tiles = new Tile[difficulty + 1];
		for (int i = 0; i < difficulty; i++) {
			int x = (tileWidth * (i % columns));
			int y = (int) (tileWidth * Math.floor(i / columns));
			Bitmap bitmap = Bitmap.createBitmap(image.getBitmap(), x, y, tileWidth, tileHeight);
            String text = String.format("%d", i + 1);

			tiles[i] = new Tile(i, bitmap, text);
		}
		emptyTilePosition = difficulty;
		tiles[emptyTilePosition] = new EmptyTile(emptyTilePosition, getBlackBitmap(tileWidth, tileHeight));
		return tiles;
	}

	protected Bitmap getBlackBitmap(int width, int height) {
		// Source: http://stackoverflow.com/questions/5663671/creating-an-empty-bitmap-and-drawing-though-canvas-in-android
		Bitmap.Config conf = Bitmap.Config.RGB_565;
		Bitmap bmp = Bitmap.createBitmap(width, height, conf);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setColor(getResources().getColor(R.color.black));
		canvas.drawRect(0, 0, width, height, paint);

		return bmp;
	}

	protected boolean isGameComplete() {
		for (int i = 0; i < tiles.length; i++) {
			if (tiles[i].number != i) {
				return false;
			}
		}
		return true;
	}

	protected boolean isActive = false;

	public boolean canMoveTile(int position) {
		List<Integer> lstValidMoves = getValidMoves();
		return lstValidMoves.contains(position);
	}

	public void moveTile(int position) {
		EmptyTile emptyTile = (EmptyTile) tiles[emptyTilePosition];
		tiles[emptyTilePosition] = tiles[position];
		tiles[position] = emptyTile;
		emptyTilePosition = position;
        if(isActive) {
            randomTileNumbers();
        }
		gvPuzzle.invalidateViews();
	}

    private void randomTileNumbers() {
        int[] numbers = getSequentialArray(difficulty + 1);
        int[] randomizedArray = randomizeArray(numbers);

        int emptyTileNo = -1;
        for (int i = 0; i < (difficulty + 1); i++) {
            if (tiles[i] instanceof EmptyTile) {
                emptyTileNo = i;
                continue;
            }
            int number = randomizedArray[i];
            if (i == difficulty) {
                number = emptyTileNo;
            }
            tiles[i].setText(String.format("%d", number));
        }
    }

    private int[] getSequentialArray(int num) {
        int[] numbers = new int[num];
        for (int i = 0; i < num; i++) {
            numbers[i] = i + 1;
        }
        return numbers;
    }

    private int[] randomizeArray(int[] ar){
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
        return ar;
    }


    private void rotateMirror(){
        gvPuzzle.animate().rotationYBy(180).setDuration(800).start();
    }

    private void flashbang(){
        vRectangle.setAlpha(1);
        vRectangle.animate().alpha(0).setInterpolator(new AccelerateInterpolator()).setDuration(3000).start();

        /*OLD
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_out);
        vRectangle.setAnimation(anim);
        anim.setDuration(3000);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.start();*/
    }

	public List<Integer> getValidMoves() {
		List<Integer> lstValidMoves = new ArrayList<Integer>();
		int columns = getColumns();
		if (emptyTilePosition - columns >= 0) { // boven
			lstValidMoves.add(emptyTilePosition - columns);
		}
		if (emptyTilePosition + columns < tiles.length) { // onder
			lstValidMoves.add(emptyTilePosition + columns);
		}
		if (emptyTilePosition - 1 >= 0 && Math.floor((emptyTilePosition - 1) / columns) == Math.floor(emptyTilePosition / columns)) { // links
			lstValidMoves.add(emptyTilePosition - 1);
		}
		if (emptyTilePosition + 1 < tiles.length && Math.floor((emptyTilePosition + 1) / columns) == Math.floor(emptyTilePosition / columns)) { // links
			lstValidMoves.add(emptyTilePosition + 1);
		}
		return lstValidMoves;
	}

	public int getRandomMove() {
		List<Integer> lstValidMoves = getValidMoves();
		int i = (int) Math.floor(Math.random() * lstValidMoves.size());
		return lstValidMoves.get(i);
	}

	public void doRandomMove() {
		int position = getRandomMove();
		moveTile(position);
	}

	/**
	 * Verplaatse een tile naar een pseudorandom plek. Op dit moment is de enige check hier dat de laatste move niet ongedaan gemaakt wordt.
	 */
	private int previousPosition;

	public void doPseudoRandomMove() {
		int position;
		do {
			position = getRandomMove();
			//Log.d("NPuzzle", String.format("previousPosition:%d position:%d", previousPosition, position));
		} while (previousPosition == position);
		previousPosition = emptyTilePosition;

		moveTile(position);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (!isActive) {
			toast("Even geduld...");
			return;
		}
		if (canMoveTile(position)) {
			moveTile(position);
			playerMoves++;
			if (toast != null) {
				toast.cancel();
				toast = null;
			}
		} else {
			toast("Alleen vakjes direct boven, onder, links of rechts van het lege vakje kunnen worden verplaatst.");
		}

		if (isGameComplete()) {
			isActive = false;
			Intent i = new Intent(this, YouWin.class);
			i.putExtra("playerMoves", playerMoves)
				.putExtra("resourceId", image.getResourceId());
			startActivity(i);
			end();
		}
	}

	public void toast(String message) {
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		toast.show();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Log.d("NPuzzle", "Back pressed: stop het spel.");
		end();
	}

	public void end() {
		cancelTimers();
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_game_play, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			case R.id.action_shuffle:
				isActive = false;
				shuffle();
				return true;
			case R.id.action_stop:
				finish();
				return true;
			case R.id.action_easy:
				difficulty = DIFFICULTY_EASY;
				prepareGame();
				return true;
			case R.id.action_medium:
				difficulty = DIFFICULTY_MEDIUM;
				prepareGame();
				return true;
			case R.id.action_hard:
				difficulty = DIFFICULTY_HARD;
				prepareGame();
				return true;
		}


		return super.onOptionsItemSelected(item);
	}
}
