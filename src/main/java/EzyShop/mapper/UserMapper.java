package EzyShop.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import EzyShop.dto.User.ProfileDto;
import EzyShop.dto.User.UserDto;
import EzyShop.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User User);

    User toEntity(UserDto userDto);

    List<UserDto> toDtoList(List<User> users);

    List<User> toEntityList(List<UserDto> UserDtos);
    
    ProfileDto toProfileDto(User user);
}
