package han.ica.projects.nPuzzle492724;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martijn on 1-4-2015.
 * Source: http://developer.android.com/guide/topics/ui/layout/gridview.html
 */
public class ImageSelectionAdapter extends BaseAdapter {
	private Context context;
	private List<Image> images = new ArrayList<Image>();

	public ImageSelectionAdapter(Context context, List<Image> images) {
		this.context = context;
		this.images = images;
	}

	public int getCount() {
		return images.size();
	}

	public Object getItem(int position) {
		return images.get(position);
	}

	public long getItemId(int position) {
		return images.get(position).getResourceId();
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		SquareImageView imageView;
		if (convertView == null) {
			// if it's not recycled, initialize some attributes
			imageView = new SquareImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, 0));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (SquareImageView) convertView;
		}

		Image image = images.get(position);

		imageView.setImageBitmap(image.getBitmap());
		return imageView;
	}
}
