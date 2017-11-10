package swipedelmenu.mcxtzhang.litemusic;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class SwipedPagesActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{
    private final String TAG = "@vir SwipedPages";
    private List<View> viewContainer;
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiped_pages);
        LayoutInflater layoutInflater = getLayoutInflater().from(SwipedPagesActivity.this);
        View page1 = layoutInflater.inflate(R.layout.page1, null);
        View page2 = layoutInflater.inflate(R.layout.page2, null);
        View page3 = layoutInflater.inflate(R.layout.page3, null);
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


    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset == 0.0) {
            Log.d(TAG, "onPageScrolled: positionOffset="+positionOffset);
            if (position == 0) {
                //动画会跳动
                pager.setCurrentItem(1);
            } else if(position == 2){
                pager.setCurrentItem(1);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
