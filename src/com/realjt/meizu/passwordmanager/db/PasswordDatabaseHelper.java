package com.realjt.meizu.passwordmanager.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.realjt.meizu.passwordmanager.R;
import com.realjt.meizu.passwordmanager.common.Constants;
import com.realjt.meizu.passwordmanager.model.Classification;
import com.realjt.meizu.passwordmanager.model.Password;
import com.realjt.meizu.passwordmanager.utils.DateUtils;
import com.realjt.meizu.passwordmanager.utils.EncryptUtils;

/**
 * 数据库助手类,使用单例模式,其中表Passwords的键为createtime
 * 
 * @author Administrator
 * 
 */
public class PasswordDatabaseHelper extends SQLiteOpenHelper
{
	/**
	 * 数据库版本
	 */
	public static final int SQLDATABASE_VERSION = 2;

	/**
	 * 数据库名
	 */
	private static final String SQLDATABASE_NAME = "PasswordManager";

	/**
	 * 表名
	 */
	private static final String TABLE_NAME = "PasswordInfo";

	/**
	 * 数据库Helper
	 */
	private static PasswordDatabaseHelper databaseHelper;

	/**
	 * 数据库
	 */
	private static SQLiteDatabase dataBase;

	private Context context;

	private PasswordDatabaseHelper(Context context)
	{
		this(context, SQLDATABASE_NAME, null, SQLDATABASE_VERSION);
	}

	private PasswordDatabaseHelper(Context context, String name,
			CursorFactory cursorFactory, int version)
	{
		super(context, name, cursorFactory, version);

		this.context = context;
	}

