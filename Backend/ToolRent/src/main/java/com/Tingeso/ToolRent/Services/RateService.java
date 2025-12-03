package com.Tingeso.ToolRent.Services;

import com.Tingeso.ToolRent.Entities.RateEntity;
import com.Tingeso.ToolRent.Repositories.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RateService {
    @Autowired
    RateRepository rateRepository;

    public RateEntity getLatestRate() {
        return rateRepository.findTopByOrderByIdDesc();
    }

    public RateEntity createRate(int dailyRentalRate, int dailyLateFeeRent) {

        RateEntity rate = new RateEntity();
        rate.setDailyRentalRate(dailyRentalRate);
        rate.setDailyLateFeeRent(dailyLateFeeRent);

        return rateRepository.save(rate);
    }

    public RateEntity addRate(RateEntity rate) {
        return rateRepository.save(rate);
    }

        RateEntity findTopByOrderByIdDesc() {
            return rateRepository.findTopByOrderByIdDesc();
        }

}
