package app.quantun.eb2c.config.mapper;

import org.modelmapper.internal.util.Strings;
import org.modelmapper.spi.NameTransformer;
import org.modelmapper.spi.NameableType;

public class LombokBuilderNameTransformer implements NameTransformer {

    public static final NameTransformer INSTANCE = new LombokBuilderNameTransformer();

    @Override
    public String transform(final String name, final NameableType nameableType) {
        return Strings.decapitalize(name);
    }

    @Override
    public String toString() {
        return "Lombok @Builder Mutator";
    }
}
