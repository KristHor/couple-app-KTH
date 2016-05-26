package com.couple.kristjanthor.appforcouple;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

public class DAO {
	
	private static final String DATABASE_NAME = "couple.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "Couple";

	private Context context;
	
	private SQLiteDatabase db;
	private SQLiteStatement insertStmt;
	private static final String INSERT = "insert into " + TABLE_NAME
			+ "(message) values (?)";

	public DAO(Context context) {
		this.context = context;
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		this.insertStmt = this.db.compileStatement(INSERT);
	}

	public long insert(BECouple p) {
		this.insertStmt.bindString(1, p.m_message);
		
		return this.insertStmt.executeInsert();
	}

	public void deleteAll() {
		this.db.delete(TABLE_NAME, null, null);
	}

	public List<BECouple> selectAll() {
		List<BECouple> list = new ArrayList<BECouple>();
		Cursor cursor = this.db.query(TABLE_NAME, new String[] { "id", "message" },
				null, null, null, null, "message asc");
		if (cursor.moveToFirst()) {
			do {
				list.add(new BECouple(cursor.getInt(0), cursor.getString(1)));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context)
        {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_NAME
					+ "(id INTEGER PRIMARY KEY, message TEXT)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db,
				              int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}

}
