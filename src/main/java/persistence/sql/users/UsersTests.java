package persistence.sql.users;

import org.junit.Assert;
import org.junit.Test;
import persistence.sql.users.Models.UsersBlockModel;
import persistence.sql.users.Models.UsersModel;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import static persistence.sql.Main.closeConnection;
import static persistence.sql.Main.openConnection;

import static persistence.sql.users.Main.*;

import static utilities.Main.generateUUID;

public class UsersTests {


//    @Test
//    public void TestCreateAndDeleteUser() throws IOException {
//        openConnection();
//
//        User dummy = new User();
//        dummy.setId(generateUUID());
//        dummy.setUsername("AhmedAsh");
//        dummy.setPhoneNumber("010123456789");
//        dummy.setPrivate(true);
//        dummy.setGender("male");
//        dummy.setDateOfBirth(new Date(311294));
//        dummy.setPasswordHash("12!@#RF1wd1@#");
//        dummy.setEmail("ahmadhamada@gmail.com");
//        dummy.setBio("7ob gamed");
//        dummy.setFullName("Ahmed Ashraf");
//        dummy.setCreatedAt(new java.util.Date());
//
//        User dummy2 = new User();
//        dummy2.setId(generateUUID());
//        dummy2.setUsername("AhmedNizo");
//        dummy2.setPhoneNumber("010123456789");
//        dummy2.setPrivate(true);
//        dummy2.setGender("male");
//        dummy2.setDateOfBirth(new Date(311294));
//        dummy2.setPasswordHash("12!@#RF1wd1@#");
//        dummy2.setEmail("ahmednizo@gmail.com");
//        dummy2.setBio("7ob gamed");
//        dummy2.setFullName("Ahmed Nazih");
//        dummy2.setCreatedAt(new java.util.Date());
//
//
//        User dummy3 = new User();
//        dummy3.setId(generateUUID());
//        dummy3.setUsername("AhmedOsh");
//        dummy3.setPhoneNumber("010123456789");
//        dummy3.setPrivate(true);
//        dummy3.setGender("male");
//        dummy3.setDateOfBirth(new Date(311294));
//        dummy3.setPasswordHash("12!@#RF1wd1@#");
//        dummy3.setEmail("ahmadosh@gmail.com");
//        dummy3.setBio("7ob gamed");
//        dummy3.setFullName("Ahmed Hesham");
//        dummy3.setCreatedAt(new java.util.Date());
//
//
//        try {
//            System.out.println(searchForUser("ahmed",""));
////            Assert.assertEquals(true,createUser(dummy));
////            Assert.assertEquals(true,createUser(dummy2));
////            Assert.assertEquals(true,createUser(dummy3));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
////        boolean deleted = deleteUser(dummy.getId());
////        Assert.assertEquals(true, deleted);
//        closeConnection();
//    }


//    public void TestUpdateUser() throws IOException{
//        openConnection();
//
//        User dummy = new User();
//        dummy.setId(generateUUID());
//        dummy.setUsername("noha");
//        dummy.setPhoneNumber("010123456789");
//        dummy.setPrivate(true);
//        dummy.setGender("male");
//        dummy.setDateOfBirth(new Date(311294));
//        dummy.setPasswordHash("12!@#RF1wd1@#");
//        dummy.setEmail("noha9@gmail.com");
//        dummy.setBio("7ob gamed");
//        dummy.setFullName("Hamada ta7aroosh");
//        dummy.setCreatedAt(new java.util.Date());
//
//        //create user
//        boolean created = createUser(dummy);
//
//        Assert.assertEquals(true, created);
//
//        //update user and check for updated
////        dummy.setEmail("hamadaTa7arosh@gmail.com");
////        updateUser(dummy.getId(), dummy);
////        Assert.assertEquals(dummy.getEmail(), getUserById(dummy.getId()).getEmail());
////
////        //delete user
////        boolean deleted = deleteUser(dummy.getId());
////        Assert.assertEquals(true, deleted);
//
//
//        closeConnection();
//    }
//    public static void main(String[] args) {
//
//    }

   @Test
    public void TestBlock() throws Exception {
        openConnection();

        User dummy = new User();
        dummy.setId(generateUUID());
        dummy.setUsername("SosoA");
        dummy.setPhoneNumber("010123456789");
        dummy.setPrivate(true);
        dummy.setGender("female");
        dummy.setDateOfBirth(new Date(311294));
        dummy.setPasswordHash("12!@#RF1wd1@#");
        dummy.setEmail("sosoA@gmail.com");
        dummy.setBio("7ob gamed");
        dummy.setFullName("Soso A");
        dummy.setCreatedAt(new java.util.Date());
//
        User dummy2 = new User();
        dummy2.setId(generateUUID());
        dummy2.setUsername("SosoB");
        dummy2.setPhoneNumber("010123456789");
        dummy2.setPrivate(true);
        dummy2.setGender("female");
        dummy2.setDateOfBirth(new Date(311294));
        dummy2.setPasswordHash("12!@#RF1wd1@#");
        dummy2.setEmail("sosob@gmail.com");
        dummy2.setBio("7ob gamed");
        dummy2.setFullName("Soso B");
        dummy2.setCreatedAt(new java.util.Date());
//
//
//        User dummy3 = new User();
//        dummy3.setId(generateUUID());
//        dummy3.setUsername("SosoC");
//        dummy3.setPhoneNumber("010123456789");
//        dummy3.setPrivate(true);
//        dummy3.setGender("male");
//        dummy3.setDateOfBirth(new Date(311294));
//        dummy3.setPasswordHash("12!@#RF1wd1@#");
//        dummy3.setEmail("sosoc@gmail.com");
//        dummy3.setBio("7ob gamed");
//        dummy3.setFullName("Soso C");
//        dummy3.setCreatedAt(new java.util.Date());

//       User user = getUserByUsername("SosoA");
//       System.out.println(user.toString());

//       Assert.assertEquals();

      //  boolean blocked = blockUser(getUserByUsername("SosoA").getId(),getUserByUsername("SosoB").getId());
       List l = searchForUser("Soso B",getUserByUsername("SosoA").getId());
       System.out.println(l);
  //     boolean blocked = blockUser("b1634279-61e2-43ae-acd7-031d9178c5c2", "169b6120-dd78-4cb3-ade1-3a11f28b3c1f");
//         Assert.assertEquals(true, blocked);
//        Assert.assertEquals(true, blocks(user1.getId(), user2.getId()));
//        Assert.assertEquals(false, blocks(user2.getId(), user1.getId()));

         closeConnection();
    }
//
//    @Test
//    public void TestReport() throws Exception {
//        openConnection();
//
//        User user1 = getAllUsers().get(1);
//        User user2 = getAllUsers().get(2);
//        boolean reported = reportUser(user1.getId(), user2.getId());
//        Assert.assertEquals(true, reported);
//        Assert.assertEquals(true, reports(user1.getId(), user2.getId()));
//        Assert.assertEquals(false, reports(user2.getId(), user1.getId()));
//
//        closeConnection();
//    }
}
