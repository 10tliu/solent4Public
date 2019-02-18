/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.solent.com600.example.journeyplanner.service.test;

import java.util.List;
import javax.naming.AuthenticationException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.solent.com600.example.journeyplanner.model.Role;
import org.solent.com600.example.journeyplanner.model.ServiceFacade;
import org.solent.com600.example.journeyplanner.model.ServiceFactory;
import org.solent.com600.example.journeyplanner.model.SysUser;
import org.solent.com600.example.journeyplanner.model.UserInfo;
import org.solent.com600.example.journeyplanner.service.ServiceFacadeImpl;
import org.solent.com600.example.journeyplanner.service.ServiceFactoryImpl;

/**
 *
 * @author gallenc
 */
public class ServiceFacadeTest {

    public static final String ADMIN1_USER = "admin1";
    public static final String ANONAMOUS1_USER = "anon1";
    public static final String RIDELEADER1_USER = "rideleader1";
    public static final String RIDER1_USER = "rider1";

    @Test
    public void facadeLowGranualarityAuthorisationTest() {
        ServiceFactory serviceFactory = new ServiceFactoryImpl();
        assertNotNull(serviceFactory);

        ServiceFacade serviceFacade = serviceFactory.getServiceFacade();
        assertNotNull(serviceFacade);

        // test we can create users in database
        createDatabaseTestUsers(serviceFacade);

        // anonymous fail delete users
        try {
            serviceFacade.deleteAllUsers(ANONAMOUS1_USER);
            fail("Anonymous user should not be able to delete users ");
        } catch (AuthenticationException ex) {
        }
        // rideleader fail delete users
        try {
            serviceFacade.deleteAllUsers(RIDELEADER1_USER);
            fail("rider leader user should not be able to delete users ");
        } catch (AuthenticationException ex) {
        }
        //rider fail delete users
        try {
            serviceFacade.deleteAllUsers(RIDER1_USER);
            fail("Rider user should not be able to delete users ");
        } catch (AuthenticationException ex) {
        }

        // anonymous fail list all users
        try {
            serviceFacade.retrieveAllUsers(ANONAMOUS1_USER);
            fail("Anonymous user should not be able to list users ");
        } catch (AuthenticationException ex) {
        }
        // rider fail list all users
        try {
            serviceFacade.retrieveAllUsers(RIDER1_USER);
            fail("Anonymous user should not be able to list users ");
        } catch (AuthenticationException ex) {
        }
        // rider leader pass list all users
        try {
            List<SysUser> users = serviceFacade.retrieveAllUsers(RIDELEADER1_USER);
            assertTrue(users.size() > 0);
        } catch (AuthenticationException ex) {
            fail("rideleader user should be able to list users ");
        }
        // admin pass list all users
        try {
            List<SysUser> users = serviceFacade.retrieveAllUsers(ADMIN1_USER);
            assertTrue(users.size() > 0);
        } catch (AuthenticationException ex) {
            fail("rideleader user should be able to list users ");
        }

        // user fail retrieve other user 
        try {
            SysUser user = serviceFacade.retrieveByUserName(ADMIN1_USER, RIDER1_USER);
            fail("rider user should not be able to retrieve other user ");
        } catch (AuthenticationException ex) {
        }

        // user pass retreive same user
        // TODO note problem - SHOULD NOT BE ABLE TO CHANGE ROLE OR USERNAME
        try {
            SysUser user = serviceFacade.retrieveByUserName(RIDER1_USER, RIDER1_USER);
            assertTrue(user.getUserName().equals(RIDER1_USER));
            user.getUserInfo().setFirstname("fred");
            serviceFacade.updateUser(user, RIDER1_USER);
        } catch (AuthenticationException ex) {
            fail("same user should be able update user");
        }

    }

