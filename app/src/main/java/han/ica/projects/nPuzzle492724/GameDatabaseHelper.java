package han.ica.projects.nPuzzle492724;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Lars on 23-4-2015.
 */
public class GameDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "nPuzzleDB";

    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table Game (" +
            "id integer primary key autoincrement," +
            "moves integer not null," +
            "difficulty integer not null," +
            "imageId integer not null," +
            "tilePositions text not null" +
        ");";

    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    // Method is called during an upgrade of the database,
    @Override
    public void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion){
//        Log.w(GameDatabaseHelper.class.getName(),
//                "Upgrading database from version " + oldVersion + " to "
//                        + newVersion + ", which will destroy all old data");
//        database.execSQL("DROP TABLE IF EXISTS MyEmployees");
//        onCreate(database);
    }
}
