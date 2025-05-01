package app.quantun.eb2c.config.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.time.format.DateTimeFormatter;

@Configuration
public class ModelMapperConfig {

    /**
     * Configures and provides a ModelMapper bean.
     *
     * This method sets up the ModelMapper with strict matching strategy and custom naming conventions.
     * The strict matching strategy ensures that only exact matches between source and destination properties are mapped.
     * The custom naming conventions are used to handle Lombok @Builder methods.
     *
     * @return ModelMapper instance
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setDestinationNamingConvention(LombokBuilderNamingConvention.INSTANCE)
                .setDestinationNameTransformer(LombokBuilderNameTransformer.INSTANCE);
        return modelMapper;
    }

    /**
     * Configures and provides a MappingJackson2HttpMessageConverter bean.
     *
     * This method customizes the Jackson ObjectMapper to disable writing dates as timestamps,
     * use a specific date-time format, and include non-null properties only.
     *
     * @return MappingJackson2HttpMessageConverter instance
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        Jackson2ObjectMapperBuilder builder =
                new Jackson2ObjectMapperBuilder()
                        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .serializers(
                                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")))
                        .serializationInclusion(JsonInclude.Include.NON_NULL);
        return new MappingJackson2HttpMessageConverter(builder.build());
    }

    /**
     * Customizes the Jackson2ObjectMapperBuilder.
     *
     * This method adds the JavaTimeModule to handle Java 8 date and time types,
     * disables writing dates as timestamps, sets a specific date-time format,
     * and configures the ObjectMapper to include non-null properties only.
     *
     * @return Jackson2ObjectMapperBuilderCustomizer instance
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.modules(new JavaTimeModule());
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            builder.simpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            builder.failOnUnknownProperties(false);
            builder.failOnEmptyBeans(false);
            builder.indentOutput(true);
        };
    }
}
