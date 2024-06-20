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

    public Adminstrative getAdminstrative(int adminId) {
        return adminstrativeRepository.findById(adminId).orElse(null);
    }

    public void addAdminstrative(Adminstrative adminstrative) {
        adminstrativeRepository.save(adminstrative);
    }

    public void updateAdminstrative(Adminstrative adminstrative) {
        adminstrativeRepository.save(adminstrative);
    }

    public void deleteAdminstrative(int adminId) {
        adminstrativeRepository.deleteById(adminId);
    }
}
