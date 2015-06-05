package han.ica.projects.nPuzzle492724;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;


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
    public void onConnect() {
    }

    @Override
    public void onMessage(Message message) {
        JSONArray data = (JSONArray) message.data;

        String command = message.command;
        switch (command) {
            case "players":
                ArrayList<String> playerList = new ArrayList<>();
                //ArrayList<String> playerIds = new ArrayList<>();
                //Map<String, String> playerIds = new HashMap<String, String>();
                try {
                    for (int i = 0; i < data.length(); i++) {
                        String id = data.getJSONObject(i).getString("id");
                        String name = data.getJSONObject(i).getString("naam");
                        //playerIds.add(id);
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
                /* for (int i = 0; i < lvPlayerSelection.getCount(); i++) {
                    View v = lvPlayerSelection.getAdapter().getView(i, null, null);
                    TextView tv = (TextView) v.findViewById(i);
                    String playerid = playerIds.get(i).toString();
                    tv.setTag(playerid);
                }*/
                break;
            case "invite":
                try {
                    showInvitationDialog(data.getJSONObject(0).getString("sender"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        String clickedPlayerName = parent.getItemAtPosition(position).toString();
        String clickedPlayerID = (String) parent.getTag();
        GameServerConnection.getInstance().sendGameInvitation(clickedPlayerName, clickedPlayerID);
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
                           .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   startMultiplayerGame();
                               }
                           })
                           .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   // do nothing
                               }
                           })
                           .show();
               }
           }
        );
    }
    private void startMultiplayerGame(){
        startActivity(new Intent(PlayerSelection.this, GamePlay.class));
        finish();
    }
}
