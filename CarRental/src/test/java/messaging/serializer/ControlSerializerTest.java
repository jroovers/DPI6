/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.serializer;

import messaging.dynrouter.ControlMessage;
import messaging.dynrouter.ControlType;
import model.Dealer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author jeroe
 */
public class ControlSerializerTest {

    private Dealer dealer;
    private String queue;
    private String filter;
    private ControlType createType;
    private ControlType updateType;
    private ControlType deleteType;

    public ControlSerializerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        dealer = new Dealer("TESTbrand");
        queue = "TESTqueue";
        filter = "#{price} <= 1000 && #{period} <= 10";
        createType = ControlType.CREATE;
        updateType = ControlType.UPDATE;
        deleteType = ControlType.DELETE;
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of controlToString method, of class ControlSerializer.
     */
    @Test
    public void testControlToString() {
        System.out.println("controlToString");
        ControlMessage msg = null;
        msg = new ControlMessage(dealer, queue, queue, ControlType.CREATE);
        ControlSerializer instance = new ControlSerializer();
        String expResult = "";
        System.out.println(instance.controlToString(msg));
        String result = instance.controlToString(msg);
        // assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of stringToControl method, of class ControlSerializer.
     */
    @Test
    @Ignore
    public void testStringToControl() {
        System.out.println("stringToControl");
        String body = "";
        ControlSerializer instance = new ControlSerializer();
        ControlMessage expResult = null;
        ControlMessage result = instance.stringToControl(body);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
