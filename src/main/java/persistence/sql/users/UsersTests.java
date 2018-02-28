package persistence.sql.users;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static persistence.sql.Main.closeConnection;
import static persistence.sql.Main.openConnection;
import static persistence.sql.users.Main.createUser;
import static persistence.sql.users.Main.getUserByUsername;
import static utilities.Main.generateUUID;

public class UsersTests {

    @Test
    public void TestCreateUser() throws IOException {
        openConnection();

        User dummy = new User();

        dummy.setId(generateUUID());
        dummy.setUsername("mpohohho");
        dummy.setPhoneNumber("0f012345789");
        dummy.setPrivate(true);
        dummy.setGender("male");
        dummy.setDateOfBirth(new Date(311294));
        dummy.setPasswordHash("12!@#RF1wd1@#");
        dummy.setEmail("mahameehoo@homail.com");
        dummy.setBio("7ob gamed");
        dummy.setFullName("Hamada ta7aroosh");
        dummy.setCreatedAt(new java.util.Date());

        boolean created = createUser(dummy);

        Assert.assertEquals(true, created);

        closeConnection();
    }

}
