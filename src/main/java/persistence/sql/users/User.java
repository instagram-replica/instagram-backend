package persistence.sql.users;

import java.util.Date;
import java.util.List;

public class User {
    private String id;
    private String username;
    private String email;
    private String passwordHash;
    private boolean isPrivate = false;
    private String fullName;
    private String gender;
    private String bio;
    private String phoneNumber;
    private String profilePictureUrl;
    private String websiteUrl;
    private String numberOfFollowings;
    private String numberOfFollowers;
    private String numberOfPosts;
    private Date verifiedAt;
    private Date createdAt;
    private Date updatedAt;
    private Date blockedAt;
    private Date deletedAt;
    private Date dateOfBirth;


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

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean privacy) {
        isPrivate = privacy;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
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

    public String getNumberOfFollowings() {
        return numberOfFollowings;
    }

    public void setNumberOfFollowings(String numberOfFollowings) {
        this.numberOfFollowings = numberOfFollowings;
    }

    public String getNumberOfFollowers() {
        return numberOfFollowers;
    }

    public void setNumberOfFollowers(String numberOfFollowers) {
        this.numberOfFollowers = numberOfFollowers;
    }

    public String getNumberOfPosts() {
        return numberOfPosts;
    }

    public void setNumberOfPosts(String numberOfPosts) {
        this.numberOfPosts = numberOfPosts;
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List getFollowers(){ return Main.getFollowers(this.getId()); }

    public List getFollowings(){ return Main.getFollowings(this.getId()); }

    @Override
    public String toString() {
        return "User {" +
                "\n\tid='" + id + '\'' +
                ", \n\tusername='" + username + '\'' +
                ", \n\temail='" + email + '\'' +
                ", \n\tpasswordHash='" + passwordHash + '\'' +
                ", \n\tisPrivate=" + isPrivate +
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
