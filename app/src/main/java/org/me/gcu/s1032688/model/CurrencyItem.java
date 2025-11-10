package org.me.gcu.s1032688.model;

import androidx.annotation.Nullable;

/** Simple model for one currency rate item (1 GBP -> rate CODE). */
public class CurrencyItem {
    /** 3-letter ISO currency code, e.g. USD. */
    public String code;
    /** Human-friendly currency name, e.g. United States Dollar. */
    public String displayName;
    /** Numeric rate: 1 GBP = rate CODE. */
    public double rate;
    /** Optional link back to the source item. */
    @Nullable public String link;
    /** Optional publication date of the item. */
    @Nullable public String pubDate;
}
