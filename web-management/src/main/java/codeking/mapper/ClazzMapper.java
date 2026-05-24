package codeking.mapper;

import codeking.pojo.Clazz;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ClazzMapper {

    List<Clazz> list(String name, LocalDate begin, LocalDate end);

    void insert(Clazz clazz);

    void updateById(Clazz clazz);

    Clazz getById(Integer id);

    void deleteById(Integer id);

    List<Clazz> findAll();
}
