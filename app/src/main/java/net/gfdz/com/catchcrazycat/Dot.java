package net.gfdz.com.catchcrazycat;

/**
 * Created by Administrator on 2015/11/26.
 */
public class Dot {
 private   int X,Y;//坐标
 private    int Stauts;//状态

    public Dot(int x, int y) {
        this.X = x;
        this.Y = y;
       this. Stauts=Status_OFF;
    }

    public static final int Status_ON=1;//可用
    public static final int Status_OFF=0;//不可用
    public static final int Status_In=9;//猫的位置

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getStauts() {
        return Stauts;
    }

    public void setStauts(int stauts) {
        Stauts = stauts;
    }
    public void setXY(int x,int y){
        this.X=x;
        this.Y=y;
    }
}
