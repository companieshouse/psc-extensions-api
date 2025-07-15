package uk.gov.companieshouse.psc.extensions.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.psc.extensions.common.TestDataManager;
import uk.gov.companieshouse.psc.extensions.exceptions.InternalServerErrorRuntimeException;
import uk.gov.companieshouse.api.accounts.user.model.User;

@ExtendWith( MockitoExtension.class )
@Tag( "unit-test" )
class ParsingUtilTest {

    private static final TestDataManager testDataManager = TestDataManager.getInstance();

    @Test
    void parseJsonToSuccessfullyParsesToSpecifiedClass() throws JsonProcessingException {
        final var user = testDataManager.fetchUserDtos( "MKUser001" ).getFirst();
        final var json = new ObjectMapper().writeValueAsString( user );
        Assertions.assertEquals( "Mario" , ParsingUtil.parseJsonTo( User.class ).apply( json ).getDisplayName() );
    }

    @Test
    void parseJsonToWithArbitraryErrorThrowsInternalServerErrorRuntimeException( ){
        Assertions.assertThrows( InternalServerErrorRuntimeException.class, () -> ParsingUtil.parseJsonTo( User.class ).apply( "}{" ) );
    }


}
