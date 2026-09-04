package com.learning.learning_management_system.service;

import com.learning.learning_management_system.dto.group.GroupDtoGroupName;
import com.learning.learning_management_system.dto.group.GroupDtoResponse;
import com.learning.learning_management_system.dto.group.GroupDtoSetStudents;

public interface GroupService {

	GroupDtoResponse getGroup(Long id);

	void addGroup(GroupDtoGroupName groupDtoGroupName);

	void updateGroup(Long id, GroupDtoGroupName groupDtoGroupName);

	void deleteGroup(Long id);

	void addStudentToGroup(Long groupId, GroupDtoSetStudents students);
}
