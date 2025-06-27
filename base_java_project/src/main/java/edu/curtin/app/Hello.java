package edu.curtin.app;
import java.util.logging.Logger;

public class Hello
{
    private static final Logger log = Logger.getLogger(Hello.class.getName());

    public static String getHello()
    {
        log.info("About to return 'Hello world!'");
        return "Hello world!";
    }
}
