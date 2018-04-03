package no.ntnu.tomme87.imt3673.lab4;

import android.util.Log;

import com.google.firebase.firestore.Exclude;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tomme on 17.03.2018.
 *
 * Class for the message object. Used for Firebase DB
 */

public class Message {
    public static final String DOCUMENT = "messages";

    private static final String TAG = "Message";
    private String id = null;
    private int time;
    private String nick;
    private String content;

    public Message() {
    }

    public Message(int time, String nick, String content) {
        this.time = time;
        this.nick = nick;
        this.content = content;
    }

    public Message(String nick, String content) {
        this.nick = nick;
        this.content = content;
        this.time = (int) (System.currentTimeMillis() / 1000);
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTime() {
        return time;
    }

    public String getNick() {
        return nick;
    }

    public String getContent() {
        return content;
    }

    @Exclude
    public String getTimeString() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date date = new Date((long) this.time*1000);
            return sdf.format(date);
        }  catch(Exception ex){
            return "N/A";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Message) {
            return ((Message) obj).getId().equals(this.getId());
        }
        return false;
    }
}
