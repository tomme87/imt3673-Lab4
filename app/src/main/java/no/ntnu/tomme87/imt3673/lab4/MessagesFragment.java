package no.ntnu.tomme87.imt3673.lab4;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {
    private static final String TAG = "MessagesFragment";


    private RecyclerView recyclerView;
    private EditText sendMessageEditText;

    private MessageListAdapter messageListAdapter;

    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;

    public MessagesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.recyclerView = getView().findViewById(R.id.rv_messages);

        this.messageListAdapter = new MessageListAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        layoutManager.setStackFromEnd(true);

        this.recyclerView.setAdapter(this.messageListAdapter);
        this.recyclerView.addItemDecoration(dividerItemDecoration);
        this.recyclerView.setLayoutManager(layoutManager);

        //this.messageListAdapter.addData(new Message(10, "per", "My content xD"));

        this.sendMessageEditText = getView().findViewById(R.id.et_sendmessage);
        this.sendMessageEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    sendMessage(sendMessageEditText.getText().toString());
                    sendMessageEditText.setText("");
                    return true;
                }
                return false;
            }
        });

        // from https://stackoverflow.com/a/19194441
        // Linter warning: I don't need preformClick, so I don't override it.
        this.sendMessageEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (sendMessageEditText.getRight() - sendMessageEditText
                            .getCompoundDrawables()[2].getBounds().width())) {
                        sendMessage(sendMessageEditText.getText().toString());
                        sendMessageEditText.setText("");
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * When sending a message.
     * <p>
     * Updates firebase db with the new message.
     *
     * @param content
     */
    private void sendMessage(String content) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String nick = sharedPreferences.getString(MainActivity.PREF_NICK, null);

        Message message = new Message(nick, content);
        db.collection(Message.DOCUMENT).add(message);
    }

    /**
     * Sets up firebase to listen for new messages
     */
    private void setupFirestore() {
        db = FirebaseFirestore.getInstance();
        listenerRegistration = db.collection(Message.DOCUMENT).orderBy("time").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    Toast.makeText(getContext(), R.string.data_unavailable, Toast.LENGTH_LONG).show();
                    return;
                }

                for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d(TAG, "New message: " + dc.getDocument().getData());
                            Message msg = dc.getDocument().toObject(Message.class);
                            Log.d(TAG, msg.getContent());
                            msg.setId(dc.getDocument().getId());
                            messageListAdapter.addData(msg);
                            recyclerView.scrollToPosition(messageListAdapter.getItemCount() - 1);
                            break;
                        case MODIFIED:
                            Log.d(TAG, "Modified message: " + dc.getDocument().getData());
                            break;
                        case REMOVED:
                            Log.d(TAG, "Removed message: " + dc.getDocument().getData());
                            Message msgRemove = dc.getDocument().toObject(Message.class);
                            msgRemove.setId(dc.getDocument().getId());
                            messageListAdapter.removeData(msgRemove);
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        this.setupFirestore();
    }

    @Override
    public void onStop() {
        super.onStop();
        listenerRegistration.remove();
        messageListAdapter.clear();
    }
}
