package com.example.user.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.user.criminalintent.database.CrimeBaseHelper;
import com.example.user.criminalintent.database.CrimeCursorWrapper;
import com.example.user.criminalintent.database.CrimeDbSchema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by user on 29-12-2017.
 */

public class CrimeLab {

    private static CrimeLab sCrimeLab;

    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;

    public static CrimeLab get(Context context){

        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
        mSQLiteDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();


    }

    public void addCrime(Crime c){
        ContentValues v = getContentValues(c);
        mSQLiteDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null,v);
            }

    public List<Crime> getCrimes(){
        List<Crime> crimes =  new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return crimes;
    }

    public Crime getCrime(UUID id){

        CrimeCursorWrapper cursor = queryCrimes(CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",new String[]{id.toString()});

        try{
            if (cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
            } finally {
            cursor.close();
        }
    }

    public void removeCrime(Crime crime) {
        String uuid = crime.getId().toString();
        mSQLiteDatabase.delete(CrimeDbSchema.CrimeTable.NAME, CrimeDbSchema.CrimeTable.Cols.UUID + " = ?", new String[]{uuid});

    }

    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeDbSchema.CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeDbSchema.CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeDbSchema.CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeDbSchema.CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeDbSchema.CrimeTable.Cols.SUSPECT, crime.getSuspect());
        values.put(CrimeDbSchema.CrimeTable.Cols.NUMBER, crime.getNumber());

        return values;
    }

    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
         ContentValues values = getContentValues(crime);

         mSQLiteDatabase.update(CrimeDbSchema.CrimeTable.NAME, values, CrimeDbSchema.CrimeTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mSQLiteDatabase.query(CrimeDbSchema.CrimeTable.NAME,null,whereClause,whereArgs,null,null,null);
        return new CrimeCursorWrapper(cursor);
    }

    public File getPhotoFile(Crime crime){
        File file = mContext.getFilesDir();
        return new File(file,crime.getPhotoFilename());
    }



}
