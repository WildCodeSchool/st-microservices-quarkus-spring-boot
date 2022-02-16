package dev.wcs.tutoring.microservice.weatherbot.route;

import javax.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import dev.wcs.tutoring.microservice.weatherbot.dto.CovidDTO;
import dev.wcs.tutoring.microservice.weatherbot.dto.WeatherDTO;
import dev.wcs.tutoring.microservice.weatherbot.rest.QueryHistoryResource;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@ApplicationScoped
public class DataAggregationBot extends RouteBuilder{

    @ConfigProperty(name = "telegram.token")
    private String telegramApiToken;

    @ConfigProperty(name = "weather.token")
    private String weatherApiToken;

    @ConfigProperty(name = "covidapi.url")
    private String covidApiUrl;

    @ConfigProperty(name = "weatherapi.url")
    private String weatherApiUrl;

    private ObjectMapper mapper = new ObjectMapper();
    private final QueryHistoryResource queryHistoryResource;

    public DataAggregationBot(QueryHistoryResource queryHistoryResource) {
        this.queryHistoryResource = queryHistoryResource;
    }

    @Override
    public void configure() throws Exception {

        from("telegram:bots?authorizationToken=" + telegramApiToken)
            .log("Receiving from Telegram")
            .to("direct:splitter");

        from("direct:splitter")
            .process(exchange -> {
                CamelContext context = exchange.getContext();
                ProducerTemplate producerTemplate = context.createProducerTemplate();

                // Asynchronous call to internal route
                String location = exchange.getIn().getBody(String.class);
                location = location.replace(" ", "%20");

                WeatherDTO weather = producerTemplate.requestBody("direct:invokeWeatherApi", location, WeatherDTO.class);
                CovidDTO covid = producerTemplate.requestBody("direct:invokeCovidApi", location, CovidDTO.class);
                queryHistoryResource.addQuery(location, weather, covid);

                String result = "The weather in " + covid.getCityName() + " is " + weather.getMain() + " at " + weather.getTemp() + "° and a 7-day incidence rate of " + covid.getCases7day100k() + ".";

                // Do rest of the work
                exchange.getMessage().setBody(result);
                exchange.getMessage().setHeader("CamelTelegramChatId", exchange.getMessage().getHeader("CamelTelegramChatId"));
            })
            .to("telegram:bots?authorizationToken=" + telegramApiToken);

        from("direct:invokeWeatherApi").description("OpenWeatherAPI").id("weather-api")
                .process(exchange -> {
                    WeatherDTO weatherDTO = extractTempAndCondition(exchange.getMessage().getBody().toString());
                    exchange.getMessage().setBody(weatherDTO);
                } );

        from("direct:invokeCovidApi").description("COVID").id("covid-api")
                .process(exchange -> {
                    CovidDTO covidDTO = extractActiveCovidCases(exchange.getMessage().getBody().toString());
                    exchange.getMessage().setBody(covidDTO);
                } );

    }

    public CovidDTO extractActiveCovidCases(String location) {
        String url = covidApiUrl + "?dataset=covid-19-germany-landkreise&q=" + location + "&facet=last_update&facet=name&facet=rs&facet=bez&facet=bl";
        HttpResponse<JsonNode> jsonResponse = Unirest.get(url).asJson();
        String jsonString = jsonResponse.getBody().toString();
        DocumentContext jsonContext = JsonPath.parse(jsonString);
        List<Object> confirmedCases = jsonContext.read("$..['cases7_per_100k']");
        Double[] casesArray = confirmedCases.toArray(new Double[0]);
        List<Object> cityNames = jsonContext.read("$..['name']");
        String[] nameArray = cityNames.toArray(new String[0]);
        return CovidDTO.builder().cases7day100k(casesArray[0].intValue()).cityName(nameArray[0]).build();
    }

    public WeatherDTO extractTempAndCondition(String location) {
        String weatherUrl = weatherApiUrl + "?q=" + location + "&lang=en&APPID=" + weatherApiToken;
        HttpResponse<JsonNode> jsonResponse = Unirest.get(weatherUrl).asJson();
        String jsonString = jsonResponse.getBody().toString();
        DocumentContext jsonContext = JsonPath.parse(jsonString);
        // read confirmed cases
        String description = jsonContext.read("$['weather'][0]['description']");
        Double temp = jsonContext.read("$['main']['temp']");
        return WeatherDTO.builder().main(description).temp(BigDecimal.valueOf(temp.doubleValue() - 273.15d).setScale(0, RoundingMode.HALF_DOWN)).build();
    }
}