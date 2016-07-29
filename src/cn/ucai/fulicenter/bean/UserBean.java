package cn.ucai.fulicenter.bean;

import java.io.Serializable;

/**
 * Created by wdyzyr on 2016/7/29.
 */
public class UserBean  implements Serializable{

    /**
     * id : 31
     * userName : ucai
     * nick : ucai
     * password : a
     * avatar : ucai.jpg
     * unreadMsgCount : 0
     * header : null
     * result : ok
     */

    private int id;
    private String userName;
    private String nick;
    private String password;
    private String avatar;
    private int unreadMsgCount;
    private Object header;
    private String result;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(int unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }

    public Object getHeader() {
        return header;
    }

    public void setHeader(Object header) {
        this.header = header;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "avatar='" + avatar + '\'' +
                ", id=" + id +
                ", userName='" + userName + '\'' +
                ", nick='" + nick + '\'' +
                ", password='" + password + '\'' +
                ", unreadMsgCount=" + unreadMsgCount +
                ", header=" + header +
                ", result='" + result + '\'' +
                '}';
    }
}
