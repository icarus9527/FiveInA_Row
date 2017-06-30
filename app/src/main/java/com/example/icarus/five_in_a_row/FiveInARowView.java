package com.example.icarus.five_in_a_row;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by icarus9527 on 2017/6/11.
 */

public class FiveInARowView extends View {

    private int mPanelWith;
    private float mLineHeight;
    private int MAX_LINE = 10;
    private int MAX_COUNT_IN_LINE = 5;

    private Paint paint;

    private List<Point> mWhiteArray = new ArrayList<>();
    private List<Point> mBlackArray = new ArrayList<>();

    private boolean isWhiteTurn = true;
    private boolean isGameOver;
    private boolean isWhiteWin;

    private Bitmap whitePiece,blackPiece;
    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;


    private BmobUtils bmobUtils;

    private int count;

    private boolean[][][] wins;
    private int[] myWin;
    private int[] computerWin;

    public FiveInARowView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setBackgroundColor(0x44ff0000);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0x88000000);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
//        messageReceiver = new GameMessageReceiver(this);
        bmobUtils = new BmobUtils();

        if (GameInfo.isPalyWithAi){
            wins = countWins(MAX_LINE,MAX_LINE);

            myWin = new int[count];
            computerWin = new int[count];
        }

        whitePiece = BitmapFactory.decodeResource(getResources(),R.drawable.stone_w2);
        blackPiece = BitmapFactory.decodeResource(getResources(),R.drawable.stone_b1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize,heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED){
            width = heightSize;
        }else if (heightMode == MeasureSpec.UNSPECIFIED){
            width = widthSize;
        }

        setMeasuredDimension(width,width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWith = w;
        mLineHeight = mPanelWith * 1.0f / MAX_LINE;

        int pieceWith = (int) (ratioPieceOfLineHeight * mLineHeight);
        whitePiece = Bitmap.createScaledBitmap(whitePiece,pieceWith,pieceWith,false);
        blackPiece = Bitmap.createScaledBitmap(blackPiece,pieceWith,pieceWith,false);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isGameOver) return false;
        int action = event.getAction();
        if (GameInfo.isPalyWithAi){
            if (!isWhiteTurn) return false;
            if(action == MotionEvent.ACTION_UP){
                int x = (int) event.getX();
                int y = (int) event.getY();

                Point p = getValidPoint(x,y);

                if (mWhiteArray.contains(p)||mBlackArray.contains(p)){
                    return false;
                }

                mWhiteArray.add(p);

                invalidate();
                isWhiteTurn = !isWhiteTurn;

                mBlackArray.add(getBestPoint(p));
                invalidate();
                isWhiteTurn = !isWhiteTurn;
            }
        }else{
            if (GameInfo.isWhite && !isWhiteTurn)return false;
            if (!GameInfo.isWhite && isWhiteTurn)return false;

            if(action == MotionEvent.ACTION_UP){
                int x = (int) event.getX();
                int y = (int) event.getY();

                Point p = getValidPoint(x,y);

                if (mWhiteArray.contains(p)||mBlackArray.contains(p)){
                    return false;
                }

                if (GameInfo.isWhite){
                    mWhiteArray.add(p);
                }else{
                    mBlackArray.add(p);
                }

                bmobUtils.sendMessage(p);

                invalidate();
                isWhiteTurn = !isWhiteTurn;
            }
        }
        return  true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int)(x/mLineHeight),(int)(y/mLineHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBoard(canvas);

        drawPiece(canvas);

        checkGanmeOver();
    }

    private void checkGanmeOver() {
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);

        if (whiteWin || blackWin){
            isGameOver = true;
            isWhiteWin = whiteWin;

            String text = isWhiteWin ? "白棋胜利" : "黑棋胜利";
            new AlertDialog.Builder(getContext()).setMessage(text).setNegativeButton("再来一局", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mWhiteArray.clear();
                    mBlackArray.clear();
                    mWhiteArray = new ArrayList<>();
                    mBlackArray = new ArrayList<>();

                    myWin = new int[count];
                    computerWin = new int[count];
                    isGameOver = false;
                    invalidate();
                }
            }).show();
        }
    }

    private boolean checkFiveInLine(List<Point> points) {
        for (Point p:points){
            int x = p.x;
            int y = p.y;

            boolean win = checkHorizontal(x,y,points);
            if (win)return true;
            win = checkVertival(x,y,points);
            if (win)return true;
            win = checkLeftDiagonal(x,y,points);
            if (win)return true;
            win = checkRightDiagonal(x,y,points);
            if (win)return true;
        }
        return false;
    }

    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point(x - i,y))){
                count++;
            }else{
                break;
            }
        }

        if (count<5){
            for (int i = 1;i<MAX_COUNT_IN_LINE;i++){
                if (points.contains(new Point(x + i,y))){
                    count++;
                }else{
                    break;
                }
            }
        }

        if (count == 5){
            return true;
        }

        return false;
    }

    private boolean checkVertival(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point(x,y-i))){
                count++;
            }else{
                break;
            }
        }

        if (count<5){
            for (int i = 1;i<MAX_COUNT_IN_LINE;i++){
                if (points.contains(new Point(x,y+i))){
                    count++;
                }else{
                    break;
                }
            }
        }

        if (count == 5){
            return true;
        }

        return false;
    }

    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point(x - i,y - i))){
                count++;
            }else{
                break;
            }
        }

        if (count<5){
            for (int i = 1;i<MAX_COUNT_IN_LINE;i++){
                if (points.contains(new Point(x + i,y + i))){
                    count++;
                }else{
                    break;
                }
            }
        }

        if (count == 5){
            return true;
        }

        return false;
    }

    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point(x - i,y + i))){
                count++;
            }else{
                break;
            }
        }

        if (count<5){
            for (int i = 1;i<MAX_COUNT_IN_LINE;i++){
                if (points.contains(new Point(x + i,y - i))){
                    count++;
                }else{
                    break;
                }
            }
        }

        if (count == 5){
            return true;
        }

        return false;
    }

    private void drawPiece(Canvas canvas) {
        for (int i=0,n=mWhiteArray.size();i<n;i++){
            Point whitePoint = mWhiteArray.get(i);
            Log.i("DrawPiece-white",whitePoint.x+"   "+whitePoint.y);
            canvas.drawBitmap(whitePiece,(whitePoint.x +(1-ratioPieceOfLineHeight)/2)*mLineHeight,(whitePoint.y +(1-ratioPieceOfLineHeight)/2)*mLineHeight,null);
        }

        for (int i=0,n=mBlackArray.size();i<n;i++){
            Point blackPoint = mBlackArray.get(i);
            Log.i("DrawPiece-black",blackPoint.x+"   "+blackPoint.y);
            canvas.drawBitmap(blackPiece,(blackPoint.x +(1-ratioPieceOfLineHeight)/2)*mLineHeight,(blackPoint.y +(1-ratioPieceOfLineHeight)/2)*mLineHeight,null);
        }
    }

    private void drawBoard(Canvas canvas) {
        int w = mPanelWith;
        float lineHeight = mLineHeight;

        for (int i=0;i<MAX_LINE;i++){
            int startX = (int) (lineHeight/2);
            int endX = (int) (w-lineHeight/2);

            int y = (int) ((0.5 + i)*lineHeight);

            canvas.drawLine(startX, y, endX, y, paint);
            canvas.drawLine(y, startX, y, endX, paint);
        }
    }

    public void setData(Point p) {
        if (GameInfo.isWhite){
            mBlackArray.add(p);
        }else{
            mWhiteArray.add(p);
        }
        Log.i("FiveInARowView","invalidate: x:"+p.x+"  y:"+p.y);
        invalidate();
        isWhiteTurn = !isWhiteTurn;
    }

    private Point getBestPoint(Point p){
        for (int k=0;k<count;k++){
            if (wins[p.x][p.y][k]){
                myWin[k]++;
                computerWin[k] = 6;
            }
        }

        int myScore[][] = new int[MAX_LINE][MAX_LINE];
        int computerScore[][] = new int[MAX_LINE][MAX_LINE];
        int maxScore = 0;

        int u=0;
        int v=0;

        for (int i=0;i<MAX_LINE;i++){
            for (int j=0;j<MAX_LINE;j++){
                Point p1=new Point(i,j);
                if (!(mWhiteArray.contains(p1)||mBlackArray.contains(p1))){
                    for (int k=0;k<count;k++){
                        if (wins[i][j][k]){
                            switch (myWin[k]){
                                case 1:
                                    myScore[i][j]+=200;
                                    break;
                                case 2:
                                    myScore[i][j]+=400;
                                    break;
                                case 3:
                                    myScore[i][j]+=2000;
                                    break;
                                case 4:
                                    myScore[i][j]+=10000;
                                    break;
                            }

                            switch (computerWin[k]){
                                case 1:
                                    computerScore[i][j]+=220;
                                    break;
                                case 2:
                                    computerScore[i][j]+=420;
                                    break;
                                case 3:
                                    computerScore[i][j]+=3000;
                                    break;
                                case 4:
                                    computerScore[i][j]+=20000;
                                    break;
                            }
                        }
                    }
                }

                if (myScore[i][j]>maxScore){
                    maxScore = myScore[i][j];
                    u=i;
                    v=j;
                }else if(myScore[i][j] == maxScore){
                    if (computerScore[i][j]>computerScore[u][v]){
                        u=i;
                        v=j;
                    }
                }
                if (computerScore[i][j]>maxScore){
                    maxScore = computerScore[i][j];
                    u=i;
                    v=i;
                }else if (computerScore[i][j] == maxScore){
                    if (myScore[i][j]>myScore[u][v]){
                        u=i;
                        v=j;
                    }
                }


            }
        }


        Point bestPoint = new Point(u,v);

        for (int k=0;k<count;k++){
            if (wins[bestPoint.x][bestPoint.y][k]){
                myWin[k] = 6;
                computerWin[k]++;
            }
        }

        return bestPoint;
    }

    public boolean[][][] countWins(int linex,int liney){

        boolean[][][] wins = new boolean[linex][liney][10000];
        count = 1;


        //横向赢法，[i，j]表示第一个棋子的坐标,
        for (int i = 0; i < linex-4; i++){
            for (int j=0; j < liney;j++){
                for (int k = 0; k < 5; k++) {
                    wins[i + k][j][count] = true;
                }
                count++;
            }
        }

        //竖向赢法
        for (int i=0;i<linex;i++){
            for (int j=0;j<liney-4;j++){
                for (int k=0;k<5;k++){
                    wins[i][j+k][count] = true;
                }
                count++;
            }
        }

        //斜向赢法
        for (int i=0;i<linex-4;i++){
            for (int j=0;j<liney-4;j++){
                for (int k=0;k<5;k++){
                    wins[i+k][j+k][count] = true;
                }
                count++;
            }
        }

        //反斜向赢法
        for (int i=0;i<linex-4;i++){
            for (int j=liney-1;j>3;j--){
                for (int k=0;k<5;k++){
                    Log.i("FiveInARowView",i+":"+j+":"+k);
                    wins[i+k][j-k][count] = true;
                }
                count++;
            }
        }
        return wins;
    }
}
