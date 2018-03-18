package no.ntnu.tomme87.imt3673.lab4;

import android.content.Context;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomme on 17.03.2018.
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageListViewHolder> {
    private static final String TAG = "MessageListAdapter";
    private final LayoutInflater inflater;
    private List<Message> messages = new ArrayList<>();


    MessageListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public void setData(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void addData(Message message) {
        this.messages.add(message);
        notifyItemInserted(this.messages.size());
    }

    public void removeData(Message message) {
        messages.remove(message);
        notifyItemRemoved(this.messages.size());
        notifyDataSetChanged();
    }

    @Override
    public MessageListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.inflater.inflate(R.layout.message, parent, false);
        return new MessageListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageListViewHolder holder, int position) {
        Message message = this.messages.get(position);
        holder.time.setText(message.getTimeString());
        holder.nick.setText(message.getNick());
        holder.content.setText(message.getContent());
    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }

    static class MessageListViewHolder extends RecyclerView.ViewHolder {

        TextView time;
        TextView nick;
        TextView content;

        public MessageListViewHolder(View itemView) {
            super(itemView);
            this.time = itemView.findViewById(R.id.tv_time);
            this.nick = itemView.findViewById(R.id.tv_nick);
            this.content = itemView.findViewById(R.id.tv_content);
        }
    }
}
