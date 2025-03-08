package app.quantun.b2b.config.mapper;

import org.modelmapper.spi.NamingConvention;
import org.modelmapper.spi.PropertyType;

public class LombokBuilderNamingConvention implements NamingConvention {

    public static LombokBuilderNamingConvention INSTANCE = new LombokBuilderNamingConvention();

    @Override
    public boolean applies(String propertyName, PropertyType propertyType) {
        return PropertyType.METHOD.equals(propertyType);
    }

    @Override
    public String toString() {
        return "Lombok @Builder Naming Convention";
    }

}
