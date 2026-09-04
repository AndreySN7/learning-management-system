package com.learning.learning_management_system.mapper;

import com.learning.learning_management_system.dto.group.GroupDtoGroupName;
import com.learning.learning_management_system.dto.group.GroupDtoResponse;
import com.learning.learning_management_system.entity.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GroupMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "students", ignore = true)
	Group toEntityWithGroupName(GroupDtoGroupName groupDtoGroupName);

	@Mapping(target = "students", ignore = true)
	GroupDtoResponse toDto(Group group);
}
