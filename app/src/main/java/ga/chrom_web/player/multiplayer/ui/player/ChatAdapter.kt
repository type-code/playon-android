package ga.chrom_web.player.multiplayer.ui.player


import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup

import java.io.File
import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedHashMap

import javax.inject.Inject

import ga.chrom_web.player.multiplayer.R
import ga.chrom_web.player.multiplayer.SmilesLoader
import ga.chrom_web.player.multiplayer.Utils
import ga.chrom_web.player.multiplayer.data.ChatItem
import ga.chrom_web.player.multiplayer.data.ChatMessage
import ga.chrom_web.player.multiplayer.data.ChatNotification
import ga.chrom_web.player.multiplayer.databinding.ListChatMessageBinding
import ga.chrom_web.player.multiplayer.databinding.ListChatNotificationBinding
import ga.chrom_web.player.multiplayer.di.App

class ChatAdapter internal constructor() : RecyclerView.Adapter<ChatAdapter.DataBindingViewHolder<ViewDataBinding>>() {

    companion object {

        private const val SMILES_MESSAGE_TOP_MARGIN_IN_DP = 4

        private const val TYPE_MESSAGE = 1
        private const val TYPE_NOTIFICATION = 2
    }


    internal val items: ArrayList<ChatItem>
    /**
     * If there are two consecutive messages from one user, the line between messages should be drawn
     * We should update previous message line no more than one time
     */
    private val updatedBottomLine: ArrayList<Boolean>
    private var smilePaths: LinkedHashMap<String, String>? = null

    @Inject
    lateinit var smilesLoader: SmilesLoader

    init {
        App.getComponent().inject(this)
        items = ArrayList()
        updatedBottomLine = ArrayList()
    }

