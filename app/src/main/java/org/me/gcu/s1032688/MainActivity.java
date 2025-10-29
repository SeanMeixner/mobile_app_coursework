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
    private CurrencyViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rawDataDisplay = findViewById(R.id.rawDataDisplay);
        startButton = findViewById(R.id.startButton);

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

        vm.lastBuildDate.observe(this, s -> updatePreview(vm.items.getValue(), s));
        // After vm.refreshNow() completes and items arrive, change button behaviour:
        vm.items.observe(this, list -> {
            updatePreview(list, vm.lastBuildDate.getValue());
            if (list != null && !list.isEmpty()) {
                startButton.setText("View All Rates");
                startButton.setOnClickListener(v ->
                    startActivity(new Intent(this, org.me.gcu.s1032688.ui.RatesActivity.class)));
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
}