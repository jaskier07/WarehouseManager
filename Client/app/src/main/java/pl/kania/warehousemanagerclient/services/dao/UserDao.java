package pl.kania.warehousemanagerclient.services.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Optional;

import pl.kania.warehousemanagerclient.model.WarehouseRole;
import pl.kania.warehousemanagerclient.model.entities.User;

import static android.provider.BaseColumns._ID;

public class UserDao {

    private final SQLiteDatabase db;
    private final Runnable open;
    private final Runnable close;

    public UserDao(SQLiteDatabase db, Runnable open, Runnable close) {
        this.db = db;
        this.open = open;
        this.close = close;
    }

    public Long insertUser(User user) {
        open.run();
        final ContentValues cv = mapProductToUser(user);
        long id = db.insert(User.UserEntry.USER_TABLE_NAME, null, cv);
        if (id == -1) {
            Log.e("insert", "user has not been inserted");
        }
        close.run();
        return id;
    }

    public Optional<User> selectUser(Long id) {
        open.run();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + User.UserEntry.USER_TABLE_NAME + " WHERE " + _ID + " = " + id, null);
        if (cursor.moveToFirst()) {
            if (!cursor.isAfterLast()) {
                close.run();
                return Optional.of(mapCursorToUser(cursor));
            }
        }
        close.run();
        return Optional.empty();
    }

    public boolean updateUserRole(Long id, WarehouseRole role) {
        open.run();
        final Cursor cursor = db.rawQuery("UPDATE user SET role = " + role.getDbKey() + " WHERE id = " + id, null);
        if (cursor != null) {
            final boolean updated = cursor.moveToFirst();
            if (!updated) {
                Log.e("update", "user's role has not been updated");
            }
            close.run();
            return updated;
        }
        Log.e("update", "user's role has not been updated");
        close.run();
        return false;
    }

    private User mapCursorToUser(Cursor cursor) {
        final User user = new User();
        user.setId(cursor.getLong(0));
        user.setLogin(cursor.getString(1));
        user.setPassword(cursor.getString(2));
        user.setRole(WarehouseRole.valueOf(cursor.getString(3)));
        return user;
    }

    private ContentValues mapProductToUser(User user) {
        final ContentValues cv = new ContentValues();
        cv.put(User.UserEntry._ID, user.getId());
        cv.put(User.UserEntry.LOGIN, user.getLogin());
        cv.put(User.UserEntry.PASSWORD, user.getPassword());
        cv.put(User.UserEntry.ROLE, user.getRole().getDbKey());
        return cv;
    }
}
