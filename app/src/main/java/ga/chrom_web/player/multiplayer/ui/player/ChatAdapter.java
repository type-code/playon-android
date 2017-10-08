package ga.chrom_web.player.multiplayer.ui.player;


import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import ga.chrom_web.player.multiplayer.R;
import ga.chrom_web.player.multiplayer.Utils;
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
        int layoutId;
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
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    private RecyclerView recyclerView;

    @Override
    public void onBindViewHolder(DataBindingViewHolder<ViewDataBinding> holder, int position) {
        if (holder.binding instanceof ListChatMessageBinding) {
            ListChatMessageBinding binding = (ListChatMessageBinding) holder.binding;
            binding.setMessage((ChatMessage) items.get(position));
            if (position > 0 && areMessagesFromSameUsers(position, position - 1)) {
                binding.setTopLineVisible(true);
                binding.getRoot().post(() -> notifyItemChanged(position - 1));
            } else {
                binding.setTopLineVisible(false);
            }
            if (position + 1 < items.size() && areMessagesFromSameUsers(position, position + 1)) {
                binding.setBottomLineVisible(true);
            } else {
                binding.setBottomLineVisible(false);
            }
        } else if (holder.binding instanceof ListChatNotificationBinding) {
            ListChatNotificationBinding binding = (ListChatNotificationBinding) holder.binding;
            binding.setNotification((ChatNotification) items.get(position));
            // we need both rounded corners and custom background color
            // rounded corners are set as background in XML
            GradientDrawable sd = (GradientDrawable) binding.tvText.getBackground();
            sd.setColor(((ChatNotification) items.get(position)).getColor());
        }
    }

    private boolean areMessagesFromSameUsers(int position1, int position2) {
        if (items.get(position1) instanceof ChatNotification || items.get(position2) instanceof ChatNotification) {
            return false;
        }
        return TextUtils.equals(items.get(position1).getNick(), items.get(position2).getNick());
    }


    void addItem(ChatItem item) {
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

    void addItems(ArrayList<ChatItem> messages) {
        int prevLength = items.size();
        items.addAll(messages);
        notifyItemRangeInserted(prevLength, items.size() - prevLength);
    }

    ArrayList<ChatItem> getItems() {
        return items;
    }

    class DataBindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

        T binding;

        DataBindingViewHolder(T binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
