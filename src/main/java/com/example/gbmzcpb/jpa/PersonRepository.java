package com.example.gbmzcpb.jpa;

import com.example.gbmzcpb.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Integer> {//INTEGER 由主键类型决定
    Person findByName(String name);  // Spring Data JPA 会自动实现这个方法
    List<Person> findByNameContaining(String name);// 根据名字包含的字符进行模糊查询
    
    List<Person> findByNameContainingAndAge(String name, Integer age);
    List<Person> findByNameContainingAndSex(String name, String sex);
    List<Person> findByAgeAndSex(Integer age, String sex);
    List<Person> findByNameContainingAndAgeAndSex(String name, Integer age, String sex);
    List<Person> findByAge(Integer age);
    List<Person> findBySex(String sex);
    List<Person>  findBySatisfactionItems_Id(Integer id);
}

