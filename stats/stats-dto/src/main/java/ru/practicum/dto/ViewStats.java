package ru.practicum.stats.dto;

import lombok.Data;

@Data
public class ViewStats {
    private String app;
    private String uri;
    private long hits;

    public ViewStats(String app, String uri, long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}