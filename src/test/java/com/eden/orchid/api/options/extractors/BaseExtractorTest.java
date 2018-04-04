package com.eden.orchid.api.options.extractors;

import com.eden.orchid.api.converters.Converters;
import com.eden.orchid.api.converters.FlexibleIterableConverter;
import com.eden.orchid.api.converters.FlexibleMapConverter;
import com.eden.orchid.api.converters.TypeConverter;
import com.eden.orchid.api.options.Extractor;
import com.eden.orchid.api.options.OptionExtractor;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public abstract class BaseExtractorTest {

    protected Extractor extractor;

    public void setupTest(OptionExtractor extractorUnderTest, TypeConverter... converters) {
        FlexibleMapConverter mapConverter = new FlexibleMapConverter();
        FlexibleIterableConverter iterableConverter = new FlexibleIterableConverter(() -> mapConverter);

        List<OptionExtractor> extractors = new ArrayList<>();
        extractors.add(extractorUnderTest);

        Converters converters1 = new Converters(new HashSet<>(Arrays.asList(converters)));

        extractors.add(new ListOptionExtractor(iterableConverter, converters1));
        extractors.add(new ArrayOptionExtractor(iterableConverter, converters1));

        extractor = new Extractor(extractors, null) {
            @Override
            protected <T> T getInstance(Class<T> clazz) {
                return null;
            }
        };
    }

    public void testExtractOption(
            final Object underTest,
            final Object sourceValue,
            final Object expectedOriginalValue,
            final Object expectedExtractedValue) throws Throwable {

        String optionName = "testValue";

        final JSONObject options = new JSONObject();
        if(sourceValue != null) {
            if(sourceValue.toString().equals("_nullValue")) {
                options.put(optionName, (String) null);
            }
            else {
                options.put(optionName, sourceValue);
            }
        }

        assertThat(underTest.getClass().getField(optionName).get(underTest), is(equalTo(expectedOriginalValue)));
        extractor.extractOptions(underTest, options);
        assertThat(underTest.getClass().getField(optionName).get(underTest), is(equalTo(expectedExtractedValue)));
    }

    public void testExtractOptionList(
            final Object underTest,
            final Object sourceValue,
            final Object[] expectedExtractedValue) throws Throwable {

        String optionName = "testValue";

        final JSONObject options = new JSONObject();
        if(sourceValue != null) {
            if(sourceValue.toString().equals("_nullValue")) {
                options.put(optionName, (String) null);
            }
            else {
                options.put(optionName, sourceValue);
            }
        }

        assertThat(underTest.getClass().getField(optionName).get(underTest), is(equalTo(null)));
        extractor.extractOptions(underTest, options);
        assertThat((Iterable<Object>) underTest.getClass().getField(optionName).get(underTest), containsInAnyOrder(expectedExtractedValue));
    }

    public void testExtractOptionArray(
            final Object underTest,
            final Object sourceValue,
            final Object[] expectedExtractedValue) throws Throwable {

        String optionName = "testValue";

        final JSONObject options = new JSONObject();
        if(sourceValue != null) {
            if(sourceValue.toString().equals("_nullValue")) {
                options.put(optionName, (String) null);
            }
            else {
                options.put(optionName, sourceValue);
            }
        }

        assertThat(underTest.getClass().getField(optionName).get(underTest), is(equalTo(null)));
        extractor.extractOptions(underTest, options);
        assertThat((Object[]) underTest.getClass().getField(optionName).get(underTest), arrayContainingInAnyOrder(expectedExtractedValue));
    }

    public void testOptionDescription(
            final Object underTest,
            final String expectedDescription) throws Throwable {

        String optionName = "testValue";

        String description = extractor.describeOption(underTest.getClass(), optionName);
        assertThat(description, is(equalTo(expectedDescription)));
    }

}
