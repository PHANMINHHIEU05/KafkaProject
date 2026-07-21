package com.example.entity.enums;

public enum OutboxStatus {
    NEW,
    PROCESSING,
    RETRY_WAIT,
    PUBLISHED,
    DEAD
}
