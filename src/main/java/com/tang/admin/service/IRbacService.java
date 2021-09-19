package com.tang.admin.service;

import java.util.List;

public interface IRbacService {

    List<String> findRolesByUserName(String username);

    List<String> findAuthoritiesByRoleName(List<String> roleNames);

}
