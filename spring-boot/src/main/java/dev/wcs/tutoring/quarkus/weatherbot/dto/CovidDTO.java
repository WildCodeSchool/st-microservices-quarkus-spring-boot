package dev.wcs.tutoring.quarkus.weatherbot.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CovidDTO {

    private Integer cases7day100k;
    private String cityName;

}
