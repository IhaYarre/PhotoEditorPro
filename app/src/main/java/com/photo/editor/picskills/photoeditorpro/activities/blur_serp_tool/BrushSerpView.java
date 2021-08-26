package com.photo.editor.picskills.photoeditorpro.activities.blur_serp_tool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class BrushSerpView extends View {
    BrushSerpSize brushSerpSize;
    boolean isBrushSize = true;
    float opacity;
    float ratioRadius;

    public BrushSerpView(Context context) {
        super(context);
        initMyView();
    }

    public BrushSerpView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initMyView();
    }

    public BrushSerpView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initMyView();
    }

    public void initMyView() {
        this.brushSerpSize = new BrushSerpSize();
    }


    public void onDraw(Canvas canvas) {
        float f;
        float f2;
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if (width != 0 && height != 0) {
            float f3 = ((float) width) / 2.0f;
            float f4 = ((float) height) / 2.0f;
            if (width > height) {
                f2 = this.ratioRadius;
                f = TouchImageView.resRatio;
            } else {
                f2 = this.ratioRadius;
                f = TouchImageView.resRatio;
            }
            float f5 = (f2 * f) / 2.0f;
            if (((int) f5) * 2 > 150) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
                int i = ((int) (2.0f * f5)) + 40;
                layoutParams.height = i;
                layoutParams.width = i;
                layoutParams.alignWithParent = true;
                setLayoutParams(layoutParams);
            }
            this.brushSerpSize.setCircle(f3, f4, f5, Path.Direction.CCW);
            canvas.drawPath(this.brushSerpSize.getPath(), this.brushSerpSize.getPaint());
            if (!this.isBrushSize) {
                canvas.drawPath(this.brushSerpSize.getPath(), this.brushSerpSize.getInnerPaint());
            }
        }
    }

    public void setShapeRadiusRatio(float f) {
        this.ratioRadius = f;
    }

    public void setShapeOpacity(float f) {
        this.opacity = f;
    }
}
