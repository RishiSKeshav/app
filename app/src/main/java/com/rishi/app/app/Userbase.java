package com.rishi.app.app;

import android.content.Intent;
import android.hardware.camera2.params.Face;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private FragmentFacebookAdapter fAdapter;
    ArrayList<Integer> mediaIDS = new ArrayList<>();
    private ArrayList<AlbumMedia> albummediaList = new ArrayList<>();
    String ID,NAME,ACTION,ALBUM_NAME,SHARED,imagedisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.userbase);

        Toolbar toolbar= (Toolbar) findViewById(R.id.userbase_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        ACTION = i.getStringExtra("action");

        if(ACTION.equals("create_shared_album"))
        {
            ALBUM_NAME = i.getStringExtra("album_name");
            mediaIDS = i.getIntegerArrayListExtra("mediaId");
        }

        if (ACTION.equals("to_others")){
            mediaIDS = i.getIntegerArrayListExtra("mediaId");
            ID = i.getStringExtra("Id");
            NAME = i.getStringExtra("Name");
            SHARED = i.getStringExtra("shared");
            if(SHARED.equals("no")){
                albummediaList = i.getParcelableArrayListExtra("al");
            }
        }

        if(ACTION.equals("add_user")){
            ID = i.getStringExtra("Id");
            NAME = i.getStringExtra("Name");
            SHARED = i.getStringExtra("shared");
        }

        if(ACTION.equals("shared_media")){
            ID = i.getStringExtra("Id");
            imagedisplay = i.getStringExtra("imagedisplay");
            mediaIDS = i. getIntegerArrayListExtra("mediaId");

        }

         viewPager = (ViewPager) findViewById(R.id.userbase_viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.userbase_tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {

//            if(ACTION.equals("create_shared_album")){
//                Intent i = new Intent(Userbase.this,NewAlbum.class);
//                i.putExtra("shared","yes");
//                Userbase.this.startActivity(i);
//            }else if(ACTION.equals("add_user")){
//
//                Intent i = new Intent(Userbase.this,SharedAlbumMediaDisplay.class);
//                i.putExtra("Id",ID);
//                i.putExtra("Name",NAME);
//                Userbase.this.startActivity(i);
//
//            }else if(ACTION.equals("shared_media")){
//                Intent i = new Intent(Userbase.this,SharedMediaDisplay.class);
//                i.putExtra("Id",ID);
//                i.putExtra("image",imagedisplay);
//                Userbase.this.startActivity(i);
//            }
//            else{
//                if(SHARED.equals("no")) {
//                    Intent i = new Intent(Userbase.this, AlbumMediaSelect.class);
//                    i.putParcelableArrayListExtra("al", albummediaList);
//                    i.putExtra("id", ID);
//                    i.putExtra("name", NAME);
//                    Userbase.this.startActivity(i);
//                }else{
//                    Intent i = new Intent(Userbase.this, SharedAlbumMediaSelect.class);
//                    i.putExtra("Id", ID);
//                    i.putExtra("Name", NAME);
//                    Userbase.this.startActivity(i);
//                }
//            }

            onBackPressed();
            return true;


        }

        return super.onOptionsItemSelected(item);
    }



    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentFacebook(),"One");
        adapter.addFrag(new ContactsFragment(), "TWO");
       // adapter.addFrag(new MoreFragment(), "THREE");
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

//        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.userbase_custom_tab, null);
//        tabThree.setText("More Share");
//        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_alarm_on_black_48dp, 0, 0);
//        tabLayout.getTabAt(2).setCustomView(tabThree);
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

            Bundle bundle = new Bundle();

            if(ACTION.equals("create_shared_album"))
            {
                bundle.putString("action",ACTION);
                bundle.putString("album_name", ALBUM_NAME);
                bundle.putIntegerArrayList("mediaId", mediaIDS);
                fragment.setArguments(bundle);
            }

            if(ACTION.equals("to_others"))
            {
                bundle.putString("action",ACTION);
                bundle.putString("Name", NAME);
                bundle.putString("Id",ID);
                bundle.putIntegerArrayList("mediaId",mediaIDS);
                bundle.putString("shared",SHARED);
                fragment.setArguments(bundle);
            }

            if(ACTION.equals("add_user")){

                bundle.putString("action",ACTION);
                bundle.putString("Name", NAME);
                bundle.putString("Id",ID);
                bundle.putString("shared",SHARED);
                fragment.setArguments(bundle);
            }

            if(ACTION.equals("shared_media")) {
                bundle.putString("action",ACTION);
                bundle.putIntegerArrayList("mediaId",mediaIDS);
                bundle.putString("Id",ID);
                bundle.putString("imagedisplay",imagedisplay);
                fragment.setArguments(bundle);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
