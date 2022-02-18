package dev.wcs.tutoring.microservice.weatherbot.rest;

import dev.wcs.tutoring.microservice.weatherbot.dto.CovidDTO;
import dev.wcs.tutoring.microservice.weatherbot.dto.WeatherDTO;
import dev.wcs.tutoring.microservice.weatherbot.persistence.QueryHistory;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.control.ActivateRequestContext;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.time.LocalDateTime;
import java.util.List;

@Path("/")
public class QueryHistoryResource {

    @GET
    @Path("/history")
    public List<QueryHistory> history() {
        return QueryHistory.findAll().list();
    }

    @GET
    @Path("/lastQuery")
    public QueryHistory queries() {
        return (QueryHistory) QueryHistory.findAll(Sort.descending("id")).list().get(0);
    }

    @GET
    @Path("/locationQuery")
    public List<QueryHistory> locationQuery(@QueryParam("location") String location) {
        return QueryHistory.find("locationFound like ?1", location).list();
    }

    @ActivateRequestContext
    @Transactional
    public void addQuery(String location, WeatherDTO weather, CovidDTO covid) {
        QueryHistory queryHistory =
            QueryHistory.builder()
                .queryTime(LocalDateTime.now())
                .locationRequest(location)
                .weatherConditions(weather.getMain())
                .temperature(weather.getTemp().intValue())
                .locationFound(covid.getCityName())
                .incidence7day(covid.getCases7day100k())
                .build();
        QueryHistory.persist(queryHistory);
    }
}