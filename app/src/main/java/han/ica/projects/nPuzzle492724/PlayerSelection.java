package han.ica.projects.nPuzzle492724;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PlayerSelection extends ActionBarActivity implements GameServerConnectionListener, AdapterView.OnItemClickListener {
	protected ListView lvPlayerSelection;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player_selection);
		lvPlayerSelection = (ListView) findViewById(R.id.lvPlayerSelection);

		GameServerConnection.getInstance().addListener(this);

		GameServerConnection.getInstance().requestPlayerList();
		lvPlayerSelection.setOnItemClickListener(this);
	}



	@Override
	public void onConnect() {}

	@Override
	public void onMessage(Message message) {
		JSONArray data = (JSONArray)message.data;
		ArrayList<String> playerList = new ArrayList<>();
		try {
			for(int i = 0; i < data.length(); i++){
				String id = data.getJSONObject(i).getString("id");
				String name = data.getJSONObject(i).getString("naam");
				lvPlayerSelection.setTag(id);
				playerList.add(name);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		final ArrayList<String> players = playerList;
		final PlayerSelection self = this;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
						self,
						android.R.layout.simple_list_item_1,
						players);

				lvPlayerSelection.setAdapter(arrayAdapter);
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String clickedPlayerName = parent.getItemAtPosition(position).toString();
		String clickedPlayerID = (String) parent.getTag();
		GameServerConnection.getInstance().sendGameInvitation(clickedPlayerName, clickedPlayerID);
	}
}
