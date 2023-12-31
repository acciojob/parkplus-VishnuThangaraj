package com.driver.services.impl;

import com.driver.model.ParkingLot;
import com.driver.model.Spot;
import com.driver.model.SpotType;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    @Autowired
    ParkingLotRepository parkingLotRepository1;
    @Autowired
    SpotRepository spotRepository1;
    @Override
    public ParkingLot addParkingLot(String name, String address) {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName(name);
        parkingLot.setAddress(address);

        parkingLotRepository1.save(parkingLot); // Save to Database

        return parkingLot;
    }

    @Override
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour) {
        ParkingLot parkingLot = parkingLotRepository1.findById(parkingLotId).orElse(null);

        Spot spot = new Spot();
        spot.setPricePerHour(pricePerHour);
        spot.setParkingLot(parkingLot);
        // Set the Spot Type
        if (numberOfWheels <= 2) {
            spot.setSpotType(SpotType.TWO_WHEELER);
        } else if (numberOfWheels <= 4) {
            spot.setSpotType(SpotType.FOUR_WHEELER);
        } else {
            spot.setSpotType(SpotType.OTHERS);
        }


        //spotRepository1.save(spot); // Save to Database

        parkingLot.getSpotList().add(spot);
        parkingLotRepository1.save(parkingLot); // Save to Database

        return spot;
    }

    @Override
    public void deleteSpot(int spotId) {
        Spot spot = spotRepository1.findById(spotId).orElse(null);

        if(spot != null){
            ParkingLot parkingLot = parkingLotRepository1.findById(spot.getParkingLot().getId())
                    .orElse(null);


            parkingLot.getSpotList().remove(spot);
        }
        spotRepository1.deleteById(spotId);
    }

    @Override
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour) {
        ParkingLot parkingLot = parkingLotRepository1.findById(parkingLotId).orElse(null);

        for(Spot spot : parkingLot.getSpotList()){
            if(spot.getId() == spotId){
                spot.setPricePerHour(pricePerHour);
                spotRepository1.save(spot);
                return spot;
            }
        }

        return null; // Spot not found
    }

    @Override
    public void deleteParkingLot(int parkingLotId) {
        ParkingLot parkingLot = parkingLotRepository1.findById(parkingLotId).orElse(null);

        if(parkingLot != null){
            parkingLot.getSpotList().forEach(spot -> spotRepository1.deleteById(spot.getId()));
        }

        parkingLotRepository1.deleteById(parkingLotId);
    }
}
