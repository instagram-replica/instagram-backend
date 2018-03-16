package persistence.sql.users;

public enum Gender {
    MALE("male"), FEMALE("female"), UNDEFINED("undefined");
    String gender;

    Gender(String gender) {
        this.gender = gender;
    }
}
