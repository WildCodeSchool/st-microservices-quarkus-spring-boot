package dev.wcs.tutoring.quarkus.weatherbot.rest;

import dev.wcs.tutoring.quarkus.weatherbot.dto.CovidDTO;
import dev.wcs.tutoring.quarkus.weatherbot.dto.WeatherDTO;
import dev.wcs.tutoring.quarkus.weatherbot.persistence.entity.QueryHistory;
import dev.wcs.tutoring.quarkus.weatherbot.persistence.repository.QueryHistoryRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class QueryHistoryResource {

    private final QueryHistoryRepository queryHistoryRepository;

    public QueryHistoryResource(QueryHistoryRepository queryHistoryRepository) {
        this.queryHistoryRepository = queryHistoryRepository;
    }

    @GetMapping("/history")
    public Iterable<QueryHistory> history() {
        return queryHistoryRepository.findAll();
    }

    @GetMapping("/lastQuery")
    public QueryHistory queries() {
        return queryHistoryRepository.findFirstByOrderByIdDesc();
    }

    @GetMapping("/locationQuery")
    public List<QueryHistory> locationQuery(@RequestParam String location) {
        return queryHistoryRepository.findQueryHistoryByLocationFoundIsLike(location);
    }

    @Transactional
    public void addQuery(String location, WeatherDTO weather, CovidDTO covid) {
        QueryHistory queryHistory = QueryHistory.builder()
                .queryTime(LocalDateTime.now())
                .locationRequest(location)
                .weatherConditions(weather.getMain())
                .temperature(weather.getTemp().intValue())
                .locationFound(covid.getCityName())
                .incidence7day(covid.getCases7day100k())
                .build();
        queryHistoryRepository.save(queryHistory);
    }
}