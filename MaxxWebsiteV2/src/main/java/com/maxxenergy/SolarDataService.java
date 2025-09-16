package com.maxxenergy;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SolarDataService {

    public Map<String, Object> getCurrentStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalInstallations", "2,847");
        stats.put("energyGenerated", "156.8 GWh");
        stats.put("co2Prevented", "98,240 tons");
        stats.put("customerSavings", "$18.7M");
        stats.put("currentProduction", Math.round(Math.random() * 500 + 200)); // fake it til you make it
        return stats;
    }

    public List<Map<String, String>> getFaqList() {
        List<Map<String, String>> faqs = new ArrayList<>();

        Map<String, String> faq1 = new HashMap<>();
        faq1.put("question", "How much can I save with MAXX Energy solar panels?");
        faq1.put("answer", "Most homeowners save 60-90% on their electricity bills. With our premium efficiency panels and smart monitoring system, you could save $1,200-$2,400 annually depending on your current usage and local rates.");
        faqs.add(faq1);

        Map<String, String> faq2 = new HashMap<>();
        faq2.put("question", "What makes MAXX Energy different from other solar companies?");
        faq2.put("answer", "We use only Tier-1 solar panels with 25-year performance warranties, provide real-time monitoring through our proprietary app, and offer the industry's most comprehensive maintenance program. Our local team averages 12+ years of solar experience.");
        faqs.add(faq2);

        Map<String, String> faq3 = new HashMap<>();
        faq3.put("question", "How long does a MAXX Energy installation take?");
        faq3.put("answer", "Most residential installations are completed in 1-2 days. Our streamlined process includes design, permitting, installation, and activation typically within 4-6 weeks from contract signing.");
        faqs.add(faq3);

        Map<String, String> faq4 = new HashMap<>();
        faq4.put("question", "Do you offer financing options?");
        faq4.put("answer", "Yes! We partner with leading solar lenders to offer $0-down financing, solar loans with rates as low as 2.99% APR, and lease options. Most customers start saving money immediately, even with financing payments.");
        faqs.add(faq4);

        Map<String, String> faq5 = new HashMap<>();
        faq5.put("question", "What happens to my solar system during power outages?");
        faq5.put("answer", "Standard grid-tied systems shut down during outages for safety. However, our battery backup solutions keep your essential loads powered for 12-48 hours depending on usage and battery capacity.");
        faqs.add(faq5);

        return faqs;
    }

    public boolean processContactForm(ContactForm form) {
        // In a real application, you would:
        // 1. Validate the form data
        // 2. Save to database
        // 3. Send notification emails
        // 4. Integrate with CRM system

        // For now, simulate processing
        try {
            Thread.sleep(500); // Simulate processing time
            return form.getName() != null && !form.getName().trim().isEmpty() &&
                    form.getEmail() != null && !form.getEmail().trim().isEmpty();
        } catch (InterruptedException e) {
            return false;
        }
    }
}