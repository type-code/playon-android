package ga.chrom_web.player.multiplayer;


import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import io.socket.client.Ack;
import io.socket.client.Socket;

public class ConnectionManager {

    private static final String EVENT_CONNECTED = "connected";
    private static final String EVENT_JOIN = "join";

    @Inject
    public Socket socket;

    private ConnectionListener connectionListener;

    public ConnectionManager() {
        App.getComponent().inject(this);
        subscribeOnEvents();
    }

    public void connect() {
        socket.connect();
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    private void subscribeOnEvents() {
        socket.on(EVENT_CONNECTED, args -> {
            if (connectionListener != null) {
                connectionListener.connected(JsonUtil.parseConnectionData(args[0]));
            }
        });
    }

    public void join(String nick) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nick", nick);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(EVENT_JOIN, jsonObject, (Ack) args -> {
            if (connectionListener != null) {
                connectionListener.joined();
            }
        });
    }

    public interface ConnectionListener {
        void connected(ConnectionData connectionData);

        void joined();
    }
}
