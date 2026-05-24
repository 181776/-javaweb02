package codeking.service;

import codeking.pojo.Clazz;
import codeking.pojo.PageResult;

import java.time.LocalDate;
import java.util.List;

public interface ClazzService {

    PageResult page(Integer page, Integer pageSize, String name, LocalDate begin, LocalDate end);

    List<Clazz> findAll();

    Clazz getById(Integer id);

    void save(Clazz clazz);

    void update(Clazz clazz);

    void deleteById(Integer id);
}
