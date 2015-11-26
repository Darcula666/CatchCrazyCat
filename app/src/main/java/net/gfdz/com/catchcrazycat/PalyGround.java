package net.gfdz.com.catchcrazycat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Administrator on 2015/11/26.
 */
public class PalyGround extends SurfaceView implements View.OnTouchListener {
    private static int WIDTH = 40;
    private static final int COL = 15;
    private static final int ROW = 15;
    private static final int BLOCKS = 15;//默认添加的路障数量

    private Dot matrix[][];
    private Dot cat;

    public PalyGround(Context context) {
        super(context);
        getHolder().addCallback(callback);
        matrix = new Dot[ROW][COL];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                matrix[i][j] = new Dot(j, i);
            }
        }
        setOnTouchListener(this);
        initGame();
    }

    private void redraw() {
        Canvas c = getHolder().lockCanvas();
        c.drawColor(Color.LTGRAY);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);//抗锯齿
        for (int i = 0; i < ROW; i++) {
            int offset = 0;
            if (i % 2 != 0) {
                offset = WIDTH / 2;
            }
            for (int j = 0; j < COL; j++) {
                Dot one = getDot(j, i);
                switch (one.getStauts()) {
                    case Dot.Status_OFF:
                        paint.setColor(0xffeeeeee);
                        break;
                    case Dot.Status_In:
                        paint.setColor(0xffff0000);
                        break;
                    case Dot.Status_ON:
                        paint.setColor(0xffffaa00);
                        break;
                }
                c.drawOval(new RectF(one.getX() * WIDTH + offset, one.getY() * WIDTH,
                        (one.getX() + 1) * WIDTH + offset, (one.getY() + 1) * WIDTH), paint);
            }
        }

        getHolder().unlockCanvasAndPost(c);
    }

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            redraw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            WIDTH = width / (COL + 1);
            redraw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    private void initGame() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                matrix[i][j].setStauts(Dot.Status_OFF);
            }
        }
        cat = new Dot((int)(COL/2)-1, (COL/2));
        getDot((COL/2)-1,(COL/2)).setStauts(Dot.Status_In);
        for (int i = 0; i < BLOCKS; ) {
            int x = (int) ((Math.random() * 1000) % COL);
            int y = (int) ((Math.random() * 1000) % ROW);
            if (getDot(x, y).getStauts() == Dot.Status_OFF) {
                getDot(x, y).setStauts(Dot.Status_ON);
                i++;
                System.out.println("BLOCK:" + i);
            }
        }

    }

    private Dot getDot(int x, int y) {
        return matrix[y][x];
    }

    //判断点是否处于地图边界
    private boolean isAtEdge(Dot d) {
        if (d.getX() * d.getY() == 0 || d.getX() + 1 == COL || d.getY() + 1 == ROW) {
            return true;
        }
        return false;
    }

    private Dot getNrighbour(Dot d, int dir) {//获取cat临近的6个点 左边第一个点为1，然后顺时针找
        switch (dir) {
            case 1:
                return getDot(d.getX() - 1, d.getY());

            case 2:
                if (d.getY() % 2 == 0) {
                    return getDot(d.getX() - 1, d.getY() - 1);
                } else {
                    return getDot(d.getX(), d.getY() - 1);
                }

            case 3:
                if (d.getY() % 2 == 0) {
                    return getDot(d.getX(), d.getY() - 1);
                } else {
                    return getDot(d.getX() + 1, d.getY() - 1);
                }

            case 4:
                return getDot(d.getX() + 1, d.getY());

            case 5:
                if (d.getY() % 2 == 0) {
                    return getDot(d.getX(), d.getY() + 1);
                } else {
                    return getDot(d.getX() + 1, d.getY() + 1);
                }

            case 6:
                if (d.getY() % 2 == 0) {
                    return getDot(d.getX() - 1, d.getY() + 1);
                } else {
                    return getDot(d.getX(), d.getY() + 1);
                }

        }
        return null;
    }

    private int getDistance(Dot one, int dir) {
        int distance = 0;
        if (isAtEdge(one)){
            return 1;
        }
        Dot ori = one, next;
        while (true) {
            next = getNrighbour(ori, dir);
            if (next.getStauts() == Dot.Status_ON) {
                return distance * -1;
            }
            if (isAtEdge(next)) {
                distance++;
                return distance;
            }
            distance++;
            ori = next;
        }
    }

    private void moveTo(Dot one) {
        one.setStauts(Dot.Status_In);
        getDot(cat.getX(), cat.getY()).setStauts(Dot.Status_OFF);
        cat.setXY(one.getX(), one.getY());
    }

    private void move() {
        if (isAtEdge(cat)) {
            lose();
            return;
        }
        Vector<Dot> avaliable = new Vector<>();//容器
        Vector<Dot> posititv = new Vector<>();//容器：记录可以直接到达屏幕边缘的路径
        HashMap<Dot,Integer> al=new HashMap<Dot,Integer>();
        for (int i = 1; i < 7; i++) {
            Dot n = getNrighbour(cat, i);
            if (n.getStauts() == Dot.Status_OFF) {
                avaliable.add(n);
                al.put(n,i);
                if(getDistance(n,i)>0){
                    posititv.add(n);

                }
            }
        }
        if (avaliable.size() == 0) {
            win();
        } else if (avaliable.size() == 1) {
            moveTo(avaliable.get(0));
        }else {
            Dot best = null;
              if(posititv.size()!=0){//存在可以直接到达屏幕边缘的走向
                int min=999;
                  for (int i = 0; i <posititv.size() ; i++) {
                    int a=  getDistance(posititv.get(i),al.get(posititv.get(i)));
                      if (a<min){
                          min=a;
                          best=posititv.get(i);
                      }
                  }

              }else {//所有方向都存在路障
                int max=0;
                  for (int i = 0; i <avaliable.size() ; i++) {
                      int  k=getDistance(avaliable.get(i),al.get(avaliable.get(i)));
                      if (k<max){
                          max=k;
                          best=avaliable.get(i);
                      }
                  }
              }
            moveTo(best);
        }
    }

    private void lose() {//游戏结束
        Toast.makeText(getContext(), "Lose", Toast.LENGTH_SHORT).show();
    }

    private void win() {
        Toast.makeText(getContext(), "You Win", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //Toast.makeText(getContext(),event.getX()+":"+event.getY(),Toast.LENGTH_SHORT).show();
            int x, y;
            y = (int) (event.getY() / WIDTH);
            if (y % 2 == 0) {
                x = (int) (event.getX() / WIDTH);
            } else {
                x = (int) ((event.getX() - WIDTH / 2) / WIDTH);
            }
            if (x + 1 > COL || y + 1 > ROW) {//点击了地图外边
                initGame();
            } else if (getDot(x, y).getStauts() == Dot.Status_OFF) {
                getDot(x, y).setStauts(Dot.Status_ON);
                move();
            }
            redraw();
        }
        return true;
    }
}
