package com.hasta.hams.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hasta.hams.model.Staff;
import com.hasta.hams.repository.StaffRepository;

import java.util.List;
import java.util.ArrayList;

@Service
public class StaffServices {

    @Autowired
    private StaffRepository staffRepository;

    public List<Staff> getAllStaffs() {
        List<Staff> staffs = new ArrayList<>();
        staffRepository.findAll().forEach(staffs::add);
        return staffs;
    }

    public Staff getStaff(int staffId) {
        return staffRepository.findById(staffId).orElse(null);
    }

    public void addStaff(Staff staff) {
        staffRepository.save(staff);
    }

    public void updateStaff(Staff staff) {
        staffRepository.save(staff);
    }

    public void deleteStaff(int staffId) {
        staffRepository.deleteById(staffId);
    }

}
