package com.app.oldYoung.domain.welfare.service;

import com.app.oldYoung.domain.welfare.entity.WelfareItem;
import com.app.oldYoung.domain.welfare.repository.WelfareRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WelfareService {
    
    private final WelfareRepository welfareRepository;
    
    public List<WelfareItem> getWelfareItemsByCity(String city) {
        log.info("지역별 복지 서비스 조회: city={}", city);
        return welfareRepository.findByCity(city);
    }
    
    public List<WelfareItem> getWelfareItemsByAge(Integer age) {
        log.info("나이별 복지 서비스 조회: age={}", age);
        return welfareRepository.findByAge(age);
    }
}