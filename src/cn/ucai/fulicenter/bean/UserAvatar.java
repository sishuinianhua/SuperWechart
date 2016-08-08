package cn.ucai.fulicenter.bean;

import java.io.Serializable;

public class UserAvatar  implements Serializable {


private boolean result;

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

    public UserAvatar(){

    }


    public UserAvatar(int mavatarId, String mavatarPath, int mavatarType, int mavatarUserId, String mavatarUserName, int msg, int muserId, String muserName, String muserNick, String muserPassword, int muserUnreadMsgCount, boolean result) {
        this.mavatarId = mavatarId;
        this.mavatarPath = mavatarPath;
        this.mavatarType = mavatarType;
        this.mavatarUserId = mavatarUserId;
        this.mavatarUserName = mavatarUserName;
        this.msg = msg;
        this.muserId = muserId;
        this.muserName = muserName;
        this.muserNick = muserNick;
        this.muserPassword = muserPassword;
        this.muserUnreadMsgCount = muserUnreadMsgCount;
        this.result = result;
    }

    public void setResult(boolean result){
this.result = result;
}
public boolean getResult(){
return this.result;
}
public void setMsg(int msg){
this.msg = msg;
}
public int getMsg(){
return this.msg;
}
public void setMavatarId(int mavatarId){
this.mavatarId = mavatarId;
}
public int getMavatarId(){
return this.mavatarId;
}
public void setMavatarUserId(int mavatarUserId){
this.mavatarUserId = mavatarUserId;
}
public int getMavatarUserId(){
return this.mavatarUserId;
}
public void setMavatarUserName(String mavatarUserName){
this.mavatarUserName = mavatarUserName;
}
public String getMavatarUserName(){
return this.mavatarUserName;
}
public void setMavatarPath(String mavatarPath){
this.mavatarPath = mavatarPath;
}
public String getMavatarPath(){
return this.mavatarPath;
}
public void setMavatarType(int mavatarType){
this.mavatarType = mavatarType;
}
public int getMavatarType(){
return this.mavatarType;
}
public void setMuserId(int muserId){
this.muserId = muserId;
}
public int getMuserId(){
return this.muserId;
}
public void setMuserName(String muserName){
this.muserName = muserName;
}
public String getMuserName(){
return this.muserName;
}
public void setMuserPassword(String muserPassword){
this.muserPassword = muserPassword;
}
public String getMuserPassword(){
return this.muserPassword;
}
public void setMuserNick(String muserNick){
this.muserNick = muserNick;
}
public String getMuserNick(){
return this.muserNick;
}
public void setMuserUnreadMsgCount(int muserUnreadMsgCount){
this.muserUnreadMsgCount = muserUnreadMsgCount;
}
public int getMuserUnreadMsgCount(){
return this.muserUnreadMsgCount;
}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAvatar)) return false;

        UserAvatar that = (UserAvatar) o;

        return getMuserId() == that.getMuserId();

    }

    @Override
    public int hashCode() {
        return getMuserId();
    }

    @Override
    public String toString() {
        return "UserAvatar{" +
                "mavatarId=" + mavatarId +
                ", result=" + result +
                ", msg=" + msg +
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