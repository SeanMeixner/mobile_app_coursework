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

    // Top-level scheduled refresh fields
    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture<?> refreshTask;

    // Constructor to schedule initial and periodic refresh
    public CurrencyViewModel() {
        // initial fetch shortly after launch
        scheduler.schedule(this::refreshNow, 500, TimeUnit.MILLISECONDS);
        // demo-friendly interval (15 min). For submission, change both 15 → 60.
        refreshTask = scheduler.scheduleWithFixedDelay(
                this::refreshNow, 15, 15, TimeUnit.MINUTES
        );
    }

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
        if (refreshTask != null) refreshTask.cancel(true);
        scheduler.shutdownNow();
        io.shutdownNow();
        super.onCleared();
    }
}
