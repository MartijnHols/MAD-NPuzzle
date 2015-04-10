package han.ica.projects.nPuzzle492724;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Martijn on 1-4-2015.
 * Sources: http://developer.android.com/guide/topics/ui/layout/gridview.html, http://stackoverflow.com/a/15264039/684353
 */
public class ImageTilesAdapter extends BaseAdapter {
	private Context context;
	private Tile[] tiles;

	private final LayoutInflater mInflater;

	public ImageTilesAdapter(Context context, Tile[] tiles) {
		mInflater = LayoutInflater.from(context);

		this.context = context;
		this.tiles = tiles;
	}

	public int getCount() {
		return tiles.length;
	}

	public Object getItem(int position) {
		return tiles[position];
	}

	public long getItemId(int position) {
		return tiles[position].number;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Tile tile = tiles[position];

		View v = convertView;
		ImageView picture;
		TextView text;

		if (v == null) {
			v = mInflater.inflate(R.layout.tile, parent, false);
			v.setTag(R.id.picture, v.findViewById(R.id.picture));
			v.setTag(R.id.text, v.findViewById(R.id.text));
		}

		picture = (SquareImageView) v.getTag(R.id.picture);
		text = (TextView) v.getTag(R.id.text);

		picture.setImageBitmap(tile.getBitmap());
        text.setText(tile.getText());
//        Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.slide_to_left);
//        v.setAnimation(anim);
//        anim.start();

		return v;
	}
}
