package com.example.david.dabplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    public static final int X = 0;
    public static final int Y = 1;

    // Created in initDimens()
    public static float
            widthPx,  // width of screen (px)
            widthDp,  // width of screen (dp)
            heightPx, // height of screen (px)
            heightDp, // height of screen (dp)
            density;  // density of screen (px -> dp conversion)

    public static float
            sbhPx, // status bar height (px)
            ivWPx, // image view width (px)
            ivHPx; // image view height (px)

    public static RelativeLayout layout;
    public static TextView tv;

    SList<SList<P>> touchInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDimens();

        touchInfo = new SList<>();

        layout = (RelativeLayout) findViewById(R.id.layout);
        tv = (TextView) findViewById(R.id.tv);

        int dab_num = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getInt("DAB_NUM", 0);
        tv.setText(String.valueOf(dab_num));
        
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Settings Activity
                // Look at Xerox Scanner Code



            }
        });

    }

    private void initDimens() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        density = displayMetrics.density;
        widthPx = displayMetrics.widthPixels;
        widthDp = widthPx / density;
        heightPx = displayMetrics.heightPixels;
        heightDp = heightPx / density;

    }

    private void runAnim(Context c, final float xs, final float ys) {
        tv.setText("" + (Integer.parseInt(tv.getText().toString()) + 1));

        // > Init imag
        final ImageView iv = new ImageView(c);
        iv.setLayoutParams(new RelativeLayout.LayoutParams(100, 180));
        iv.setImageResource(R.drawable.dab_right);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        layout.addView(iv);

        ViewTreeObserver vto = iv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                iv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ivWPx = iv.getWidth();
                ivHPx = iv.getHeight();
                sbhPx = heightPx - layout.getHeight();
                float totalW = widthPx - ivWPx;
                float totalH = heightPx - ivHPx - sbhPx;

                // Which side you going to?
                //  1
                // 0 2
                //  3
                int side = (int) Math.floor(Math.random() * 4);

                // Where on that side will you land?
                float xe = 0, ye = 0;
                switch (side) {
                    case 0:
                        xe = (int) (0 - ivWPx);
                        ye = (int) (totalH * Math.random());
                        break;
                    case 1:
                        xe = (int) (totalW * Math.random());
                        ye = (int) (0 - ivHPx);
                        break;
                    case 2:
                        xe = (int) (totalW + ivWPx);
                        ye = (int) (totalH * Math.random());
                        break;
                    case 3:
                        xe = (int) (totalW * Math.random());
                        ye = (int) (totalH + ivHPx);
                        break;
                }


                // Random Size of IV
                float scaleSize = (float) (Math.random() * 2 + 0.25);

                iv.setScaleX(scaleSize);
                iv.setScaleY(scaleSize);

                // > Actual animations
                iv.setX(xs);
                iv.setY(ys);

                // Dab switching
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (iv.getTag() == null || iv.getTag() == "RIGHT") {
                            iv.setImageResource(R.drawable.dab_left);
                            iv.setTag("LEFT");
                        } else {
                            iv.setImageResource(R.drawable.dab_right);
                            iv.setTag("RIGHT");
                        }
                        handler.postDelayed(this, (int) (Math.random() * 500 + 50));
                    }
                }, (int) (Math.random() * 500 + 50));

                // Movement animation
                List<Interpolator> is = new ArrayList<>();
                is.add(new AccelerateInterpolator());
                is.add(new DecelerateInterpolator());
                is.add(new AccelerateDecelerateInterpolator());
                is.add(new BounceInterpolator());
                is.add(new AnticipateInterpolator());
                is.add(new AnticipateOvershootInterpolator());
                is.add(new OvershootInterpolator());
                is.add(new LinearInterpolator());

                // random between
                long animDuration = (long) (Math.random() * 3000) + 500;

                iv.animate().translationX(xe).setDuration(animDuration).setInterpolator(is.get((int) Math.floor(Math.random() * is.size()))).start();
                iv.animate().translationY(ye).setDuration(animDuration).setInterpolator(is.get((int) Math.floor(Math.random() * is.size()))).start();

                // Rotation animation
                iv.animate().rotationBy((float) Math.random() * 1440 - 720).setDuration(animDuration).setInterpolator(is.get((int) Math.floor(Math.random() * is.size()))).start();

                // Destroy iv after animation
                final Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout.removeView(iv);
                        // also add random image sizes
                        handler.removeCallbacksAndMessages(null);
                    }
                }, animDuration);
            }
        });
    }

    private void runAnim(final Context c, float x1, float y1, float x2, float y2, final long time) {
        tv.setText("" + (Integer.parseInt(tv.getText().toString()) + 1));

        final float xs, ys, xe, ye;

        try {
            float slope = (y2 - y1) / (x2 - x1);

            //  1
            // 0 2
            //  3
            float i0x = 0;
            float i0y = y1 - (slope * x1);

            float i1x = x2 - (y2 / slope);
            float i1y = 0;

            float i2x = widthPx;
            float i2y = y2 + (slope * (widthPx - x2));

            float i3x = x2 + ((heightPx - y2) / slope);
            float i3y = heightPx;


            int counter = 0;
            float[][] vs = new float[2][2];

            if (i0y >= 0 && i0y <= heightPx) {
                if (counter == 2) throw new Exception("triple intersection?");
                vs[X][counter] = i0x;
                vs[Y][counter] = i0y;
                counter++;
            }

            if (i1x >= 0 && i1x <= widthPx) {
                if (counter == 2) throw new Exception("triple intersection?");
                vs[X][counter] = i1x;
                vs[Y][counter] = i1y;
                counter++;
            }


            if (i2y >= 0 && i2y <= heightPx) {
                if (counter == 2) throw new Exception("triple intersection?");
                vs[X][counter] = i2x;
                vs[Y][counter] = i2y;
                counter++;
            }

            if (i3x >= 0 && i3x <= widthPx) {
                if (counter == 2) throw new Exception("triple intersection???");
                vs[X][counter] = i3x;
                vs[Y][counter] = i3y;
                counter++;
            }

            if (counter != 2) throw new Exception("not double intersection :(");

            double d1 = Math.sqrt(Math.pow(x1 - vs[X][0], 2) + Math.pow(y1 - vs[Y][0], 2));
            double d2 = Math.sqrt(Math.pow(x2 - vs[X][0], 2) + Math.pow(y2 - vs[Y][0], 2));

            xs = x1;
            ys = y1;

            if (d1 <= d2) {
                xe = vs[X][1];
                ye = vs[Y][1];
            } else {
                xe = vs[X][0];
                ye = vs[Y][0];
            }

        } catch (Exception e) {
            runAnim(c, x1, y1);
            return;
        }

        // > Init imag
        final ImageView iv = new ImageView(c);
        iv.setLayoutParams(new RelativeLayout.LayoutParams(100, 180));
        iv.setImageResource(R.drawable.dab_right);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        layout.addView(iv);

        ViewTreeObserver vto = iv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                iv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ivWPx = iv.getWidth();
                ivHPx = iv.getHeight();
                sbhPx = heightPx - layout.getHeight();

                // Random Size of IV
                float scaleSize = (float) (Math.random() * 2 + 0.25);

                iv.setScaleX(scaleSize);
                iv.setScaleY(scaleSize);

                // > Actual animations
                iv.setX(xs);
                iv.setY(ys);

                // Dab switching
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (iv.getTag() == null || iv.getTag() == "RIGHT") {
                            iv.setImageResource(R.drawable.dab_left);
                            iv.setTag("LEFT");
                        } else {
                            iv.setImageResource(R.drawable.dab_right);
                            iv.setTag("RIGHT");
                        }
                        handler.postDelayed(this, (int) (Math.random() * 500 + 50));
                    }
                }, (int) (Math.random() * 500 + 50));


                // random between
                long animDuration = time;

                iv.animate().translationX(xe).setDuration(animDuration).start();
                iv.animate().translationY(ye).setDuration(animDuration).start();

                // Rotation animation
                iv.animate().rotationBy((float) Math.random() * 1440 - 720).setDuration(animDuration).start();

                // Destroy iv after animation
                final Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout.removeView(iv);
                        // also add random image sizes
                        handler.removeCallbacksAndMessages(null);
                    }
                }, animDuration);
            }
        });
    }


