package han.ica.projects.nPuzzle492724;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;


public class MultiplayerConnecting extends ActionBarActivity implements GameServerConnectionListener, LocationListener {
	private TextView txtConnectStatus;
	private LocationManager locationManager;

	private Location location = null;

	public static int MAX_LOCATION_AGE = 60 * 60 * 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiplayer_connecting);

		txtConnectStatus = (TextView) findViewById(R.id.txtConnectStatus);

		GameServerConnection con = GameServerConnection.getInstance();
		if (con.isConnected()) {
			Log.i("MultiplayerConnection", "Already connected, waiting for location...");
			txtConnectStatus.setText("Already connected, waiting for location...");
			isConnected = true;
			loadProgress();
		} else {
			Log.i("MultiplayerConnection", "Connecting...");
			txtConnectStatus.setText("Connecting...");
			con.addListener(this);
			con.connect();
		}

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		// Source: http://stackoverflow.com/a/10524443/684353
		Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastKnownLocation != null && lastKnownLocation.getTime() > Calendar.getInstance().getTimeInMillis() - MAX_LOCATION_AGE) {
			Log.i("MultiplayerConnection", "Already known location: " + location.getLatitude() + " and " + location.getLongitude());
			txtConnectStatus.setText("Location received, waiting for connection...");
			location = lastKnownLocation;
			loadProgress();
		} else {
			Log.i("MultiplayerConnection", "Requesting location updates...");
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		}
	}

	private boolean isConnected = false;
	@Override
	public void onConnect() {
		Log.i("MultiplayerConnection", "Connected!");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				txtConnectStatus.setText("Connected! Wachten op locatie...");
			}
		});

		isConnected = true;
		loadProgress();
	}
	@Override
	public void onLocationChanged(final Location location) {
		Log.i("MultiplayerConnection", "onLocationChanged");
		if (location != null) {
			Log.i("MultiplayerConnection", "Location Changed: " + location.getLatitude() + " and " + location.getLongitude());
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					txtConnectStatus.setText("Location received, waiting for connection...");
				}
			});
			locationManager.removeUpdates(this);

			this.location = location;
			loadProgress();
		} else {
			Log.e("MultiplayerConnection", "Error! No location received :(");
		}
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.i("MultiplayerConnection", "onStatusChanged");
	}
	@Override
	public void onProviderEnabled(String provider) {
		Log.i("MultiplayerConnection", "onProviderEnabled");
	}
	@Override
	public void onProviderDisabled(String provider) {
		Log.i("MultiplayerConnection", "onProviderDisabled");
	}

	public void loadProgress() {
		if (isConnected && location != null) {
			GameServerConnection.getInstance().register(getIntent().getStringExtra("name"), location);
		}
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
		Intent i = new Intent(MultiplayerConnecting.this, PlayerSelection.class);
		i.putExtra("location", location);
		startActivity(i);
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
