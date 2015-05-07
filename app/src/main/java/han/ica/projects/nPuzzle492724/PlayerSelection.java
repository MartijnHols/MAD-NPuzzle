package han.ica.projects.nPuzzle492724;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class PlayerSelection extends ActionBarActivity {
protected ListView lvPlayerSelection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player_selection);
		lvPlayerSelection = (ListView) findViewById(R.id.lvPlayerSelection);

		List<String> playerArray = new ArrayList<String>();
		playerArray.add("player1");
		playerArray.add("player2");

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
			this,
			android.R.layout.simple_list_item_1,
			playerArray );

		lvPlayerSelection.setAdapter(arrayAdapter);
	}
}
