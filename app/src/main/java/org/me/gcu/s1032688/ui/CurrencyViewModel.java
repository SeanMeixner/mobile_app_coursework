package org.me.gcu.s1032688.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.me.gcu.s1032688.data.CurrencyRepository;
import org.me.gcu.s1032688.model.CurrencyItem;
/**
 * ViewModel that fetches and exposes currency rates from the repository.
 * - Performs network and parsing off the main thread
 * - Exposes LiveData for items, timestamps, loading and errors
 * - Schedules an initial fetch and hourly periodic refresh
 */
public class CurrencyViewModel extends ViewModel {

    private final CurrencyRepository repo = new CurrencyRepository();
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    private final MutableLiveData<ArrayList<CurrencyItem>> _items = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> _lastBuildDate = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> _loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> _error = new MutableLiveData<>(null);

    public LiveData<ArrayList<CurrencyItem>> items = _items;
    public LiveData<String> lastBuildDate = _lastBuildDate;
    public LiveData<Boolean> loading = _loading;
    public LiveData<String> error = _error;

    // Background scheduler for periodic refresh
    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture<?> refreshTask;

    // Schedule initial and periodic refresh
    public CurrencyViewModel() {
        // Initial fetch shortly after launch
        scheduler.schedule(this::refreshNow, 500, TimeUnit.MILLISECONDS);
        // Auto-update every hour (submission requirement).
        refreshTask = scheduler.scheduleWithFixedDelay(this::refreshNow, 60, 60, java.util.concurrent.TimeUnit.MINUTES);
    }
    /** Trigger an immediate refresh of the RSS feed. */
    public void refreshNow() {
        _loading.postValue(true);
        _error.postValue(null);
        io.execute(() -> {
            try {
                String xml = repo.fetchFeedString();                 // network
                CurrencyRepository.ParseResult result = repo.parseRssString(xml); // parse
                _items.postValue(result.items);
                _lastBuildDate.postValue(result.lastBuildDate);
            } catch (Exception e) {
                _error.postValue(e.getMessage());
            } finally {
                _loading.postValue(false);
            }
        });
    }

    @Override
    protected void onCleared() {
        // Stop background work when ViewModel is no longer used
        if (refreshTask != null) refreshTask.cancel(true);
        scheduler.shutdownNow();
        io.shutdownNow();
        super.onCleared();
    }
}

