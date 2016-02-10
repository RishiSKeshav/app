package com.rishi.app.app;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.TextView;
import com.rishi.app.app.FragmentFacebook;
import com.facebook.FacebookSdk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amitrajula on 2/9/16.
 */
public class Userbase extends AppCompatActivity {

    TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Displays Home Screen
        setContentView(R.layout.userbase);

         viewPager = (ViewPager) findViewById(R.id.userbase_viewpager);
       // viewPager.setAdapter(new UserbaseSampleFragmentPagerAdapter(getSupportFragmentManager(),
         //       Userbase.this));

        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.userbase_tabs);
        tabLayout.setupWithViewPager(viewPager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Users");

        setupTabIcons();

    }



    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentFacebook(),"One");
        adapter.addFrag(new ContactsFragment(), "TWO");
        adapter.addFrag(new MoreFragment(), "THREE");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.userbase_custom_tab, null);
        tabOne.setText("Facebook");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_photo_black_48dp, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.userbase_custom_tab, null);
        tabTwo.setText("Contacts");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_person_black_48dp, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.userbase_custom_tab, null);
        tabThree.setText("More Share");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_alarm_on_black_48dp, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
