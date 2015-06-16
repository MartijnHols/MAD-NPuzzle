package han.ica.projects.nPuzzle492724;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MultiplayerWaitingForGameStart extends ActionBarActivity implements GameServerConnectionListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiplayer_waiting_for_game_start);

		GameServerConnection.getInstance().addListener(this);
	}

	@Override
	public void onMessage(Message message) {
		String command = message.command;
		switch (command) {
			case "startGame":
				JSONObject data = (JSONObject) message.data;
				try {
					String otherPlayerId = data.getString("otherPlayerId");
					Long resourceId = data.getLong("resourceId");
					int difficulty = data.getInt("difficulty");

					onStartGame(otherPlayerId, resourceId, difficulty);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
		}
	}

	public void onStartGame(String otherPlayerId, long resourceId, int difficulty) {
		Intent i = new Intent(this, GamePlay.class);
		i.putExtra("versusPlayerId", otherPlayerId)
			.putExtra("resourceId", (int) resourceId)
			.putExtra("difficulty", difficulty);
		startActivity(i);
		finish();
	}

	@Override
	public void onConnect() {

	}
}
