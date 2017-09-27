package ga.chrom_web.player.multiplayer;


import org.json.JSONObject;

import java.util.HashMap;

import ga.chrom_web.player.multiplayer.data.ConnectionData;

public class ConnectionManager extends Manager {

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
        socket.on(EVENT_JOIN, args -> {
                if (connectionListener != null) {
                    connectionListener.joined(JsonUtil.parseJoined(args[0]));
                }
        });
    }

    public void join(String nick) {
        HashMap<String, String> map = new HashMap<>();
        map.put("nick", nick);
        socket.emit(EVENT_JOIN, new JSONObject(map));
    }

    public interface ConnectionListener {
        void connected(ConnectionData connectionData);

        void joined(String nick);
    }
}
