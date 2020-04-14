package arcsky.steph.covidtracker;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class CasesDataLoader extends AsyncTaskLoader<List<JSONData>> {

    private static final String LOG_TAG = CasesDataLoader.class.getName();

    private String mUrl;

    /**
     * Constructs a new {@link CasesDataLoader}.
     */
    public CasesDataLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "TEST: onStartLoading() called ...");
        forceLoad();
    }

    @Override
    public List<JSONData> loadInBackground() {
        Log.i(LOG_TAG, "TEST: loadInBackground() called ...");
        if (mUrl == null) {
            return null;
        }

        List<JSONData> jsonData = QueryUtils.fetchCasesData(mUrl);
        return jsonData;
    }
}
