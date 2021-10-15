package com.parkit.parkingsystem.unit;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.UnnecessaryStubbingException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processIncomingVehicle_CarTypeTest() throws Exception {
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        parkingService.processIncomingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    }

    @Test
    public void processIncomingVehicle_BikeTypeTest() throws Exception {
        when(parkingSpotDAO.getNextAvailableSlot((any(ParkingType.class)))).thenReturn(1);
        when(inputReaderUtil.readSelection()).thenReturn(2);
        parkingService.processIncomingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    }


    @Test
    public void processIncomingVehicle_WithAlreadyInParkingErrorTest() throws Exception{
        when(parkingSpotDAO.getNextAvailableSlot((any(ParkingType.class)))).thenReturn(1);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(ticketDAO.checkAlreadyInParking(anyString())).thenReturn(true);
        assertThrows(Exception.class, ()-> parkingService.processIncomingVehicle() );
    }

    @Test
    public void processIncomingVehicle_WithErrorFetchingParkingNumber() throws Exception{
        when(parkingSpotDAO.getNextAvailableSlot((any(ParkingType.class)))).thenReturn(0);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        parkingService.processIncomingVehicle();
        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));
    }
    @Test
    public void processExitingVehicleTest() throws Exception {
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }


    @Test
    public void processExitingVehicle_WithUpdatingTicketErrorTest() throws Exception{
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
        assertThrows(Exception.class, ()-> parkingService.processExitingVehicle() );
    }

}






