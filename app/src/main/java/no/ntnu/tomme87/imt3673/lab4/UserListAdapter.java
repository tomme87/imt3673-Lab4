package no.ntnu.tomme87.imt3673.lab4;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomme on 28.03.2018.
 * <p>
 * Adapter that holds all users for view
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {
    private final LayoutInflater inflater;
    private List<User> users = new ArrayList<>();

    public UserListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public void addUser(User user) {
        if (!this.users.contains(user)) {
            this.users.add(user);
            notifyItemInserted(this.users.size());
        }

    }

    public User getUser(int position) {
        return this.users.get(position);
    }

    public void clear() {
        this.users.clear();
        notifyDataSetChanged();
    }

    @Override
    public UserListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.inflater.inflate(R.layout.user, parent, false);
        return new UserListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserListViewHolder holder, int position) {
        User user = this.users.get(position);
        holder.nick.setText(user.getNick());
    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }

    static class UserListViewHolder extends RecyclerView.ViewHolder {

        TextView nick;

        public UserListViewHolder(View itemView) {
            super(itemView);
            this.nick = itemView.findViewById(R.id.tv_user_nick);
        }
    }
}
