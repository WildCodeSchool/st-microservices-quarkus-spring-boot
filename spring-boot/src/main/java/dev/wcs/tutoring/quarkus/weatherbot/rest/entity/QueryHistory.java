package dev.wcs.tutoring.quarkus.weatherbot.rest.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "query_history")
public class QueryHistory {

    @Id
    @GeneratedValue
    private Long id;

    private String locationRequest;
    private String locationFound;
    private Integer temperature;
    private Integer incidence7day;
    private String weatherConditions;
    private LocalDateTime queryTime;

}
