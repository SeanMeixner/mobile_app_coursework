package org.me.gcu.s1032688.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import org.me.gcu.s1032688.R;
import org.me.gcu.s1032688.model.CurrencyItem;

import java.util.ArrayList;

/**
 * Displays the full list of exchange rates with search and color coding.
 * Clicking a row opens the converter pre-filled for that currency.
 */
public class RatesActivity extends AppCompatActivity implements RatesAdapter.OnRateClick {
    private CurrencyViewModel vm;
    private RatesAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        // Sets up RecyclerView, search behavior and observers.
        super.onCreate(b);
        setContentView(R.layout.activity_rates);

        RecyclerView rv = findViewById(R.id.recycler);
        SearchView sv = findViewById(R.id.searchView);
        androidx.appcompat.widget.Toolbar tb = findViewById(R.id.toolbar);
        if (tb != null) tb.setNavigationOnClickListener(v -> finish());

        // Add bottom padding equal to keyboard height when it's visible
        ViewCompat.setOnApplyWindowInsetsListener(rv, (v, insets) -> {
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottom = ime.bottom; // 0 when keyboard hidden
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bottom);
            // Let the system keep dispatching insets
            return insets;
        });

        // Helpful: list can scroll "under" the padding smoothly
        rv.setClipToPadding(false);

        // Optional: hide keyboard when user scrolls the list
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrollStateChanged(@NonNull RecyclerView r, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) sv.clearFocus();
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RatesAdapter(new ArrayList<>(), this, this);
        rv.setAdapter(adapter);

        vm = new ViewModelProvider(this).get(CurrencyViewModel.class);
        vm.items.observe(this, list -> adapter.submit(list));
        vm.error.observe(this, e -> { if (e != null) Toast.makeText(this, e, Toast.LENGTH_LONG).show(); });

        // Pull data (separate screen can fetch again; simple and fine)
        if (vm.items.getValue()==null || vm.items.getValue().isEmpty()) vm.refreshNow();

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String q) { adapter.filter(q); return true; }
            @Override public boolean onQueryTextChange(String q) { adapter.filter(q); return true; }
        });
    }

    @Override
    public void onRateClicked(CurrencyItem item) {
        // Launch converter for the selected currency
        Intent i = new Intent(this, ConverterActivity.class);
        i.putExtra("code", item.code);
        i.putExtra("name", item.displayName);
        i.putExtra("rate", item.rate);
        startActivity(i);
    }
}