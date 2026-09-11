package com.learning.learning_management_system.service;

import com.learning.learning_management_system.dto.group.GroupDtoGroupName;
import com.learning.learning_management_system.dto.group.GroupDtoResponse;
import com.learning.learning_management_system.dto.group.GroupDtoSetStudents;

public interface GroupService {

	GroupDtoResponse getGroup(Long id);

	GroupDtoResponse addGroup(GroupDtoGroupName groupDtoGroupName);

	GroupDtoResponse updateGroup(Long id, GroupDtoGroupName groupDtoGroupName);

	GroupDtoResponse deleteGroup(Long id);

	GroupDtoResponse addStudentToGroup(Long groupId, GroupDtoSetStudents students);
}
