package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;


import static com.parkit.parkingsystem.constants.Fare.REGULAR_USER_REDUCTION;


public class FareCalculatorService {

    private static final int ONE_HOUR_IN_MILLISECONDS = 60 * 60 * 1000;
    private static final double A_HALF_HOUR = 0.5;

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();


        //TODO: Some tests are failing here. Need to check if this logic is correct
        //Check

        double duration = (outHour - inHour);
        double timeAccount = duration / ONE_HOUR_IN_MILLISECONDS;

        //first 30 minutes free
        timeAccount = Math.max(0, timeAccount-A_HALF_HOUR);


        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                double price = timeAccount * Fare.CAR_RATE_PER_HOUR;
                double roundPrice = Math.round((price)*100.0)/100.0;
                //5% reduction for regular users
                if(ticket.getRegularUser() && roundPrice > 0)
                    roundPrice = Math.round((price * REGULAR_USER_REDUCTION)*100)/100.0;
                ticket.setPrice(roundPrice);
                break;
            }
            case BIKE: {
                double price = timeAccount * Fare.BIKE_RATE_PER_HOUR;
                double roundPrice = Math.round((price)*100.0)/100.0;
                //5% reduction for regular users
                if(ticket.getRegularUser() && roundPrice > 0)
                    roundPrice = Math.round((price * REGULAR_USER_REDUCTION)*100)/100.0;
                ticket.setPrice(roundPrice);
                break;
            }
            default: throw new IllegalArgumentException("Unknown Parking Type");
        }
    }
}