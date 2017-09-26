package ga.chrom_web.player.multiplayer;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import javax.inject.Inject;

import io.socket.client.Ack;
import io.socket.client.Socket;

public class ConnectionManager extends Manager {

    private static final String EVENT_CONNECTED = "connected";
    private static final String EVENT_JOIN = "join";

    private ConnectionListener connectionListener;

    public void connect() {
        socket.connect();
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    @Override
    void subscribeOnEvents() {
        socket.on(EVENT_CONNECTED, args -> {
            if (connectionListener != null) {
                connectionListener.connected(JsonUtil.jsonToObject(args[0], ConnectionData.class));
            }
        });
    }

    public void join(String nick) {
        HashMap<String, String> map = new HashMap<>();
        map.put("nick", nick);
        socket.emit(EVENT_JOIN, new JSONObject(map), (Ack) args -> {
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
