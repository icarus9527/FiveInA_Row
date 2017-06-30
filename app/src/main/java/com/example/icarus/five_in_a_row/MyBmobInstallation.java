package com.example.icarus.five_in_a_row;

import cn.bmob.v3.BmobInstallation;

/**
 * Created by Administrator on 2017/6/28/028.
 */

public class MyBmobInstallation extends BmobInstallation {

    private String uid;
    private String roomNum;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
    }
}
