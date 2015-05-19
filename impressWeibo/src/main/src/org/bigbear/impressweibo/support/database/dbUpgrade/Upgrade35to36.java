package org.bigbear.impressweibo.support.database.dbUpgrade;

import org.bigbear.impressweibo.support.database.DatabaseHelper;
import org.bigbear.impressweibo.support.database.table.AtUsersTable;

import android.database.sqlite.SQLiteDatabase;

/**
 * User: qii
 * Date: 14-4-8
 */
public class Upgrade35to36 {

    public static void upgrade(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + AtUsersTable.TABLE_NAME);
        db.execSQL(DatabaseHelper.CREATE_ATUSERS_TABLE_SQL);
    }
}
