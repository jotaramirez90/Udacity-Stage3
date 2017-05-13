package com.udacity.stockhawk.sync;

import android.os.AsyncTask;
import java.util.Map;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class CheckStockTask extends AsyncTask<String, Void, Boolean> {

  public interface CheckStockListener {
    void checkStock(boolean exist);
  }

  private CheckStockListener mCheckStockListener;

  public CheckStockTask(CheckStockListener listener) {
    mCheckStockListener = listener;
  }

  @Override protected Boolean doInBackground(String... strings) {
    String[] symbolArray = { strings[0] };
    Map<String, Stock> quotes;
    try {
      quotes = YahooFinance.get(symbolArray);
    } catch (Exception e) {
      System.err.print("Could not reach Yahoo API");
      return false;
    }
    return quotes.get(symbolArray[0]).getName() != null;
  }

  @Override protected void onPostExecute(Boolean exists) {
    super.onPostExecute(exists);
    mCheckStockListener.checkStock(exists);
  }
}
