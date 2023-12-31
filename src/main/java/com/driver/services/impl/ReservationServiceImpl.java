package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception{
        Reservation reservation = null;
        Spot spot = null;
        User user = userRepository3.findById(userId).orElse(null);
        ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId).orElse(null);

        if (parkingLot == null || user == null) {
            throw new Exception("Cannot make reservation");
        }

        // Find the minimum cost spot
        int minCost = Integer.MAX_VALUE;
        for(Spot spots : parkingLot.getSpotList()){
            int cost = timeInHours * spots.getPricePerHour();

            if(!spots.getOccupied()){
                if(numberOfWheels > 2 && numberOfWheels <= 4){
                    if(spots.getSpotType() == SpotType.TWO_WHEELER) continue;
                }
                else if(numberOfWheels > 4){
                    if(spots.getSpotType() != SpotType.OTHERS) continue;
                }
                if(minCost > cost){
                    minCost = cost;
                    spot = spots;
                }
            }

        }

        if (spot == null) throw new Exception("Cannot make reservation");

        reservation = new Reservation();
        reservation.setSpot(spot);
        reservation.setUser(user);
        reservation.setNumberOfHours(timeInHours);
        spot.setOccupied(true);
        spot.getReservationList().add(reservation);
        user.getReservationList().add(reservation);

        spotRepository3.save(spot);
       // reservationRepository3.save(reservation);
        userRepository3.save(user);
        return reservation;
    }
}
