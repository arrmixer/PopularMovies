package com.ae.andriod.popularmovies.Util;

import android.content.Context;
import android.preference.PreferenceManager;

public class MenuQuery {
     private static final String PREF_SEARCH_QUERY = "com.ae.andriod.popularmovies.MenuQuery";

     public static String getPrefSearchQuery(Context context) {
          return PreferenceManager.getDefaultSharedPreferences(context)
                  .getString(PREF_SEARCH_QUERY, null);
     }

     public static void setPrefSearchQuery(Context context, String query){
          PreferenceManager.getDefaultSharedPreferences(context)
                  .edit()
                  .putString(PREF_SEARCH_QUERY, query)
                  .apply();
     }


}
