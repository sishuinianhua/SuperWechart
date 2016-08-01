package cn.ucai.fulicenter.bean;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class UserBean implements Serializable{

    /**
     * result : true
     * msg : 403
     * mavatarId : 2
     * mavatarUserId : 2
     * mavatarUserName : aa
     * mavatarPath : user_avatar
     * mavatarType : 0
     * muserId : 3
     * muserName : aa
     * muserPassword : a
     * muserNick : 测试用户
     * muserUnreadMsgCount : 0
     */
    @JsonProperty("isResult")
    private boolean isResult;
    private int msg;
    private int mavatarId;
    private int mavatarUserId;
    private String mavatarUserName;
    private String mavatarPath;
    private int mavatarType;
    private int muserId;
    private String muserName;
    private String muserPassword;
    private String muserNick;
    private int muserUnreadMsgCount;

    @JsonIgnore
    public boolean isResult() {
        return isResult;
    }

    public void setResult(boolean result) {
        this.isResult = result;
    }

    public int getMsg() {
        return msg;
    }

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public int getMavatarId() {
        return mavatarId;
    }

    public void setMavatarId(int mavatarId) {
        this.mavatarId = mavatarId;
    }

    public int getMavatarUserId() {
        return mavatarUserId;
    }

    public void setMavatarUserId(int mavatarUserId) {
        this.mavatarUserId = mavatarUserId;
    }

    public String getMavatarUserName() {
        return mavatarUserName;
    }

    public void setMavatarUserName(String mavatarUserName) {
        this.mavatarUserName = mavatarUserName;
    }

    public String getMavatarPath() {
        return mavatarPath;
    }

    public void setMavatarPath(String mavatarPath) {
        this.mavatarPath = mavatarPath;
    }

    public int getMavatarType() {
        return mavatarType;
    }

    public void setMavatarType(int mavatarType) {
        this.mavatarType = mavatarType;
    }

    public int getMuserId() {
        return muserId;
    }

    public void setMuserId(int muserId) {
        this.muserId = muserId;
    }

    public String getMuserName() {
        return muserName;
    }

    public void setMuserName(String muserName) {
        this.muserName = muserName;
    }

    public String getMuserPassword() {
        return muserPassword;
    }

    public void setMuserPassword(String muserPassword) {
        this.muserPassword = muserPassword;
    }

    public String getMuserNick() {
        return muserNick;
    }

    public void setMuserNick(String muserNick) {
        this.muserNick = muserNick;
    }

    public int getMuserUnreadMsgCount() {
        return muserUnreadMsgCount;
    }

    public void setMuserUnreadMsgCount(int muserUnreadMsgCount) {
        this.muserUnreadMsgCount = muserUnreadMsgCount;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "isResult=" + isResult +
                ", msg=" + msg +
                ", mavatarId=" + mavatarId +
                ", mavatarUserId=" + mavatarUserId +
                ", mavatarUserName='" + mavatarUserName + '\'' +
                ", mavatarPath='" + mavatarPath + '\'' +
                ", mavatarType=" + mavatarType +
                ", muserId=" + muserId +
                ", muserName='" + muserName + '\'' +
                ", muserPassword='" + muserPassword + '\'' +
                ", muserNick='" + muserNick + '\'' +
                ", muserUnreadMsgCount=" + muserUnreadMsgCount +
                '}';
    }
}