//    // Path -- legacy. Doesn't really work? I'm too lazy to figure out canvas...
//    public void runAnim(Context c, final List<P> path) {
//
//        // Animate it exactly along the path
//        // Animate it out following the last slope (from runAnim swipe?)
//
//
//
//        // > Init imag
//        final ImageView iv = new ImageView(c);
//        iv.setLayoutParams(new RelativeLayout.LayoutParams(100, 180));
//        iv.setImageResource(R.drawable.dab_right);
//        iv.setScaleType(ImageView.ScaleType.FIT_XY);
//        layout.addView(iv);
//
//        ViewTreeObserver vto = iv.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                iv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                ivWPx = iv.getWidth();
//                ivHPx = iv.getHeight();
//                sbhPx = heightPx - layout.getHeight();
//
//                // Random Size of IV
//                float scaleSize = (float) (Math.random() * 2 + 0.25);
//
//                iv.setScaleX(scaleSize);
//                iv.setScaleY(scaleSize);
//
//                // Dab switching
//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (iv.getTag() == null || iv.getTag() == "RIGHT") {
//                            iv.setImageResource(R.drawable.dab_left);
//                            iv.setTag("LEFT");
//                        } else {
//                            iv.setImageResource(R.drawable.dab_right);
//                            iv.setTag("RIGHT");
//                        }
//                        handler.postDelayed(this, (int) (Math.random() * 500 + 50));
//                    }
//                }, (int) (Math.random() * 500 + 50));
//
//
//                // > Create points leading the image out of screen
//                int len = path.size();
//                while (path.get(len - 1).x > 0 && path.get(len - 1).x < widthPx && path.get(len - 1).y > 0 && path.get(len - 1).y < heightPx) {
//                    path.add(new P(
//                            path.get(len - 1).x + (path.get(len - 1).x - path.get(len - 2).x),
//                            path.get(len - 1).y + (path.get(len - 1).y - path.get(len - 2).y),
//                            path.get(len - 1).t + (path.get(len - 1).t - path.get(len - 2).t)
//                    ));
//                    len++;
//                }
//
//                // > Animate along path
//                iv.setX(path.get(0).x);
//                iv.setY(path.get(0).y);
//
//                long totalDelay = 0;
//
//                while (path.size() >= 2) {
//                    long duration = path.get(1).t - path.get(0).t;
//                    move(iv, path.get(1).x, path.get(1).y, duration, totalDelay);
//                    totalDelay += duration;
//                    path.remove(0);
//                }
//
//                // Rotation animation
//                iv.animate().rotationBy((float) Math.random() * 1440 - 720).setDuration(totalDelay).start();
//
//                // > Destroy iv after animation
//                final Handler handler1 = new Handler();
//                handler1.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        layout.removeView(iv);
//                        // also add random image sizes
//                        handler.removeCallbacksAndMessages(null);
//                    }
//                }, totalDelay);
//            }
//        });
//    }
//
//    public static void move(final ImageView iv, final float x, final float y, final long t, long delay) {
//        final Handler handler1 = new Handler();
//        handler1.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                iv.animate().translationX(x).setDuration(t).start();
//                iv.animate().translationY(y).setDuration(t).start();
//            }
//        }, delay);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int index = event.getActionIndex();
//                Log.d("TAG", event.toString());

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            // create new array of points, add it to the end
            touchInfo.add(new SList<P>());
            touchInfo.getR(0).add(new P(event.getX(index), event.getY(index), event.getDownTime()));

