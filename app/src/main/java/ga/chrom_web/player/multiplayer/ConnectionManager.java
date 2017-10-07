package ga.chrom_web.player.multiplayer;


import org.json.JSONObject;

import java.util.HashMap;

import ga.chrom_web.player.multiplayer.data.PlayerData;
import io.socket.client.Socket;

public class ConnectionManager extends Manager {

    private ConnectionListener connectionListener;

    public void connect() {
        Utils.debugLog("Trying to connect...");
        if (!socket.connected()) {
            socket.connect();
        } else {
            Utils.debugLog("But already connected...");
        }
    }

    public boolean isConnected() {
        return socket.connected();
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    @Override
    protected void subscribeOnEvents() {
        socket.on(EVENT_CONNECTED, args -> {
            Utils.debugLog("Connected. Data: " + args[0]);
            if (connectionListener != null) {
                connectionListener.connected(JsonUtil.jsonToObject(args[0], PlayerData.class));
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
        socket.on(Socket.EVENT_DISCONNECT, args -> {
            Utils.debugLog("Disconnected!!!");
        });
        socket.on(Socket.EVENT_RECONNECT, args -> {
            Utils.debugLog("Reconnect successful");
        });
    }

    public void join(String nick) {
        HashMap<String, String> map = new HashMap<>();
        map.put("nick", nick);
        getSocket().emit(EVENT_JOIN, new JSONObject(map));
    }

    public interface ConnectionListener {
        void connected(PlayerData playerData);

        void joined(String nick);

        void someoneDisconnected(String nick);
    }
}
