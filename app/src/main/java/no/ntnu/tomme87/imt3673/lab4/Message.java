package no.ntnu.tomme87.imt3673.lab4;

/**
 * Created by Tomme on 17.03.2018.
 */

public class Message {
    private int time;
    private String nick;
    private String content;

    public Message(int time, String nick, String content) {
        this.time = time;
        this.nick = nick;
        this.content = content;
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
}
