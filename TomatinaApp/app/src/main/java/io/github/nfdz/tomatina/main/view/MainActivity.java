package io.github.nfdz.tomatina.main.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.nfdz.tomatina.R;
import io.github.nfdz.tomatina.historical.view.HistoricalFragment;
import io.github.nfdz.tomatina.home.view.HomeFragment;
import io.github.nfdz.tomatina.user.view.UserFragment;

public class MainActivity extends AppCompatActivity {

    private static final int HOME_TAB_POSITION = 0;
    private static final int USER_TAB_POSITION = 1;
    private static final int HISTORICAL_TAB_POSITION = 2;

    @BindView(R.id.main_activity_vp) ViewPager main_activity_vp;
    @BindView(R.id.main_activity_tl) TabLayout main_activity_tl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupView();
    }

    private void setupView() {
        setUpViewPager();
    }

    private void setUpViewPager() {
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        main_activity_vp.setAdapter(pagerAdapter);
        main_activity_tl.setupWithViewPager(main_activity_vp);
    }

    private class MainPagerAdapter extends FragmentStatePagerAdapter {

        MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case HOME_TAB_POSITION:
                    return HomeFragment.newInstance();
                case USER_TAB_POSITION:
                    return UserFragment.newInstance();
                case HISTORICAL_TAB_POSITION:
                    return HistoricalFragment.newInstance();
                default:
                    throw new IllegalArgumentException("Invalid tab position: " + position);

            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case HOME_TAB_POSITION:
                    return getString(R.string.home_tab);
                case USER_TAB_POSITION:
                    return getString(R.string.user_tab);
                case HISTORICAL_TAB_POSITION:
                    return getString(R.string.historical_tab);
                default:
                    throw new IllegalArgumentException("Invalid tab position: " + position);

            }
        }

    }

}
