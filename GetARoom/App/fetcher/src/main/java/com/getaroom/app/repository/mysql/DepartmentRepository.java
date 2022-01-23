package com.getaroom.app.repository.mysql;

import com.getaroom.app.entity.mysql.Department;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {}
