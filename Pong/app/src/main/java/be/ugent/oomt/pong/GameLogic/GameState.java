package be.ugent.oomt.pong.GameLogic;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class GameState {

    private final Paint mPaint = new Paint();
    private final Rect bounds = new Rect();

    private Ball mBall = new Ball();
    private Paddle mPlayer = new Paddle();
    private Paddle mOpponent = new Paddle();

    //private boolean mInitialized = false;
    private boolean updatePaddlesAndBall = false;
    private int height=0;
    private int width=0;
    private static final int PADDING = 20;

    public GameState() {
        init();
    }

    private void init() {
        //set the game colour
        mPaint.setARGB(200, 0, 200, 0);
        //change font size
        mPaint.setTextSize(120f);
        // decreases max speed of opponent
        mOpponent.setHandicap(20);
        resetBall();
    }

    //The update method
    void update() {
        if (height == 0 || width == 0) {
            return;
        }

        if (updatePaddlesAndBall) {
            mOpponent.updatePaddle(width>>1, PADDING);
            mPlayer.updatePaddle(width>>1, height - PADDING - Paddle.PADDLE_THICKNESS);
            mBall.setX(width>>1); //devide by 2
            mBall.setY(height>>1); //devide by 2
            updatePaddlesAndBall = false;
        }

        mBall.move();

        // Shake it up if it appears to not be moving vertically
        if(!mBall.goingUp() && !mBall.goingDown() && !mBall.isPaused()) {
            mBall.randomAngle();
        }

        // Do some basic paddle AI
        mOpponent.destination = (int) bound(mBall.getX(), mOpponent.getWidth()>>1, width - (mOpponent.getWidth()>>1));
        mOpponent.move(true);

        mPlayer.move(false);

        handleBounces();

        // See if all is lost
        if(mBall.getY() >= height) {
            mPlayer.loseLife();
            resetBall();
        } else if (mBall.getY() <= 0) {
            mOpponent.loseLife();
            resetBall();
        }
    }

    private void handleBounces() {
        // Handle bouncing off of a wall
        if(mBall.getX() <= Ball.RADIUS || mBall.getX() >= width - Ball.RADIUS) {
            mBall.bounceVertical();
            // correct ball position
            if (mBall.getX() < Ball.RADIUS) {
                mBall.setX(Ball.RADIUS - mBall.getX());
            } else if (mBall.getX() > width - Ball.RADIUS) {
                mBall.setX(mBall.getX() - Ball.RADIUS + mBall.getXp() - width);
            }
        }

        // Handle opponent bounce
        collisionWithPaddle(mOpponent);

        // Handle player bounce
        collisionWithPaddle(mPlayer);
    }

    private void collisionWithPaddle(Paddle paddle) {
        float dx = Math.max(Math.abs(mBall.getX() - paddle.centerX()) - (paddle.getWidth()>>1) - Ball.RADIUS, 0);
        float dy = Math.max(Math.abs(mBall.getY() - paddle.centerY()) - (paddle.getHeight()>>1)  - Ball.RADIUS, 0);
        float distance = dx * dx + dy * dy;

        if (distance <= 0) { //collision
            if (paddle.getTop() - Ball.RADIUS > mBall.getYp() &&
                    paddle.getTop() - Ball.RADIUS <= mBall.getY() &&
                    paddle.getLeft() - Ball.RADIUS <= mBall.getX() &&
                    mBall.getX() <= paddle.getRight() + Ball.RADIUS) { // bounce on top side
                mBall.setY(paddle.getTop() - Ball.RADIUS - (mBall.getY() - paddle.getTop()));
                mBall.bounceHorizontal(paddle);
            } else if (paddle.getBottom() + Ball.RADIUS < mBall.getYp() &&
                    paddle.getBottom() + Ball.RADIUS >= mBall.getY() &&
                    paddle.getLeft() - Ball.RADIUS <= mBall.getX() &&
                    mBall.getX() <= paddle.getRight() + Ball.RADIUS) { // bounce on bottom side
                mBall.setY(paddle.getBottom() + Ball.RADIUS + (paddle.getBottom() - mBall.getY()));
                mBall.bounceHorizontal(paddle);
            }else if (paddle.getRight() + Ball.RADIUS < mBall.getXp() &&
                    paddle.getRight() + Ball.RADIUS >= mBall.getX() &&
                    paddle.getTop() - Ball.RADIUS < mBall.getY() &&
                    mBall.getY() < paddle.getBottom() + Ball.RADIUS) { // bounce on right side
                mBall.bounceVertical();
                mBall.setX(paddle.getRight() + Ball.RADIUS + (paddle.getRight() - mBall.getX()));
            } else if (paddle.getLeft() - Ball.RADIUS > mBall.getXp() &&
                    paddle.getLeft() - Ball.RADIUS <= mBall.getX() &&
                    paddle.getTop() - Ball.RADIUS < mBall.getY() &&
                    mBall.getY() < paddle.getBottom() + Ball.RADIUS) { // bounce on left side
                mBall.bounceVertical();
                mBall.setX(paddle.getLeft() - Ball.RADIUS - (mBall.getX() - paddle.getLeft()));
            }
            increaseDifficulty();
        }
    }

    /**
     * Reset ball to an initial state
     */
    private void resetBall() {
        mBall.setX(width>>1); //devide by 2
        mBall.setY(height>>1); //devide by 2
        mBall.setPaused(true);
        mBall.speed = Ball.SPEED;
        mBall.randomAngle();
    }

    /**
     * Starts a game from a paused state. If one of the players has won/lost reset the score and
     * the game.
     */
    public void play() {
        mBall.setPaused(false);
        if (!mPlayer.living() || !mOpponent.living()) {
            mPlayer.setLives(Paddle.STARTING_LIVES);
            mOpponent.setLives(Paddle.STARTING_LIVES);
        }
    }

    /**
     * Knocks up the framerate a bit to keep it difficult.
     */
    private void increaseDifficulty() {
        mBall.speed++;
    }

    /**
     * Moves the paddle based on the angle [-90,-90] and with respect to the maximum speed of the
     * paddle. Minimum an maximum paddle location is calculated based on the screens width.
     *
     * @param angle the angle between [-90,90]
     */
    public void movePaddle(int angle) {
        movePaddleTo(angle + mPlayer.destination);
    }

    /**
     * Moves the paddle to the target destination with respect to the maximum speed of the paddle.
     * The maximum or minimum paddle location is calculated based on the screens width.
     *
     * @param destination the absolute position to move the paddle to.
     */
    public void movePaddleTo(int destination) {
        mPlayer.destination = (int) bound(destination, mPlayer.getWidth()>>1, width - (mPlayer.getWidth()>>1));
    }

    private float bound(float x, float low, float hi) {
        return Math.max(low, Math.min(x, hi));
    }

    private static final String win = "WON";
    private static final String lose = "LOST";

    //the draw method
    void draw(Canvas canvas, float interpolation) {
        if (this.height != canvas.getHeight() || this.width != canvas.getWidth()) {
            this.height = canvas.getHeight();
            this.width = canvas.getWidth();
            updatePaddlesAndBall = true;
        }

        //Clear the screen
        canvas.drawColor(Color.BLACK);

        //draw the ball
        mBall.draw(canvas, mPaint, interpolation);

        //draw the bats
        mPlayer.draw(canvas, mPaint, interpolation);
        mOpponent.draw(canvas, mPaint, interpolation);

        //draw line
        // remember x >> 1 is equivalent to x / 2, but works much much faster
        canvas.drawLine(0, (height>>1), width, (height>>1), mPaint);

        //draw score
        // remember x >> 1 is equivalent to x / 2, but works much much faster
        String a = String.valueOf(mPlayer.getLives()), b = String.valueOf(mOpponent.getLives());
        //measure height of font
        mPaint.getTextBounds(a, 0, a.length(), bounds);
        canvas.drawText(a, PADDING, (this.height>>1) + PADDING + bounds.height(), mPaint);
        mPaint.getTextBounds(b, 0, b.length(), bounds);
        canvas.drawText(b, this.width - PADDING - bounds.width(), (this.height>>1) - PADDING, mPaint);

        //draw win/lose
        if (!mOpponent.living()) {
            mPaint.getTextBounds(win, 0, win.length(), bounds);
            canvas.drawText(lose, (this.width - bounds.width())>>1, ((this.height - bounds.height())>>2), mPaint);
            canvas.drawText(win, (this.width - bounds.width())>>1, (this.height>>1) + ((this.height - bounds.height())>>2), mPaint);
        } else if (!mPlayer.living()) {
            mPaint.getTextBounds(lose, 0, lose.length(), bounds);
            canvas.drawText(win, (this.width - bounds.width()) >> 1, ((this.height - bounds.height()) >> 2), mPaint);
            canvas.drawText(lose, (this.width - bounds.width())>>1, (this.height>>1) + ((this.height - bounds.height())>>2), mPaint);
        }
    }
}
