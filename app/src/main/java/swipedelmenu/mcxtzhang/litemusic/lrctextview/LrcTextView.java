package swipedelmenu.mcxtzhang.litemusic.lrctextview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import swipedelmenu.mcxtzhang.litemusic.R;
import swipedelmenu.mcxtzhang.litemusic.entity.LrcLine;

/**
 * Created by daily on 11/9/17.
 */

public class LrcTextView extends android.support.v7.widget.AppCompatTextView {
    private float hightlightSize;
    private float normalSize;
    private int hightLightColor;
    private int normalColor;
    private int halfViewW, halfViewH;
    private Paint paint;
    private List<LrcLine> mLrcLines = new ArrayList<>();
    private int currentLine;
    private int lyricHeight;

    public LrcTextView(Context context) {
        super(context);
        initialize();
    }

    public LrcTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public LrcTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        hightlightSize = getResources().getDimension(R.dimen.lrc_hightlight_size);
        normalSize = getResources().getDimension(R.dimen.lrc_normal_size);
        hightLightColor = Color.GRAY;
        normalColor = Color.GRAY;
        lyricHeight = getResources().getDimensionPixelOffset(R.dimen.lyric_hieght);//行高
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(hightlightSize);
        paint.setColor(hightLightColor);

        for (int i = 0; i < 100; i++) {
            mLrcLines.add(new LrcLine(i, Integer.toString(i)));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        halfViewW = w / 2;
        halfViewH = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Rect bounds = new Rect();
//
//        int halfTextW = bounds.width() / 2;
//        int halfTextH = bounds.height() / 2;
//        int drawX = halfViewW - halfTextW;
//        int drawY = halfTextH + halfViewH;
//
//        canvas.drawText("loading", drawX, drawY, paint);
        drawMutiLineText(canvas);
    }

    private void drawMutiLineText(Canvas canvas) {
        currentLine = 5;
        List<LrcLine> lyricBeans = new ArrayList<>();
        lyricBeans.add(mLrcLines.get(0));
        lyricBeans.add(mLrcLines.get(1));
        lyricBeans.add(mLrcLines.get(2));
        lyricBeans.add(mLrcLines.get(3));
        lyricBeans.add(mLrcLines.get(4));
        //获取高亮行Y 的位置
        Rect bounds = new Rect();
        //计算text的宽和高
        paint.getTextBounds(lyricBeans.get(0).getLrcText(), 0, lyricBeans.get(0).getLrcText().length(),
                bounds);
        // int halfTextW=bounds.width()/2;
        int halfTextH = bounds.height() / 2;
        int centerY = halfTextH + halfViewH;

        for (int i = 0; i < lyricBeans.size(); i++) {
            if (i == currentLine) {
                paint.setColor(hightLightColor);
                paint.setTextSize(hightlightSize);
            } else {
                paint.setColor(normalColor);
                paint.setTextSize(normalSize);
            }

            // 绘制的Y位置=centerY+(当前行数-高亮行)*行高
            int drawY = (int) (centerY + (i - currentLine) * lyricHeight);
            drawHorizontal(canvas, lyricBeans.get(i).getLrcText(), drawY);
        }

    }

    private void drawHorizontal(Canvas canvas, String text, int drawY) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        int halfTextWidth = bounds.width() / 2;
        int drawX = halfViewW - halfTextWidth;
        canvas.drawText(text, drawX, drawY, paint);

    }
}
