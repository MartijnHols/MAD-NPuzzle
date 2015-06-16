package han.ica.projects.nPuzzle492724;

import android.location.Location;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

interface GameServerConnectionListener {
	public void onMessage(Message message);
	public void onConnect();
}

class Message {
	public String command;
	public Object data;

	public Message(String cmd) {
		command = cmd;
	}

	@Override
	public String toString() {
		JSONObject message = new JSONObject();
		try {
			message.put("command", command);
			message.put("data", data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return message.toString();
	}
}

public class GameServerConnection {
	private static GameServerConnection instance = new GameServerConnection();

	public static GameServerConnection getInstance() {
		return instance;
	}

	private GameServerConnection() {
	}

	private WebSocketClient mWebSocketClient;

	public boolean isConnected() {
		return mWebSocketClient != null;
	}

	public void connect() {
		if (isConnected()) {
			return; // kan geen normale exception in java
		}
		String address = "ws://192.168.0.116:1337";
		Log.i("WebSocket", "Connecting to: " + address);
		URI uri;
		try {
			uri = new URI(address);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}

		final GameServerConnection self = this;

		mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
			@Override
			public void onOpen(ServerHandshake serverHandshake) {
				Log.i("Websocket", "Opened");

				self.onConnect();
			}

			@Override
			public void onMessage(String s) {
				Log.d("Websocket", "Message received: " + s);

				try {
					JSONObject json = new JSONObject(s);

					Message m = new Message(json.getString("command"));
					m.data = json.get("data");

					self.onMessage(m);
				} catch (JSONException e) {
					e.printStackTrace();
				}
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
		Log.i("WebSocket", "Connecting...");
		mWebSocketClient.connect();
		Log.i("WebSocket", "Connected.");
	}
	public void disconnect() {
		mWebSocketClient.close();
		mWebSocketClient = null;
		listeners.clear();
	}

	List<GameServerConnectionListener> listeners = new ArrayList<GameServerConnectionListener>();

	public void addListener(GameServerConnectionListener listener) {
		listeners.add(listener);
	}
	public void removeListener(GameServerConnectionListener listener) {
		listeners.remove(listener);
	}

	private void onConnect() {
		for (GameServerConnectionListener hl : listeners) {
			hl.onConnect();
		}
	}
	private void onMessage(Message m) {
		for (GameServerConnectionListener hl : listeners) {
			hl.onMessage(m);
		}
	}

	private void send(Message m) {
		mWebSocketClient.send(m.toString());
	}

	public  void sendGameInvitation (String name, String id){
		String sender = "sendername";
		Message m = new Message("sendInvitation");
		JSONObject data = new JSONObject();
		try {
			data.put("sender", sender);
			data.put("name", name);
			data.put("id", id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		m.data = data;

		send(m);
	}

	public void register(String name, Location location) {
		Message m = new Message("register");
		JSONObject data = new JSONObject();
		try {
			data.put("name", name);
			data.put("lat", location.getLatitude());
			data.put("lon", location.getLongitude());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		m.data = data;

		send(m);
	}

	public void requestPlayerList() {
		Message m = new Message("getPlayers");
		send(m);
	}
}
