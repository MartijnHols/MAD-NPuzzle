package han.ica.projects.nPuzzle492724;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class PlayerListItem {
    public String naam;
    public String id;
    public int afstand;

    public String getId() {
        return id;
    }
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

        location = (Location)getIntent().getParcelableExtra("location");
    }

    @Override
    public void onConnect() {
    }

	//Source: http://stackoverflow.com/a/15893406/684353
	public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return dist;
    }

    @Override
    public void onMessage(Message message) {
        JSONArray data = (JSONArray) message.data;
        final PlayerSelection self = this;
        String command = message.command;
        switch (command) {
            case "players":
                ArrayList<PlayerListItem> playerList = new ArrayList<>();
                try {
                    for (int i = 0; i < data.length(); i++) {
                        PlayerListItem pli = new PlayerListItem();
                        pli.id = data.getJSONObject(i).getString("id");
                        pli.naam = data.getJSONObject(i).getString("naam");
                        JSONObject playerLoc = data.getJSONObject(i).getJSONObject("location");
                        pli.afstand = (int)distFrom(location.getLatitude(), location.getLongitude(), playerLoc.getDouble("lat"), playerLoc.getDouble("lon"));
                        playerList.add(pli);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Collections.sort(playerList, new Comparator<PlayerListItem>() {
                    @Override
                    public int compare(PlayerListItem o1, PlayerListItem o2) {
                        return o1.afstand - o2.afstand;
                    }
                });

                final ArrayList<PlayerListItem> players = playerList;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lvPlayerSelection.setAdapter(new PlayerListAdapter(self, players));
                    }
                });
                break;
            case "invite":
                try {
                    showInvitationDialog(data.getJSONObject(0).getString("sender"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "playerUnavailable":
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(self, "This player is unavailable. Please refresh", Toast.LENGTH_LONG).show();
                    }
                });
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String clickedPlayerID = (String) view.getTag();

        GameServerConnection.getInstance().sendGameInvitation("harry", clickedPlayerID);
    }

    private void showInvitationDialog(final String sender) {
        final String senderName = sender;
        final PlayerSelection self = this;
        PlayerSelection.this.runOnUiThread(new Runnable() {
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
                                                                   startMultiplayerGame();
                                                               }
                                                           })
                                                           .show();
                                               }
                                           }
        );
    }

    private void startMultiplayerGame() {
        startActivity(new Intent(PlayerSelection.this, GamePlay.class));
        finish();
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
