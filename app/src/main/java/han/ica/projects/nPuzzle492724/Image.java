package han.ica.projects.nPuzzle492724;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Image {
	private Bitmap bitmap;
	private int resourceId;

	public Image(Context context, int resourceId) {
		this(context, resourceId, 1);
	}
	public Image(Context context, int resourceId, int sampleSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = sampleSize;
		this.bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
		this.resourceId = resourceId;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
	public int getResourceId() {
		return resourceId;
	}
}