//            Log.d("TAG", String.format("Down: (%f, %f)", event.getX(index), event.getY(index)));
        } else if (action == MotionEvent.ACTION_MOVE) {
            // update the move location in touchInfo
            if (touchInfo.get(index).get(0).t == -1) {
                // is being held
                runAnim(MainActivity.this, event.getX(index), event.getY(index));
            } else if (touchInfo.get(index).getR(0).t - touchInfo.get(index).get(0).t > 1000
                    && Math.abs(touchInfo.get(index).get(0).x - event.getX(index)) + Math.abs(touchInfo.get(index).get(0).y - event.getY(index)) < 100) {
                // initiate hold
                touchInfo.get(index).get(0).t = -1;
            } else {
                // just update
                touchInfo.get(index).add(new P(event.getX(index), event.getY(index), event.getEventTime()));
            }

//                Log.d("TAG", String.format("Move: (%f, %f)", event.getX(index), event.getY(index)));

        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            if (touchInfo.get(index).get(0).t == -1) {
                // end of Hold
                runAnim(MainActivity.this, event.getX(index), event.getY(index));
            } else if (Math.abs(touchInfo.get(index).get(0).x - event.getX(index)) + Math.abs(touchInfo.get(index).get(0).y - event.getY(index)) < 100) {
                // is Tap
                runAnim(MainActivity.this, event.getX(index), event.getY(index));
//                        Log.d("TAG", "> TAP <");
            } else {
                // is Swipe

                // Path call. Alas, too big a dream
//                runAnim(MainActivity.this, touchInfo.get(index));

                runAnim(MainActivity.this,
                        touchInfo.get(index).get(0).x,
                        touchInfo.get(index).get(0).y,
                        event.getX(index),
                        event.getY(index),
                        event.getEventTime() - event.getDownTime());

//                        Log.d("TAG", "> SWIPE <");
            }

                touchInfo.remove(index);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();

        int dab_num = Integer.parseInt(tv.getText().toString());
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putInt("DAB_NUM", dab_num).apply();
    }

    public static class P {

        float x;
        float y;
        long t;

        P(float x0, float y0, long t0) {
            this.x = x0;
            this.y = y0;
            this.t = t0;
        }

        @Override
        public String toString() {
            return String.format("P:(%f, %f) @ t=%d", this.x, this.y, this.t);
        }
    }

}

