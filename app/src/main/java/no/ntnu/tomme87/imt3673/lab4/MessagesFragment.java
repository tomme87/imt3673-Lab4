package no.ntnu.tomme87.imt3673.lab4;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {
    private RecyclerView recyclerView;
    private MessageListAdapter messageListAdapter;

    public MessagesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.recyclerView = getView().findViewById(R.id.rv_messages);

        this.messageListAdapter = new MessageListAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        this.recyclerView.setAdapter(this.messageListAdapter);
        this.recyclerView.setLayoutManager(layoutManager);

        this.messageListAdapter.setData(Arrays.asList(new Message(10, "per", "My content xD")));
    }
}
