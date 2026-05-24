package codeking.service;

import codeking.pojo.Dept;

import java.util.List;

public interface DeptService {
    List<Dept> findAll();

    void deleteById(Integer id);

    Dept getById(Integer id);

    void update(Dept dept);

    void save(Dept dept);
}
