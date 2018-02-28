package persistence.sql.users;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static persistence.sql.Main.openConnection;
import static persistence.sql.users.Main.createUser;

public class UsersTests {

    @Test
    public void TestCreateUser(){
        try {
            openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        User dummy = new User();
        dummy.setUsername("hamada");
        dummy.setPhoneNumber("0100");
        dummy.setPrivate(true);
        dummy.setGender("male");
        dummy.setDateOfBirth(new Date(311294));
        dummy.setPasswordHash("12!@#RF1wd1@#");
        dummy.setEmail("hamada@g.c");
        dummy.setBio("7ob gamed");
        dummy.setFullName("Hamada ta7aroosh");

        boolean created = createUser(dummy);
        Assert.assertTrue(created);
    }

}
