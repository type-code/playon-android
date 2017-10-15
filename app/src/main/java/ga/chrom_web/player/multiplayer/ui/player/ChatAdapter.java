package ga.chrom_web.player.multiplayer.ui.player;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import ga.chrom_web.player.multiplayer.R;
import ga.chrom_web.player.multiplayer.SmilesLoader;
import ga.chrom_web.player.multiplayer.Utils;
import ga.chrom_web.player.multiplayer.data.ChatItem;
import ga.chrom_web.player.multiplayer.data.ChatMessage;
import ga.chrom_web.player.multiplayer.data.ChatNotification;
import ga.chrom_web.player.multiplayer.databinding.ListChatMessageBinding;
import ga.chrom_web.player.multiplayer.databinding.ListChatNotificationBinding;
import kotlin.Pair;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.DataBindingViewHolder<ViewDataBinding>> {

    private static final int SMILES_MESSAGE_TOP_MARGIN_IN_DP = 4;

    private static final int TYPE_MESSAGE = 1;
    private static final int TYPE_NOTIFICATION = 2;
    private ArrayList<ChatItem> items;
    /**
     * If there are two consecutive messages from one user, the line between messages should be drawn
     * We should update previous message line no more than one time
     */
    private ArrayList<Boolean> updatedBottomLine;
    private HashMap<String, String> smilePaths;

    ChatAdapter() {
        items = new ArrayList<>();
        updatedBottomLine = new ArrayList<>();
        new SmilesLoader().getSmilesPaths(smiles -> {
            this.smilePaths = smiles;
        });
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
    public void onBindViewHolder(DataBindingViewHolder<ViewDataBinding> holder, int position) {
        if (holder.binding instanceof ListChatMessageBinding) {
            ListChatMessageBinding binding = (ListChatMessageBinding) holder.binding;
            binding.setMessage((ChatMessage) items.get(position));
            // set up top and bottom lines
            // top line
            if (position > 0 && areMessagesFromSameUsers(position, position - 1)) {
                binding.setTopLineVisible(true);
                if (!updatedBottomLine.get(position - 1)) {
                    updatedBottomLine.set(position - 1, true);
                    // notify previous item to draw top line
                    binding.getRoot().post(() -> notifyItemChanged(position - 1));
                }
            } else {
                binding.setTopLineVisible(false);
            }
            // bottom line
            if (position + 1 < items.size() && areMessagesFromSameUsers(position, position + 1)) {
                binding.setBottomLineVisible(true);
            } else {
                binding.setBottomLineVisible(false);
            }
            binding.tvText.setText(createMessage(binding.getRoot().getContext(),
                    ((ChatMessage) items.get(position)).getFullMessage(), smilePaths));
            if (areSmilesInMessage(items.get(position))) {
                binding.setAreSmilesInMessage(areSmilesInMessage(items.get(position)));
                int margin = (int) (binding.tvText.getTextSize()
                        + Utils.dpToPx(binding.getRoot().getContext(), SMILES_MESSAGE_TOP_MARGIN_IN_DP));
                ((ViewGroup.MarginLayoutParams) binding.circleView.getLayoutParams()).topMargin = margin;
            }
        } else if (holder.binding instanceof ListChatNotificationBinding) {
            ListChatNotificationBinding binding = (ListChatNotificationBinding) holder.binding;
            binding.setNotification((ChatNotification) items.get(position));
            // we need rounded corners and custom background color
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
        updatedBottomLine.add(false);
        notifyItemInserted(items.size() - 1);
    }

    private boolean areSmilesInMessage(ChatItem message) {
        if (!(message instanceof ChatMessage)) {
            return false;
        }
        if (smilePaths != null) {
            for (String key: smilePaths.keySet()) {
                if (((ChatMessage) message).getFullMessage().contains(key)) {
                    return true;
                }
            }
        }
        return false;
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
        for (int i = 0; i < items.size() - prevLength; i++) {
            updatedBottomLine.add(false);
        }
        notifyItemRangeInserted(prevLength, items.size() - prevLength);
    }

    ArrayList<ChatItem> getItems() {
        return items;
    }

    private SpannableStringBuilder createMessage(Context context, String textMessage, HashMap<String, String> smiles) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        for (int i = 0; i < textMessage.length();) {
            Pair<Integer, String> firstSmile = findFirstSmile(textMessage, i, smiles);
            if (firstSmile == null) {
                ssb.append(textMessage.substring(i));
                break;
            }
            textMessage = textMessage.replaceFirst(firstSmile.getSecond(), " ");
            ssb.append(textMessage.substring(i, firstSmile.getFirst()));

            Drawable smileDrawable = getSmileDrawable(context, smiles.get(firstSmile.getSecond()));
            ImageSpan span = new ImageSpan(smileDrawable, ImageSpan.ALIGN_BOTTOM);
            ssb.append("123", span, Spannable.SPAN_INTERMEDIATE);
            i = firstSmile.getFirst();
        }

        return ssb;
    }

    private Drawable getSmileDrawable(Context context, String path) {
        int width = Utils.spToPx(context, 24);
        int height = Utils.spToPx(context, 24);
        Drawable d = Drawable.createFromPath(path);
        d.setBounds(0, 0, width, height);
        return d;
    }

    private Pair<Integer, String> findFirstSmile(String str, int fromIndex, HashMap<String, String> smiles) {
        int firstSmileIndex = Integer.MAX_VALUE;
        String firstSmile = null;
        for (String smile : smiles.keySet()) {
            int indexOf = str.indexOf(smile, fromIndex);
            if (indexOf != -1) {
                if (firstSmileIndex > indexOf) {
                    firstSmileIndex = indexOf;
                    firstSmile = smile;
                }
            }
        }
        if (firstSmileIndex == Integer.MAX_VALUE) {
            return null;
        }
        return new Pair<>(firstSmileIndex, firstSmile);
    }

    class DataBindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

        T binding;

        DataBindingViewHolder(T binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
