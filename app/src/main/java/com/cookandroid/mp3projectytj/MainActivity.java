package com.cookandroid.mp3projectytj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements Fragment1.OnItemLongClickListener {
    private TabLayout tabLayout;
    private ViewPager2 pager;

    //음악리스트 정보
    private ArrayList<MusicData> arrayList = new ArrayList<>();
    private ArrayList<MusicData> likeList = new ArrayList<>();

    //프래그먼트 스테이트 어댑터
    private final static int NUM_PAGES = 2;
    private final static String[] tabElement = {"Total Music", "Like Music"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        pager = findViewById(R.id.pager);

        //Content Provider 통해서 음악파일을 가져와야 한다
        getMusicList();
        //DataBase에서 음악파일 insert를 진행한다.
        MyDBHelper myDBHelper = new MyDBHelper(this);
        boolean flag = myDBHelper.insertMusicDataAll(myDBHelper.getWritableDatabase(), arrayList);

        //DataBase에서 모든 리스트와 좋아요 리스트를 가져온다
        arrayList = myDBHelper.getTableAllMusicList(myDBHelper.getReadableDatabase());
        likeList = myDBHelper.getTableLikeMusicList(myDBHelper.getReadableDatabase());

        //프래그먼트 어댑터 생성 (FragmentStateAdapter)
        ScreeSlidePagerAdapter screeSlidePagerAdapter = new ScreeSlidePagerAdapter(this);
        pager.setAdapter(screeSlidePagerAdapter);

        //Tablayout 과 프래그먼트 어탭터를 연결
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                TextView textView = new TextView(MainActivity.this);
                textView.setGravity(Gravity.CENTER);
                textView.setText(tabElement[position]);
                tab.setCustomView(textView);
            }
        });
        tabLayoutMediator.attach();
    }

    //Content Provider에서 contentResolver를 이용해서 음악파일을 가져와야 한다
    private void getMusicList() {
        //퍼미션허용요청
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                MODE_PRIVATE);
        Cursor cursor = null;
        try {
            //contentResolver를 이용해서 음악파일을 가져온다(아이디,앨범아이디,타이틀,아티스트)
            cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Media._ID,
                            MediaStore.Audio.Media.ALBUM_ID,
                            MediaStore.Audio.Media.TITLE,
                            MediaStore.Audio.Media.ARTIST}, null, null, null);
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String albumId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

                MusicData musicData = new MusicData(id, albumId, title, artist, "no");
                arrayList.add(musicData);
            }
        } catch (Exception e) {
            Log.d("음악플레이어", "getMusicList() 외부에서 음악파일가져오기 오류" + e.toString());
        } finally {
            cursor.close();
        }
    }

    @Override
    public void onItemLongClick(int position) {
        Log.e("음악플레이어", "인터페이스 프래그먼트에서 액티비로 전달방법 " + position);
    }

    private class ScreeSlidePagerAdapter extends FragmentStateAdapter {

        public ScreeSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                Fragment fragment1 = new Fragment1(MainActivity.this);
                Bundle bundle = new Bundle();
                bundle.putSerializable("arrayList", arrayList);
                fragment1.setArguments(bundle);
                return fragment1;

            } else if (position == 1) {
                Fragment fragment2 = new Fragment2(MainActivity.this);
                Bundle bundle = new Bundle();
                bundle.putSerializable("likeList", likeList);
                fragment2.setArguments(bundle);
                return fragment2;
            } else {
                Log.d("음악플레이어", "createFragment() 프레그먼트 생성 오류");
                return null;
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

}