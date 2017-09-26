package ga.chrom_web.player.multiplayer.view.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ga.chrom_web.player.multiplayer.R;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MESSAGE = 1;
    private static final int TYPE_NOTIFICATION = 2;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TYPE_MESSAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chat_message, parent, false);
            return new MessageViewHolder(view);
        } else if (viewType == TYPE_NOTIFICATION) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chat_notification, parent, false);
            return new NotificationViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessageViewHolder) {

        } else if (holder instanceof NotificationViewHolder) {

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0) {
            return TYPE_MESSAGE;
        } else {
            return TYPE_NOTIFICATION;
        }
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public MessageViewHolder(View itemView) {
            super(itemView);
        }
    }


    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        public NotificationViewHolder(View itemView) {
            super(itemView);
        }
    }
}
