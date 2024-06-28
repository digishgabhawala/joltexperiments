package com.drg.joltexperiments.KYC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kyc")
public class KycController {

    @Autowired
    private KycRepository kycRepository;

    @GetMapping
    public List<Kyc> getAllKycs() {
        return kycRepository.findAll();
    }

    @GetMapping("/{id}")
    public Kyc getKycById(@PathVariable Long id) {
        return kycRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("KYC not found with id " + id));
    }

    @PostMapping
    public Kyc createKyc(@RequestBody Kyc kyc) {
        return kycRepository.save(kyc);
    }

    @PutMapping("/{id}")
    public Kyc updateKyc(@PathVariable Long id, @RequestBody Kyc updatedKyc) {
        Kyc existingKyc = kycRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("KYC not found with id " + id));
        existingKyc.setDocumentType(updatedKyc.getDocumentType());
        existingKyc.setDocumentNumber(updatedKyc.getDocumentNumber());
        existingKyc.setCountry(updatedKyc.getCountry());
        existingKyc.setCustomerId(updatedKyc.getCustomerId()); // Update customer ID reference
        return kycRepository.save(existingKyc);
    }

    @DeleteMapping("/{id}")
    public void deleteKyc(@PathVariable Long id) {
        kycRepository.deleteById(id);
    }
}
