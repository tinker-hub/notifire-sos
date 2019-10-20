package com.bsk.floatingbubblelib;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

/**
 * Touch event for the Floating Bubble Service
 * Created by bijoysingh on 2/19/17.
 * Updated by Janjan Medina on 10/16/19
 */

public class FloatingBubbleTouch implements View.OnTouchListener {

  private static final int TOUCH_CLICK_TIME = 250;
  private static final float EXPANSION_FACTOR = 1.25f;

  private int sizeX;
  private int sizeY;

  private View bubbleView;
  private View removeBubbleView;
  private WindowManager windowManager;
  private FloatingBubbleTouchListener listener;
  private FloatingBubbleTouchListener physics;
  private FloatingBubbleActionListener actionListener;
  private FloatingBubbleConfig config;
  private int marginBottom;

  private WindowManager.LayoutParams bubbleParams;
  private WindowManager.LayoutParams removeBubbleParams;
  private int removeBubbleStartSize;
  private int removeBubbleExpandedSize;
  private FloatingBubbleAnimator animator;

  private long touchStartTime = 0;

  private FloatingBubbleTouch(Builder builder) {
    config = builder.config;
    int removeBubbleSize = builder.removeBubbleSize;
    physics = builder.physics;
    listener = builder.listener;
    windowManager = builder.windowManager;
    removeBubbleView = builder.removeBubbleView;
    bubbleView = builder.bubbleView;
    sizeY = builder.sizeY;
    sizeX = builder.sizeX;
    marginBottom = builder.marginBottom;

    actionListener = config.getActionListener();
    bubbleParams = (WindowManager.LayoutParams) bubbleView.getLayoutParams();
    removeBubbleParams = (WindowManager.LayoutParams) removeBubbleView.getLayoutParams();
    removeBubbleStartSize = removeBubbleSize;
    removeBubbleExpandedSize = (int) (EXPANSION_FACTOR * removeBubbleSize);
    animator = new FloatingBubbleAnimator.Builder()
        .sizeX(sizeX)
        .sizeY(sizeY)
        .windowManager(windowManager)
        .bubbleView(bubbleView)
        .bubbleParams(bubbleParams)
        .build();
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouch(View view, MotionEvent motionEvent) {
    switch (motionEvent.getActionMasked()) {
      case MotionEvent.ACTION_DOWN:
        touchStartTime = System.currentTimeMillis();
        showRemoveBubble(View.VISIBLE);
        if (listener != null) {
          listener.onDown(motionEvent.getRawX(), motionEvent.getRawY());
        }
        if (sendEventToPhysics()) {
          physics.onDown(motionEvent.getRawX(), motionEvent.getRawY());
        }
        break;

      case MotionEvent.ACTION_MOVE:
        long lastTouchTime = System.currentTimeMillis();
        moveBubbleView(motionEvent);
        if (lastTouchTime - touchStartTime > TOUCH_CLICK_TIME) {
          showRemoveBubble(View.VISIBLE);
        }

        if (listener != null) {
          listener.onMove(motionEvent.getRawX(), motionEvent.getRawY());
        }
        if (sendEventToPhysics()) {
          physics.onMove(motionEvent.getRawX(), motionEvent.getRawY());
        }
        break;

      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        showRemoveBubble(View.GONE);
        lastTouchTime = System.currentTimeMillis();
        if (lastTouchTime - touchStartTime >= TOUCH_CLICK_TIME) {
          boolean isRemoved = checkRemoveBubble(
                  motionEvent.getRawX(),
                  motionEvent.getRawY()
          );
          if (listener != null) {
            listener.onUp(motionEvent.getRawX(), motionEvent.getRawY());
          }
          if (!isRemoved && sendEventToPhysics()) {
            physics.onUp(motionEvent.getRawX(), motionEvent.getRawY());
          }
          actionListener.onDragToRemove();
        }
    }
    return true;
  }

  private void moveBubbleView(MotionEvent motionEvent) {
    float halfClipSize = bubbleView.getWidth() / 2;
    float clipSize = bubbleView.getWidth();

    float leftX = motionEvent.getRawX() - halfClipSize;
    leftX = (leftX > sizeX - clipSize) ? (sizeX - clipSize) : leftX;
    leftX = leftX < 0 ? 0 : leftX;

    float topY = motionEvent.getRawY() - halfClipSize;
    topY = (topY > sizeY - clipSize) ? (sizeY - clipSize) : topY;
    topY = topY < 0 ? 0 : topY;

    bubbleParams.x = (int) leftX;
    bubbleParams.y = (int) topY;

    handleRemove();
    windowManager.updateViewLayout(bubbleView, bubbleParams);
    windowManager.updateViewLayout(removeBubbleView, removeBubbleParams);
  }

  private void handleRemove() {
    if (isInsideRemoveBubble()) {
      removeBubbleParams.height = removeBubbleExpandedSize;
      removeBubbleParams.width = removeBubbleExpandedSize;
      removeBubbleParams.x = (sizeX - removeBubbleParams.width) / 2;
      removeBubbleParams.y = sizeY - removeBubbleParams.height - marginBottom;
      bubbleParams.x = removeBubbleParams.x + (removeBubbleExpandedSize - bubbleView.getWidth()) / 2;
      bubbleParams.y = removeBubbleParams.y + (removeBubbleExpandedSize - bubbleView.getWidth()) / 2;
    } else {
      removeBubbleParams.height = removeBubbleStartSize;
      removeBubbleParams.width = removeBubbleStartSize;
      removeBubbleParams.x = (sizeX - removeBubbleParams.width) / 2;
      removeBubbleParams.y = sizeY - removeBubbleParams.height - marginBottom;
    }
  }

  private boolean isInsideRemoveBubble() {
    int bubbleSize = removeBubbleView.getWidth() == 0
        ? removeBubbleStartSize
        : removeBubbleView.getWidth();
    int top = removeBubbleParams.y;
    int right = removeBubbleParams.x + bubbleSize;
    int bottom = removeBubbleParams.y + bubbleSize;
    int left = removeBubbleParams.x;

    int centerX = bubbleParams.x + bubbleView.getWidth() / 2;
    int centerY = bubbleParams.y + bubbleView.getWidth() / 2;

    return centerX > left && centerX < right && centerY > top && centerY < bottom;
  }

  private boolean checkRemoveBubble(float x, float y) {
    if (isInsideRemoveBubble()) {
      physics.onUp(x, y);

      expandView(x, y);
      if (listener != null) {
        listener.onTap(false);
      }
      if (sendEventToPhysics()) {
        physics.onTap(false);
      }
      return true;
    }
    return false;
  }

  private boolean sendEventToPhysics() {
    return config.isPhysicsEnabled() && physics != null;
  }

  private void showRemoveBubble(int visibility) {
    removeBubbleView.setVisibility(visibility);
  }

  private void expandView(float x, float y) {
    physics.onUp(x, y);
    actionListener.onExpandedView();
  }

  public static final class Builder {
    private int sizeX;
    private int sizeY;
    private View bubbleView;
    private View removeBubbleView;
    private WindowManager windowManager;
    private FloatingBubbleTouchListener listener;
    private int removeBubbleSize;
    private FloatingBubbleTouchListener physics;
    private FloatingBubbleConfig config;
    private int marginBottom;

    Builder() {
    }

    Builder sizeX(int val) {
      sizeX = val;
      return this;
    }

    Builder sizeY(int val) {
      sizeY = val;
      return this;
    }

    Builder bubbleView(View val) {
      bubbleView = val;
      return this;
    }

    Builder removeBubbleView(View val) {
      removeBubbleView = val;
      return this;
    }

    public Builder logger(FloatingBubbleLogger val) {
      return this;
    }

    Builder windowManager(WindowManager val) {
      windowManager = val;
      return this;
    }

    public FloatingBubbleTouch build() {
      return new FloatingBubbleTouch(this);
    }

    Builder removeBubbleSize(int val) {
      removeBubbleSize = val;
      return this;
    }

    Builder physics(FloatingBubbleTouchListener val) {
      physics = val;
      return this;
    }

    Builder listener(FloatingBubbleTouchListener val) {
      listener = val;
      return this;
    }

    Builder config(FloatingBubbleConfig val) {
      config = val;
      return this;
    }

    Builder marginBottom(int val) {
      marginBottom = val;
      return this;
    }
  }
}
