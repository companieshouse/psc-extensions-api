package uk.gov.companieshouse.psc.extensions.api.service.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Array;
import java.util.EnumSet;
import uk.gov.companieshouse.psc.extensions.api.enumerations.PscType;

public class TestBaseService {

    static final String COMPANY_NUMBER = "12345678";
    static Class<PscType> enumPscType = PscType.class;

    //Use to mock out the PscType enum class
    @SuppressWarnings("unchecked")
    static <T extends Enum<T>> T[] addNewEnumValue() {
        final EnumSet<T> enumSet = EnumSet.allOf((Class<T>) enumPscType);
        final T[] newValues = (T[]) Array.newInstance(enumPscType, enumSet.size() + 1);
        int i = 0;
        for (final T value : enumSet) {
            newValues[i] = value;
            i++;
        }

        final T newEnumValue = mock((Class<T>) enumPscType);
        newValues[newValues.length - 1] = newEnumValue;

        when(newEnumValue.ordinal()).thenReturn(newValues.length - 1);

        return newValues;
    }
}
