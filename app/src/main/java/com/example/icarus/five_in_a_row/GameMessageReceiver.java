package com.example.icarus.five_in_a_row;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;

import cn.bmob.push.PushConstants;

/**
 * Created by Administrator on 2017/6/28/028.
 */

public class GameMessageReceiver extends BroadcastReceiver{

    public static GamePresenterImpl gamePresenter;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            String message = intent.getStringExtra("msg");
            if (GameInfo.connectGame){
                Log.i("GameMessageReceiver",message);
                int x = Integer.parseInt(message.substring(12,message.indexOf("y:")));
                int y = Integer.parseInt(message.substring(message.indexOf("y:")+2,message.length()-2));

                Log.i("GameMessageReceiver","get Point");
                if (gamePresenter != null){
                    gamePresenter.setData(new Point(x,y));
                }

            }else{
                String oid = message.substring(10,message.length()-2);

                GameInfo.otherId = oid;
                GameInfo.connectGame = true;
                Log.i("GameMessageReceiver",GameInfo.otherId);
            }
        }
    }
}
