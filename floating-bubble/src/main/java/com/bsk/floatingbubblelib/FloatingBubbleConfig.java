package com.bsk.floatingbubblelib;

import android.graphics.Color;
import android.view.Gravity;

/**
 * Floating configurations
 * Created by bijoysingh on 2/19/17.
 * Updated by Janjan Medina on 10/16/19
 */

public class FloatingBubbleConfig {
  private int bubbleIcon;
  private int removeBubbleIcon;
  private int expandableView;
  private int bubbleIconDp;
  private int removeBubbleIconDp;
  private float removeBubbleAlpha;
  private int expandableColor;
  private int triangleColor;
  private int gravity;
  private int paddingDp;
  private int borderRadiusDp;
  private boolean physicsEnabled;
  private FloatingBubbleActionListener actionListener;

  private FloatingBubbleConfig(Builder builder) {
    bubbleIcon = builder.bubbleIcon;
    removeBubbleIcon = builder.removeBubbleIcon;
    expandableView = builder.expandableView;
    bubbleIconDp = builder.bubbleIconDp;
    removeBubbleIconDp = builder.removeBubbleIconDp;
    expandableColor = builder.expandableColor;
    triangleColor = builder.triangleColor;
    gravity = builder.gravity;
    paddingDp = builder.paddingDp;
    borderRadiusDp = builder.borderRadiusDp;
    physicsEnabled = builder.physicsEnabled;
    removeBubbleAlpha = builder.removeBubbleAlpha;
    actionListener = builder.actionListener;
  }

  private static Builder getDefaultBuilder() {
    return new Builder()
        .bubbleIcon(R.layout.floating_bubble_view)
        .removeBubbleIcon(R.layout.floating_remove_bubble_view)
        .bubbleIconDp(64)
        .removeBubbleIconDp(64)
        .paddingDp(4)
        .removeBubbleAlpha(1.0f)
        .physicsEnabled(true)
        .expandableColor(Color.WHITE)
        .triangleColor(Color.WHITE)
        .gravity(Gravity.END);
  }

  static FloatingBubbleConfig getDefault() {
    return getDefaultBuilder().build();
  }

  int getBubbleIcon() {
    return bubbleIcon;
  }

  int getRemoveBubbleIcon() {
    return removeBubbleIcon;
  }

  int getExpandableView() {
    return expandableView;
  }

  int getBubbleIconDp() {
    return bubbleIconDp;
  }

  int getRemoveBubbleIconDp() {
    return removeBubbleIconDp;
  }

  int getExpandableColor() {
    return expandableColor;
  }

  int getTriangleColor() {
    return triangleColor;
  }

  int getGravity() {
    return gravity;
  }

  int getPaddingDp() {
    return paddingDp;
  }

  boolean isPhysicsEnabled() {
    return physicsEnabled;
  }

  int getBorderRadiusDp() {
    return borderRadiusDp;
  }

  float getRemoveBubbleAlpha() {
    return removeBubbleAlpha;
  }

  FloatingBubbleActionListener getActionListener() {
    return actionListener;
  }

  public static final class Builder {
    private int bubbleIcon;
    private int removeBubbleIcon;
    private int expandableView;
    private int bubbleIconDp = 64;
    private int removeBubbleIconDp = 64;
    private int expandableColor = Color.WHITE;
    private int triangleColor = Color.WHITE;
    private int gravity = Gravity.END;
    private int paddingDp = 4;
    private int borderRadiusDp = 4;
    private float removeBubbleAlpha = 1.0f;
    private boolean physicsEnabled = true;
    private FloatingBubbleActionListener actionListener = null;

    public Builder() {
    }

    public Builder bubbleIcon(int val) {
      bubbleIcon = val;
      return this;
    }

    public Builder removeBubbleIcon(int val) {
      removeBubbleIcon = val;
      return this;
    }

    public Builder expandableView(int val) {
      expandableView = val;
      return this;
    }

    public Builder bubbleIconDp(int val) {
      bubbleIconDp = val;
      return this;
    }

    public Builder removeBubbleIconDp(int val) {
      removeBubbleIconDp = val;
      return this;
    }

    public Builder triangleColor(int val) {
      triangleColor = val;
      return this;
    }

    public Builder expandableColor(int val) {
      expandableColor = val;
      return this;
    }

    public Builder onActionListener(FloatingBubbleActionListener val) {
      actionListener = val;
      return this;
    }

    public FloatingBubbleConfig build() {
      return new FloatingBubbleConfig(this);
    }

    public Builder gravity(int val) {
      gravity = val;
      if (gravity == Gravity.CENTER ||
          gravity == Gravity.CENTER_VERTICAL ||
          gravity == Gravity.CENTER_HORIZONTAL) {
        gravity = Gravity.CENTER_HORIZONTAL;
      } else if (gravity == Gravity.TOP ||
          gravity == Gravity.BOTTOM) {
        gravity = Gravity.END;
      }
      return this;
    }

    public Builder paddingDp(int val) {
      paddingDp = val;
      return this;
    }

    public Builder borderRadiusDp(int val) {
      borderRadiusDp = val;
      return this;
    }

    public Builder physicsEnabled(boolean val) {
      physicsEnabled = val;
      return this;
    }

    Builder removeBubbleAlpha(float val) {
      removeBubbleAlpha = val;
      return this;
    }
  }
}
