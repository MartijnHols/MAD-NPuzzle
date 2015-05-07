package han.ica.projects.nPuzzle492724;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Lars on 23-4-2015.
 */
public class GameDatabase {
    private GameDatabaseHelper dbHelper;

    private SQLiteDatabase database;

    public final static String TABLE_GAME = "Game";

    public final static String FIELD_MOVES = "moves";
    public final static String FIELD_DIFFICULTY = "difficulty";
    public final static String FIELD_IMAGEID = "imageId";
    public final static String FIELD_TILEPOSITIONS = "tilePositions";

    /**
     * @param context
     */
    public GameDatabase(Context context) {
        dbHelper = new GameDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public long saveGame(Game game) {
        ContentValues values = new ContentValues();
        values.put(FIELD_DIFFICULTY, game.difficulty);
        values.put(FIELD_IMAGEID, game.imageResourceId);
        values.put(FIELD_MOVES, game.numMoves);
        values.put(FIELD_TILEPOSITIONS, game.getJsonTiles());
        return database.insert(TABLE_GAME, null, values);
    }

    public Game loadGame() {
        Cursor mCursor = database.query(true, TABLE_GAME, null, null, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return null;
    }
}