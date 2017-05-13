package com.udacity.stockhawk.app.navigator;

import android.content.Context;
import android.content.Intent;
import com.udacity.stockhawk.ui.DetailsActivity;

import static com.udacity.stockhawk.ui.DetailsActivity.EXTRA_SYMBOL;

public class Navigator {

  public static void toDetails(Context context, String symbol) {
    Intent intentToStartDetailActivity = new Intent(context, DetailsActivity.class);
    intentToStartDetailActivity.putExtra(EXTRA_SYMBOL, symbol);
    context.startActivity(intentToStartDetailActivity);
  }
}
