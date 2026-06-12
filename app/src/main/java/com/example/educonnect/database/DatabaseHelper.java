//package com.example.educonnect.database;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import com.example.educonnect.model.Post;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class DatabaseHelper extends SQLiteOpenHelper {
//
//    private static final String DATABASE_NAME = "educonnect.db";
//    private static final int DATABASE_VERSION = 1;
//
//    public static final String TABLE_BOOKMARK = "bookmarks";
//    public static final String TABLE_CACHE    = "posts_cache";
//    public static final String COL_ID         = "id";
//    public static final String COL_TITLE      = "title";
//    public static final String COL_BODY       = "body";
//    public static final String COL_USER_ID    = "user_id";
//
//    private static DatabaseHelper instance;
//
//    public static synchronized DatabaseHelper getInstance(Context context) {
//        if (instance == null) {
//            instance = new DatabaseHelper(context.getApplicationContext());
//        }
//        return instance;
//    }
//
//    private DatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        String createBookmark =
//                "CREATE TABLE " + TABLE_BOOKMARK + " ("
//                        + COL_ID      + " INTEGER PRIMARY KEY, "
//                        + COL_TITLE   + " TEXT, "
//                        + COL_BODY    + " TEXT, "
//                        + COL_USER_ID + " INTEGER)";
//
//        String createCache =
//                "CREATE TABLE " + TABLE_CACHE + " ("
//                        + COL_ID      + " INTEGER PRIMARY KEY, "
//                        + COL_TITLE   + " TEXT, "
//                        + COL_BODY    + " TEXT, "
//                        + COL_USER_ID + " INTEGER)";
//
//        db.execSQL(createBookmark);
//        db.execSQL(createCache);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARK);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CACHE);
//        onCreate(db);
//    }
//
//    // ── BOOKMARK ──────────────────────────────────
//
//    public boolean addBookmark(Post post) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COL_ID,      post.getId());
//        values.put(COL_TITLE,   post.getTitle());
//        values.put(COL_BODY,    post.getBody());
//        values.put(COL_USER_ID, post.getUserId());
//        long result = db.insertWithOnConflict(
//                TABLE_BOOKMARK, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//        return result != -1;
//    }
//
//    public boolean removeBookmark(int postId) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        int rows = db.delete(TABLE_BOOKMARK,
//                COL_ID + "=?", new String[]{String.valueOf(postId)});
//        return rows > 0;
//    }
//
//    public boolean isBookmarked(int postId) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_BOOKMARK,
//                new String[]{COL_ID},
//                COL_ID + "=?",
//                new String[]{String.valueOf(postId)},
//                null, null, null);
//        boolean exists = cursor.getCount() > 0;
//        cursor.close();
//        return exists;
//    }
//
//    public List<Post> getAllBookmarks() {
//        List<Post> list = new ArrayList<>();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_BOOKMARK,
//                null, null, null, null, null, null);
//        if (cursor.moveToFirst()) {
//            do {
//                Post post = new Post();
//                post.setId(cursor.getInt(
//                        cursor.getColumnIndexOrThrow(COL_ID)));
//                post.setTitle(cursor.getString(
//                        cursor.getColumnIndexOrThrow(COL_TITLE)));
//                post.setBody(cursor.getString(
//                        cursor.getColumnIndexOrThrow(COL_BODY)));
//                post.setUserId(cursor.getInt(
//                        cursor.getColumnIndexOrThrow(COL_USER_ID)));
//                post.setBookmarked(true);
//                list.add(post);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return list;
//    }
//
//    // ── CACHE (offline) ───────────────────────────
//
//    public void cachePosts(List<Post> posts) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.beginTransaction();
//        try {
//            db.delete(TABLE_CACHE, null, null);
//            for (Post post : posts) {
//                ContentValues values = new ContentValues();
//                values.put(COL_ID,      post.getId());
//                values.put(COL_TITLE,   post.getTitle());
//                values.put(COL_BODY,    post.getBody());
//                values.put(COL_USER_ID, post.getUserId());
//                db.insertWithOnConflict(TABLE_CACHE, null,
//                        values, SQLiteDatabase.CONFLICT_REPLACE);
//            }
//            db.setTransactionSuccessful();
//        } finally {
//            db.endTransaction();
//        }
//    }
//
//    public List<Post> getCachedPosts() {
//        List<Post> list = new ArrayList<>();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_CACHE,
//                null, null, null, null, null, null);
//        if (cursor.moveToFirst()) {
//            do {
//                Post post = new Post();
//                post.setId(cursor.getInt(
//                        cursor.getColumnIndexOrThrow(COL_ID)));
//                post.setTitle(cursor.getString(
//                        cursor.getColumnIndexOrThrow(COL_TITLE)));
//                post.setBody(cursor.getString(
//                        cursor.getColumnIndexOrThrow(COL_BODY)));
//                post.setUserId(cursor.getInt(
//                        cursor.getColumnIndexOrThrow(COL_USER_ID)));
//                list.add(post);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return list;
//    }
//}

