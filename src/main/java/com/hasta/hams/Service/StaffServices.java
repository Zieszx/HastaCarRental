package com.hasta.hams.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hasta.hams.Model.Staff;
import com.hasta.hams.Repository.StaffRepository;

import java.util.List;
import java.util.ArrayList;

@Service
public class StaffServices {

    @Autowired
    private StaffRepository staffRepository;

    public List<Staff> getAllStaff() {
        List<Staff> staffs = new ArrayList<>();
        staffRepository.findAll().forEach(staffs::add);
        return staffs;
    }

    public Staff getStaff(int id) {
        return staffRepository.findById(id).get();
    }

    public void addStaff(Staff staff) {
        staffRepository.save(staff);
    }

    public void updateStaff(Staff staff) {
        staffRepository.save(staff);
    }

    public void deleteStaff(int id) {
        staffRepository.deleteById(id);
    }

}
