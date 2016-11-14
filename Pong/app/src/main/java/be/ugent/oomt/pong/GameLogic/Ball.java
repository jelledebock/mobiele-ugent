package be.ugent.oomt.pong.GameLogic;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

class Ball {
    static final float SPEED = 30;
    static final int RADIUS = 20;
    private static final double SALT = 4 * Math.PI / 9;
    private static final double BOUND = Math.PI / 9;

    /** Random number generator */
    private static final Random RNG = new Random();

    float getX() {
        return x;
    }

    float getY() {
        return y;
    }

    float getXp() {
        return xp;
    }

    float getYp() {
        return yp;
    }

    private float x, y, xp, yp, vx, vy;
    float speed = SPEED;

    private double mAngle;
    private int mCounter = 0;
    private boolean paused = false;

    Ball() {
        findVector();
    }

    private void findVector() {
        vx = (float) (speed * Math.cos(mAngle));
        vy = (float) (speed * Math.sin(mAngle));
    }

    boolean goingUp() {
        return Math.PI < mAngle;// && mAngle < 2*Math.PI; // last check can be ignored (modulus 2*PI)
    }

    boolean goingDown() {
        return 0 < mAngle && mAngle < Math.PI;
    }

    void move() {
        this.xp = x;
        this.yp = y;
        if (!paused) {
            setX(x + vx);
            setY(y + vy);
        }
        mCounter++;
    }

    void setX(float x) {
        //xp = this.x;
        this.x = x;
    }

    void setY(float y) {
        //yp = this.y;
        this.y = y;
    }

    void randomAngle() {
        //setAngle(Math.PI / 2 + RNG.nextInt(2) * Math.PI + Math.PI / 2 * RNG.nextGaussian());
        setAngle(boundAngle(Math.PI / 2 + RNG.nextInt(2) * Math.PI + Math.PI / 4 * RNG.nextGaussian()));

    }

    private void setAngle(double angle) {
        mAngle = angle % (2 * Math.PI);
        findVector();
    }

    void draw(Canvas canvas, Paint mPaint, float interpolation) {
        if((mCounter / 10) % 2 == 1 || !paused) {
            interpolation = paused ? 1 : interpolation;
            canvas.drawCircle(xp + ((x-xp) * interpolation), yp + ((y-yp) * interpolation), Ball.RADIUS, mPaint);
        }
    }

    /**
     * Bounce the ball off a horizontal axis.
     */
    void bounceVertical() {
        setAngle(3 * Math.PI - mAngle);
    }

    /**
     * Method bounces the ball across a vertical axis. Seriously it's that easy.
     * Math failed me when figuring this out so I guessed instead.
     */
    void bounceHorizontal(Paddle p) {
        double angle = (2 * Math.PI - mAngle) % (2 * Math.PI);
        angle = salt(angle, p);
        setAngle(angle);
    }

    private double salt(double angle, Paddle paddle) {
        int cx = paddle.centerX();
        double halfWidth = paddle.getWidth()>>1;
        double change;

        if(goingUp()) change = SALT * ((cx - x) / halfWidth);
        else change = SALT * ((x - cx) / halfWidth);

        return boundAngle(angle, change);
    }

    /**
     * Bounds sum of <code>angle</code> and <code>angleChange</code> to the side of the
     * unit circle that <code>angle</code> is on.
     * @param angle The initial angle.
     * @param angleChange Amount to add to angle.
     * @return bounded angle sum
     */
    private double boundAngle(double angle, double angleChange) {
        return boundAngle(angle + angleChange, angle >= Math.PI);
    }

    private double boundAngle(double angle) {
        return boundAngle(angle, angle >= Math.PI);
    }
    /**
     * Bounds an angle in radians to a subset of the top
     * or bottom part of the unit circle.
     * @param angle The angle in radians to bound.
     * @param top Flag which indicates if we should bound to the top or not.
     * @return the bounded angle
     */
    private double boundAngle(double angle, boolean top) {
        if(top) {
            return Math.max(Math.PI + BOUND, Math.min(2 * Math.PI - BOUND, angle));
        }
        return Math.max(BOUND, Math.min(Math.PI - BOUND, angle));
    }

    void setPaused(boolean paused) {
        this.paused = paused;
    }

    boolean isPaused() {
        return paused;
    }
}
