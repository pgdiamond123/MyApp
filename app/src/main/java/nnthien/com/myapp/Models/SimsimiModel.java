package nnthien.com.myapp.Models;

/**
 * Created by T440S on 7/18/2017.
 */

public class SimsimiModel {
    private String response;
    private String id;
    private int result;
    private String msg;

    public SimsimiModel(String response, String id, int result, String msg) {
        this.response = response;
        this.id = id;
        this.result = result;
        this.msg = msg;
    }

    public String getResponse() {
        return response;
    }

    public void setReponse(String response) {
        this.response = response;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public SimsimiModel(){

    }

}
