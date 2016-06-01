package com.example.jayden.hw4_b;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.Toast;

/**
 * Created by Jayden on 2016-06-01.
 */
public class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback {
    Canvas cacheCanvas;
    Bitmap backBuffer;
    int width, height, clientHeight, horizontal, vertical;
    Paint paint;
    Bitmap pic, pic2;
    Context context;
    SurfaceHolder mHolder;
    int[][] map1 = new int[][]{{0, 1, 1, 1, 1}, {0, 0, 0, 1, 1}, {1, 1, 0, 1, 1}, {1, 1, 0, 1, 1}, {1, 1, 0, 0, 2}};
    // 사전에 정의된 미로
    int[][] map2 = {
            {0,1,1,1,1,1,1,1,1,1},
            {0,0,1,0,1,0,1,0,0,1},
            {1,0,1,0,0,0,1,0,1,1},
            {1,0,0,0,1,1,1,0,0,1},
            {1,0,1,0,0,0,0,0,0,1},
            {1,0,1,0,1,1,1,0,1,1},
            {1,0,1,0,1,0,0,0,1,1},
            {1,0,1,0,1,1,1,0,1,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,0,2}
    };

    int lastX, lastY, currX, currY, Xlength, Ylength, nowX, nowY;
    boolean isDeleting, isFinish;

    public DrawingSurface(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public DrawingSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public DrawingSurface(Context context, int hori, int ver) {
        super(context);
        this.context = context;
        horizontal = hori;
        vertical = ver;
        init();
    }

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        horizontal = 10;
        vertical = 10;
        // 이미지 bitmap형식으로 변환.
        pic = BitmapFactory.decodeResource(getResources(), R.drawable.firewall);
        pic2 = BitmapFactory.decodeResource(getResources(), R.drawable.exit);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        width = getWidth();
        height = getHeight();
        //한 블럭의 가로,세로 길이를 결정
        Xlength = width / horizontal;
        Ylength = width / vertical;
        nowX = Xlength / 2;
        nowY = Ylength / 2;
        cacheCanvas = new Canvas();
        backBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); //back buffer
        cacheCanvas.setBitmap(backBuffer);
        cacheCanvas.drawColor(Color.WHITE);
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        isFinish = false;
        draw();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                int tempX =  (int) event.getX();
                int tempY =  (int) event.getY();
                double distance;
                // 현재 포인터와 나와의 거리를 구하여서 나로부터 선 연결이 시작되는지를 구분한다.
                distance =  Math.sqrt( (tempX - nowX) * (tempX - nowX) +
                        (tempY - nowY) * (tempY - nowY) );
                if(distance >= 40) { // 원 안에 포인터가 있는지 체크
                    cacheCanvas.drawColor(Color.WHITE);
                    isDeleting = true;
                    break;
                }
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDeleting) break;
                currX = (int) event.getX();
                currY = (int) event.getY();
                if(currY > width)
                    break;
                int tempHorizontal = (int)Math.floor(currX/Xlength);
                int tempVertical = (int)Math.floor(currY/Ylength);
                // 벽에 부딪힌 경우 (이때도 위치 초기화)
                if(map2[tempVertical][tempHorizontal] == 1) {
                    isFinish = true;
                    nowX = Xlength/2;
                    nowY = Ylength/2;
                    cacheCanvas.drawColor(Color.WHITE);
                    isDeleting = true;
                    break;
                }
                // 도착한 경우
                else if(map2[tempVertical][tempHorizontal] == 2) {
                    Toast.makeText(context, "도착!", Toast.LENGTH_SHORT).show();
                    isFinish = true;
                    nowX = Xlength/2;
                    nowY = Ylength/2;
                    cacheCanvas.drawColor(Color.WHITE);
                    break;
                }
                cacheCanvas.drawLine(lastX, lastY, currX, currY, paint);
                lastX = currX;
                lastY = currY;
                break;
            case MotionEvent.ACTION_UP:
                if (isDeleting) isDeleting = false;
//                Paint red = new Paint();
//                red.setColor(Color.RED);
                cacheCanvas.drawColor(Color.WHITE);
                //도착한 경우 위치 초기화 해주기
                if(isFinish != true) {
                    nowX = (lastX / Xlength) * Xlength + Xlength / 2;
                    nowY = (lastY / Ylength) * Ylength + Ylength / 2;
                }
                isFinish = false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                cacheCanvas.drawColor(Color.WHITE);
                isDeleting = true;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
        }
        draw(); // SurfaceView에 그리는 function을 직접 제작 및 호출
        return true;
    }

    protected void draw() {
        if (clientHeight == 0) {
            clientHeight = getClientHeight();
            height = clientHeight;
            backBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            cacheCanvas.setBitmap(backBuffer);
            cacheCanvas.drawColor(Color.WHITE);
        }
        // 벽 색갈 설정
        Paint wall = new Paint();
        wall.setColor(Color.BLACK);
        // 한 칸의 길이 설정
            for (int i = 0; i < horizontal; i++) {
            for (int j = 0; j < vertical; j++) {
                if (map2[i][j] == 1) {
                    //벽 채우기
                    cacheCanvas.drawBitmap(pic, new Rect(0, 0, pic.getWidth(), pic.getHeight()), new Rect(Xlength * j, Ylength * i, Xlength * (j + 1), Ylength * (i + 1)), wall);
                }
                else if(map2[i][j] == 2){
                    //출구 그리기
                    cacheCanvas.drawBitmap(pic2, new Rect(0, 0, pic2.getWidth(), pic2.getHeight()), new Rect(Xlength * j, Ylength * i, Xlength * (j + 1), Ylength * (i + 1)), wall);
                }
            }
        }
        // 현재 위치 그리기
        Paint red = new Paint();
        red.setColor(Color.RED);
        cacheCanvas.drawCircle(nowX, nowY, 40, red);

        Canvas canvas = null;
        try {
            canvas = mHolder.lockCanvas(null);
            //back buffer에 그려진 비트맵을 스크린 버퍼에 그린다
            canvas.drawBitmap(backBuffer, 0, 0, paint);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (mHolder != null) mHolder.unlockCanvasAndPost(canvas);
        }
    }

    /* 상태바, 타이틀바를 제외한 클라이언트 영역의 높이를 구한다 */
    private int getClientHeight() {
        Rect rect = new Rect();
        Window window = ((Activity) context).getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;
        return ((Activity) context).getWindowManager().getDefaultDisplay().
                getHeight() - statusBarHeight - titleBarHeight;
    }
} // class DrawingSurface
