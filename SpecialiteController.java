package com.santerdv.santerdvapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialites")
public class SpecialiteController {

    @Autowired
    private SpecialiteRepository specialiteRepository;

    @GetMapping
    public List<Specialite> getAllSpecialites() {
        return specialiteRepository.findAll();
    }
}