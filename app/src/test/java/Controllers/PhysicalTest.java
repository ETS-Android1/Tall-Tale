package Controllers;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

public class PhysicalTest {

    @Test
    public void testCalculateThickness() {
        //input value
        int input = 250;
        //expected function output
        int expected = 1;
        //instantiate Physical object to run method with ("input" is passed as an argument then used by the method)
        Physical physical = new Physical("", "", "", "", 0, false, input);

        //get output
        int output = physical.calculateThickness(physical);

        //check for correct value
        assertEquals(output, expected);
    }


}