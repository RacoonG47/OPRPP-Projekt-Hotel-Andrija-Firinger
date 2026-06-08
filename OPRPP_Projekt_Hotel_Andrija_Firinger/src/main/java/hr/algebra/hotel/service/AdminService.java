package hr.algebra.hotel.service;

import hr.algebra.hotel.dao.IAdminRepository;

public class AdminService {

    private final IAdminRepository adminRepository;

    public AdminService(IAdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public void deleteAllData() {
        adminRepository.deleteAllData();
    }
}