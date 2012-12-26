package com.shilgapira.funkyval.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.*;
import static org.junit.Assert.*;

import com.shilgapira.funkyval.Funkyval;

public class FunkyvalTests {
    
    private static Map<String, String> v;
    
    private static boolean fb(String s) {
        return Funkyval.fromExpression(s).evaluateBoolean(v);
    }
    
    private static int fi(String s) {
        return Funkyval.fromExpression(s).evaluateInteger(v);
    }
    
    private static String fs(String s) {
        return Funkyval.fromExpression(s).evaluateString(v);
    }
    
    @Before
    public void setup() {
        v = new HashMap<String, String>();
        v.put("door", "shut");
        v.put("number", "8");
        v.put("sleeping", "yes");
    }

    @Test
    public void testBool() {
        assertFalse(        fb(null));
        assertFalse(        fb(""));
        assertFalse(        fb("0"));
        assertFalse(        fb("foo"));
        assertFalse(        fb("1 + 2"));
        assertTrue(         fb("1"));
        assertTrue(         fb("true"));
        assertTrue(         fb("yes"));
        assertFalse(        fb("!true"));
        assertTrue(         fb("!no"));
        assertTrue(         fb("!foo"));
        assertTrue(         fb("2 - 1"));
        assertTrue(         fb("yes == true"));
    }
    
    @Test
    public void testInt() {
        assertEquals(       fi("1 + 2")             , 3     );
        assertEquals(       fi("4 * 4")             , 16    );
        assertEquals(       fi("20 - 200")          , -180  );
        assertEquals(       fi("80 % 30")           , 20    );
        assertEquals(       fi("80 / 20")           , 4     );
        assertEquals(       fi("20 - 200")          , -180  );
        assertEquals(       fi("(80 % 30) + 1")     , 21    );
    }
    
    @Test
    public void testString() {
        assertTrue(         fs("hello")     .equalsIgnoreCase("hello")  );
        assertFalse(        fs("hello")     .equalsIgnoreCase("world")  );
        assertTrue(         fs("sleeping")  .equalsIgnoreCase("yes")    );
        assertTrue(         fs("door")      .equalsIgnoreCase("shut")   );
    }

    @Test
    public void testVariables() {
        assertTrue(         fb("sleeping")                              );
        assertTrue(         fb("sleeping == 1")                         );
        assertTrue(         fb("sleeping != false")                     );
        assertTrue(         fi("number") == 8                           );
        assertFalse(        fi("number") == 80                          );
        assertTrue(         fb("door == shut")                          );
        assertFalse(        fb("door == open")                          );
        
        assertTrue(         fb("(door == shut)")                        );
        assertTrue(         fb("(door == shut) && sleeping")            );
        assertFalse(        fb("(door != shut) && sleeping")            );
        assertTrue(         fb("(door != shut) || sleeping")            );
        
        assertTrue(         fb("yes")                                   );
        v.put("yes", "false");
        assertFalse(        fb("yes")                                   );
        
        assertTrue(         fb("yes = true")                            );
        assertTrue(         fb("yes")                                   );
        assertTrue(         fi("number++") == 9                         );
        assertTrue(         fi("number *= 2") == 18                     );
        
        assertTrue(         fb("number == 18")                          );
        assertTrue(         fi("number") == 18                          );
        assertTrue(         fs("number").equals("18")                   );
        
        for (int i = fi("number"); i < 100; i++) {
            assertTrue(     fs("number++").equalsIgnoreCase(Integer.toString(i + 1)));
        }
    }
    
    @Test
    public void testOperators() {
        assertTrue(         fb("number < 10")                           );
        assertTrue(         fb("number >= 8")                           );
        assertTrue(         fi("number * 2") == 16                      );
        assertFalse(        fi("number")    == 80                       );
        assertTrue(         fb("door == shut")                          );
        assertFalse(        fb("door == open")                          );
        
        assertTrue(         fb("(door == shut)")                        );
        assertTrue(         fb("(door == shut) && sleeping")            );
        assertFalse(        fb("(door != shut) && sleeping")            );
        assertTrue(         fb("(door != shut) || sleeping")            );
        
        assertFalse(        fb("door = open")                           );
        assertFalse(        fb("door > 8")                              );
        assertTrue(         fb("door < 8")                              );
        v.put("door", "8");
        assertTrue(         fb("(door + 10) == 18")                     );
        assertTrue(         fb("door = 1")                              );
        assertTrue(         fb("(door * 1000) > 100")                   );
        assertTrue(         fb("(door * 1000) >= 1000")                 );
        assertFalse(        fb("(door * 1000) > 1000")                  );
        assertFalse(        fb("(door * 1000) <= 100")                  );
    }
    
    @Test
    public void testGroup() {
        assertTrue(         fb("number == 8, number++, number == 11")   );
        assertTrue(         fb("number == 9, 3 == 4, number = 800")     );
        assertTrue(         fb("number == 800")                         );
    }
    
}
