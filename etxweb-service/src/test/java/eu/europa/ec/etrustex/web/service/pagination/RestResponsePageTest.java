package eu.europa.ec.etrustex.web.service.pagination;

import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockUsers;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RestResponsePageTest {
    @Test
    void should_create_with_all_args_constructor() {
        int totalElements = 17;
        int pageSize = 5;
        int pageNumber = 1;
        int totalPages = (int) Math.ceil((double) totalElements / (double) pageSize);

        List<User> userList = mockUsers(totalElements);
        RestResponsePage<User> userRestResponsePage = new RestResponsePage<>(
                userList,
                pageNumber,
                pageSize,
                totalElements);

        assertArrayEquals(userList.toArray(), userRestResponsePage.getContent().toArray());
        assertEquals(totalElements, userRestResponsePage.getNumberOfElements());
        assertEquals(pageSize, userRestResponsePage.getSize());
        assertEquals(totalPages, userRestResponsePage.getTotalPages());
        assertEquals(totalElements, userRestResponsePage.getTotalElements());
        assertEquals(pageSize, userRestResponsePage.getPageable().getPageSize());
        assertEquals(pageNumber, userRestResponsePage.getPageable().getPageNumber());
        assertFalse(userRestResponsePage.isFirst());
        assertFalse(userRestResponsePage.isLast());
    }

    @Test
    void should_create_with_pageable() {
        int totalElements = 17;
        int pageSize = 5;
        int pageNumber = 1;
        int totalPages = (int) Math.ceil((double) totalElements / (double) pageSize);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "name"));

        List<User> userList = mockUsers(totalElements);
        RestResponsePage<User> userRestResponsePage = new RestResponsePage<>(
                userList,
                pageRequest,
                totalElements);

        assertArrayEquals(userList.toArray(), userRestResponsePage.getContent().toArray());
        assertEquals(totalElements, userRestResponsePage.getNumberOfElements());
        assertEquals(pageSize, userRestResponsePage.getSize());
        assertEquals(totalPages, userRestResponsePage.getTotalPages());
        assertEquals(totalElements, userRestResponsePage.getTotalElements());
        assertEquals(pageRequest, userRestResponsePage.getPageable());
        assertFalse(userRestResponsePage.isFirst());
        assertFalse(userRestResponsePage.isLast());
    }

    @Test
    void should_create_with_content() {
        int totalElements = 17;
        List<User> userList = mockUsers(totalElements);
        RestResponsePage<User> userRestResponsePage = new RestResponsePage<>(userList);

        assertArrayEquals(userList.toArray(), userRestResponsePage.getContent().toArray());
        assertEquals(totalElements, userRestResponsePage.getNumberOfElements());
        assertEquals(totalElements, userRestResponsePage.getTotalElements());
        assertEquals(totalElements, userRestResponsePage.getSize());
        assertEquals(1, userRestResponsePage.getTotalPages());
        assertTrue(userRestResponsePage.isFirst());
        assertTrue(userRestResponsePage.isLast());
    }

    @Test
    void should_create_with_empty_content() {
        RestResponsePage<User> userRestResponsePage = new RestResponsePage<>();

        assertTrue(userRestResponsePage.getContent().isEmpty());
        assertEquals(0, userRestResponsePage.getNumberOfElements());
        assertEquals(0, userRestResponsePage.getTotalElements());
        assertEquals(0, userRestResponsePage.getSize());
        assertEquals(1, userRestResponsePage.getTotalPages());
    }
}
