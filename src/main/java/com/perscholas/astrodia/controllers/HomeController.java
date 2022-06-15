package com.perscholas.astrodia.controllers;

import com.perscholas.astrodia.dto.RoundtripDTO;
import com.perscholas.astrodia.models.Flight;
import com.perscholas.astrodia.services.FlightService;
import com.perscholas.astrodia.services.PortService;
import com.perscholas.astrodia.services.RegionService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Controller @Slf4j
@RequestMapping("astrodia")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HomeController {
    FlightService flightService;
    RegionService regionService;
    PortService portService;

    @Autowired
    public HomeController(FlightService flightService, RegionService regionService, PortService portService) {
        this.flightService = flightService;
        this.regionService = regionService;
        this.portService = portService;

    }
    @GetMapping("test")
    public String index() {return "index";}

    @GetMapping("")
    public String mainPage(@ModelAttribute("roundtripDTO") RoundtripDTO roundtripDTO, BindingResult result, Model model){
        model.addAttribute("ports", portService.findAll());
        model.addAttribute("roundtripSearch", roundtripDTO);
        return "home";
    }

    @GetMapping("/roundtrip")
    public String roundtripFlightSearch(Model model, @ModelAttribute("roundtripDTO") RoundtripDTO roundtripDTO) {
        model.addAttribute("result", roundtripDTO);
        List<Flight> flights = flightService.findFlightsByDeparture(roundtripDTO.getDepartureDate());
        model.addAttribute("flights", flights);
        return "results";
    }
    @GetMapping("/signup")
    public String createUserForm() {
        return "signup";
    }

    @GetMapping("/stations")
    public String staysPage() { return "stations"; }

    @GetMapping("/signin")
    public String signInUserForm() { return "signin"; }

    @PostMapping("/signup")
    public String signInUser() {

        return "redirect:astrodia";
    }
}
