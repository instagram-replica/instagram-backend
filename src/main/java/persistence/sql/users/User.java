package persistence.sql.users;

import java.util.Date;

public class User {
    public final String id;
    public final String username;
    public final String email;
    public final String password;
    public final String passwordHash;
    public final boolean isPrivate;
    public final String fullName;
    public final String gender;
    public final String bio;
    public final String phoneNumber;
    public final String profilePictureUrl;
    public final String websiteUrl;
    public final Date verifiedAt;
    public final Date createdAt;
    public final Date updatedAt;
    public final Date blockedAt;
    public final Date deletedAt;

    public User(
            String id,
            String username,
            String email,
            String password,
            String passwordHash,
            boolean isPrivate,
            String fullName,
            String gender,
            String bio,
            String phoneNumber,
            String profilePictureUrl,
            String websiteUrl,
            Date verifiedAt,
            Date createdAt,
            Date updatedAt,
            Date blockedAt,
            Date deletedAt
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.passwordHash = passwordHash;
        this.isPrivate = isPrivate;
        this.fullName = fullName;
        this.gender = gender;
        this.bio = bio;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = profilePictureUrl;
        this.websiteUrl = websiteUrl;
        this.verifiedAt = verifiedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.blockedAt = blockedAt;
        this.deletedAt = deletedAt;
    }

    public static class Builder {
        private String id;
        private String username;
        private String email;
        private String password;
        private String passwordHash;
        private boolean isPrivate;
        private String fullName;
        private String gender;
        private String bio;
        private String phoneNumber;
        private String profilePictureUrl;
        private String websiteUrl;
        private Date verifiedAt;
        private Date createdAt;
        private Date updatedAt;
        private Date blockedAt;
        private Date deletedAt;

        public Builder() {}

        public Builder (User user) {
            this.id = user.id;
            this.username = user.username;
            this.email = user.email;
            this.password = user.password;
            this.passwordHash = user.passwordHash;
            this.isPrivate = user.isPrivate;
            this.fullName = user.fullName;
            this.gender = user.gender;
            this.bio = user.bio;
            this.phoneNumber = user.phoneNumber;
            this.profilePictureUrl = user.profilePictureUrl;
            this.websiteUrl = user.websiteUrl;
            this.verifiedAt = user.verifiedAt;
            this.createdAt = user.createdAt;
            this.updatedAt = user.updatedAt;
            this.blockedAt = user.blockedAt;
            this.deletedAt = user.deletedAt;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder isPrivate(boolean isPrivate) {
            this.isPrivate = isPrivate;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder profilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
            return this;
        }

        public Builder websiteUrl(String websiteUrl) {
            this.websiteUrl = websiteUrl;
            return this;
        }

        public Builder verifiedAt(Date verifiedAt) {
            this.verifiedAt = verifiedAt;
            return this;
        }

        public Builder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder blockedAt(Date blockedAt) {
            this.blockedAt = blockedAt;
            return this;
        }

        public Builder deletedAt(Date deletedAt) {
            this.deletedAt = deletedAt;
            return this;
        }

        public User build() {
            return new User(
                    this.id,
                    this.username,
                    this.email,
                    this.password,
                    this.passwordHash,
                    this.isPrivate,
                    this.fullName,
                    this.gender,
                    this.bio,
                    this.phoneNumber,
                    this.profilePictureUrl,
                    this.websiteUrl,
                    this.verifiedAt,
                    this.createdAt,
                    this.updatedAt,
                    this.blockedAt,
                    this.deletedAt
            );
        }
    }

    @Override
    public String toString() {
        return "User {" +
                "\n\tid='" + id + '\'' +
                ", \n\tusername='" + username + '\'' +
                ", \n\temail='" + email + '\'' +
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
