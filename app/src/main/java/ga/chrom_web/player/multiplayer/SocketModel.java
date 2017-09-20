package ga.chrom_web.player.multiplayer;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketModel {

    private Socket socket;

    public SocketModel() {
        try {
            socket = IO.socket("http://player.chrom-web.ga:8080");

            // TODO: if not connect ???
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}

