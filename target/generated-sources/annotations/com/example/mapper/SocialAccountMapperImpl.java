package com.example.mapper;

import com.example.dto.CreateSocialAccountRequest;
import com.example.dto.SocialAccountResponse;
import com.example.entity.SocialAccount;
import com.example.entity.User;
import com.example.entity.enums.ConnectionStatus;
import com.example.entity.enums.Platform;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-21T15:28:56+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Red Hat, Inc.)"
)
@Component
public class SocialAccountMapperImpl implements SocialAccountMapper {

    @Override
    public SocialAccountResponse toResponse(SocialAccount socialAccount) {
        if ( socialAccount == null ) {
            return null;
        }

        UUID userId = null;
        UUID id = null;
        Platform platform = null;
        String externalAccountId = null;
        String accountName = null;
        boolean active = false;
        ConnectionStatus connectionStatus = null;
        Instant connectedAt = null;
        Instant createdAt = null;
        Instant updatedAt = null;

        userId = socialAccountUserId( socialAccount );
        id = socialAccount.getId();
        platform = socialAccount.getPlatform();
        externalAccountId = socialAccount.getExternalAccountId();
        accountName = socialAccount.getAccountName();
        active = socialAccount.isActive();
        connectionStatus = socialAccount.getConnectionStatus();
        connectedAt = socialAccount.getConnectedAt();
        createdAt = socialAccount.getCreatedAt();
        updatedAt = socialAccount.getUpdatedAt();

        SocialAccountResponse socialAccountResponse = new SocialAccountResponse( id, userId, platform, externalAccountId, accountName, active, connectionStatus, connectedAt, createdAt, updatedAt );

        return socialAccountResponse;
    }

    @Override
    public SocialAccount toEntity(CreateSocialAccountRequest request) {
        if ( request == null ) {
            return null;
        }

        SocialAccount.SocialAccountBuilder socialAccount = SocialAccount.builder();

        socialAccount.platform( request.platform() );
        socialAccount.accountName( request.accountName() );
        socialAccount.externalAccountId( request.externalAccountId() );

        return socialAccount.build();
    }

    @Override
    public void updateEntity(CreateSocialAccountRequest request, SocialAccount entity) {
        if ( request == null ) {
            return;
        }

        entity.setPlatform( request.platform() );
        entity.setAccountName( request.accountName() );
        entity.setExternalAccountId( request.externalAccountId() );
    }

    private UUID socialAccountUserId(SocialAccount socialAccount) {
        if ( socialAccount == null ) {
            return null;
        }
        User user = socialAccount.getUser();
        if ( user == null ) {
            return null;
        }
        UUID id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
