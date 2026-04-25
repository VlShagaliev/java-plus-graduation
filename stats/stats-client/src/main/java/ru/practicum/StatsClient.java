package ru.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.common.StatsApiError;
import ru.practicum.dto.HitCreateDto;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.ViewStats;
import ru.practicum.exception.StatsClientException;
import ru.practicum.exception.StatsServerUnavailableException;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static ru.practicum.common.Constants.DATE_TIME_FORMAT;

@Component
public final class StatsClient {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private final RestTemplate rest;
    private final ObjectMapper mapper;
    private final DiscoveryClient discoveryClient;
    private final RetryTemplate retryTemplate;
    private final String statsServiceId;

    public StatsClient(@Value("${stats-server.service-id:stats-server}") String statsServiceId,
                       RestTemplateBuilder builder,
                       ObjectMapper mapper,
                       DiscoveryClient discoveryClient) {
        this.rest = builder.build();
        this.mapper = mapper;
        this.discoveryClient = discoveryClient;
        this.statsServiceId = statsServiceId;
        this.retryTemplate = createRetryTemplate();
    }

    public List<ViewStats> findStats(LocalDateTime start,
                                     LocalDateTime end,
                                     Iterable<String> uris,
                                     Boolean unique) {
        final UriComponentsBuilder builder = UriComponentsBuilder.fromUri(makeUri("/stats"))
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

    private URI makeUri(String path) {
        ServiceInstance instance = retryTemplate.execute(context -> getInstance());
        return URI.create("http://" + instance.getHost() + ":" + instance.getPort() + path);
    }

    private ServiceInstance getInstance() {
        try {
            return discoveryClient.getInstances(statsServiceId).getFirst();
        } catch (Exception exception) {
            throw new StatsServerUnavailableException(
                    "Ошибка обнаружения адреса сервиса статистики с id: " + statsServiceId,
                    exception
            );
        }
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

    private RetryTemplate createRetryTemplate() {
        RetryTemplate template = new RetryTemplate();

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(3000L);
        template.setBackOffPolicy(backOffPolicy);

        MaxAttemptsRetryPolicy retryPolicy = new MaxAttemptsRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        template.setRetryPolicy(retryPolicy);

        return template;
    }
}