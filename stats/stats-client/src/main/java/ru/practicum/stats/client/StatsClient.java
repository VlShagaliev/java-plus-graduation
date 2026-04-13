package ru.practicum.stats.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stats.client.exception.StatsClientException;
import ru.practicum.stats.common.StatsApiError;
import ru.practicum.stats.dto.HitCreateDto;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static ru.practicum.stats.common.Constants.DATE_TIME_FORMAT;

@Component
public final class StatsClient {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private final RestTemplate rest;
    private final ObjectMapper mapper;

    public StatsClient(
            @Value("${stats-server.service-id:stats-server}") String serverUrl,
            RestTemplateBuilder builder,
            ObjectMapper mapper) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build();
        this.mapper = mapper;
    }

    public List<ViewStats> findStats(LocalDateTime start,
                                     LocalDateTime end,
                                     Iterable<String> uris,
                                     Boolean unique) {
        final UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .path("/stats")
                .queryParam("start", start.format(FORMATTER))
                .queryParam("end", end.format(FORMATTER))
                .queryParam("unique", unique);
        if (uris != null) {
            uris.forEach(uri -> builder.queryParam("uris", uri));
        }
        final String url = builder.build().toUriString();
        final ResponseEntity<ViewStats[]> response = request(HttpMethod.GET, url, null, ViewStats[].class);
        final ViewStats[] body = response.getBody();
        return body == null ? List.of() : List.of(body);
    }

    public HitDto hit(HitCreateDto hit) {
        final ResponseEntity<HitDto> response = request(HttpMethod.POST, "/hit", hit, HitDto.class);
        return response.getBody();
    }

    private <T> ResponseEntity<T> request(HttpMethod method,
                                          String url,
                                          Object body,
                                          Class<T> responseType) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        final HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        try {
            return rest.exchange(url, method, entity, responseType, Map.of());
        } catch (HttpStatusCodeException e) {
            final String errorMessage = "On the stats server, an unexpected error occurred";
            try {
                final StatsApiError response = mapper.readValue(e.getResponseBodyAsString(), StatsApiError.class);
                throw new StatsClientException(errorMessage, response);
            } catch (Throwable ignored) {
                throw new StatsClientException(errorMessage, null);
            }
        }
    }
}