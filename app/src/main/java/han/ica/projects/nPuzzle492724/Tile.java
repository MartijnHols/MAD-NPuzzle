package han.ica.projects.nPuzzle492724;

import android.graphics.Bitmap;

/**
 * Created by Martijn on 1-4-2015.
 */
public class Tile {
	protected int number;
	protected Bitmap bitmap;

	public Tile() {
	}
	public Tile(int number, Bitmap bitmap) {
		this.number = number;
		this.bitmap = bitmap;
	}

	public int getNumber() {
		return number;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
    public String getText() {
        return String.format("%d", this.number + 1);
    }
}

