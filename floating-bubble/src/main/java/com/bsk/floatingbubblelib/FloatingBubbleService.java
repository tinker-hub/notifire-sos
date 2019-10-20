package com.bsk.floatingbubblelib;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

/**
 * Floating Bubble Service. This file is the actual bubble view.
 * Created by bijoy on 1/6/17.
 * Updated by Janjan Medina on 10/16/19
 */

public class FloatingBubbleService extends Service {

    protected static final String TAG = FloatingBubbleService.class.getSimpleName();

    // Constructor Variable
    protected FloatingBubbleLogger logger;

    // The Window Manager View
    protected WindowManager windowManager;

    // The layout inflater
    protected LayoutInflater inflater;

    // Window Dimensions
    protected Point windowSize = new Point();

    // The Views
    protected View bubbleView;
    protected View removeBubbleView;
    protected View expandableView;

    protected WindowManager.LayoutParams bubbleParams;
    protected WindowManager.LayoutParams removeBubbleParams;
    protected WindowManager.LayoutParams expandableParams;

    private FloatingBubbleConfig config;
    private FloatingBubbleTouch touch;
    private FloatingBubbleActionListener actionListener;

    @Override
    public void onCreate() {
        super.onCreate();
        logger = new FloatingBubbleLogger().setDebugEnabled(true).setTag(TAG);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || !onGetIntent(intent)) {
            return Service.START_NOT_STICKY;
        }

        logger.log("Start with START_STICKY");

        // Remove existing views
        removeAllViews();

        // Load the Window Managers
        setupWindowManager();
        setupViews();
        setTouchListener();
        return super.onStartCommand(intent, flags, Service.START_STICKY);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logger.log("onDestroy");
        removeAllViews();
        actionListener.onBubbleViewClosed();
    }

    private void removeAllViews() {
        if (windowManager == null) {
            return;
        }

        if (bubbleView != null) {
            windowManager.removeView(bubbleView);
            bubbleView = null;
        }

        if (removeBubbleView != null) {
            windowManager.removeView(removeBubbleView);
            removeBubbleView = null;
        }

        if (expandableView != null) {
            windowManager.removeView(expandableView);
            expandableView = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    private void setupWindowManager() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        setLayoutInflater();
        windowManager.getDefaultDisplay().getSize(windowSize);
    }

    protected LayoutInflater setLayoutInflater() {
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        return inflater;
    }

    /**
     * Creates the views
     */
    protected void setupViews() {
        config = getConfig();
        int padding = dpToPixels(config.getPaddingDp());
        int iconSize = dpToPixels(config.getBubbleIconDp());
        int bottomMargin = getExpandableViewBottomMargin();

        actionListener = config.getActionListener();

        // Setting up view
        bubbleView = inflater.inflate(config.getBubbleIcon(), null);
        removeBubbleView = inflater.inflate(config.getRemoveBubbleIcon(), null);

        // Setting up the Remove Bubble View setup
        removeBubbleParams = getDefaultWindowParams();
        removeBubbleParams.gravity = Gravity.TOP | Gravity.START;
        removeBubbleParams.width = dpToPixels(config.getRemoveBubbleIconDp());
        removeBubbleParams.height = dpToPixels(config.getRemoveBubbleIconDp());
        removeBubbleParams.x = (windowSize.x - removeBubbleParams.width) / 2;
        removeBubbleParams.y = windowSize.y - removeBubbleParams.height - bottomMargin;
        removeBubbleView.setVisibility(View.GONE);
        removeBubbleView.setAlpha(config.getRemoveBubbleAlpha());
        windowManager.addView(removeBubbleView, removeBubbleParams);

        // Setting up the Floating Bubble View
        bubbleParams = getDefaultWindowParams();
        bubbleParams.gravity = Gravity.TOP | Gravity.START;
        bubbleParams.width = iconSize;
        bubbleParams.height = iconSize;
        windowManager.addView(bubbleView, bubbleParams);

        actionListener.onBubbleViewCreated();
    }

    /**
     * Get the Bubble config
     *
     * @return the config
     */
    protected FloatingBubbleConfig getConfig() {
        return FloatingBubbleConfig.getDefault();
    }

    /**
     * Sets the touch listener
     */
    protected void setTouchListener() {
        FloatingBubblePhysics physics = new FloatingBubblePhysics.Builder()
                .sizeX(windowSize.x)
                .sizeY(windowSize.y)
                .bubbleView(bubbleView)
                .config(config)
                .windowManager(windowManager)
                .build();

        touch = new FloatingBubbleTouch.Builder()
                .sizeX(windowSize.x)
                .sizeY(windowSize.y)
                .listener(getTouchListener())
                .physics(physics)
                .bubbleView(bubbleView)
                .removeBubbleSize(dpToPixels(config.getRemoveBubbleIconDp()))
                .windowManager(windowManager)
                .removeBubbleView(removeBubbleView)
                .config(config)
                .marginBottom(getExpandableViewBottomMargin())
                .build();

        bubbleView.setOnTouchListener(touch);
    }

    /**
     * Gets the touch listener for the bubble
     *
     * @return the touch listener
     */
    public FloatingBubbleTouchListener getTouchListener() {
        return new DefaultFloatingBubbleTouchListener() {
            @Override
            public void onRemove() {
                stopSelf();
            }
        };
    }

    /**
     * Get the default window layout params
     *
     * @return the layout param
     */
    protected WindowManager.LayoutParams getDefaultWindowParams() {
        return getDefaultWindowParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Get the default window layout params
     *
     * @return the layout param
     */
    protected WindowManager.LayoutParams getDefaultWindowParams(int width, int height) {
        return new WindowManager.LayoutParams(
                width,
                height,
                Build.VERSION.SDK_INT >= 26
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
    }

    /**
     * Handles the intent for the service (only if it is not null)
     *
     * @param intent the intent
     */
    protected boolean onGetIntent(@NonNull Intent intent) {
        return true;
    }

    /**
     * Get the layout inflater for view inflation
     *
     * @return the layout inflater
     */
    protected LayoutInflater getInflater() {
        return inflater == null ? setLayoutInflater() : inflater;
    }

    /**
     * Get the context for the service
     *
     * @return the context
     */
    protected Context getContext() {
        return getApplicationContext();
    }

    /**
     * Sets the state of the expanded view
     *
     * @param expanded the expanded view state
     */
    protected void setState(boolean expanded) {
//        touch.setState(expanded);
    }

    /**
     * Get the expandable view's bottom margin
     *
     * @return margin
     */
    private int getExpandableViewBottomMargin() {
        Resources resources = getContext().getResources();
        int resourceId =
                resources.getIdentifier(
                        "navigation_bar_height",
                        "dimen",
                        "android"
                );
        int navBarHeight = 0;
        if (resourceId > 0) {
            navBarHeight = resources.getDimensionPixelSize(resourceId);
        }

        return navBarHeight;
    }

    /**
     * Converts DPs to Pixel values
     *
     * @return the pixel value
     */
    private int dpToPixels(int dpSize) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dpSize * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
