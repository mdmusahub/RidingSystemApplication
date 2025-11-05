package com.mecaps.ridingBookingSystem.security;

import com.mecaps.ridingBookingSystem.entity.TokenBlackList;
import com.mecaps.ridingBookingSystem.repository.TokenBlackListRepository;
import org.springframework.stereotype.Service;

@Service
public class TokenBlackListService {
    private final TokenBlackListRepository tokenBlackListRepository;

    public TokenBlackListService(TokenBlackListRepository tokenBlackListRepository){
        this.tokenBlackListRepository = tokenBlackListRepository;
    }

    public void blackListToken(String token){
        if(!tokenBlackListRepository.existsByBlackListedToken(token)){
            TokenBlackList tokenBlackList = new TokenBlackList();
            tokenBlackList.setBlackListedToken(token);

            tokenBlackListRepository.save(tokenBlackList);
        }
    }

    public boolean isBlacklisted(String token){
        return tokenBlackListRepository.existsByBlackListedToken(token);
    }
}
