package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

public class StockWidgetRemoteViewsService extends RemoteViewsService {

  @Override public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return new RemoteViewsFactory() {
      private Cursor data = null;

      @Override public void onCreate() {
      }

      @Override public void onDataSetChanged() {
        if (data != null) {
          data.close();
        }
        final long identityToken = Binder.clearCallingIdentity();
        data = getContentResolver().query(Contract.Quote.URI, null, null, null,
            Contract.Quote.COLUMN_SYMBOL);
        Binder.restoreCallingIdentity(identityToken);
      }

      @Override public void onDestroy() {
        if (data != null) {
          data.close();
          data = null;
        }
      }

      @Override public int getCount() {
        return data == null ? 0 : data.getCount();
      }

      @Override public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(
            position)) {
          return null;
        }
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.list_item_quote);

        float percentage =
            data.getFloat(data.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE));
        views.setTextViewText(R.id.symbol,
            data.getString(data.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)));
        views.setTextViewText(R.id.price,
            data.getString(data.getColumnIndex(Contract.Quote.COLUMN_PRICE)));
        views.setTextViewText(R.id.change,
            percentage + getResources().getString(R.string.details_percentage));
        views.setInt(R.id.change, getResources().getString(R.string.widget_resource_background),
            percentage > 0 ? R.drawable.percent_change_pill_green
                : R.drawable.percent_change_pill_red);

        return views;
      }

      @Override public RemoteViews getLoadingView() {
        return new RemoteViews(getPackageName(), R.layout.list_item_quote);
      }

      @Override public int getViewTypeCount() {
        return 1;
      }

      @Override public long getItemId(int position) {
        return position;
      }

      @Override public boolean hasStableIds() {
        return true;
      }
    };
  }
}
