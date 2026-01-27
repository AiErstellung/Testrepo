package com.elmon.app.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.elmon.app.data.model.VideoRating;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class VideoRatingDao_Impl implements VideoRatingDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<VideoRating> __insertionAdapterOfVideoRating;

  public VideoRatingDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfVideoRating = new EntityInsertionAdapter<VideoRating>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `VideoRating` (`videoId`,`liked`,`timestamp`,`feedback`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @Nullable final VideoRating entity) {
        if (entity.getVideoId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getVideoId());
        }
        final int _tmp = entity.getLiked() ? 1 : 0;
        statement.bindLong(2, _tmp);
        statement.bindLong(3, entity.getTimestamp());
        if (entity.getFeedback() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getFeedback());
        }
      }
    };
  }

  @Override
  public Object insert(final VideoRating rating, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfVideoRating.insert(rating);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRatedIds(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT videoId FROM VideoRating";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            if (_cursor.isNull(0)) {
              _item = null;
            } else {
              _item = _cursor.getString(0);
            }
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAll(final Continuation<? super List<VideoRating>> $completion) {
    final String _sql = "SELECT * FROM VideoRating";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<VideoRating>>() {
      @Override
      @NonNull
      public List<VideoRating> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVideoId = CursorUtil.getColumnIndexOrThrow(_cursor, "videoId");
          final int _cursorIndexOfLiked = CursorUtil.getColumnIndexOrThrow(_cursor, "liked");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfFeedback = CursorUtil.getColumnIndexOrThrow(_cursor, "feedback");
          final List<VideoRating> _result = new ArrayList<VideoRating>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VideoRating _item;
            final String _tmpVideoId;
            if (_cursor.isNull(_cursorIndexOfVideoId)) {
              _tmpVideoId = null;
            } else {
              _tmpVideoId = _cursor.getString(_cursorIndexOfVideoId);
            }
            final boolean _tmpLiked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfLiked);
            _tmpLiked = _tmp != 0;
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpFeedback;
            if (_cursor.isNull(_cursorIndexOfFeedback)) {
              _tmpFeedback = null;
            } else {
              _tmpFeedback = _cursor.getString(_cursorIndexOfFeedback);
            }
            _item = new VideoRating(_tmpVideoId,_tmpLiked,_tmpTimestamp,_tmpFeedback);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
