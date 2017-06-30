package com.example.icarus.five_in_a_row;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2017/6/28/028.
 */

public class BmobUtils {

    private Context context;

    public void initPlayer(final Context context){
        this.context = context;
        BmobQuery<MyBmobInstallation> query = new BmobQuery<>();
        query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(context));
        Log.i("BmobUtils",BmobInstallation.getInstallationId(context));

        query.findObjects(new FindListener<MyBmobInstallation>() {
            @Override
            public void done(List<MyBmobInstallation> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        MyBmobInstallation mbi = list.get(0);
                        mbi.setUid(GameInfo.myId);
                        mbi.setRoomNum("0");
                        mbi.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                Log.i("BmobUtils","创建player成功"+GameInfo.myId);
                            }
                        });
                    }
                }else{
                    Log.i("BmobUtils","findObjects failure");
                }
            }
        });
    }

    public void createRoom( final String roomNum){

        GameInfo.isWhite = true;
        GameInfo.connectGame = false;

        BmobQuery<MyBmobInstallation> query = new BmobQuery<>();
        query.addWhereEqualTo("uid", GameInfo.myId);
        query.findObjects(new FindListener<MyBmobInstallation>() {
            @Override
            public void done(List<MyBmobInstallation> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        MyBmobInstallation mbi = list.get(0);
                        mbi.setRoomNum(roomNum);
                        mbi.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                Log.i("BmobUtils","创建Room成功");

                            }
                        });
                    }
                }else{
                    Log.i("BmobUtils","findObjects failure");
                }
            }
        });
    }

    public void enterRoom(final String roomNum){

        GameInfo.isWhite = false;
        GameInfo.connectGame = true;

        BmobPushManager<MyBmobInstallation> bmobPush = new BmobPushManager<>();
        BmobQuery<MyBmobInstallation> query = MyBmobInstallation.getQuery();
        query.addWhereEqualTo("roomNum", roomNum);
        query.findObjects(new FindListener<MyBmobInstallation>() {
            @Override
            public void done(List<MyBmobInstallation> list, BmobException e) {
                if (list!= null && list.size()>0){
                    GameInfo.otherId = list.get(0).getUid();
                    Log.i("searchRoom",GameInfo.otherId);
                }
            }
        });
        bmobPush.setQuery(query);
        bmobPush.pushMessage(GameInfo.myId, new PushListener() {
            @Override
            public void done(BmobException e) {
            }
        });

        Toast.makeText(context,GameInfo.otherId,Toast.LENGTH_SHORT).show();
    }

    public void searchRoom(final String roomNum){
        BmobQuery<MyBmobInstallation> query = BmobInstallation.getQuery();
        query.addWhereEqualTo("roomNum", roomNum);

        query.findObjects(new FindListener<MyBmobInstallation>() {
            @Override
            public void done(List<MyBmobInstallation> list, BmobException e) {
                if (list!= null && list.size()>0){
                    Log.i("searchRoom","enter Game");
                    enterRoom(roomNum);
                }else{
                    Log.i("searchRoom","create Game");
                    createRoom(roomNum);
                }
            }
        });
    }

    public void sendMessage(Point p){
        String msg = "x:"+p.x+"y:"+p.y;
        BmobPushManager<MyBmobInstallation> bmobPush = new BmobPushManager<>();
        BmobQuery<MyBmobInstallation> query = MyBmobInstallation.getQuery();
        query.addWhereEqualTo("uid", GameInfo.otherId);
        bmobPush.setQuery(query);
        bmobPush.pushMessage(msg, new PushListener() {
            @Override
            public void done(BmobException e) {
            }
        });

    }
}
