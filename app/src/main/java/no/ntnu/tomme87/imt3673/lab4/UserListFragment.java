package no.ntnu.tomme87.imt3673.lab4;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
public class UserListFragment extends Fragment {
    private static final String TAG = "UserListFragment";

    private RecyclerView recyclerView;
    private UserListAdapter userListAdapter;

    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;


    public UserListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.recyclerView = getView().findViewById(R.id.rv_users);

        this.userListAdapter = new UserListAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());

        this.recyclerView.setAdapter(this.userListAdapter);
        this.recyclerView.addItemDecoration(dividerItemDecoration);
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext()));
    }

    private void setupFirestore() {
        db = FirebaseFirestore.getInstance();
        listenerRegistration = db.collection(User.DOCUMENT).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            Log.d(TAG, "New user: " + dc.getDocument().getData());
                            User user = dc.getDocument().toObject(User.class);
                            Log.d(TAG, user.getNick());
                            userListAdapter.addUser(user);
                            break;
                        case MODIFIED:
                            Log.d(TAG, "Modified user: " + dc.getDocument().getData());
                            break;
                        case REMOVED:
                            Log.d(TAG, "Removed user: " + dc.getDocument().getData());
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
        this.userListAdapter.clear();
    }

    private class RecyclerTouchListener extends RecyclerView.SimpleOnItemTouchListener {
        private final String TAG = "RecyclerTouchListener";
        private GestureDetector gestureDetector;

        RecyclerTouchListener(Context context) {
            this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if (child != null && gestureDetector.onTouchEvent(e)) {
                Log.d(TAG, "click!");
                User user = userListAdapter.getUser(rv.getChildAdapterPosition(child));
                final Intent i = new Intent(getContext(), MessagesFromUserActivity.class);
                i.putExtra(MessagesFromUserActivity.EXTRA_USER, user.getNick());
                startActivity(i);
            }

            return false;
        }
    }
}
