package com.eden.orchid.api.options;

import com.eden.common.util.EdenUtils;
import com.eden.orchid.api.converters.BooleanConverter;
import com.eden.orchid.api.converters.ClogStringConverterHelper;
import com.eden.orchid.api.converters.Converters;
import com.eden.orchid.api.converters.DoubleConverter;
import com.eden.orchid.api.converters.ExtractableConverter;
import com.eden.orchid.api.converters.FlexibleIterableConverter;
import com.eden.orchid.api.converters.FlexibleMapConverter;
import com.eden.orchid.api.converters.FloatConverter;
import com.eden.orchid.api.converters.IntegerConverter;
import com.eden.orchid.api.converters.LongConverter;
import com.eden.orchid.api.converters.NumberConverter;
import com.eden.orchid.api.converters.StringConverter;
import com.eden.orchid.api.converters.StringConverterHelper;
import com.eden.orchid.api.converters.TypeConverter;
import com.eden.orchid.api.options.extractors.AnyOptionExtractor;
import com.eden.orchid.api.options.extractors.BooleanOptionExtractor;
import com.eden.orchid.api.options.extractors.DoubleOptionExtractor;
import com.eden.orchid.api.options.extractors.EnumOptionExtractor;
import com.eden.orchid.api.options.extractors.ExtractableOptionExtractor;
import com.eden.orchid.api.options.extractors.FloatOptionExtractor;
import com.eden.orchid.api.options.extractors.IntOptionExtractor;
import com.eden.orchid.api.options.extractors.ListOptionExtractor;
import com.eden.orchid.api.options.extractors.LongOptionExtractor;
import com.eden.orchid.api.options.extractors.StringArrayOptionExtractor;
import com.eden.orchid.api.options.extractors.StringOptionExtractor;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AndroidExtractor {

    private AndroidExtractor() {
    }

    private static Extractor instance;

    public static void initialize(Extractor instance) {
        if (AndroidExtractor.instance != null) {
            throw new IllegalStateException("AndroidExtractor is already setup!");
        }

        AndroidExtractor.instance = instance;
    }

    public static Extractor getInstance() {
        if (instance == null) {
            instance = Extractor.builder()
                    .instanceCreator(new DefaultInstanceCreator())
                    .extractors(
                            getExtractors(
                                    new Provider<Extractor>() {
                                        @Override
                                        public Extractor get() {
                                            return instance;
                                        }
                                    },
                                    null,
                                    null
                            )
                    )
                    .build();
        }
        return instance;
    }

    public static List<OptionExtractor> getExtractors(
            Provider<Extractor> extractorProvider,
            Collection<TypeConverter<?>> extraConverters,
            Collection<OptionExtractor<?>> extraExtractors
    ) {
        // String converter Helpers
        Set<StringConverterHelper> stringConverterHelpers = new HashSet<>();
        stringConverterHelpers.add(new ClogStringConverterHelper());

        // TypeConverters
        StringConverter stringConverter = new StringConverter(stringConverterHelpers);
        LongConverter longConverter = new LongConverter(stringConverter);
        IntegerConverter integerConverter = new IntegerConverter(stringConverter);
        DoubleConverter doubleConverter = new DoubleConverter(stringConverter);
        FloatConverter floatConverter = new FloatConverter(stringConverter);
        NumberConverter numberConverter = new NumberConverter(longConverter, doubleConverter);
        BooleanConverter booleanConverter = new BooleanConverter(stringConverter, numberConverter);
        FlexibleMapConverter flexibleMapConverter = new FlexibleMapConverter();
        FlexibleIterableConverter flexibleIterableConverter = new FlexibleIterableConverter(flexibleMapConverter);
        ExtractableConverter extractableConverter = new ExtractableConverter(extractorProvider, flexibleMapConverter);

        Set<TypeConverter> typeConverters = new HashSet<>();
        typeConverters.add(stringConverter);
        typeConverters.add(longConverter);
        typeConverters.add(integerConverter);
        typeConverters.add(doubleConverter);
        typeConverters.add(floatConverter);
        typeConverters.add(numberConverter);
        typeConverters.add(booleanConverter);
        typeConverters.add(flexibleMapConverter);
        typeConverters.add(flexibleIterableConverter);
        typeConverters.add(extractableConverter);

        if (!EdenUtils.isEmpty(extraConverters)) {
            typeConverters.addAll(extraConverters);
        }

        Converters converters = new Converters(typeConverters);

        // OptionExtractor
        List<OptionExtractor> extractors = new ArrayList<>();
        extractors.add(new AnyOptionExtractor());
        extractors.add(new StringArrayOptionExtractor(flexibleIterableConverter, converters));
        extractors.add(new BooleanOptionExtractor(booleanConverter));
        extractors.add(new DoubleOptionExtractor(doubleConverter));
        extractors.add(new FloatOptionExtractor(floatConverter));
        extractors.add(new IntOptionExtractor(integerConverter));
        extractors.add(new ListOptionExtractor(extractorProvider, flexibleIterableConverter, flexibleMapConverter, converters));
        extractors.add(new LongOptionExtractor(longConverter));
        extractors.add(new StringOptionExtractor(stringConverter));
        extractors.add(new EnumOptionExtractor(stringConverter));
        extractors.add(new ExtractableOptionExtractor(extractableConverter));

        if (!EdenUtils.isEmpty(extraExtractors)) {
            extractors.addAll(extraExtractors);
        }

        return extractors;
    }
}
