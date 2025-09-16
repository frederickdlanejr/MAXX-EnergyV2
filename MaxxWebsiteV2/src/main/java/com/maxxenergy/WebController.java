package com.maxxenergy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebController {

    @Autowired
    private SolarDataService solarDataService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("solarStats", solarDataService.getCurrentStats());
        model.addAttribute("activePage", "home");
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("activePage", "about");
        return "about";
    }

    @GetMapping("/faq")
    public String faq(Model model) {
        model.addAttribute("activePage", "faq");
        model.addAttribute("faqs", solarDataService.getFaqList());
        return "faq";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("activePage", "contact");
        model.addAttribute("contactForm", new ContactForm());
        return "contact";
    }

    @PostMapping("/contact")
    public String submitContact(@ModelAttribute ContactForm contactForm, Model model) {
        // Process contact form
        boolean success = solarDataService.processContactForm(contactForm);
        model.addAttribute("activePage", "contact");
        model.addAttribute("contactForm", new ContactForm());
        model.addAttribute("success", success);
        model.addAttribute("message", success ?
                "Thank you for your inquiry! We'll contact you within 24 hours." :
                "Sorry, there was an error. Please try again or call us directly.");
        return "contact";
    }
}