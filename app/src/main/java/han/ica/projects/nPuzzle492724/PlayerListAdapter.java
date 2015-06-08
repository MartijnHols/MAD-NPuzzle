package han.ica.projects.nPuzzle492724;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martijn on 1-4-2015.
 * Source: http://developer.android.com/guide/topics/ui/layout/gridview.html
 */
public class PlayerListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<PlayerListItem> playerInfo = new ArrayList<>();

	public PlayerListAdapter(Context context, ArrayList<PlayerListItem> playerInfo) {
		this.context = context;
		this.playerInfo = playerInfo;
	}

	public int getCount() {
		return playerInfo.size();
	}

	public Object getItem(int position) {
		return playerInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	// create a new ImageView for each item referenced by the Adapter
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv;
		if (convertView == null) {
			tv = new TextView(context);
			System.out.println(tv);
			tv.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, 0));
		} else {
			tv = (TextView)convertView;
		}
		tv.setText(playerInfo.get(position).naam);
		tv.setTag(playerInfo.get(position).id);
		return tv;
	}

}

