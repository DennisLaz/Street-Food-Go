package gr.hua.dit.StreetFoodGo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * controller for <strong> testing </strong>.
 */
@Controller
public class TestController {

    public TestController() {}

    /*
    @GetMapping(value = "/test/error/404")
    public String test() {

        return "error/404";
    }
    @GetMapping(value = "/error/error")
    public String testErrorError() {
        return "error/error";
    }
    */

    @GetMapping(value = "/test/error/NullPointerException")
    public String testErrorNullPointerException() {
        final Integer a = null;
        final int b=0;
        final int c=b+a; //throws NullPointerException
        return null; //unreachable
    }
}
