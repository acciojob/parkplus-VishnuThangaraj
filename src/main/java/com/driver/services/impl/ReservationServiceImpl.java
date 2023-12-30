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
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        User user = userRepository3.findById(userId).orElse(null);
        ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId).orElse(null);

        if(user == null || parkingLot == null){
            return null;
        }


        Reservation reservation = new Reservation();

        // Find the minimum cost spot
        Spot spot = null;
        int minCost = Integer.MAX_VALUE;
        for(Spot spots : parkingLot.getSpotList()){
            int cost = timeInHours * spots.getPricePerHour();

            if(!spots.getOccupied()){
                if(numberOfWheels == 2){
                    if(cost < minCost){
                        minCost = cost;
                        spot = spots;
                    }
                }
                else if(numberOfWheels == 4){
                    if(spots.getSpotType() == SpotType.FOUR_WHEELER || spots.getSpotType() == SpotType.OTHERS){
                        if(cost < minCost){
                            minCost = cost;
                            spot = spots;
                        }
                    }
                }
                else{
                    if(spots.getSpotType() == SpotType.OTHERS){
                        if(cost < minCost){
                            minCost = cost;
                            spot = spots;
                        }
                    }
                }
            }

        }

        if(spot == null)
            return null;

        reservation.setSpot(spot);
        reservation.setUser(user);

        reservationRepository3.save(reservation);

        spot.setOccupied(true);
        spot.getReservationList().add(reservation);
        spotRepository3.save(spot);

        return reservation;
    }
}
