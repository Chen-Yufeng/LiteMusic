package swipedelmenu.mcxtzhang.litemusic.lrctextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import swipedelmenu.mcxtzhang.litemusic.R;
import swipedelmenu.mcxtzhang.litemusic.entity.LrcLine;

/**
 * Created by daily on 11/9/17.
 */

public class LrcTextView extends android.support.v7.widget.AppCompatTextView {
    private final String TAG = "@vir LrcTextView";
    private float hightlightSize;
    private float normalSize;
    private int hightLightColor;
    private int normalColor;
    private int halfViewW, halfViewH;
    private Paint paint;
    private List<LrcLine> mLrcLines;
    private long position;
    private int currentLine = 0;
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
    }

    public void setListAndPosition(ArrayList<LrcLine> lrcList, long position) {
        Log.d(TAG, "setListAndPosition: position="+position);
        mLrcLines = lrcList;
        this.position = position;
    }

    public void setPosition(long position) {
        this.position = position;
        invalidate();
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
//        currentline
        currentLine = 0;
        List<LrcLine> lyricBeans = new ArrayList<>();
        if (mLrcLines == null) {
            Log.e(TAG, "drawMutiLineText: ISNULL");
        }
        Log.d(TAG, "drawMutiLineText: size="+mLrcLines.size());
        LrcLine temp = mLrcLines.get(currentLine);
        if (mLrcLines.size() > 5) {
            if (position > mLrcLines.get(mLrcLines.size() - 3).getMillisecond()) {
                for (int i = mLrcLines.size() - 5; i <= mLrcLines.size() - 1; i++) {
                    lyricBeans.add(mLrcLines.get(i));
                }
                currentLine = 2;
                while (position > mLrcLines.get(mLrcLines.size() + currentLine - 6)
                        .getMillisecond()) {
                    currentLine++;
                }
            } else {
                while (position >= temp.getMillisecond()) {
                    temp = mLrcLines.get(++currentLine);
                }
                currentLine--;
                if (currentLine == 0) {
                    for (int i = 0; i < 5; i++) {
                        lyricBeans.add(mLrcLines.get(i));
                    }
                } else {
                    lyricBeans.add(mLrcLines.get(currentLine - 1));
                    lyricBeans.add(mLrcLines.get(currentLine));
                    lyricBeans.add(mLrcLines.get(currentLine + 1));
                    lyricBeans.add(mLrcLines.get(currentLine + 2));
                    lyricBeans.add(mLrcLines.get(currentLine + 3));
                }
                if (currentLine >= 5) {
                    currentLine = 2;
                }
            }
        } else {
            for(int i = 0;i < mLrcLines.size();i++) {
                lyricBeans.add(mLrcLines.get(i));
                if (position > mLrcLines.get(i).getMillisecond()) {
                    currentLine = i;
                }
            }
        }



        //获取高亮行Y 的位置
        Rect bounds = new Rect();
        //计算text的宽和高
        paint.getTextBounds(lyricBeans.get(0).getLrcText(), 0, lyricBeans.get(0).getLrcText()
                .length(), bounds);
        // int halfTextW=bounds.width()/2;
        int halfTextH = bounds.height() / 2;
//        int centerY = halfTextH + halfViewH;
        int centerY = halfViewH - halfTextH;

        for (int i = 0; i < lyricBeans.size(); i++) {
            if (i == currentLine) {
                paint.setColor(hightLightColor);
                paint.setTextSize(hightlightSize);
            } else {
                paint.setColor(normalColor);
                paint.setTextSize(normalSize);
            }

            // 绘制的Y位置=centerY+(当前行数-高亮行)*行高
            int drawY = centerY + (i - currentLine) * lyricHeight;
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
