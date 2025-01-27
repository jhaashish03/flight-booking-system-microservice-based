package com.ewallet.userservice.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, builder = @Builder(disableBuilder = true))
public interface AccountMapper {

//    @Mapping(target = "role", ignore = true)
//    @Mapping(target = "authorities",ignore = true)
//    @Mapping(target = "lastModifiedDate", ignore = true)
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "createdDate", ignore = true)
//    @Mapping(target = "authority", ignore = true)
//    @Mapping(target = "firstName",source = "accountCreationDto.firstName")
//    @Mapping(target = "lastName",source = "accountCreationDto.lastName")
//    @Mapping(target = "email",source = "accountCreationDto.email")
//    @Mapping(target = "password",source = "accountCreationDto.password")
//    @Mapping(target = MappingConstants.ANY_REMAINING,ignore = true)
//    Account accountCreationDtoToAccount(AccountCreationDto accountCreationDto);


}
