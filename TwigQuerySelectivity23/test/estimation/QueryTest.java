/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package estimation;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rik
 */
public class QueryTest {

    public QueryTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getQueryString method, of class Query.
     */
    @Test
    public void testGetQueryString() {
        System.out.println("getQueryString");
        Query instance = new Query("//A[b]");
        String expResult = "//A[b]";
        String result = instance.getQueryString();
        assertEquals(expResult, result);
    }

    /**
     * Test of containsNextQuery method, of class Query.
     */
    @Test
    public void testContainsNextQuery() {
        System.out.println("containsNextQuery");
        Query instance = new Query("//A");
        boolean expResult = false;
        boolean result = instance.containsNextQuery();
        assertEquals(expResult, result);

        instance = new Query("//A[b]");
        expResult = false;
        result = instance.containsNextQuery();
        assertEquals(expResult, result);

        instance = new Query("//A[b]/c");
        expResult = true;
        result = instance.containsNextQuery();
        assertEquals(expResult, result);

        instance = new Query("//A/b");
        expResult = true;
        result = instance.containsNextQuery();
        assertEquals(expResult, result);
        instance = new Query("");
        expResult = false;
        result = instance.containsNextQuery();
        assertEquals(expResult, result);
    }

    /**
     * Test of containsFilter method, of class Query.
     */
    @Test
    public void testContainsFilterNoQuery() {
        System.out.println("containsFilter");
        Query instance = new Query("//A");
        boolean expResult = false;
        boolean result = instance.containsFilter();
        assertEquals(expResult, result);
    }

    @Test
    public void testContainsFilterWithFilter() {
        Query instance = new Query("//A[b]");
        boolean expResult = true;
        boolean result = instance.containsFilter();
        assertEquals(expResult, result);
    }

    @Test
    public void testContainsFilterWithNext() {
        Query instance = new Query("//A/c");
        boolean expResult = false;
        boolean result = instance.containsFilter();
        assertEquals(expResult, result);
    }

    @Test
    public void testContainsFilterWithFilterAndNext() {
        Query instance = new Query("//A[b]/d");
        boolean expResult = true;
        boolean result = instance.containsNextQuery();
        assertEquals(expResult, result);

    }

}
