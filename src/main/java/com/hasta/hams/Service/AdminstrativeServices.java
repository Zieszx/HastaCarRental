package com.hasta.hams.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hasta.hams.Model.Adminstrative;
import com.hasta.hams.Repository.AdminstrativeRepository;

import java.util.List;
import java.util.ArrayList;

@Service
public class AdminstrativeServices {
    @Autowired
    private AdminstrativeRepository adminstrativeRepository;

    public List<Adminstrative> getAllAdminstrative() {
        List<Adminstrative> adminstratives = new ArrayList<>();
        adminstrativeRepository.findAll().forEach(adminstratives::add);
        return adminstratives;
    }

    public Adminstrative getAdminstrative(int id) {
        return adminstrativeRepository.findById(id).get();
    }

    public void addAdminstrative(Adminstrative adminstrative) {
        adminstrativeRepository.save(adminstrative);
    }

    public void updateAdminstrative(Adminstrative adminstrative) {
        adminstrativeRepository.save(adminstrative);
    }

    public void deleteAdminstrative(int id) {
        adminstrativeRepository.deleteById(id);
    }
}
