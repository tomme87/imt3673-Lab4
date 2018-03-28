package no.ntnu.tomme87.imt3673.lab4;

/**
 * Created by Tomme on 28.03.2018.
 */

public class User {
    public final static String DOCUMENT = "users";

    private String nick;

    public User() {
    }

    public User(String nick) {
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User && this.nick.equals(((User) obj).getNick());
    }
}
