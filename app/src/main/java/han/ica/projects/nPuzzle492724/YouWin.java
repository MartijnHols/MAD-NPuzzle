package han.ica.projects.nPuzzle492724;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class YouWin extends ActionBarActivity {
	ImageView ivPicture;
	TextView tvPlayerMoves;
	Button btnNewGame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_you_win);

		ivPicture = (ImageView) findViewById(R.id.ivPicture);
		tvPlayerMoves = (TextView) findViewById(R.id.tvPlayerMoves);
		btnNewGame = (Button) findViewById(R.id.btnNewGame);

		int resourceId = getIntent().getIntExtra("resourceId", 0);
		int playerMoves = getIntent().getIntExtra("playerMoves", -1);

		ivPicture.setImageResource(resourceId);
		tvPlayerMoves.setText(String.format("%d", playerMoves));
		btnNewGame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(YouWin.this, ImageSelection.class));
			}
		});
	}
}
