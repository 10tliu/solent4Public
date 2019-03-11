/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.solent.com600.example.journeyplanner.jpadao.test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solent.com600.example.journeyplanner.jpadao.DAOFactory;
import org.solent.com600.example.journeyplanner.model.ItinearyItem;
import org.solent.com600.example.journeyplanner.model.ItinearyItemType;
import org.solent.com600.example.journeyplanner.model.Rideout;
import org.solent.com600.example.journeyplanner.model.RideoutDAO;
import org.solent.com600.example.journeyplanner.model.RideoutDay;
import org.solent.com600.example.journeyplanner.model.RideoutState;
import org.solent.com600.example.journeyplanner.model.Role;
import org.solent.com600.example.journeyplanner.model.SysUser;
import org.solent.com600.example.journeyplanner.model.SysUserDAO;

/**
 *
 * @author cgallen
 */
public class TestRideoutDAO {

    private static final Logger LOG = LoggerFactory.getLogger(TestRideoutDAO.class);

    @Test
    public void testDAOFactory() {
        SysUserDAO userDAO = DAOFactory.getSysUserDAO();
        assertNotNull(userDAO);
        RideoutDAO rideoutDAO = DAOFactory.getRideoutDAO();
        assertNotNull(rideoutDAO);
    }

    @Test
    public void testCreateRideout() {
        // set up rideoutDAO before creating rideouts
        RideoutDAO rideoutDAO = DAOFactory.getRideoutDAO();
        assertNotNull(rideoutDAO);
        
        // first delete all rideouts before deleting/creating  users
        rideoutDAO.deleteAll();

        // set up sysUsers for test
        SysUserDAO userDAO = DAOFactory.getSysUserDAO();
        assertNotNull(userDAO);
        TestSysUserDAO.testCreateSysUsersDatabase(userDAO);

        // now start testing the rideout dao


        // create multiple rideouts
        createMockRideouts(rideoutDAO);
        List<Rideout> createdRideouts = rideoutDAO.retreiveAll();
        LOG.debug("createdRideouts.size:" + createdRideouts.size());

        List<SysUser> sysUsers = userDAO.retrieveAll();

        Rideout testRideout = createdRideouts.get(0);
        assertNotNull(testRideout);

        SysUser rideLeader = sysUsers.get(0);
        assertNotNull(rideLeader);

        testRideout.setRideLeader(rideLeader);

        testRideout.setWaitlist(sysUsers); //TODO

        testRideout.setRiders(sysUsers);

        Long testRideoutID = testRideout.getId();

        rideoutDAO.update(testRideout);

        Rideout retreivedRideout = rideoutDAO.retreive(testRideoutID);
        assertNotNull(retreivedRideout);
        LOG.debug("retreivedRideout:" + retreivedRideout);
        assertTrue(retreivedRideout.toString().equals(testRideout.toString()));

        
        // retreive rideouts by state
        List<RideoutState> rideoutStates = Arrays.asList(RideoutState.PUBLISHED,RideoutState.PLANNING);
        List<Rideout> retreivedRideouts =  rideoutDAO.retreiveAll(rideoutStates);
        LOG.debug("retreivedRideouts (published planning) size :"+retreivedRideouts.size() );
        for(Rideout rideout:retreivedRideouts){
                LOG.debug("      " + rideout);
        }

    }

    public void createMockRideouts(RideoutDAO rideoutDAO) {
        // delete any pre-existing rideouts
        rideoutDAO.deleteAll();
        List<Rideout> createdRideouts = rideoutDAO.retreiveAll();
        assertTrue(createdRideouts.isEmpty());
        
        RideoutState[] rideoutStateValues = RideoutState.values();

        // create test rideouts from scratch
        for (int rideoutNo = 0; rideoutNo < rideoutStateValues.length; rideoutNo++) {
            Rideout rideout = createMockRideout("rideoutNo_" + rideoutNo);
            rideout.setRideoutstate(rideoutStateValues[rideoutNo]);
            rideout = rideoutDAO.createRideout(rideout);
            assertNotNull(rideout);
            LOG.debug("created rideout:rideoutStateValues[rideoutNo]"+rideoutStateValues[rideoutNo]+"  " + rideout.toString());
        }
    }

    public Rideout createMockRideout(String title) {
        Rideout rideout = new Rideout();

        rideout.setRideoutstate(RideoutState.PLANNING);
        rideout.setTitle(title);

        Date startDate = new Date();
        rideout.setStartDate(startDate);

        String descriptionMd = "#description " + title;
        rideout.setDescriptionMd(descriptionMd);
        rideout.setMaxRiders(10);

        // add 7 rideoutNo days
        for (int day = 0; day < 7; day++) {
            RideoutDay rideoutDay = new RideoutDay();
            rideoutDay.setDescriptionMd("#rideout " + title + "day " + day);

            // add 4 items per day
            for (int item = 0; item < 4; item++) {
                ItinearyItem itinearyItem = new ItinearyItem();
                itinearyItem.setDescriptionMd("#rideout " + title + "day " + day + "itineary item " + item);
                itinearyItem.setItinearyItemType(ItinearyItemType.JOURNEY);
                rideoutDay.getItinearyItems().add(itinearyItem);
            }
            rideout.getRideoutDays().add(rideoutDay);
        }

        //        SysUser rideLeader = new SysUser();
//        rideLeader.setRole(Role.RIDELEADER);
//        rideoutNo.setRideLeader(rideLeader);
//
//        List<SysUser> riders = rideoutNo.getRiders();
//        List<SysUser> waitlist = rideoutNo.getWaitlist();
//        waitlist.add(new SysUser());
        return rideout;
    }

}