    @Test
    public void facadeHiGranualarityAuthorisationTest() {
        ServiceFactory serviceFactory = new ServiceFactoryImpl();
        assertNotNull(serviceFactory);

        ServiceFacade serviceFacade = serviceFactory.getServiceFacade();
        assertNotNull(serviceFacade);

        // test we can create users in database
        createDatabaseTestUsers(serviceFacade);

        // admin allowed to get user info
        try {
            String userName = RIDER1_USER;
            String actingSysUserName = ADMIN1_USER;
            serviceFacade.getUserInfoByUserName(userName, actingSysUserName);;
        } catch (AuthenticationException ex) {
            fail("Admin should be able to change password ");
        }

        // same user allowed to get user info
        try {
            String userName = RIDER1_USER;
            String actingSysUserName = RIDER1_USER;
            serviceFacade.getUserInfoByUserName(userName, actingSysUserName);;
        } catch (AuthenticationException ex) {
            fail("same user should be able to change password ");
        }

        try {
            String userName = ADMIN1_USER;
            String actingSysUserName = RIDER1_USER;
            serviceFacade.getUserInfoByUserName(userName, actingSysUserName);
            fail("rider user should not be able to get admin user info ");
        } catch (AuthenticationException ex) {
        }

        //TODO ADD TESTS
        // allowed to modify user info
        // not allowed to get user info
        // not allowed to modify user info
        //allowec to change password
        // not allowed to change password
        //allowed to change insuranceVerified
        // not allowed to change insurance verified
    }

    // utility methods to create users
    public void createDatabaseTestUsers(ServiceFacade serviceFacade) {

        // clear all user data in database at beginning of test
        try {
            serviceFacade.deleteAllUsers(ServiceFacadeImpl.SUPERADMIN_SYSUSERNAME);
        } catch (AuthenticationException ex) {
            fail("cannot delete all users " + ex.getMessage());
        }

        // create users with different permissions
        SysUser adminUser1 = new SysUser();
        adminUser1.setUserName(ADMIN1_USER);
        adminUser1.getUserInfo().setFirstname("AdminFirstname");
        adminUser1.getUserInfo().setSurname("AdminSurname");
        adminUser1.setRole(Role.ADMIN);

        // create first user in database
        try {
            serviceFacade.createUser(adminUser1, ServiceFacadeImpl.SUPERADMIN_SYSUSERNAME);
        } catch (AuthenticationException ex) {
            fail("cannot create new user " + ex.getMessage());
        }

        SysUser anonUser = new SysUser();
        anonUser.setUserName(ANONAMOUS1_USER);
        anonUser.getUserInfo().setFirstname("AnonFirstname");
        anonUser.getUserInfo().setSurname("AnonSurname");
        anonUser.setRole(Role.ANONYMOUS);

        // create anonymous user in database
        try {
            serviceFacade.createUser(anonUser, ADMIN1_USER);
        } catch (AuthenticationException ex) {
            fail("cannot create new user " + ex.getMessage());
        }

        SysUser rideLeaderUser1 = new SysUser();
        rideLeaderUser1.setUserName(RIDELEADER1_USER);
        rideLeaderUser1.getUserInfo().setFirstname("RideLeader1Firstname");
        rideLeaderUser1.getUserInfo().setSurname("RideLeaderSurname");
        rideLeaderUser1.setRole(Role.RIDELEADER);

        // create anonymous user in database
        try {
            serviceFacade.createUser(rideLeaderUser1, ADMIN1_USER);
        } catch (AuthenticationException ex) {
            fail("cannot create new user " + ex.getMessage());
        }

        SysUser riderUser1 = new SysUser();
        riderUser1.setUserName(RIDER1_USER);
        riderUser1.getUserInfo().setFirstname("rider1Firstname");
        riderUser1.getUserInfo().setSurname("rider1Surname");
        riderUser1.setRole(Role.RIDER);

        try {
            serviceFacade.createUser(riderUser1, ADMIN1_USER);
        } catch (AuthenticationException ex) {
            fail("cannot create new user " + ex.getMessage());
        }

    }
}
