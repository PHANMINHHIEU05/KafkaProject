package com.example.service;

import com.example.dto.CreateSocialAccountRequest;
import com.example.dto.SocialAccountResponse;
import com.example.entity.OrganizationMember;
import com.example.entity.SocialAccount;
import com.example.exception.ConflictException;
import com.example.exception.ErrorCode;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.SocialAccountMapper;
import com.example.repository.SocialAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocialAccountService {

    private final SocialAccountRepository socialAccountRepository;
    private final SocialAccountMapper socialAccountMapper;
    private final OrganizationMemberService organizationMemberService;
    private final AuthorizationService authorizationService;

    public SocialAccountService(
        SocialAccountRepository socialAccountRepository,
        SocialAccountMapper socialAccountMapper,
        OrganizationMemberService organizationMemberService,
        AuthorizationService authorizationService
    ) {
        this.socialAccountRepository = socialAccountRepository;
        this.socialAccountMapper = socialAccountMapper;
        this.organizationMemberService = organizationMemberService;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public SocialAccountResponse createSocialAccount(
        CreateSocialAccountRequest request
    ) {
        authorizationService.requirePermission("SOCIAL_ACCOUNT_CONNECT");
        OrganizationMember currentMember = organizationMemberService.getCurrentMember();
        var user = currentMember.getUser();

        if (socialAccountRepository.existsByOrgIdAndPlatformAndExternalAccountId(
            currentMember.getOrganization().getId(),
            request.platform(),
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
        socialAccount.setOrganization(currentMember.getOrganization());
        socialAccount.setUser(user);

        return socialAccountMapper.toResponse(socialAccountRepository.save(socialAccount));
    }

    public List<SocialAccountResponse> findActiveAccounts() {
        authorizationService.requirePermission("SOCIAL_ACCOUNT_READ");
        OrganizationMember currentMember = organizationMemberService.getCurrentMember();

        return socialAccountRepository.findActiveAccountsByOrgIdAndUserId(
                currentMember.getOrganization().getId(),
                currentMember.getUser().getId()
            )
            .stream()
            .map(socialAccountMapper::toResponse)
            .toList();
    }
    public void disconnectSocialAccount(Integer accountId) {
        authorizationService.requirePermission("SOCIAL_ACCOUNT_DISCONNECT");
        OrganizationMember currentMember = organizationMemberService.getCurrentMember();

        var socialAccount = socialAccountRepository.findByIdAndOrgId(
                accountId,
                currentMember.getOrganization().getId()
            )
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND,
                "Social account not found with id: " + accountId
            ));
        socialAccountRepository.delete(socialAccount);
    }
}
