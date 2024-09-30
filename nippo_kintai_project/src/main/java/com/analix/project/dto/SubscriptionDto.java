package com.analix.project.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class SubscriptionDto {

    private Integer userId;
    private String endpoint;
    private Keys keys;
    private LocalDate createdAt;
	public String p256dh;

    // デフォルトコンストラクタ
    public SubscriptionDto() {
        this.keys = new Keys(); // keysを初期化
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Keys getKeys() {
        return keys;
    }

    public void setKeys(Keys keys) {
        this.keys = keys;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public static class Keys {
        private String p256dh;
        private String auth;

        // デフォルトコンストラクタ
        public Keys() {
            // No-args constructor
        }

        public String getP256dh() {
            return p256dh;
        }

        public void setP256dh(String p256dh) {
            this.p256dh = p256dh;
        }

        public String getAuth() {
            return auth;
        }

        // setAuthメソッドを追加
        public void setAuth(String auth) {
            this.auth = auth;
        }
    }
}
