package han.ica.projects.nPuzzle492724;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class ModeSelection extends ActionBarActivity {
	private EditText txtUsername;
	private Button btnSingleplayer;
	private Button btnMultiplayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mode_selection);

		txtUsername = (EditText) findViewById(R.id.txtUsername);
		btnSingleplayer = (Button) findViewById(R.id.btnSingleplayer);
		btnMultiplayer = (Button) findViewById(R.id.btnMultiplayer);

		btnSingleplayer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ModeSelection.this, ImageSelection.class));
			}
		});
		final ModeSelection self = this;
		btnMultiplayer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ModeSelection.this, MultiplayerConnecting.class);
				i.putExtra("name", txtUsername.getText().toString());
				startActivity(i);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		GameServerConnection con = GameServerConnection.getInstance();
		if (con.isConnected()) {
			con.disconnect();
		}
	}
}
