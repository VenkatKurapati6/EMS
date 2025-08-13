package com.Springboot.UserAuth.Controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class PageController {

    private Map<String, String> createResponse(String message) {
        return Map.of("message", message);
    }

    @GetMapping(value = "/home", produces = MediaType.TEXT_HTML_VALUE)
    public String homePageHtml(Model model) {
        System.out.println("Accessed home page (HTML).");
        return "index";
    }

    @GetMapping(value = "/home", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> homePageJson() {
        System.out.println("Accessed home page (JSON).");
        return createResponse("Accessed home page.");
    }

    @GetMapping(value = "/aboutus", produces = MediaType.TEXT_HTML_VALUE)
    public String aboutUsPageHtml() {
        System.out.println("Accessed about us page (HTML).");
        return "aboutus";
    }

    @GetMapping(value = "/aboutus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> aboutUsPageJson() {
        System.out.println("Accessed about us page (JSON).");
        return createResponse("Accessed about us page.");
    }

    @GetMapping(value = "/services", produces = MediaType.TEXT_HTML_VALUE)
    public String servicesPageHtml() {
        System.out.println("Accessed services page (HTML).");
        return "services";
    }

    @GetMapping(value = "/services", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> servicesPageJson() {
        System.out.println("Accessed services page (JSON).");
        return createResponse("Accessed services page.");
    }

  
    @GetMapping(value = "/DigitalDesignServices", produces = MediaType.TEXT_HTML_VALUE)
    public String digitalDesignPageHtml() {
        System.out.println("Accessed Digital Design services page (HTML).");
        return "DigitalDesignServices";
    }

    @GetMapping(value = "/DigitalDesignServices", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> digitalDesignPageJson() {
        System.out.println("Accessed Digital Design services page (JSON).");
        return createResponse("Accessed Digital Design services page.");
    }

    @GetMapping(value = "/DigitalVerificationServices", produces = MediaType.TEXT_HTML_VALUE)
    public String digitalVerificationPageHtml() {
        System.out.println("Accessed Digital Verification services page (HTML).");
        return "DigitalVerificationServices";
    }

    @GetMapping(value = "/DigitalVerificationServices", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> digitalVerificationPageJson() {
        System.out.println("Accessed Digital Verification services page (JSON).");
        return createResponse("Accessed Digital Verification services page.");
    }

    @GetMapping(value = "/DftServices", produces = MediaType.TEXT_HTML_VALUE)
    public String dftPageHtml() {
        System.out.println("Accessed DFT services page (HTML).");
        return "DFT";
    }

    @GetMapping(value = "/DftServices", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> dftPageJson() {
        System.out.println("Accessed DFT services page (JSON).");
        return createResponse("Accessed DFT services page.");
    }
    
    @GetMapping(value = "/SoftwareServices", produces = MediaType.TEXT_HTML_VALUE)
    public String softwarePageHtml() {
        System.out.println("Accessed Software services page (HTML).");
        return "SoftwareServices";
    }

    @GetMapping(value = "/SoftwareServices", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> softwarePageJson() {
        System.out.println("Accessed Software services page (JSON).");
        return createResponse("Accessed Software services page.");
    }

    @GetMapping(value = "/careers", produces = MediaType.TEXT_HTML_VALUE)
    public String careersPageHtml() {
        System.out.println("Accessed Careers page (HTML).");
        return "careers";
    }

    @GetMapping(value = "/careers", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> careersPageJson() {
        System.out.println("Accessed Careers page (JSON).");
        return createResponse("Accessed Careers page.");
    }

    @GetMapping(value = "/contactus", produces = MediaType.TEXT_HTML_VALUE)
    public String contactUsPageHtml() {
        System.out.println("Accessed Contact Us page (HTML).");
        return "contactus";
    }

    @GetMapping(value = "/contactus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> contactUsPageJson() {
        System.out.println("Accessed Contact Us page (JSON).");
        return createResponse("Accessed Contact Us page.");
    }
  
}
