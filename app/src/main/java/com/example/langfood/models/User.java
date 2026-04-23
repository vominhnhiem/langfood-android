package com.example.langfood.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private String id;

    @SerializedName("username")
    private String username;

    @SerializedName("passwordHash")
    private String passwordHash;

    @SerializedName("roleId")
    private int roleId;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("ktxBuilding")
    private String ktxBuilding;

    @SerializedName("ktxRoom")
    private String ktxRoom;

    @SerializedName("isVerifiedResident")
    private boolean isVerifiedResident;

    @SerializedName("studentCardImageUrl")
    private String studentCardImageUrl;

    // Constructor không tham số (Bắt buộc)
    public User() {}

    // Constructor dùng khi Đăng nhập (chỉ cần username/password)
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    // --- GETTER AND SETTER ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getKtxBuilding() { return ktxBuilding; }
    public void setKtxBuilding(String ktxBuilding) { this.ktxBuilding = ktxBuilding; }

    public String getKtxRoom() { return ktxRoom; }
    public void setKtxRoom(String ktxRoom) { this.ktxRoom = ktxRoom; }

    public boolean isVerifiedResident() { return isVerifiedResident; }
    public void setVerifiedResident(boolean verifiedResident) { isVerifiedResident = verifiedResident; }

    public String getStudentCardImageUrl() { return studentCardImageUrl; }
    public void setStudentCardImageUrl(String studentCardImageUrl) { this.studentCardImageUrl = studentCardImageUrl; }
}