package persistence.sql.users;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static persistence.sql.Main.closeConnection;
import static persistence.sql.Main.openConnection;

import static persistence.sql.users.Main.*;

import static utilities.Main.generateUUID;

public class UsersTests {

    @Test
    public void TestCreateAndDeleteUser() throws IOException {
        openConnection();

        User dummy = new User();
        dummy.setId(generateUUID());
        dummy.setUsername("hamadaHelal");
        dummy.setPhoneNumber("010123456789");
        dummy.setPrivate(true);
        dummy.setGender("male");
        dummy.setDateOfBirth(new Date(311294));
        dummy.setPasswordHash("12!@#RF1wd1@#");
        dummy.setEmail("hamadaHelal@gmail.com");
        dummy.setBio("7ob gamed");
        dummy.setFullName("Hamada ta7aroosh");
        dummy.setCreatedAt(new java.util.Date());

        boolean created = createUser(dummy);
        Assert.assertEquals(true, created);

        boolean deleted = deleteUser(dummy.getId());
        Assert.assertEquals(true, deleted);
        closeConnection();
    }

    @Test
    public void TestUpdateUser() throws IOException{
        openConnection();

        User dummy = new User();
        dummy.setId(generateUUID());

        dummy.setUsername("hamada9");
        dummy.setPhoneNumber("010123456789");
        dummy.setPrivate(true);
        dummy.setGender("male");
        dummy.setDateOfBirth(new Date(311294));
        dummy.setPasswordHash("12!@#RF1wd1@#");

        dummy.setEmail("hamada9@gmail.com");
        dummy.setBio("7ob gamed");
        dummy.setFullName("Hamada ta7aroosh");
        dummy.setCreatedAt(new java.util.Date());

        //create user
        boolean created = createUser(dummy);

        Assert.assertEquals(true, created);

        //update user and check for updated
        dummy.setEmail("hamadaTa7arosh@gmail.com");
        updateUser(dummy.getId(), dummy);
        Assert.assertEquals(dummy.getEmail(), getUserById(dummy.getId()).getEmail());

        //delete user
        boolean deleted = deleteUser(dummy.getId());
        Assert.assertEquals(true, deleted);
        closeConnection();
    }

    @Test
    public void TestBlock() throws  IOException{
        openConnection();

        User user1 = getAllUsers().get(0);
        User user2 = getAllUsers().get(1);
        boolean blocked = blockUser(user1.getId(), user2.getId());
        Assert.assertEquals(true, blocked);
        Assert.assertEquals(true, blocks(user1.getId(), user2.getId()));
        Assert.assertEquals(false, blocks(user2.getId(), user1.getId()));

        closeConnection();
    }

    @Test
    public void TestReport() throws  IOException{
        openConnection();

        User user1 = getAllUsers().get(1);
        User user2 = getAllUsers().get(2);
        boolean reported = reportUser(user1.getId(), user2.getId());
        Assert.assertEquals(true, reported);
        Assert.assertEquals(true, reports(user1.getId(), user2.getId()));
        Assert.assertEquals(false, reports(user2.getId(), user1.getId()));

        closeConnection();
    }
}
