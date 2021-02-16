package arcsky.steph.covidtracker;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class JSONCasesAdapter extends ArrayAdapter<JSONData> {

    private LayoutInflater mInflater;

    public JSONCasesAdapter(final Activity context, ArrayList<JSONData> weatherData) {
        super(context, 0, weatherData);
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        JSONData currentCasesData = getItem(position);

        Resources res = this.getContext().getResources();

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.list_item, parent, false);
        }

        // Assign TextViews
        TextView positivesTextView = convertView.findViewById(R.id.positivesTotalText);
        TextView negativesTextView = convertView.findViewById(R.id.negativeTotalText);
        TextView hospitalizedTextView = convertView.findViewById(R.id.hospitalizedTotalText);
        TextView deathsTextView = convertView.findViewById(R.id.deathsTotalText);
        TextView testsTotalTextView = convertView.findViewById(R.id.testNumbersText);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date date = new Date();
        try {
            date = dateFormat.parse(currentCasesData.getLastModified());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat formatter = new SimpleDateFormat("MMM d, yyyy - h:m a zzz"); //If you need time just put specific format for time like 'HH:mm:ss'
        String formattedDateLastModified = formatter.format(date);

//        // Create a date string.
//        String todayDate = new SimpleDateFormat("MMM dd, yyyy",
//                Locale.getDefault()).format(new Date());

        // Get the Positive and Negative JSON count, set them to Strings, and convert them to ints.
        String stringPositives = currentCasesData.getPositives();
        String stringNegatives = currentCasesData.getNegatives();
        String stringHospitalized = currentCasesData.getHospitalized();
        String stringDeaths = currentCasesData.getDeaths();
        String stringTests = currentCasesData.getTestsTotal();
        int intPositives = Integer.parseInt(stringPositives);
        int intNegatives = Integer.parseInt(stringNegatives);
        int intHospitalized = Integer.parseInt(stringHospitalized);
        int intDeaths = Integer.parseInt(stringDeaths);
        int intTests = Integer.parseInt(stringTests);

        // Use the DecimalFormat class to format the Positives and Negatives ints to include commas.
        DecimalFormat dFormatter = new DecimalFormat("#,###");
        String positiveFormatted = dFormatter.format(intPositives);
        String negativeFormatted = dFormatter.format(intNegatives);
        String hospitalizedFormatted = dFormatter.format(intHospitalized);
        String deathsFormatted = dFormatter.format(intDeaths);
        String testsFormatted = dFormatter.format(intTests);
        String lastModified = res.getString(R.string.last_modified);

        // Set Strings to TextViews
        positivesTextView.setText(positiveFormatted);
        negativesTextView.setText(negativeFormatted);
        hospitalizedTextView.setText(hospitalizedFormatted);
        deathsTextView.setText(deathsFormatted);
        testsTotalTextView.setText(testsFormatted);
        dateTextView.setText(lastModified + " " + formattedDateLastModified);

        return convertView;
    }
}