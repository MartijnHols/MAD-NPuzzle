package han.ica.projects.nPuzzle492724;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

class PlayerListItem {
	public String naam;
	public String id;
	public double afstand;
	public String stad;
}

public class PlayerSelection extends ActionBarActivity implements GameServerConnectionListener, AdapterView.OnItemClickListener {
	protected ListView lvPlayerSelection;
	protected Location location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player_selection);
		lvPlayerSelection = (ListView) findViewById(R.id.lvPlayerSelection);

		GameServerConnection.getInstance().addListener(this);

		GameServerConnection.getInstance().requestPlayerList();
		lvPlayerSelection.setOnItemClickListener(this);

		location = (Location) getIntent().getParcelableExtra("location");
	}

	@Override
	public void onConnect() {
	}

	//Source: http://stackoverflow.com/a/15893406/684353
	public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
						Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		return dist;
	}

	public String getCityName(double lat, double lng) {
		//http://stackoverflow.com/a/28939857/684353
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		List<Address> addresses;
		try {
			addresses = geocoder.getFromLocation(lat, lng, 1);
			if (addresses.size() > 0) {
				return addresses.get(0).getLocality();
//				String stateName = addresses.get(0).getAddressLine(1);
//				//Toast.makeText(getApplicationContext(),stateName , 1).show();
//				String countryName = addresses.get(0).getAddressLine(2);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onMessage(Message message) {
		String command = message.command;
		switch (command) {
			case "players":
				onPlayersReceived((JSONArray) message.data);
				break;
			case "inviteReceived":
				// Ik wordt uigenodigd door iemand anders
				onInviteReceived((JSONObject) message.data);
				break;
			case "inviteAccepted":
				// Mijn uitnodiging aan iemand anders is geaccepteerd
				{
					JSONObject data = (JSONObject) message.data;
					String playerId = null;
					try {
						playerId = data.getString("invitedPlayerId");
					} catch (JSONException e) {
						e.printStackTrace();
					}

					onInviteAccepted(playerId);
				}
				break;
			case "inviteAcceptedSuccessful":
				// Ik heb een invite geaccepteerd en dat ging goed
				onInviteAcceptedSuccessful();
				break;
			case "playerUnavailable":
				onInvitePlayerUnavailable();
				break;
		}
	}

	private void onInviteReceived(JSONObject data) {
		try {
			final String senderName = data.getString("sendername");
			final String senderID = data.getString("senderID");
			final PlayerSelection self = this;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					new AlertDialog.Builder(self)
							.setTitle("Game invite")
							.setMessage(senderName + " is inviting you to play N-Puzzle! Accept?")
							.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									// do nothing
								}
							})
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									sendAccept(senderID);
								}
							})
							.show();
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	private void onInvitePlayerUnavailable() {
		final PlayerSelection self = this;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(self, "This player is unavailable. Please refresh.", Toast.LENGTH_LONG).show();
			}
		});
	}
	private void onInviteAccepted(String playerId) {
		Intent i = new Intent(this, ImageSelection.class);
		i.putExtra("versusPlayerId", playerId);
		startActivity(i);
	}
	private void onInviteAcceptedSuccessful() {
		Intent i = new Intent(this, MultiplayerWaitingForGameStart.class);
		startActivity(i);
	}

	private void onPlayersReceived(JSONArray data) {
		ArrayList<PlayerListItem> playerList = new ArrayList<>();
		try {
			for (int i = 0; i < data.length(); i++) {
				PlayerListItem pli = new PlayerListItem();
				pli.id = data.getJSONObject(i).getString("id");
				pli.naam = data.getJSONObject(i).getString("naam");
				JSONObject playerLoc = data.getJSONObject(i).getJSONObject("location");
				pli.afstand = distFrom(location.getLatitude(), location.getLongitude(), playerLoc.getDouble("lat"), playerLoc.getDouble("lon"));
				pli.stad = getCityName(playerLoc.getDouble("lat"), playerLoc.getDouble("lon"));
				Log.i("PlayerSelection", "Afstand tot " + pli.naam + ": " + pli.afstand + "(" + pli.stad + ")");
				playerList.add(pli);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Collections.sort(playerList, new Comparator<PlayerListItem>() {
			@Override
			public int compare(PlayerListItem o1, PlayerListItem o2) {
				if (o1.afstand > o2.afstand) {
					return 1;
				} else if (o1.afstand < o2.afstand) {
					return -1;
				} else {
					return 0;
				}
			}
		});

		final PlayerSelection self = this;
		final ArrayList<PlayerListItem> players = playerList;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lvPlayerSelection.setAdapter(new PlayerListAdapter(self, players));
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
		PlayerListItem pli = (PlayerListItem) view.getTag();
		GameServerConnection.getInstance().sendInvite(pli.id);
		showPlayerInvitedDialog();
	}

	private void showPlayerInvitedDialog() {
		final PlayerSelection self = this;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new AlertDialog.Builder(self)
					.setTitle("Invite sent")
					.setMessage("Waiting for response...")
					.setCancelable(true)
					.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// do nothing
						}
					})
					.show();
			}
		});
	}

	private void sendAccept(String senderID) {
		GameServerConnection.getInstance().acceptInvite(senderID);
	}

	private void refreshPlayerList() {
		GameServerConnection.getInstance().requestPlayerList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_player_selection, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_refresh:
				refreshPlayerList();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
