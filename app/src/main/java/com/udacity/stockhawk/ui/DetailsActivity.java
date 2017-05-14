package com.udacity.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetailsActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

  public static final String EXTRA_SYMBOL = "symbolExtra";
  private static final String PATTERN_DATE = "MM/yyyy";
  private static final String SPLIT_COMMA = ",";
  private static final String SPLIT_JUMP = "\n";
  private static final int LOADER_ID = 0;
  private static String mSymbol;

  @BindView(R.id.tv_price) TextView mPriceTextView;
  @BindView(R.id.tv_abs) TextView mAbsTextView;
  @BindView(R.id.tv_change) TextView mChangeTextView;
  @BindView(R.id.lc_chart) LineChart mLineChart;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_details);
    ButterKnife.bind(this);
    Intent intent = getIntent();
    if (intent != null && intent.hasExtra(EXTRA_SYMBOL)) {
      mSymbol = intent.getStringExtra(EXTRA_SYMBOL);
      setTitle(mSymbol);
      getLoaderManager().initLoader(LOADER_ID, null, this);
    }
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    String selection = Contract.Quote._ID + " = ?";
    String[] selectionArgs = new String[] { mSymbol };
    return new CursorLoader(this, Contract.Quote.makeUriForStock(mSymbol), null, selection,
        selectionArgs, null);
  }

  @Override public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    boolean isAvailable = cursor.moveToFirst();
    if (isAvailable) {
      String currentPrice = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_PRICE));
      float absoluteChange =
          cursor.getFloat(cursor.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE));
      StringBuilder percentageChange = new StringBuilder(
          cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE)));
      String history = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY));

      if (absoluteChange >= 0) {
        mAbsTextView.setBackgroundResource(R.drawable.percent_change_pill_green);
        mChangeTextView.setBackgroundResource(R.drawable.percent_change_pill_green);
      } else {
        mAbsTextView.setBackgroundResource(R.drawable.percent_change_pill_red);
        mChangeTextView.setBackgroundResource(R.drawable.percent_change_pill_red);
      }
      percentageChange.append(getResources().getString(R.string.details_percentage));
      mChangeTextView.setText(percentageChange);
      mAbsTextView.setText(String.valueOf(absoluteChange));
      mPriceTextView.setText(currentPrice);

      ArrayList<Entry> prices = new ArrayList<>();
      ArrayList<String> dates = new ArrayList<>();
      String[] historyElement = history.split(SPLIT_JUMP);
      float x = 0;
      for (int counter = historyElement.length - 1; counter >= 0; counter--) {
        String element = historyElement[counter];
        String timestampInMills = element.split(SPLIT_COMMA)[0];
        String stringPriceWithLeadingSpace = element.split(SPLIT_COMMA)[1];
        String stringPrice = stringPriceWithLeadingSpace.substring(1);
        Float price = Float.valueOf(stringPrice);

        Date date = new Date();
        date.setTime(Long.valueOf(timestampInMills));
        String dateString = new SimpleDateFormat(PATTERN_DATE).format(date);

        dates.add(dateString);
        prices.add(new Entry(x, price));

        x++;
      }

      createDiagram(mSymbol, prices, dates);
    }
  }

  @Override public void onLoaderReset(Loader<Cursor> loader) {

  }

  private void createDiagram(String symbol, ArrayList<Entry> stockPrices,
      final ArrayList<String> dates) {

    LineDataSet pricesDataSet = new LineDataSet(stockPrices, symbol);

    XAxis xAxis = mLineChart.getXAxis();
    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    xAxis.setTextColor(Color.WHITE);
    IAxisValueFormatter formatter = new IAxisValueFormatter() {
      @Override public String getFormattedValue(float value, AxisBase axis) {
        return dates.get((int) value);
      }
    };
    xAxis.setValueFormatter(formatter);

    YAxis left = mLineChart.getAxisLeft();
    left.setEnabled(true);
    left.setLabelCount(10, true);
    left.setTextColor(Color.WHITE);

    mLineChart.getAxisRight().setEnabled(false);
    mLineChart.getLegend().setEnabled(false);
    mLineChart.setGridBackgroundColor(Color.BLACK);
    Description desc = new Description();
    desc.setText("");
    mLineChart.setDescription(desc);
    mLineChart.animateX(1000, Easing.EasingOption.Linear);

    pricesDataSet.setDrawCircles(false);
    pricesDataSet.setColor(Color.WHITE);
    LineData lineData = new LineData(pricesDataSet);
    mLineChart.setData(lineData);
  }
}
