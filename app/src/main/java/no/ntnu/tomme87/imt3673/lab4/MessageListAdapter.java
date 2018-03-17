package no.ntnu.tomme87.imt3673.lab4;

import android.content.Context;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
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
    private final LayoutInflater inflater;
    private List<Message> messages = new ArrayList<>();


    MessageListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public void setData(List<Message> messages) {
        this.messages = messages;
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
        holder.message.setText(message.getContent());
    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }

    static class MessageListViewHolder extends RecyclerView.ViewHolder {

        TextView message;

        public MessageListViewHolder(View itemView) {
            super(itemView);
            this.message = itemView.findViewById(R.id.tv_message);
        }
    }
}
