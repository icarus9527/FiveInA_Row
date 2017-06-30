package com.example.icarus.five_in_a_row;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Administrator on 2017/6/28/028.
 */

public class GameActivity extends AppCompatActivity implements GamePresenterImpl{

    private FiveInARowView game;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        game = (FiveInARowView) findViewById(R.id.game_five);

        GameMessageReceiver.gamePresenter = this;
    }

    @Override
    public void setData(Point p) {
        game.setData(p);
    }
}
