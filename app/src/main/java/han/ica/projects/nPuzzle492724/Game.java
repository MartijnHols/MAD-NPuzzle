package han.ica.projects.nPuzzle492724;

import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lars on 23-4-2015.
 */
public class Game {
    public static final int DIFFICULTY_EASY = 8;
    public static final int DIFFICULTY_MEDIUM = 15;
    public static final int DIFFICULTY_HARD = 24;

    public boolean isActive = false;
    public int difficulty;
    public int imageResourceId;
    public int numMoves;


    public List<Integer> rowsCompleted = new ArrayList();
    /**
     * De posities van de tiles zoals dat wordt weergegeven (1 tot difficulty + 1).
     */
    public int[] tilePositions;
    /**
     * De index van de empiteTilePosition (zitten tussen 0 en difficulty).
     */
    public int emptyTilePosition;

    public String getJsonTiles(){
        JSONArray list = new JSONArray();
        for(int tile : tilePositions){
            list.put(tile);
        }
        return list.toString();
    }
    public void setTilesFromJson(String json) throws JSONException {
        JSONArray a = new JSONArray(json);
        tilePositions = new int[a.length()];
        for (int i = 0; i < a.length(); i++) {
            tilePositions[i] = (int)a.get(i);
        }
    }

    public boolean isGameComplete() {
        for (int i = 0; i < tilePositions.length; i++) {
            if (tilePositions[i] != i) {
                return false;
            }
        }
        return true;
    }

    protected int getRows() {
        return (int) Math.sqrt(difficulty + 1);
    }

    public List<Integer> getValidMoves() {
        List<Integer> lstValidMoves = new ArrayList<Integer>();
        int columns = getRows();
        if (emptyTilePosition - columns >= 0) { // boven
            lstValidMoves.add(emptyTilePosition - columns);
        }
        if (emptyTilePosition + columns < tilePositions.length) { // onder
            lstValidMoves.add(emptyTilePosition + columns);
        }
        if (emptyTilePosition - 1 >= 0 && Math.floor((emptyTilePosition - 1) / columns) == Math.floor(emptyTilePosition / columns)) { // links
            lstValidMoves.add(emptyTilePosition - 1);
        }
        if (emptyTilePosition + 1 < tilePositions.length && Math.floor((emptyTilePosition + 1) / columns) == Math.floor(emptyTilePosition / columns)) { // links
            lstValidMoves.add(emptyTilePosition + 1);
        }
        return lstValidMoves;
    }

    public boolean canMoveTile(int position) {
        List<Integer> lstValidMoves = getValidMoves();
        return lstValidMoves.contains(position);
    }

    public void moveTile(int position) {
        int emptyTileNumber = tilePositions[emptyTilePosition];
        tilePositions[emptyTilePosition] = tilePositions[position];
        tilePositions[position] = emptyTileNumber;
        emptyTilePosition = position;
    }

    public List<Integer> completedRows = new ArrayList<Integer>();

    public List<Integer> getCompletedRows(){
        for (int rij = 0; rij < getRows(); rij++) {
            if (isRowComplete(rij)) {
                rowsCompleted.add(rij);
            }
        }
        return rowsCompleted;
    }

    private boolean isRowComplete(int rij) {
        int columns = getRows();
        int rows = columns;
        for (int kolom = 0; kolom < columns; kolom++) {
			int currentTileInLoop = (rows * rij) + kolom;
			if (tilePositions[currentTileInLoop] != currentTileInLoop){
				return false;
			}
		}
        return true;
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
}
