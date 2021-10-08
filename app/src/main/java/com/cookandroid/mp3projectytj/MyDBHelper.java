package com.cookandroid.mp3projectytj;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyDBHelper extends SQLiteOpenHelper {

    public MyDBHelper(@Nullable Context context) {
        super(context, "MusicDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("Create table if not exists musicTBL(" +
                "id Text primary key," +
                "albumId Text," +
                "title Text," +
                "artist Text," +
                "myLike Text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("drop table if exists musicTBL;");
        onCreate(sqLiteDatabase);
    }
    
    //음악 테이블에 데이터 넣기
    public boolean insertMusicDataAll(SQLiteDatabase sqLiteDatabase, ArrayList<MusicData> arrayList) {
        boolean flag = false;
        try {
            for (MusicData md : arrayList) {
                md.setTitle(md.getTitle().replaceAll("'", "''"));
                md.setArtist(md.getArtist().replaceAll("'", "''"));

                String data = String.format("insert into musicTBL values('%s','%s','%s','%s','%s');",
                        md.getId(), md.getAlbumId(), md.getTitle(), md.getArtist(), md.getOk());
                sqLiteDatabase.execSQL(data);
            }
            flag = true;
        } catch (Exception e) {
            Log.d("음악플레이어", "insertMusicDataAll() 전체음악파일 삽입 오류" + e.toString());
        } finally {
            sqLiteDatabase.close();
        }
        return flag;
    }
    
    //좋아요 음악 목록 테이블
    public ArrayList<MusicData> getTableLikeMusicList(SQLiteDatabase readableDatabase) {

        Cursor cursor = null;
        ArrayList<MusicData> arrayList = new ArrayList<>();
        try {
            cursor = readableDatabase.rawQuery("SELECT * FROM musicTBL where myLike = 'yes';", null);

            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String albumId = cursor.getString(1);
                String title = cursor.getString(2);
                String artist = cursor.getString(3);
                String ok = cursor.getString(4);
                MusicData musicData = new MusicData(id, albumId, title, artist, ok);
                arrayList.add(musicData);
            }
        } catch (Exception e) {
            Log.d("음악플레이어", "getTableLikeMusicList 좋아요 음악파일 가져오기 오류" + e.toString());
        } finally {
            cursor.close();
            readableDatabase.close();
        }
        return arrayList;
    }
    //전체 음악 목록 테이블
    public ArrayList<MusicData> getTableAllMusicList(SQLiteDatabase readableDatabase) {
        Cursor cursor = null;
        ArrayList<MusicData> arrayList = new ArrayList<>();
        try {
            cursor = readableDatabase.rawQuery("SELECT * FROM musicTBL;", null);

            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String albumId = cursor.getString(1);
                String title = cursor.getString(2);
                String artist = cursor.getString(3);
                String ok = cursor.getString(4);
                MusicData musicData = new MusicData(id, albumId, title, artist, ok);
                arrayList.add(musicData);
            }
        } catch (Exception e) {
            Log.d("음악플레이어", "getTableAllMusicList 음악파일 가져오기 오류" + e.toString());
        } finally {
            cursor.close();
            readableDatabase.close();
        }
        return arrayList;
    }
    //좋아요 수정 테이블
    public void updateMusicTBLMyLike(SQLiteDatabase sqLiteDatabase, String id, String myLike) {

        try {
            String data = String.format("update musicTBL set myLike = '%s' where id ='%s';", myLike, id);
            sqLiteDatabase.execSQL(data);
        } catch (Exception e) {
            Log.d("음악플레이어", "insertMusicDataAll() 전체음악파일 삽입 오류" + e.toString());
        } finally {
            sqLiteDatabase.close();
        }
    }
}
