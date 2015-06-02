package han.ica.projects.nPuzzle492724;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MultiplayerConnecting extends ActionBarActivity implements GameServerConnectionListener {
	private TextView txtConnectStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiplayer_connecting);

		txtConnectStatus = (TextView)findViewById(R.id.txtConnectStatus);

		GameServerConnection con = GameServerConnection.getInstance();
		if (con.isConnected()) {
			goToPlayerSelection();
		} else {
			con.addListener(this);
			Log.i("MultiplayerConnection", "Connecting..");
			con.connect();
		}
	}

	@Override
	public void onConnect() {
		Log.i("MultiplayerConnection", "Connected!");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				txtConnectStatus.setText("Connected!");
			}
		});
		GameServerConnection.getInstance().register(getIntent().getStringExtra("name"), 0, 0);
	}
	@Override
	public void onMessage(Message message) {
		Log.i("MultiplayerConnection", message.command);
		if (message.command.equals("register_successful")) {
			Log.i("MultiplayerConnection", "Registered");
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					txtConnectStatus.setText("Registered!");
					goToPlayerSelection();
				}
			});
		}
	}

	public void goToPlayerSelection() {
		startActivity(new Intent(MultiplayerConnecting.this, PlayerSelection.class));
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	@Override
	public void finish() {
		GameServerConnection.getInstance().removeListener(this);

		super.finish();
	}
}
