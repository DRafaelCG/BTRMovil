package com.grupocsi.btrmovil.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.grupocsi.btrmovil.model.DocumentosItems;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper{
	private Context _context;
	DatabaseHelper helper;
	private static final String DATABASE_NAME = "dbDoctosPendientes";
	private static final int DATABASE_VERSION = 1;
	private Dao<DocumentosItems, Integer> documentoItemDao = null;
	
	private RuntimeExceptionDao<DocumentosItems, Integer>simpleRuntimeDao = null;
	
	public DatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		_context = context;
	}
	
	public Dao<DocumentosItems, Integer> getDao() 
			throws java.sql.SQLException{
		if (documentoItemDao == null) {
			documentoItemDao = getDao(DocumentosItems.class);
		}
		return documentoItemDao;
	}
	
	public RuntimeExceptionDao<DocumentosItems, Integer> getSimpleDataDao(){
		if (simpleRuntimeDao == null) {
			simpleRuntimeDao = getRuntimeExceptionDao(DocumentosItems.class);
		}
		return simpleRuntimeDao;
	}
	
	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource){
		try {
			TableUtils.createTableIfNotExists(connectionSource, DocumentosItems.class);
			Log.i("Creando Tabla ", DatabaseHelper.class.getName());
		} catch (SQLException e) {
			Log.e("No se pudo crear la Tabla ", DatabaseHelper.class.getName(), e);
			throw new RuntimeException(e);
		}catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion){
		try {
			List<String> allSql = new ArrayList<String>();
			switch (oldVersion) {
			case 1:
			}
			for (String sql: allSql) {
				db.execSQL(sql);
			}
		} catch (SQLException e) {
			Log.e("Excepcion durante actualizacion", DatabaseHelper.class.getName(), e);
			throw new RuntimeException(e);
		}
	}
	
	public int getMaxIdReg(){
		int i = 0;
		try {
			DatabaseHelper helper = new DatabaseHelper(_context);
			if (helper.getDao().countOf() > 0) {
				QueryBuilder<DocumentosItems, Integer> dao = getSimpleDataDao().queryBuilder();
				List<DocumentosItems> list = null;
				dao.selectColumns("idReg");
				dao.orderBy("id", false);
				if (!dao.query().equals(null)) {
					list = dao.query();
					if (list.get(0).getIdReg() > 0) {
						i = list.get(0).getIdReg();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return i;
	}
	
	public List<DocumentosItems> getData(){
		List<DocumentosItems> list = null;
		try {
			DatabaseHelper helper = new DatabaseHelper(_context);
			QueryBuilder<DocumentosItems, Integer> dao = helper.getSimpleDataDao().queryBuilder();
			if (helper.getDao().countOf() > 0) {
				dao.groupBy("idReg");
				list = dao.query();
			}else {
				list = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<DocumentosItems> getDataWithIdRegGroupByIdDoc(int idReg){
		List<DocumentosItems> list = null;
		try {
			QueryBuilder<DocumentosItems, Integer> dao = getSimpleDataDao().queryBuilder();
			dao.query().clear();
			dao.where().eq("idReg", idReg);
			dao.groupBy("idDocumento");
			dao.orderBy("idDocumento", true);
			list = dao.query();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<DocumentosItems> getDataWithidReg(int idReg){
		List<DocumentosItems> list = null;
		try {
			QueryBuilder<DocumentosItems, Integer> dao = getSimpleDataDao().queryBuilder();
			dao.query().clear();
			dao.where().eq("idReg", idReg);
			list = dao.query();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<DocumentosItems> getDataWithIdRegAndIdDoc(int idReg, int idDoc){
		List<DocumentosItems> list = null;
		try {
			QueryBuilder<DocumentosItems, Integer> dao = getSimpleDataDao().queryBuilder();
			dao.query().clear();
			dao.where().eq("idReg", idReg).and().eq("idDocumento", idDoc);
			list = dao.query();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public void deleteByIdRegPend(int idRegPend){
		List<DocumentosItems> list = null;
		try {
			QueryBuilder<DocumentosItems, Integer> dao = getSimpleDataDao().queryBuilder();
			dao.query().clear();
			RuntimeExceptionDao<DocumentosItems, Integer> daoDel = getSimpleDataDao();
			dao.where().eq("idReg", idRegPend);
			list = dao.query();
			daoDel.delete(list);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	
	@Override
	public void close(){
		super.close();
		simpleRuntimeDao = null;
	}
	
	/**
	 * Crea registro en la base
	 * */
	public int addRegistro(DocumentosItems documentosItems){
		RuntimeExceptionDao<DocumentosItems, Integer> dao = getSimpleDataDao();
		int i = dao.create(documentosItems);
		return i;
	}
}
