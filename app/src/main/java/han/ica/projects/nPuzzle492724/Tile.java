package han.ica.projects.nPuzzle492724;

import android.graphics.Bitmap;

/**
 * Created by Martijn on 1-4-2015.
 */
public class Tile {
	protected int number;
	protected Bitmap bitmap;
    protected String text;

	public Tile() {
	}
	public Tile(int number, Bitmap bitmap, String text) {
		this.number = number;
		this.bitmap = bitmap;
        this.text = text;
	}

	public int getNumber() {
		return number;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
    public String getText() {
        return text;
    }
    public void setText(String text){
        this.text = text;
    }
}


