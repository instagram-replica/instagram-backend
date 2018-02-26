package persistence.sql.users;

import java.util.Date;



public class User {
    private String id;
    private String username;
    private String email;
    private String passwordHash;
    private boolean isPublic;
    private String fullName;
    private Gender gender;
    private String bio;
    private String phoneNumber;
    private String profilePictureUrl;
    private String websiteUrl;
    private Date verifiedAt;
    private Date createdAt;
    private Date updatedAt;
    private Date blockedAt;
    private Date deletedAt;


    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public Date getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(Date verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getBlockedAt() {
        return blockedAt;
    }

    public void setBlockedAt(Date blockedAt) {
        this.blockedAt = blockedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return "User {" +
                "\n\tid='" + id + '\'' +
                ", \n\tusername='" + username + '\'' +
                ", \n\temail='" + email + '\'' +
                ", \n\tpasswordHash='" + passwordHash + '\'' +
                ", \n\tisPublic=" + isPublic +
                ", \n\tfullName='" + fullName + '\'' +
                ", \n\tgender='" + gender + '\'' +
                ", \n\tbio='" + bio + '\'' +
                ", \n\tphoneNumber='" + phoneNumber + '\'' +
                ", \n\tprofilePictureUrl='" + profilePictureUrl + '\'' +
                ", \n\twebsiteUrl='" + websiteUrl + '\'' +
                ", \n\tverifiedAt=" + verifiedAt +
                ", \n\tcreatedAt=" + createdAt +
                ", \n\tupdatedAt=" + updatedAt +
                ", \n\tblockedAt=" + blockedAt +
                ", \n\tdeletedAt=" + deletedAt +
                "\n}";
    }
}
