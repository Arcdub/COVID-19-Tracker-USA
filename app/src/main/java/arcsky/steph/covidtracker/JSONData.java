package arcsky.steph.covidtracker;

public class JSONData {

    private String mPositive;
    private String mNegative;
    private String mHospitalized;
    private String mDeaths;
    private String mTests;
    private String mLastModified;
//    private String mWindSpeed;
//
//    private long mTimeInMilliseconds;
//
//    private int mClouds;

    /**
     * Constructs a new {@link JSONData} object.
     */
    public JSONData(String positive, String negative, String hospitalized, String deaths, String tests, String lastModified) {

        mPositive = positive;
        mNegative = negative;
        mHospitalized = hospitalized;
        mDeaths = deaths;
        mTests = tests;
        mLastModified = lastModified;

//        mWindSpeed = windSpeed;
//
//        mClouds = clouds;
    }

    public String getPositives() { return mPositive; }

    public String getNegatives() { return mNegative; }

    public String getHospitalized() { return mHospitalized; }

    public String getDeaths() { return mDeaths; }

    public String getTestsTotal() { return mTests; }

    public String getLastModified() { return mLastModified; }

//    public String getWindSpeed() {
//        return mWindSpeed;
//    }
//
//    public int getClouds() { return mClouds; }
}

