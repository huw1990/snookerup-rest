package com.huwdunnit.snookeruprest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Models a pageable list of users.
 *
 * @author Huwdunnit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponse {

    private List<User> users;

    private Integer pageSize;

    private Integer pageNumber;

    private Integer totalPages;

    private Long totalItems;

    public UserListResponse(Page<User> pageOfUsers) {
        this.users = pageOfUsers.getContent();
        this.pageSize = pageOfUsers.getSize();
        this.pageNumber = pageOfUsers.getNumber();
        this.totalPages = pageOfUsers.getTotalPages();
        this.totalItems = pageOfUsers.getTotalElements();
    }
}
