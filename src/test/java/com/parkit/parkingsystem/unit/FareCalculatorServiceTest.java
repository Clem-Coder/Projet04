package com.parkit.parkingsystem.unit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.apache.logging.log4j.core.util.SystemClock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


import static com.parkit.parkingsystem.constants.Fare.REGULAR_USER_REDUCTION;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;
    private static final long AN_HOUR_AND_A_HOULF_IN_MILLISEDONDS = 5_400_000;
    private static final double CAR_FIRST_30_FREE_MINUTES = 0.75;
    private static final double BIKE_FIRST_30_FREE_MINUTES = 0.50
            ;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // One hour in parking
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR - CAR_FIRST_30_FREE_MINUTES);
    }

    @Test
    public void calculateFareBike() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // One hour in parking
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR - BIKE_FIRST_30_FREE_MINUTES);
    }

    //EXCEPTIONS

    @Test
    public void calculateFareUnkownType() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }


    @Test
    public void calculateFareBikeWithFutureInTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithoutOutTime() {
        Date inTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareWithUnknowVehiculeType_ExpectedIllegalArgumentException() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.UNKNOW_PARKING_TYPE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    // DIFFERENT TIMING VALUES
    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime_AndExpectOneQuarterBikeParkingPrice() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR) - BIKE_FIRST_30_FREE_MINUTES, ticket.getPrice());
    }

    @Test
    public void calculateFareCar45MinutesParkingTime_AndExpectOneQuarterCarParkingPrice() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(Math.round(((0.75 * Fare.CAR_RATE_PER_HOUR) - CAR_FIRST_30_FREE_MINUTES) * 100.0) / 100.0, ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanOneDayParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (25 * 60 * 60 * 1000));//25 hours parking time should give 25 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((25 * Fare.CAR_RATE_PER_HOUR) - CAR_FIRST_30_FREE_MINUTES, ticket.getPrice());
    }

    @Test
    public void calculateFareBikeWithMoreThanOneDayParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (25 * 60 * 60 * 1000));//25 hours parking time should give 25 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((25 * Fare.BIKE_RATE_PER_HOUR) -BIKE_FIRST_30_FREE_MINUTES, ticket.getPrice());
    }

    @Test
    public void calculateFareWithLessThanAHalfHour() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000)); //29 mean 29 minutes
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0, ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanAHalfHour() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (31 * 60 * 1000)); //31 mean 31 minutes
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(Math.round(((Fare.CAR_RATE_PER_HOUR * 0.52) - CAR_FIRST_30_FREE_MINUTES) * 100.0) / 100.0, ticket.getPrice());
    }

    @Test
    public void calculateFareBikeWithMoreThanAHalfHour() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (31 * 60 * 1000)); //31 mean 31 minutes
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(Math.round(((Fare.BIKE_RATE_PER_HOUR * 0.52) - BIKE_FIRST_30_FREE_MINUTES) * 100.0) / 100.0, ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithAHalfHour() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000)); //31 mean 31 minutes
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0 , ticket.getPrice());
    }

    @Test
    public void calculateFareBikeWithAHalfHour() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000)); //31 mean 31 minutes
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0 , ticket.getPrice());
    }

    @Test
    public void watchIfRegularUserReductionIsApply_ForCarTest() {
        Date inTime = new Date();
        Date outTime = new Date();
        outTime.setTime(inTime.getTime() + AN_HOUR_AND_A_HOULF_IN_MILLISEDONDS);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setRegularUser(true);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(Math.round(((Fare.CAR_RATE_PER_HOUR * REGULAR_USER_REDUCTION ) ) * 100.0) / 100.0, ticket.getPrice()); // First 30 free minutes
    }

    @Test
    public void watchIfRegularUserReductionIsApply_ForBikeTest() {
        Date inTime = new Date();
        Date outTime = new Date();
        outTime.setTime(inTime.getTime() + AN_HOUR_AND_A_HOULF_IN_MILLISEDONDS);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setRegularUser(true);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(Math.round(((Fare.BIKE_RATE_PER_HOUR * REGULAR_USER_REDUCTION ) ) * 100.0) / 100.0, ticket.getPrice()); // First 30 free minutes
    }
}