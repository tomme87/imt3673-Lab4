package no.ntnu.tomme87.imt3673.lab4;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * To show messages from a specific user.
 */
public class MessagesFromUserActivity extends AppCompatActivity {
    public final static String EXTRA_USER = "no.ntnu.tomme87.imt3673.lab4.USER_CLICKED";
    private static final String TAG = "MFromUserActivity";

    private RecyclerView recyclerView;
    private TextView textView;

    private MessageListAdapter messageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_from_user);

        this.recyclerView = findViewById(R.id.rv_messages_from_user);
        this.textView = findViewById(R.id.tv_message_header);

        this.messageListAdapter = new MessageListAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());

        this.recyclerView.setAdapter(this.messageListAdapter);
        this.recyclerView.addItemDecoration(dividerItemDecoration);
        this.recyclerView.setLayoutManager(layoutManager);

        String nick = getIntent().getStringExtra(EXTRA_USER);

        this.textView.setText(getString(R.string.tv_message_header, nick));

        this.showMessagesFromUser(nick);
    }

    /**
     * Show messages from a specific user in the view
     * Gets messages from a nick from Firebase.
     *
     * @param user
     */
    private void showMessagesFromUser(String user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Message.DOCUMENT)
                .whereEqualTo("nick", user)
                .orderBy("time")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Task success");
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        Message message = document.toObject(Message.class);
                        messageListAdapter.addData(message);
                    }
                } else {
                    Log.d(TAG, "Tak not successfull" + task.getException());
                    Toast.makeText(getApplicationContext(), R.string.data_unavailable, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
