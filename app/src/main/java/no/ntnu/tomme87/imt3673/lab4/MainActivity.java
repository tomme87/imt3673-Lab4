package no.ntnu.tomme87.imt3673.lab4;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements GetNicknameDialogFragment.NoticeDialogListener {
    private final static String TAG = "MainActivity";

    final static String PREF_NICK = "no.ntnu.tomme87.imt3673.lab4.nickname";
    private static final int JOB_ID = 1002;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        this.startAuth();
    }

    /**
     * Authenticate with Firebase. Sing in anonymously if not signed in.
     */
    private void startAuth() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            setupUi(user);
            return;
        }

        // Sign in
        auth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        setupUi(auth.getCurrentUser());
                    }
                });
    }

    /**
     * Setup the UI if we got a user
     *
     * @param user
     */
    private void setupUi(FirebaseUser user) {
        if (user != null) {
            this.setupAuthenticatedUi(user);
            this.showNicknameDialog();
        } else {
            Toast.makeText(this, R.string.auth_user_not_authenticated, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Setup UI with message and user tab.
     * <p>
     * Inspiration: http://www.truiton.com/2015/06/android-tabs-example-fragments-viewpager/
     *
     * @param user
     */
    private void setupAuthenticatedUi(FirebaseUser user) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_messages_name));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_user_list_name));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final ViewPager viewPager = findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * Shows the dialog to input nickname.
     */
    private void showNicknameDialog() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String nick = sharedPreferences.getString(PREF_NICK, null);
        if (nick == null) {
            Toast.makeText(this, R.string.dialog_nickname_toast_need_nick, Toast.LENGTH_LONG).show();

            DialogFragment nicknameDialog = new GetNicknameDialogFragment();
            nicknameDialog.show(getSupportFragmentManager(), GetNicknameDialogFragment.TAG);
        }


    }

    /**
     * Setup scheduler for periodic checking of new messages.
     */
    private void setupScheduler() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String frequencyString = sharedPreferences.getString(SettingsFragment.FREQUENCY, getString(R.string.list_result_frequency_default));
        long frequency = Long.parseLong(frequencyString) * 60000L; // 60k in 1 minute

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(this, DataUpdateService.class));
        builder.setPersisted(true)
                .setPeriodic(frequency)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        jobScheduler.schedule(builder.build());
    }

    /**
     * Cancel the scheduled checking for new messages when we are using the application.
     */
    @Override
    protected void onStart() {
        super.onStart();
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(JOB_ID);
    }

    /**
     * Start the scheduling when we are not using the application.
     */
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putLong(DataUpdateService.PREF_LAST, System.currentTimeMillis() / 1000L).apply();
        this.setupScheduler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    /**
     * Show Settings when settings pressed
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            final Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * When a new nickname is set in dialog.
     * Change preferences and add it to firebase database
     *
     * @param nick
     */
    @Override
    public void onNewNickname(String nick) {
        Log.d(TAG, "POSITIVE! " + nick);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(PREF_NICK, nick).apply();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(User.DOCUMENT).add(new User(nick));
    }

    /**
     * If the dialog is cancelled.
     */
    @Override
    public void onCancelNickname() {
        Log.d(TAG, "NEGATIVE!");

        this.showNicknameDialog();
    }
}
