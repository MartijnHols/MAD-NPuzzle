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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class GamePlay extends ActionBarActivity implements AdapterView.OnItemClickListener {
    public Game game;

	private Image image;
	private Tile[] tiles;

	protected GridView gvPuzzle;
    protected View vRectangle;

	private Toast toast;

	private String versusPlayerId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_play);

		Intent i = getIntent();
        
        game = new Game();
        game.imageResourceId = i.getIntExtra("resourceId", 0);
        game.difficulty = i.getIntExtra("difficulty", 1);
		versusPlayerId = i.getStringExtra("versusPlayerId");

		image = new Image(this, game.imageResourceId);
		gvPuzzle = (GridView) findViewById(R.id.gvPuzzle);
        vRectangle = (View) findViewById(R.id.vRectangle);

		prepareGame();
	}

	private Timer tmrDisplaySolution;
	private Timer tmrShuffler;
    private Timer tmrResetNumbers;

	public void prepareGame() {
		tiles = getTiles();
        int[] tilePositions = new int[tiles.length];
        for (int i = 0; i < tiles.length; i++) {
            tilePositions[i] = tiles[i].getNumber();
        }
        game.tilePositions = tilePositions;

		gvPuzzle.setNumColumns(game.getRows());
		gvPuzzle.setAdapter(new ImageTilesAdapter(this, tiles));
		gvPuzzle.setOnItemClickListener(this);

		game.numMoves = 0;

		// Wacht 3 seconden om het verwachte resultaat te tonen
		game.isActive = false;

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
		final int shuffleMoves = 10 + game.difficulty * 5;
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
						if (!game.isActive) {
							//Log.d("NPuzzle", "Shuffling...");
							game.doPseudoRandomMove();
                            updateTilePositions();
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
		if (tmrResetNumbers != null) {
            tmrResetNumbers.cancel();
            tmrResetNumbers = null;
		}
	}

	public void begin() {
		Log.d("NPuzzle", "Start!");
		game.isActive = true;
		toast("Succes!");
	}

	protected final Activity getActivity() {
		return this;
	}

	protected Tile[] getTiles() {
		int columns = game.getRows();
		int width = image.getBitmap().getWidth();
		int height = image.getBitmap().getHeight();
		int tileWidth = width / columns;
		int tileHeight = height / columns;

		Tile[] tiles = new Tile[game.difficulty + 1];
		for (int i = 0; i < game.difficulty; i++) {
			int x = (tileWidth * (i % columns));
			int y = (int) (tileWidth * Math.floor(i / columns));
			Bitmap bitmap = Bitmap.createBitmap(image.getBitmap(), x, y, tileWidth, tileHeight);
            String text = String.format("%d", i + 1);

			tiles[i] = new Tile(i, bitmap, text);
		}
		game.emptyTilePosition = game.difficulty;
		tiles[game.emptyTilePosition] = new EmptyTile(game.emptyTilePosition, getBlackBitmap(tileWidth, tileHeight));
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

    public void moveTile(int position) {
        game.moveTile(position);
        updateTilePositions();
    }
    public void updateTilePositions() {
        HashMap<Integer, Tile> dictTiles = new HashMap<>();
        for (Tile tile : tiles) {
            dictTiles.put(tile.getNumber(), tile);
        }
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = dictTiles.get(game.tilePositions[i]);
        }
        gvPuzzle.invalidateViews();
    }

    private void randomizeTileNumbers() {
        int[] numbers = getSequentialArray(game.difficulty);
        int[] randomizedArray = randomizeArray(numbers);

        int emptyTileNo = -1;
        for (int i = 0; i < (game.difficulty + 1); i++) {
            int number;
            if (i == game.difficulty) {
                number = emptyTileNo;
            } else {
                number = randomizedArray[i];
            }
            if (tiles[i] instanceof EmptyTile) {
                emptyTileNo = number;
                continue;
            }
            tiles[i].setText(String.format("%d", number));
        }

        toast("Shuffled tile numbers!");

        if (tmrResetNumbers != null) {
            tmrResetNumbers.cancel();
        }
        tmrResetNumbers = new Timer();
        tmrResetNumbers.schedule(new TimerTask() {
            @Override
            public void run() {
                resetTileNumber();
            }
        }, 10 * 1000);
    }
    private void resetTileNumber() {
        for (int i = 0; i < (game.difficulty + 1); i++) {
            Tile tile = tiles[i];
            tile.setText(String.format("%d", tile.getNumber() + 1));
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gvPuzzle.invalidateViews();
            }
        });
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
        toast("Rotate!");
        gvPuzzle.animate().rotationYBy(180).setDuration(800).start();
    }

    private void flashbang(){
        vRectangle.setAlpha(1);
        vRectangle.animate().alpha(0).setInterpolator(new AccelerateInterpolator()).setDuration(3000).start();

        toast("Flashbang!");
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (!game.isActive) {
			toast("Even geduld...");
			return;
		}
		if (game.canMoveTile(position)) {
			moveTile(position);
            game.numMoves++;
		}

		if (game.isGameComplete()) {
			game.isActive = false;
			Intent i = new Intent(this, YouWin.class);
			i.putExtra("playerMoves", game.numMoves)
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
				game.isActive = false;
				shuffle();
				return true;
			case R.id.action_stop:
				finish();
				return true;
			case R.id.action_easy:
				game.difficulty = Game.DIFFICULTY_EASY;
				prepareGame();
				return true;
			case R.id.action_medium:
				game.difficulty = Game.DIFFICULTY_MEDIUM;
				prepareGame();
				return true;
			case R.id.action_hard:
				game.difficulty = Game.DIFFICULTY_HARD;
				prepareGame();
				return true;
		}


		return super.onOptionsItemSelected(item);
	}
}
