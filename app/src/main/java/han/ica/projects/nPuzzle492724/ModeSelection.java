package han.ica.projects.nPuzzle492724;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class ModeSelection extends ActionBarActivity {
	private Button btnSingleplayer;
	private Button btnMultiplayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_mode);

		btnSingleplayer = (Button) findViewById(R.id.btnSingleplayer);
		btnMultiplayer = (Button) findViewById(R.id.btnMultiplayer);

		btnSingleplayer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ModeSelection.this, ImageSelection.class));
			}
		});
		btnMultiplayer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO: Add activity
				startActivity(new Intent(ModeSelection.this, PlayerSelection.class));
			}
		});
	}
}
