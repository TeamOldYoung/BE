package com.app.oldYoung.domain.welfare.repository;

import com.app.oldYoung.domain.welfare.entity.WelfareItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WelfareRepository extends JpaRepository<WelfareItem, Long> {
    
    List<WelfareItem> findByCity(String city);
    
    List<WelfareItem> findByAge(Integer age);
}
