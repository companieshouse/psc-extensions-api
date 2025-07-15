package uk.gov.companieshouse.psc.extensions.api.common;


import uk.gov.companieshouse.api.accounts.user.model.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class TestDataManager {

    private static TestDataManager instance = null;

    public static TestDataManager getInstance(){
        if ( Objects.isNull( instance ) ){
            instance = new TestDataManager();
        }
        return instance;
    }

    private final Map<String, Supplier<User>> userDtoSuppliers = new HashMap<>();

    private void instantiateUserDtoSuppliers(){
        userDtoSuppliers.put( "MKUser001", () -> new User().userId( "MKUser001" ).email( "mario@mushroom.kingdom" ).displayName( "Mario" ) );
        userDtoSuppliers.put( "MKUser002", () -> new User().userId( "MKUser002" ).email( "luigi@mushroom.kingdom" ).displayName( "Luigi" ) );
        userDtoSuppliers.put( "MKUser003", () -> new User().userId( "MKUser003" ).email( "peach@mushroom.kingdom" ).displayName( "Peach" ) );
    }


    private TestDataManager(){
        instantiateUserDtoSuppliers();
    }

    public List<User> fetchUserDtos( final String... ids  ){
        return Arrays.stream( ids )
                .map( userDtoSuppliers::get )
                .map( Supplier::get )
                .collect( Collectors.toList() );
    }

}
