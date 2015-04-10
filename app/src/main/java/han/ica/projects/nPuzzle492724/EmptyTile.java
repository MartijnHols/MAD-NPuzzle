package han.ica.projects.nPuzzle492724;

import android.graphics.Bitmap;

public class EmptyTile extends Tile {
	public EmptyTile(int number, Bitmap bitmap) {
		super(number, bitmap, "");
	}

    public String getText() {
        return "";
    }
}
