package nnthien.com.myapp.Models;

/**
 * Created by T440S on 7/18/2017.
 */

public class ChatModel {
    private String message;

    public ChatModel(String message, boolean isSend) {
        this.message = message;
        this.isSend = isSend;
    }
    public ChatModel() {
    }

    private boolean isSend;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }
}
