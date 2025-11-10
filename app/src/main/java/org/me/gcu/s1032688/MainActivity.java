/*  Starter project for Mobile Platform Development - 1st diet 25/26
    You should use this project as the starting point for your assignment.
    This project simply reads the data from the required URL and displays the
    raw data in a TextField
*/

//
// Name                 Sean Meixner
// Student ID           S1032688
// Programme of Study   Computing
//

// UPDATE THE PACKAGE NAME to include your Student Identifier
package org.me.gcu.s1032688;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.me.gcu.s1032688.model.CurrencyItem;
import org.me.gcu.s1032688.ui.CurrencyViewModel;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView rawDataDisplay;
    private Button startButton;
    private TextView majorUsd, majorUsdRate, majorUsdSub,
            majorEur, majorEurRate, majorEurSub,
            majorJpy, majorJpyRate, majorJpySub;
    private View cardUsd, cardEur, cardJpy;
    private TextView lastUpdatedChip;
    private CurrencyViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rawDataDisplay = findViewById(R.id.rawDataDisplay);
        startButton = findViewById(R.id.startButton);
        majorUsd = findViewById(R.id.majorUsd);
        majorUsdSub = findViewById(R.id.majorUsdSub);
        majorUsdRate = findViewById(R.id.majorUsdRate);
        majorEur = findViewById(R.id.majorEur);
        majorEurSub = findViewById(R.id.majorEurSub);
        majorEurRate = findViewById(R.id.majorEurRate);
        majorJpy = findViewById(R.id.majorJpy);
        majorJpySub = findViewById(R.id.majorJpySub);
        majorJpyRate = findViewById(R.id.majorJpyRate);
        cardUsd = findViewById(R.id.cardUsd);
        cardEur = findViewById(R.id.cardEur);
        cardJpy = findViewById(R.id.cardJpy);
        lastUpdatedChip = findViewById(R.id.lastUpdatedChip);

        vm = new ViewModelProvider(this).get(CurrencyViewModel.class);

        // Observers
        vm.loading.observe(this, loading -> {
            startButton.setEnabled(!Boolean.TRUE.equals(loading));
            if (Boolean.TRUE.equals(loading)) rawDataDisplay.setText("Fetching latest GBP rates…");
        });

        vm.error.observe(this, err -> {
            if (err != null && !err.isEmpty()) {
                Toast.makeText(this, "Error: " + err, Toast.LENGTH_LONG).show();
            }
        });

        vm.lastBuildDate.observe(this, s -> {
            renderPreview(vm.items.getValue(), s);
            // Keep the hint text instead of last updated time
            lastUpdatedChip.setText(getString(R.string.majors_hint));
        });
        // After vm.refreshNow() completes and items arrive, change button behaviour:
        vm.items.observe(this, list -> {
            renderPreview(list, vm.lastBuildDate.getValue());

            if (list != null && !list.isEmpty()) {
                // Reuse button to open the list
                startButton.setText("View All Rates");
                startButton.setOnClickListener(v ->
                    startActivity(new android.content.Intent(this, org.me.gcu.s1032688.ui.RatesActivity.class)));

                // Find majors
                CurrencyItem usd = findByCode(list, "USD");
                CurrencyItem eur = findByCode(list, "EUR");
                CurrencyItem jpy = findByCode(list, "JPY");

                setMajor(majorUsd, majorUsdRate, majorUsdSub, usd);
                setMajor(majorEur, majorEurRate, majorEurSub, eur);
                setMajor(majorJpy, majorJpyRate, majorJpySub, jpy);

                // Make whole cards tappable to access conversion
                if (cardUsd != null) cardUsd.setOnClickListener(v -> openConverter(usd));
                if (cardEur != null) cardEur.setOnClickListener(v -> openConverter(eur));
                if (cardJpy != null) cardJpy.setOnClickListener(v -> openConverter(jpy));

            }
        });

        // Button triggers a manual refresh (auto-refresh added later)
        startButton.setOnClickListener(v -> vm.refreshNow());

        // Initial load
        if (vm.items.getValue() == null || vm.items.getValue().isEmpty()) {
            vm.refreshNow();
        }
    }

    private void updatePreview(ArrayList<CurrencyItem> list, String lastBuild) {
        if (list == null) list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("Parsed items: ").append(list.size()).append("\n");
        if (lastBuild != null) sb.append("Last updated: ").append(lastBuild).append("\n\n");

        int show = Math.min(6, list.size());
        for (int i = 0; i < show; i++) {
            CurrencyItem ci = list.get(i);
            sb.append(String.format(Locale.UK, "%s (%s): 1 GBP = %.4f %s\n",
                    ci.displayName, ci.code, ci.rate, ci.code));
        }
        if (list.isEmpty()) {
            sb.append("\nNo items parsed. Tap the button to try again.");
        } else {
            sb.append("\n• Data held in ViewModel (survives rotation)\n");
            sb.append("• Ready for list/search/converter views\n");
        }
        rawDataDisplay.setText(sb.toString());
    }

    private CurrencyItem findByCode(ArrayList<CurrencyItem> list, String code) {
        if (list == null) return null;
        for (CurrencyItem item : list) {
            if (code.equalsIgnoreCase(item.code)) return item;
        }
        return null;
    }

    private void setMajor(TextView label, TextView rateView, TextView sub, CurrencyItem item) {
        if (item == null) {
            label.setText("--");
            if (rateView != null) rateView.setText("--");
            sub.setText("Not available");
            return;
        }
        label.setText(item.code);
        if (rateView != null) rateView.setText(String.format(Locale.UK, "%.3f", item.rate));
        if (sub != null) {
            sub.setText("");
            sub.setVisibility(View.GONE);
        }

        // Attach tap handler to open converter
        label.setOnClickListener(v -> openConverter(item));
        if (rateView != null) rateView.setOnClickListener(v -> openConverter(item));
        if (sub != null) sub.setOnClickListener(v -> openConverter(item));
    }

    // removed unused bindMajorCard()

    // chip styling removed for dashboard per request

    // New preview renderer without debug lines; shows last updated and first 12 rates
    private void renderPreview(ArrayList<CurrencyItem> list, String lastBuild) {
        StringBuilder sb = new StringBuilder();
        if (lastBuild != null && !lastBuild.isEmpty()) {
            sb.append("Last updated: ").append(lastBuild).append("\n\n");
        }

        if (list != null && !list.isEmpty()) {
            int show = Math.min(12, list.size());
            for (int i = 0; i < show; i++) {
                CurrencyItem ci = list.get(i);
                sb.append(String.format(Locale.UK, "\u2022 %s (%s): 1 GBP = %.4f %s\n",
                        ci.displayName, ci.code, ci.rate, ci.code));
            }
        } else {
            sb.append("No rates available. Tap Refresh to try again.");
        }
        rawDataDisplay.setText(sb.toString());
    }

    private void openConverter(CurrencyItem item) {
        if (item == null) return;
        Intent intent = new Intent(this, org.me.gcu.s1032688.ui.ConverterActivity.class);
        intent.putExtra("code", item.code);
        intent.putExtra("rate", item.rate);
        intent.putExtra("name", item.displayName);
        startActivity(intent);
    }
}
