package com.example.service;

import com.example.dto.CreateSocialAccountRequest;
import com.example.dto.SocialAccountResponse;
import com.example.entity.SocialAccount;
import com.example.exception.ConflictException;
import com.example.exception.ErrorCode;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.SocialAccountMapper;
import com.example.repository.SocialAccountRepository;
import com.example.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocialAccountService {

    private final SocialAccountRepository socialAccountRepository;
    private final UserRepository userRepository;
    private final SocialAccountMapper socialAccountMapper;

    public SocialAccountService(
        SocialAccountRepository socialAccountRepository,
        UserRepository userRepository,
        SocialAccountMapper socialAccountMapper
    ) {
        this.socialAccountRepository = socialAccountRepository;
        this.userRepository = userRepository;
        this.socialAccountMapper = socialAccountMapper;
    }

    @Transactional
    public SocialAccountResponse createSocialAccount(
        Integer userId,
        CreateSocialAccountRequest request
    ) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.USER_NOT_FOUND,
                "User not found with id: " + userId
            ));

        if (socialAccountRepository.existsByPlatformAndExternalAccountId(
            request.platform().name(),
            request.externalAccountId()
        )) {
            throw new ConflictException(
                ErrorCode.DUPLICATE_SOCIAL_ACCOUNT,
                "Social account already exists for platform: "
                    + request.platform()
                    + ", externalAccountId: "
                    + request.externalAccountId()
            );
        }

        SocialAccount socialAccount = socialAccountMapper.toEntity(request);
        socialAccount.setUser(user);

        return socialAccountMapper.toResponse(socialAccountRepository.save(socialAccount));
    }

    public List<SocialAccountResponse> findActiveAccounts(Integer userId) {
        return socialAccountRepository.findActiveAccountsByUserId(userId)
            .stream()
            .map(socialAccountMapper::toResponse)
            .toList();
    }
    public void disconnectSocialAccount(Integer userId, Integer accountId) {
        var socialAccount = socialAccountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND,
                "Social account not found with id: " + accountId
            ));
        socialAccountRepository.delete(socialAccount);
    }
}
