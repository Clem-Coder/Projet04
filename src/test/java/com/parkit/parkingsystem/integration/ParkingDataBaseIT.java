package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.unit.FareCalculatorServiceTest;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static final String REG_NUMBER_FOR_TEST = "ABCDEF";

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.setDataBaseConfig(dataBaseTestConfig);
        ticketDAO = new TicketDAO();

        ticketDAO.setDataBaseConfig (dataBaseTestConfig);
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){}



    @Test
    public void testParkingACar() throws Exception {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

        parkingService.processIncomingVehicle();

        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
        assertEquals(REG_NUMBER_FOR_TEST, ticketDAO.getTicket(REG_NUMBER_FOR_TEST).getVehicleRegNumber());
        assertEquals(true, parkingSpotDAO.updateParking(ticketDAO.getTicket(REG_NUMBER_FOR_TEST).getParkingSpot()));
    }

    @Test
    public void testParkingLotExit() throws Exception {
        testParkingACar();
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date = new Date();

        //TODO: check that the fare generated and out time are populated correctly in the database
        assertEquals(dateFormat.format(date), dateFormat.format(ticketDAO.getTicket(REG_NUMBER_FOR_TEST).getOutTime()));
        assertEquals(0.00, ticketDAO.getTicket(REG_NUMBER_FOR_TEST).getPrice()); // 0.00 because first 30 minutes free
    }
    @Test
    public void testDatabaseConfig_connection() throws Exception {
        DataBaseConfig dataBaseConfig = new DataBaseConfig();
        Connection connection = dataBaseConfig.getConnection();
        assertEquals(false, dataBaseConfig.getConnection().isClosed());
    }

    @Test
    public void testDatabaseConfig_connectionIsClose() throws Exception {
        DataBaseConfig dataBaseConfig = new DataBaseConfig();
        Connection connection = dataBaseConfig.getConnection();
        dataBaseConfig.closeConnection(connection);
        assertEquals(false, dataBaseConfig.getConnection().isClosed());
    }

    @Test
    public void testDatabaseConfig_PreparedStatementIsClose() throws Exception {
        DataBaseConfig dataBaseConfig = new DataBaseConfig();
        Connection con = dataBaseConfig.getConnection();
        PreparedStatement ps = con.prepareStatement((DBConstants.GET_NEXT_PARKING_SPOT));
        dataBaseConfig.closePreparedStatement(ps);
        dataBaseConfig.closeConnection(con);
        assertEquals(false, dataBaseConfig.getConnection().isClosed());
    }

    @Test
    public void testDatabaseConfig_ResultsetIsClose() throws Exception {
        ParkingType parkingType = ParkingType.CAR;
        DataBaseConfig dataBaseConfig = new DataBaseConfig();
        Connection con = dataBaseConfig.getConnection();
        PreparedStatement ps = con.prepareStatement((DBConstants.GET_NEXT_PARKING_SPOT));
        ps.setString(1, parkingType.toString());
        ResultSet rs = ps.executeQuery();
        dataBaseConfig.closePreparedStatement(ps);
        dataBaseConfig.closeConnection(con);
        dataBaseConfig.closeResultSet(rs);
        assertEquals(false, dataBaseConfig.getConnection().isClosed());
    }
}