package com.example.educonnect.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.educonnect.model.Post;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "educonnect.db";
    private static final int DATABASE_VERSION = 2; // Ubah ke 2 agar onCreate dipanggil lagi

    public static final String TABLE_BOOKMARK = "bookmarks";
    public static final String TABLE_CACHE    = "posts_cache";
    public static final String TABLE_USER     = "users"; // Tabel baru

    public static final String COL_ID         = "id";
    public static final String COL_TITLE      = "title";
    public static final String COL_BODY       = "body";
    public static final String COL_USER_ID    = "user_id";

    // Kolom untuk user
    public static final String COL_EMAIL      = "email";
    public static final String COL_PASSWORD   = "password";
    public static final String COL_NAME       = "name";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createBookmark =
                "CREATE TABLE " + TABLE_BOOKMARK + " ("
                        + COL_ID      + " INTEGER PRIMARY KEY, "
                        + COL_TITLE   + " TEXT, "
                        + COL_BODY    + " TEXT, "
                        + COL_USER_ID + " INTEGER)";

        String createCache =
                "CREATE TABLE " + TABLE_CACHE + " ("
                        + COL_ID      + " INTEGER PRIMARY KEY, "
                        + COL_TITLE   + " TEXT, "
                        + COL_BODY    + " TEXT, "
                        + COL_USER_ID + " INTEGER)";

        String createUserTable =
                "CREATE TABLE " + TABLE_USER + " ("
                        + COL_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COL_NAME     + " TEXT, "
                        + COL_EMAIL    + " TEXT UNIQUE, "
                        + COL_PASSWORD + " TEXT)";

        db.execSQL(createBookmark);
        db.execSQL(createCache);
        db.execSQL(createUserTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CACHE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // ── AUTENTIKASI ──────────────────────────────

    public long registerUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        return db.insert(TABLE_USER, null, values);
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER,
                new String[]{COL_ID},
                COL_EMAIL + "=? AND " + COL_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    // ── BOOKMARK ──────────────────────────────────

    public boolean addBookmark(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ID,      post.getId());
        values.put(COL_TITLE,   post.getTitle());
        values.put(COL_BODY,    post.getBody());
        values.put(COL_USER_ID, post.getUserId());
        long result = db.insertWithOnConflict(
                TABLE_BOOKMARK, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public boolean removeBookmark(int postId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_BOOKMARK,
                COL_ID + "=?", new String[]{String.valueOf(postId)});
        return rows > 0;
    }

    public boolean isBookmarked(int postId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKMARK,
                new String[]{COL_ID},
                COL_ID + "=?",
                new String[]{String.valueOf(postId)},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public List<Post> getAllBookmarks() {
        List<Post> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKMARK,
                null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Post post = new Post();
                post.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                post.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
                post.setBody(cursor.getString(cursor.getColumnIndexOrThrow(COL_BODY)));
                post.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)));
                post.setBookmarked(true);
                list.add(post);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // ── CACHE ───────────────────────────

    public void cachePosts(List<Post> posts) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_CACHE, null, null);
            for (Post post : posts) {
                ContentValues values = new ContentValues();
                values.put(COL_ID,      post.getId());
                values.put(COL_TITLE,   post.getTitle());
                values.put(COL_BODY,    post.getBody());
                values.put(COL_USER_ID, post.getUserId());
                db.insertWithOnConflict(TABLE_CACHE, null,
                        values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<Post> getCachedPosts() {
        List<Post> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CACHE,
                null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Post post = new Post();
                post.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                post.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
                post.setBody(cursor.getString(cursor.getColumnIndexOrThrow(COL_BODY)));
                post.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)));
                list.add(post);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}