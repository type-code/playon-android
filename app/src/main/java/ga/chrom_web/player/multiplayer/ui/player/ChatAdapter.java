package ga.chrom_web.player.multiplayer.ui.player;


import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import ga.chrom_web.player.multiplayer.R;
import ga.chrom_web.player.multiplayer.data.ChatItem;
import ga.chrom_web.player.multiplayer.data.ChatMessage;
import ga.chrom_web.player.multiplayer.data.ChatNotification;
import ga.chrom_web.player.multiplayer.databinding.ListChatMessageBinding;
import ga.chrom_web.player.multiplayer.databinding.ListChatNotificationBinding;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.DataBindingViewHolder<ViewDataBinding>> {

    private static final int TYPE_MESSAGE = 1;
    private static final int TYPE_NOTIFICATION = 2;
    private ArrayList<ChatItem> items;

    public ChatAdapter() {
        items = new ArrayList<>();
    }

    @Override
    public DataBindingViewHolder<ViewDataBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = -1;
        if (viewType == TYPE_MESSAGE) {
            layoutId = R.layout.list_chat_message;
        } else if (viewType == TYPE_NOTIFICATION) {
            layoutId = R.layout.list_chat_notification;
        } else {
            return null;
        }
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                layoutId, parent, false);
        return new DataBindingViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(DataBindingViewHolder<ViewDataBinding> holder, int position) {
        if (holder.binding instanceof ListChatMessageBinding) {
            ListChatMessageBinding binding = (ListChatMessageBinding) holder.binding;
            binding.setMessage(((ChatMessage) items.get(position)));
        } else if (holder.binding instanceof ListChatNotificationBinding) {
            ListChatNotificationBinding binding = (ListChatNotificationBinding) holder.binding;
            binding.setNotification((ChatNotification) items.get(position));
        }
    }


    public void addItem(ChatItem item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        ChatItem item = items.get(position);
        if (item instanceof ChatMessage) {
            return TYPE_MESSAGE;
        } else if (item instanceof ChatNotification) {
            return TYPE_NOTIFICATION;
        }
        return TYPE_MESSAGE;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class DataBindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

        T binding;

        DataBindingViewHolder(T binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
