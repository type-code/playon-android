package ga.chrom_web.player.multiplayer;


import javax.inject.Inject;

import io.socket.client.Socket;

public abstract class Manager {

    @Inject
    Socket socket;

    public Manager() {
        App.getComponent().inject(this);
        subscribeOnEvents();
    }

    abstract void subscribeOnEvents();

    protected void printEmit(String eventName) {
        System.out.println("EMIT " + eventName);
    }
}
