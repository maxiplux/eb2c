package app.quantun.b2b.config.mapper;

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
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setDestinationNamingConvention(LombokBuilderNamingConvention.INSTANCE)
                .setDestinationNameTransformer(LombokBuilderNameTransformer.INSTANCE);
        return modelMapper;
    }

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
