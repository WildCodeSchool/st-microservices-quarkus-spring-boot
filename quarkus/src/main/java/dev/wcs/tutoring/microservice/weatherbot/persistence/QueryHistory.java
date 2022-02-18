package dev.wcs.tutoring.microservice.weatherbot.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "query_history")
public class QueryHistory extends PanacheEntity {

    @Column(name = "location_request")
    public String locationRequest;
    @Column(name = "location_found")
    public String locationFound;
    public Integer temperature;
    public Integer incidence7day;
    @Column(name = "weather_conditions")
    public String weatherConditions;
    @Column(name = "query_time")
    public LocalDateTime queryTime;

}
