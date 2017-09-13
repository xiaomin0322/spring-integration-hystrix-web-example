package xp.hystrix.main.contioller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {
    /**
     *
     * @return
     */
    @RequestMapping
    public String index(){
        return "";
    }
}