	/**
	 * 数据库助手类对象初始化
	 * 
	 * @param context
	 *            上下文对象
	 * @return
	 */
	public static PasswordDatabaseHelper getInstance(Context context)
	{
		if (null == databaseHelper)
		{
			databaseHelper = new PasswordDatabaseHelper(context);
		}

		if (null == dataBase)
		{
			dataBase = databaseHelper.getWritableDatabase();
		}

		return databaseHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase)
	{
		// String initSql =
		// "CREATE TABLE Passwords(name TEXT,account TEXT,password TEXT,remarks TEXT,createtime TEXT PRIMARY KEY,revisetime TEXT,classification TEXT)";
		String initSql = "CREATE TABLE PasswordInfo(_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,account TEXT,password TEXT,remarks TEXT,createtime TEXT,revisetime TEXT,classification TEXT)";

		sqLiteDatabase.execSQL(initSql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion,
			int newVersion)
	{
		if (1 == oldVersion && SQLDATABASE_VERSION == newVersion)
		{
			sqLiteDatabase
					.execSQL("CREATE TABLE PasswordInfo(_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,account TEXT,password TEXT,remarks TEXT,createtime TEXT,revisetime TEXT,classification TEXT)");

			upgradeData(sqLiteDatabase);

			sqLiteDatabase.execSQL("DROP TABLE Passwords");
		}
	}

	/**
	 * 数据割接
	 * 
	 * @param sqLiteDatabase
	 */
	private void upgradeData(SQLiteDatabase sqLiteDatabase)
	{
		Cursor cursor = sqLiteDatabase.query("Passwords", new String[] {
				"name", "account", "password", "remarks", "createtime",
				"revisetime", "classification" }, null, null, null, null,
				"name");

		while (cursor.moveToNext())
		{
			// 名称未加密
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String account = cursor.getString(cursor.getColumnIndex("account"));
			String password = cursor.getString(cursor
					.getColumnIndex("password"));
			String remarks = cursor.getString(cursor.getColumnIndex("remarks"));
			String createtime = cursor.getString(cursor
					.getColumnIndex("createtime"));
			String revisetime = cursor.getString(cursor
					.getColumnIndex("revisetime"));
			String classification = EncryptUtils.decrypt(
					cursor.getString(cursor.getColumnIndex("classification")),
					Constants.LOGIN_PASSWORD);

			ContentValues contentValues = new ContentValues();
			// 名称不加密
			contentValues.put("name", name);
			contentValues.put("account", account);
			contentValues.put("password", password);
			contentValues.put("remarks", remarks);
			contentValues.put("createtime", createtime);
			contentValues.put("revisetime", revisetime);
			contentValues.put("classification", classification);

			sqLiteDatabase.insert("PasswordInfo", null, contentValues);
		}

		cursor.close();
	}

	/**
	 * 插入一条数据
	 * 
	 * @param password
	 */
	public void insertPassword(Password password)
	{
		if (password != null)
		{
			ContentValues contentValues = getContentValues(password);

			dataBase.insert(TABLE_NAME, null, contentValues);
		}
	}

	/**
	 * 删除一条数据
	 * 
	 * @param password
	 */
	public void deletePassword(Password password)
	{
		if (password != null)
		{
			dataBase.delete(TABLE_NAME, "_id=?",
					new String[] { String.valueOf(password.getId()) });
		}
	}

	/**
	 * 删除所有数据
	 */
	public void deleteAllPassword()
	{
		dataBase.delete(TABLE_NAME, null, null);
	}

	/**
	 * 更新一条数据
	 * 
	 * @param password
	 */
	public void updatePassword(Password password)
	{
		if (null != password)
		{
			ContentValues contentValues = getContentValues(password);

			dataBase.update(TABLE_NAME, contentValues, "_id=?",
					new String[] { String.valueOf(password.getId()) });
		}
	}

	/**
	 * 根据主键查询数据
	 * 
	 * @param createTime
	 *            主键 创建时间
	 * @return null或Password对象
	 */
	public Password queryPasswordById(int id)
	{
		Password password = null;

		Cursor cursor = dataBase.query(TABLE_NAME, new String[] { "_id",
				"name", "account", "password", "remarks", "createtime",
				"revisetime", "classification" }, "_id=?",
				new String[] { String.valueOf(id) }, null, null, null);

		if (cursor.moveToFirst())
		{
			password = setPassword(cursor);
		}

		cursor.close();

		return password;
	}

	/**
	 * 根据主键查询数据
	 * 
	 * @param createTime
	 *            主键 创建时间
	 * @return null或Password对象
	 */
	public Password queryPasswordByCreateTime(String createTime)
	{
		if (null == createTime || "".equals(createTime))
		{
			return null;
		}

		Password password = null;

		Cursor cursor = dataBase.query(TABLE_NAME, new String[] { "_id",
				"name", "account", "password", "remarks", "createtime",
				"revisetime", "classification" }, "createtime=?",
				new String[] { EncryptUtils.encrypt(createTime,
						Constants.LOGIN_PASSWORD) }, null, null, null);

		if (cursor.moveToFirst())
		{
			password = setPassword(cursor);
		}

		cursor.close();

		return password;
	}

	/**
	 * 模式匹配查询
	 * 
	 * @param name
	 *            名称
	 * @param classification
	 *            分类,查询全部则传入-1
	 * @return 密码对象列表
	 */
	public List<Password> queryPasswordLike(String name, int classification)
	{
		if (null != name && !"".equals(name))
		{
			Cursor cursor = null;
			if (-1 == classification)
			{
				cursor = dataBase.query(TABLE_NAME, new String[] { "_id",
						"name", "account", "createtime", }, "name like ?",
						new String[] { "%" + name + "%" }, null, null, "name");
			} else
			{
				cursor = dataBase
						.query(TABLE_NAME,
								new String[] { "_id", "name", "account",
										"createtime" },
								"name like ? and classification = ?",
								new String[] { "%" + name + "%",
										String.valueOf(classification) }, null,
								null, "name");
			}

			List<Password> passwordList = new ArrayList<Password>(
					cursor.getCount());

			while (cursor.moveToNext())
			{
				Password password = setSimplePassword(cursor);
				if (null != password)
				{
					passwordList.add(password);
				}
			}

			cursor.close();

			return passwordList;
		} else
		{
			return new ArrayList<Password>(0);
		}
	}

	/**
	 * 查询得到表Passwords中所有数据,并按名称排列
	 * 
	 * @return 密码对象列表List
	 */
	public List<Password> queryAllPassword()
	{
		Cursor cursor = dataBase.query(TABLE_NAME, new String[] { "_id",
				"name", "account", "password", "remarks", "createtime",
				"revisetime", "classification" }, null, null, null, null,
				"name");

		List<Password> passwords = new ArrayList<Password>(cursor.getCount());

		while (cursor.moveToNext())
		{
			Password password = setPassword(cursor);
			if (null != password)
			{
				passwords.add(password);
			}
		}

		cursor.close();

		return passwords;
	}

	public List<Password> queryAllSimplePassword()
	{
		Cursor cursor = dataBase.query(TABLE_NAME, new String[] { "_id",
				"name", "account", "createtime" }, null, null, null, null,
				"name");

		List<Password> passwords = new ArrayList<Password>(cursor.getCount());

		while (cursor.moveToNext())
		{
			Password password = setSimplePassword(cursor);
			if (null != password)
			{
				passwords.add(password);
			}
		}

		cursor.close();

		return passwords;
	}

	/**
	 * 分类查询
	 * 
	 * @param classificationData
	 *            分类标记
	 * @return 密码对象列表List
	 */
	public List<Password> queryPasswordByClassification(int classification)
	{
		Cursor cursor;

		if (-1 == classification)
		{
			cursor = dataBase.query(TABLE_NAME, new String[] { "_id", "name",
					"account", "password", "remarks", "createtime",
					"revisetime", "classification" }, null, null, null, null,
					"name");
		} else
		{
			cursor = dataBase.query(TABLE_NAME, new String[] { "_id", "name",
					"account", "password", "remarks", "createtime",
					"revisetime", "classification" }, "classification = ?",
					new String[] { String.valueOf(classification) }, null,
					null, "name");
		}

		List<Password> passwordList = new ArrayList<Password>(cursor.getCount());

		while (cursor.moveToNext())
		{
			Password password = setPassword(cursor);
			if (null != password)
			{
				passwordList.add(password);
			}
		}

		cursor.close();

		return passwordList;
	}

	/**
	 * 分类查询
	 * 
	 * @param name
	 *            名称
	 * @return 密码对象列表
	 */
	public List<Password> querySimplePasswordByClassification(int classification)
	{
		Cursor cursor;
		if (-1 == classification)
		{
			cursor = dataBase.query(TABLE_NAME, new String[] { "_id", "name",
					"account", "createtime" }, null, null, null, null, "name");
		} else
		{
			cursor = dataBase.query(TABLE_NAME, new String[] { "_id", "name",
					"account", "createtime" }, "classification = ?",
					new String[] { String.valueOf(classification) }, null,
					null, "name");
		}

		List<Password> passwordList = new ArrayList<Password>(cursor.getCount());

		while (cursor.moveToNext())
		{
			Password password = setSimplePassword(cursor);
			if (null != password)
			{
				passwordList.add(password);
			}
		}

		cursor.close();

		return passwordList;
	}

	/**
	 * 恢复一条数据
	 * 
	 * @param password
	 */
	public void recoveryPassword(Password password)
	{
		if (null != password)
		{
			Password originalPassword = queryPasswordByCreateTime(DateUtils
					.dateToString(password.getCreateTime()));

			if (null == originalPassword)
			{
				insertPassword(password);
			} else
			{
				if (!originalPassword.equals(password))
				{
					password.setName(password.getName() + "["
							+ this.context.getString(R.string.recovery) + "]");

					insertPassword(password);
				}
			}
		}
	}

	/**
	 * 计数
	 * 
	 * @return
	 */
	public int getPasswordCount()
	{
		int count = 0;

		Cursor cursor = dataBase.query(TABLE_NAME, new String[] { "name" },
				null, null, null, null, null);
		count = cursor.getCount();

		cursor.close();

		return count;
	}

	/**
	 * 分类计数
	 * 
	 * @param classification
	 * @return
	 */
	public int getPasswordCountByClassification(int classification)
	{
		int count = 0;

		Cursor cursor = dataBase.query(TABLE_NAME, new String[] { "name" },
				"classification=?",
				new String[] { String.valueOf(classification) }, null, null,
				null);
		count = cursor.getCount();

		cursor.close();

		return count;
	}

	/**
	 * 完成加密,名称,分类不加密
	 * 
	 * @param password
	 * @return
	 */
	private ContentValues getContentValues(Password password)
	{
		ContentValues contentValues = new ContentValues();
		// 名称不加密
		contentValues.put("name", password.getName());
		contentValues.put("account", EncryptUtils.encrypt(
				password.getAccount(), Constants.LOGIN_PASSWORD));
		contentValues.put("password", EncryptUtils.encrypt(
				password.getPassword(), Constants.LOGIN_PASSWORD));
		contentValues.put("remarks", EncryptUtils.encrypt(
				password.getRemarks(), Constants.LOGIN_PASSWORD));
		contentValues.put("createtime", EncryptUtils.encrypt(
				DateUtils.dateToString(password.getCreateTime()),
				Constants.LOGIN_PASSWORD));
		contentValues.put("revisetime", EncryptUtils.encrypt(
				DateUtils.dateToString(password.getReviseTime()),
				Constants.LOGIN_PASSWORD));
		contentValues.put("classification", String.valueOf(password
				.getClassification().getClassification()));

		return contentValues;
	}

	/**
	 * 完成解密
	 * 
	 * @param cursor
	 * @return
	 */
	private Password setPassword(Cursor cursor)
	{
		Password password = new Password();
		// 名称未加密
		password.setId(cursor.getInt(cursor.getColumnIndex("_id")));
		password.setName(cursor.getString(cursor.getColumnIndex("name")));
		password.setAccount(EncryptUtils.decrypt(
				cursor.getString(cursor.getColumnIndex("account")),
				Constants.LOGIN_PASSWORD));
		password.setPassword(EncryptUtils.decrypt(
				cursor.getString(cursor.getColumnIndex("password")),
				Constants.LOGIN_PASSWORD));
		password.setRemarks(EncryptUtils.decrypt(
				cursor.getString(cursor.getColumnIndex("remarks")),
				Constants.LOGIN_PASSWORD));
		password.setCreateTime(EncryptUtils.decrypt(
				cursor.getString(cursor.getColumnIndex("createtime")),
				Constants.LOGIN_PASSWORD));
		password.setReviseTime(EncryptUtils.decrypt(
				cursor.getString(cursor.getColumnIndex("revisetime")),
				Constants.LOGIN_PASSWORD));
		password.setClassification(Classification.initClassification(Integer
				.valueOf(cursor.getString(cursor
						.getColumnIndex("classification")))));

		if (null == password.getCreateTime())
		{
			return null;
		}

		return password;
	}

	private Password setSimplePassword(Cursor cursor)
	{
		Password password = new Password();
		// 名称未加密
		password.setId(cursor.getInt(cursor.getColumnIndex("_id")));
		password.setName(cursor.getString(cursor.getColumnIndex("name")));
		password.setAccount(EncryptUtils.decrypt(
				cursor.getString(cursor.getColumnIndex("account")),
				Constants.LOGIN_PASSWORD));
		password.setCreateTime(EncryptUtils.decrypt(
				cursor.getString(cursor.getColumnIndex("createtime")),
				Constants.LOGIN_PASSWORD));

		if (null == password.getCreateTime())
		{
			return null;
		}

		return password;
	}

}