    fun setSmilePaths(smilePaths: LinkedHashMap<String, String>) {
        this.smilePaths = smilePaths
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<ViewDataBinding>? {
        val layoutId: Int
        if (viewType == TYPE_MESSAGE) {
            layoutId = R.layout.list_chat_message
        } else if (viewType == TYPE_NOTIFICATION) {
            layoutId = R.layout.list_chat_notification
        } else {
            return null
        }
        val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context),
                layoutId, parent, false)
        return DataBindingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder<ViewDataBinding>, position: Int) {
        if (holder.binding is ListChatMessageBinding) {
            val binding = holder.binding as ListChatMessageBinding
            binding.message = items[position] as ChatMessage
            // set up top and bottom lines
            // top line
            if (position > 0 && areMessagesFromSameUsers(position, position - 1)) {
                binding.topLineVisible = true
                if (!updatedBottomLine[position - 1]) {
                    updatedBottomLine[position - 1] = true
                    // notify previous item to draw top line
                    binding.root.post { notifyItemChanged(position - 1) }
                }
            } else {
                binding.topLineVisible = false
            }
            // bottom line
            binding.bottomLineVisible = position + 1 < items.size && areMessagesFromSameUsers(position, position + 1)

            binding.tvText.text = createMessage(position,
                    binding.root.context,
                    items[position].nick,
                    (items[position] as ChatMessage).getText())

            if (areSmilesInMessage(items[position])) {
                binding.areSmilesInMessage = areSmilesInMessage(items[position])
                // make lower top margin for chat messages with smile
                val margin = (binding.tvText.textSize + Utils.dpToPx(binding.root.context, SMILES_MESSAGE_TOP_MARGIN_IN_DP)).toInt()
                (binding.circleView.layoutParams as ViewGroup.MarginLayoutParams).topMargin = margin
            }
        } else if (holder.binding is ListChatNotificationBinding) {
            val binding = holder.binding as ListChatNotificationBinding
            binding.notification = items[position] as ChatNotification
            // we need rounded corners and custom background color
            // rounded corners are set as background in XML
            val sd = binding.tvText.background as GradientDrawable
            sd.setColor((items[position] as ChatNotification).color)
        }
    }

    private fun areMessagesFromSameUsers(position1: Int, position2: Int): Boolean {
        if (items[position1] is ChatNotification || items[position2] is ChatNotification) {
            return false
        }
        return TextUtils.equals(items[position1].nick, items[position2].nick)
    }


    internal fun addItem(item: ChatItem) {
        items.add(item)
        updatedBottomLine.add(false)
        notifyItemInserted(items.size - 1)
    }

    private fun areSmilesInMessage(message: ChatItem): Boolean {
        if (message !is ChatMessage) {
            return false
        }
        smilePaths?.let { smilePaths ->
            for (key in smilePaths.keys) {
                if (message.getFullMessage().contains(key)) {
                    return true
                }
            }
        }
        return false
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        if (item is ChatMessage) {
            return TYPE_MESSAGE
        } else if (item is ChatNotification) {
            return TYPE_NOTIFICATION
        }
        return TYPE_MESSAGE
    }

    override fun getItemCount(): Int {
        return items.size
    }

    internal fun addItems(messages: ArrayList<ChatItem>) {
        val prevLength = items.size
        items.addAll(messages)
        for (i in 0 until items.size - prevLength) {
            updatedBottomLine.add(false)
        }
        notifyItemRangeInserted(prevLength, items.size - prevLength)
    }

    /**
     * Creates spannable message to be inserted in chat.<br></br>
     * Parses all smiles and put them in message
     *
     * @return SpannableStringBuilder - message
     */
    private fun createMessage(position: Int, context: Context, nick: String?, message: String?): SpannableStringBuilder {
        var message = message;
        val ssb = SpannableStringBuilder()

        val bold = StyleSpan(Typeface.BOLD)
        // append nick
        // nick should be bold
        ssb.append(nick!! + ": ", bold, Spanned.SPAN_INTERMEDIATE)

        // append message
        var i = 0
        while (i < message!!.length) {
            // smile pair:
            // @first - start position of smile in string
            // @second - name of smile
            val nearestSmile = findFirstSmile(message, i)
            if (nearestSmile == null) {
                // no smiles left
                ssb.append(message.substring(i))
                break
            }
            // remove nearest smile from message
            message = message.replaceFirst(nearestSmile.second.toRegex(), " ")
            // append text before smile
            ssb.append(message.substring(i, nearestSmile.first))

            // append smile
            val isSmileBig = nearestSmile.second.contains("Big")
            val smileDrawable = getSmileDrawable(context, nearestSmile.second, isSmileBig)
            if (smileDrawable == null) {
                // smile not loaded yet, load it
                var smileNameToLoad = nearestSmile.second
                if (isSmileBig) {
                    // remove 'Big' from the end
                    smileNameToLoad = smileNameToLoad.substring(0, smileNameToLoad.length - 3)
                }
                // TODO: not load same smile a lot of times
                smilesLoader.loadSmile(smileNameToLoad, onSmileReadyListener = {
                    notifyItemChanged(position)
                })
                // before smile loaded show just text
                ssb.append(nearestSmile.second)
            } else {
                val span = ImageSpan(smileDrawable, ImageSpan.ALIGN_BOTTOM)
                if (isSmileBig) {
                    ssb.append("\n")
                }
                ssb.append("123", span, Spannable.SPAN_INTERMEDIATE)
                if (isSmileBig) {
                    ssb.append("\n")
                }
            }
            i = nearestSmile.first
        }

        return ssb
    }

    private fun getSmileDrawable(context: Context, smile: String, isBig: Boolean): Drawable? {
        if (smilePaths == null) {
            return null
        }
        val path: String? = smilePaths!![smile]
        val width: Int
        val height: Int
        if (isBig) {
            width = Utils.spToPx(context, 100f)
            height = Utils.spToPx(context, 100f)
        } else {
            width = Utils.spToPx(context, 24f)
            height = Utils.spToPx(context, 24f)
        }
        val d = Drawable.createFromPath(path) ?: // no such smile
                return null
        d.setBounds(0, 0, width, height)
        return d
    }

    private fun findFirstSmile(str: String, fromIndex: Int): Pair<Int, String>? {

        smilePaths?.let { smilePaths ->
            var firstSmileIndex = Integer.MAX_VALUE
            var firstSmile: String? = null
            for (smile in smilePaths.keys) {
                val indexOf = str.indexOf(smile, fromIndex)
                if (indexOf != -1) {
                    if (firstSmileIndex > indexOf) {
                        firstSmileIndex = indexOf
                        firstSmile = smile
                    }
                }
            }
            if (firstSmileIndex == Integer.MAX_VALUE) {
                return null
            }
            return Pair<Int, String>(firstSmileIndex, firstSmile!!)
        }
        return null
    }

    class DataBindingViewHolder<T : ViewDataBinding>(var binding: T) : RecyclerView.ViewHolder(binding.root)

}
