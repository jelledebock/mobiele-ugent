package be.ugent.oomt.pong.GameLogic;

import android.animation.RectEvaluator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;

class Paddle {
    /** Thickness of the paddle */
    static final int PADDLE_THICKNESS = 20;
    /** Width of the paddle */
    private static final int PADDLE_WIDTH = 120;

    static final int STARTING_LIVES = 3;
    private static final int PLAYER_PADDLE_SPEED = 40;

    // for interpolation
    private RectEvaluator rectEvaluator;
    private Rect mRectToDraw;
    private final Rect mRect;
    private final Rect mRectPrevious;


    private int mHandicap = 0;
    private int mSpeed = PLAYER_PADDLE_SPEED;
    private int mLives = STARTING_LIVES;

    int destination;

    Paddle() {
        mRect = new Rect();
        mRectPrevious = new Rect();
        mRectToDraw = new Rect();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rectEvaluator = new RectEvaluator(mRectToDraw);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            rectEvaluator = new RectEvaluator();
        }
    }

    void updatePaddle(int x, int y) {
        mRect.set(0,y,PADDLE_WIDTH, y + PADDLE_THICKNESS);
        mRectPrevious.set(mRect);
        mRectToDraw.set(mRect);
        setPosition(x);
        destination = x;
    }

    void move(boolean handicapped) {
        move((handicapped) ? mSpeed - mHandicap : mSpeed);
    }

    private void move(int s) {
        mRectPrevious.set(mRect);

        int dx = Math.abs(mRect.centerX() - destination);

        if(destination < mRect.centerX()) {
            mRect.offset((dx > s) ? -s : -dx,0);
        }
        else if(destination > mRect.centerX()) {
            mRect.offset((dx > s) ? s : dx,0);
        }
    }

    void setLives(int lives) {
        mLives = Math.max(0, lives);
    }

    private void setPosition(int x) {
        mRect.offset(x - mRect.centerX(), 0);
    }

    void setHandicap(int h) {
        mHandicap = (h >= 0 && h < mSpeed) ? h : mHandicap;
    }

    void loseLife() {
        mLives = Math.max(0, mLives - 1);
    }

    boolean living() {
        return mLives > 0;
    }

    int getWidth() {
        return Paddle.PADDLE_WIDTH;
    }

    int getHeight() { return Paddle.PADDLE_THICKNESS; }

    int getTop() {
        return mRect.top;
    }

    int getBottom() {
        return mRect.bottom;
    }

    int centerX() {
        return mRect.centerX();
    }

    int centerY() {
        return mRect.centerY();
    }

    int getLeft() {
        return mRect.left;
    }

    int getRight() {
        return mRect.right;
    }

    int getLives() {
        return mLives;
    }

    void draw(Canvas canvas, Paint mPaint, float interpolation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mRectToDraw = rectEvaluator.evaluate(interpolation, mRectPrevious, mRect);
        } else {
            mRectToDraw.set(mRect); // no interpolation on older devices
        }
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mRectToDraw, mPaint);
    }
}
