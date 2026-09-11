package com.learning.learning_management_system.controller;

import com.learning.learning_management_system.dto.group.GroupDtoGroupName;
import com.learning.learning_management_system.dto.group.GroupDtoResponse;
import com.learning.learning_management_system.dto.group.GroupDtoSetStudents;
import com.learning.learning_management_system.service.GroupService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/group")
@AllArgsConstructor
public class GroupController {
	private final GroupService groupService;

	@GetMapping("/{id}")
	public GroupDtoResponse getGroup(@PathVariable(name = "id") Long id) {
		return groupService.getGroup(id);
	}

	@PostMapping
	public ResponseEntity<GroupDtoResponse> addGroup(@Valid @RequestBody GroupDtoGroupName groupDtoGroupName) {
		GroupDtoResponse dto = groupService.addGroup(groupDtoGroupName);
		return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}

	@PutMapping("/{id}")
	public ResponseEntity<GroupDtoResponse> updateGroup(@PathVariable(name = "id") Long id,
	                                        @Valid @RequestBody GroupDtoGroupName groupDtoGroupName) {
		GroupDtoResponse dto = groupService.updateGroup(id, groupDtoGroupName);
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<GroupDtoResponse> deleteGroup(@PathVariable(name = "id") Long id) {
		GroupDtoResponse dto = groupService.deleteGroup(id);
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}

	@PostMapping("/{group_id}")
	public ResponseEntity<GroupDtoResponse> addStudentToGroup(@PathVariable(name = "group_id") Long id,
	                                              @RequestBody GroupDtoSetStudents groupDtoSetStudents) {
		GroupDtoResponse dto = groupService.addStudentToGroup(id, groupDtoSetStudents);
		return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}
}
