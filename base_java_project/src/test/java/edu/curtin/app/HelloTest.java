package edu.curtin.app;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class HelloTest
{
    @Test
    void testHello()
    {
        assertEquals("Hello world!", Hello.getHello());
    }
}
