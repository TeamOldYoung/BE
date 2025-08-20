package com.app.oldYoung.domain.user.service;

import com.app.oldYoung.domain.user.dto.UserResponseDTO.UserMyPageResponseDTO;
import com.app.oldYoung.domain.user.entity.User;
import com.app.oldYoung.domain.user.repository.UserRepository;
import com.app.oldYoung.global.common.apiResponse.exception.CustomException;
import com.app.oldYoung.global.common.apiResponse.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserMyPageResponseDTO getMyPageInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Long incomeBracket = null;
        Long expBracket = null;
        if (user.getIncomeSnapshot() != null) {
            incomeBracket = user.getIncomeSnapshot().getMidRatio();
            expBracket = user.getIncomeSnapshot().getExpBracket();
        }

        return new UserMyPageResponseDTO(
                user.getMembername(),
                incomeBracket,
                expBracket,
                user.getBirthDate(),
                user.getEmail()
        );
    }
}