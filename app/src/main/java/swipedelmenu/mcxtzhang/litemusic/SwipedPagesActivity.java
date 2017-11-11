package swipedelmenu.mcxtzhang.litemusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import swipedelmenu.mcxtzhang.litemusic.entity.LrcLine;
import swipedelmenu.mcxtzhang.litemusic.lrctextview.LrcTextView;

public class SwipedPagesActivity extends AppCompatActivity implements ViewPager
        .OnPageChangeListener {
    private final String TAG = "@vir SwipedPages";
    private List<View> viewContainer;
    ViewPager pager;
    ArrayList<LrcLine> lrcList;
    long position;
    private ImageButton mImageButton;
    private ImageView mImageView;
    private AudioInfo mAudioInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiped_pages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        Intent intent_receive = getIntent();
        lrcList = (ArrayList<LrcLine>) intent_receive.getSerializableExtra(getResources()
                .getString(R.string.intent_lrclist));
        position = intent_receive.getIntExtra(getResources().getString(R.string.player_position)
                ,0);
        mAudioInfo = (AudioInfo) intent_receive.getSerializableExtra(getResources().getString(R.string
                .intent_audio_info));
        toolbar.setTitle(mAudioInfo.getTitle());
        toolbar.setSubtitle(mAudioInfo.getName()+" "+mAudioInfo.getAlbum());
        LayoutInflater layoutInflater = getLayoutInflater().from(SwipedPagesActivity.this);
        View page1 = layoutInflater.inflate(R.layout.page1, null);
        View page2 = layoutInflater.inflate(R.layout.page2, null);
        View page3 = layoutInflater.inflate(R.layout.page3, null);
        final LrcTextView lrcTextView = page2.findViewById(R.id.lrc_text_view);
        lrcTextView.setVisibility(View.INVISIBLE);
        lrcTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lrcTextView.getVisibility() == View.INVISIBLE) {
                    lrcTextView.setVisibility(View.VISIBLE);
                    mImageView.setVisibility(View.GONE);
                    mImageView.clearAnimation();
                    mImageButton.setVisibility(View.INVISIBLE);
                } else {
                    lrcTextView.setVisibility(View.INVISIBLE);
                    mImageView.setVisibility(View.VISIBLE);
                    mImageButton.setVisibility(View.VISIBLE);
                }
            }
        });
        mImageView = page2.findViewById(R.id.pointer);
        mImageView.setVisibility(View.VISIBLE);
        mImageButton = page2.findViewById(R.id.disk);
        mImageButton.setVisibility(View.VISIBLE);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lrcTextView.getVisibility() == View.INVISIBLE) {
                    lrcTextView.setVisibility(View.VISIBLE);
                    mImageView.setVisibility(View.GONE);
                    mImageView.clearAnimation();
                    mImageButton.setVisibility(View.INVISIBLE);
                } else {
                    lrcTextView.setVisibility(View.INVISIBLE);
                    mImageView.setVisibility(View.VISIBLE);
                    mImageButton.setVisibility(View.VISIBLE);
                }
            }
        });
        lrcTextView.setListAndPosition(lrcList,position);
        viewContainer = new ArrayList<>();
        viewContainer.add(page1);
        viewContainer.add(page2);
        viewContainer.add(page3);
        pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setOnPageChangeListener(this);
        pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewContainer.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewContainer.get(position));
                return viewContainer.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
//                super.destroyItem(container, position, object);  //may be wrong!!!
                container.removeView(viewContainer.get(position));
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
        pager.setCurrentItem(1);

        registerReceiver(new BroadcastReceiver() {
            boolean bl = false;
            @Override
            public void onReceive(Context context, Intent intent) {
                position = intent.getIntExtra(getResources().getString(R.string.loop_position),0);
//                if (bl) {
//                    lrcTextView.setPosition(position);
//                } else {
//                    bl = true;
//                }
//                viewContainer.remove(1);
//                LayoutInflater layoutInflater = getLayoutInflater().from(SwipedPagesActivity.this);
//                View page2 = layoutInflater.inflate(R.layout.page2, null);
//                final LrcTextView lrcTextView = page2.findViewById(R.id.lrc_text_view);
//                lrcTextView.setListAndPosition(lrcList,position);
//                viewContainer.add(1,page2);
//                pager.setCurrentItem(1);
            }
        },new IntentFilter(getResources().getString(R.string.loop_broadcast_to_pager)));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset == 0.0) {
            Log.d(TAG, "onPageScrolled: positionOffset=" + positionOffset);
            if (position == 0) {
                //动画会跳动
                pager.setCurrentItem(1);
            } else if (position == 2) {
                pager.setCurrentItem(1);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Animation animation = AnimationUtils.loadAnimation(SwipedPagesActivity.this,R.anim
                .pointer_anim);
        Animation animationReset = AnimationUtils.loadAnimation(SwipedPagesActivity.this,R.anim
                .pointer_anim_reset);
        animation.setFillAfter(true);
        animationReset.setFillAfter(true);
        if (state == ViewPager.SCROLL_STATE_DRAGGING) {
            mImageView.startAnimation(animation);
        } else if (state == ViewPager.SCROLL_STATE_IDLE) {
            mImageView.startAnimation(animationReset);
        }

    }
}
