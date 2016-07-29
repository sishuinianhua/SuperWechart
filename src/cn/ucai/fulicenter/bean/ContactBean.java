package cn.ucai.fulicenter.bean;

import java.io.Serializable;

/**
 * Created by wdyzyr on 2016/7/29.
 */
public class ContactBean implements Serializable{

    /**
     * result : ok
     * myuid : 4
     * cuid : 5
     */

    private String result;
    private int myuid;
    private int cuid;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getMyuid() {
        return myuid;
    }

    public void setMyuid(int myuid) {
        this.myuid = myuid;
    }

    public int getCuid() {
        return cuid;
    }

    public void setCuid(int cuid) {
        this.cuid = cuid;
    }

    @Override
    public String toString() {
        return "ContactBean{" +
                "cuid=" + cuid +
                ", result='" + result + '\'' +
                ", myuid=" + myuid +
                '}';
    }
}
