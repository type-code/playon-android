package ga.chrom_web.player.multiplayer;


import org.json.JSONObject;

import java.util.HashMap;

public class ChatManager extends Manager {

    private static final String EVENT_MESSAGE = "message";

    private ChatListener chatListener;

    @Override
    void subscribeOnEvents() {
        socket.on(EVENT_MESSAGE, args -> {
            if (chatListener != null) {
                chatListener.onMessage(JsonUtil.jsonToObject(args[0], ChatMessage.class));
            }
        });
    }

    public void setChatListener(ChatListener chatListener) {
        this.chatListener = chatListener;
    }

    public void sendMessage(String text, String hexColor) {
        HashMap<String, String> map = new HashMap<>();
        map.put("text", text);
        map.put("color", hexColor);
        socket.emit(EVENT_MESSAGE, new JSONObject(map));
    }

    public interface ChatListener {
        void onMessage(ChatMessage chatMessage);
    }
}
