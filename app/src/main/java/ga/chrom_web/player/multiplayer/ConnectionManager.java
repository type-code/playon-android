package ga.chrom_web.player.multiplayer;


import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import io.socket.client.Ack;
import io.socket.client.Socket;

public class ConnectionManager {

    @Inject
    public Socket socket;

    private ConnectionListener connectionListener;

    public ConnectionManager() {
        App.getComponent().inject(this);
        socket.connect();
        subscribeOnEvents();
    }

    private void subscribeOnEvents() {
        socket.on("connected", args -> {
            // TODO: do something on connect
            // send join
            System.out.println("connected");
            if (connectionListener != null) {
                connectionListener.connected(ParseUtil.parseConnectionData(args[0]));
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
        socket.emit("join", jsonObject, (Ack) args -> {
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
