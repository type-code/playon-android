package ga.chrom_web.player.multiplayer;


import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

import ga.chrom_web.player.multiplayer.data.ConnectionData;
import io.socket.client.Socket;

public class ConnectionManager extends Manager {

    private ConnectionListener connectionListener;

    public void connect() {
        Utils.debugLog("Trying to connect...");
        socket.connect();
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    @Override
    void subscribeOnEvents() {
        socket.on(EVENT_CONNECTED, args -> {
            Utils.debugLog("Connected");
            if (connectionListener != null) {
                connectionListener.someoneConnected(JsonUtil.jsonToObject(args[0], ConnectionData.class));
            }
        });
        socket.on(EVENT_JOIN, args -> {
            Utils.debugLog("Someone joined maybe it's me: " + args[0]);
            if (connectionListener != null) {
                connectionListener.joined(JsonUtil.parseNick(args[0]));
            }
        });
        socket.on(EVENT_DISCONNECT, args -> {
            Utils.debugLog("Someone disconnect: " + args[0]);
            if (connectionListener != null) {
                connectionListener.someoneDisconnected(JsonUtil.parseNick(args[0]));
            }
        });
    }

    public void join(String nick) {
        HashMap<String, String> map = new HashMap<>();
        map.put("nick", nick);
        socket.emit(EVENT_JOIN, new JSONObject(map));
    }

    public interface ConnectionListener {
        void someoneConnected(ConnectionData connectionData);

        void joined(String nick);

        void someoneDisconnected(String nick);
    }
}
