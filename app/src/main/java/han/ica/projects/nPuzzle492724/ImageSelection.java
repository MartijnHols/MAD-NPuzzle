package han.ica.projects.nPuzzle492724;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.drafts.Draft_75;
import org.java_websocket.drafts.Draft_76;
import org.java_websocket.handshake.ServerHandshake;

public class ImageSelection extends ActionBarActivity implements AdapterView.OnItemClickListener {
	protected RadioButton rbEasy;
	protected RadioButton rbMedium;
	protected RadioButton rbHard;

	protected GridView gvImageSelection;

	public static int IMAGE_SAMPLE_SIZE = 2;

	public int getDifficulty() {
		if (rbEasy.isChecked()) {
			return Game.DIFFICULTY_EASY;
		} else if (rbMedium.isChecked()) {
			return Game.DIFFICULTY_MEDIUM;
		} else if (rbHard.isChecked()) {
			return Game.DIFFICULTY_HARD;
		}
		return Game.DIFFICULTY_HARD;
	}

	private List<Image> getImages() {
		List<Image> images = new ArrayList<Image>();
		for (Field field : R.drawable.class.getFields()) {
			if (field.getName().startsWith("puzzle_")) {
				try {
					Image image = new Image(this, field.getInt(null), IMAGE_SAMPLE_SIZE);
					images.add(image);
				} catch (Exception e) {
				}
			}
		}
		return images;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_selection);

		rbEasy = (RadioButton) findViewById(R.id.rbEasy);
		rbMedium = (RadioButton) findViewById(R.id.rbMedium);
		rbHard = (RadioButton) findViewById(R.id.rbHard);
		gvImageSelection = (GridView) findViewById(R.id.gvImageSelection);

		gvImageSelection.setAdapter(new ImageSelectionAdapter(this, getImages()));
		gvImageSelection.setOnItemClickListener(this);
        connectWebSocket();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long resourceId) {
		Intent i = new Intent(this, GamePlay.class);
		i.putExtra("resourceId", (int) resourceId)
			.putExtra("difficulty", getDifficulty());
		startActivity(i);
	}

    private WebSocketClient mWebSocketClient;

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://192.168.0.27:1337");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        TextView textView = (TextView)findViewById(R.id.messages);
//                        textView.setText(textView.getText() + "\n" + message);
                        Log.d("kip", message);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

}
