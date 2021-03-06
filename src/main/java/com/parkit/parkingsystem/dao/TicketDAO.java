package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");
    private DataBaseConfig dataBaseConfig = new DataBaseConfig();

    public DataBaseConfig getDataBaseConfig(){
        return this.dataBaseConfig;
    }
    public void setDataBaseConfig (DataBaseConfig dataBaseConfig){
        this.dataBaseConfig = dataBaseConfig;
    }


    public boolean saveTicket(Ticket ticket){
            Connection con = null;
            PreparedStatement ps = null;
        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.SAVE_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            //ps.setInt(1,ticket.getId());
            ps.setInt(1,ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
            ps.setTimestamp(5, (ticket.getOutTime() == null)?null: (new Timestamp(ticket.getOutTime().getTime())) );
            return ps.execute();
        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }

    public Ticket getTicket(String vehicleRegNumber) {
        Ticket ticket = null;
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1,vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)),false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(2));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(3));
                ticket.setInTime(rs.getTimestamp(4));
                ticket.setOutTime(rs.getTimestamp(5));
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
            return ticket;
        }
    }

        public boolean checkRegularUsers (String readVehicleRegistrationNumber) {
            boolean regularUser = false;
            Connection con = null;
            try {
                con = dataBaseConfig.getConnection();
                PreparedStatement ps = con.prepareStatement(DBConstants.CHECK_REGULAR_USERS);
                //(ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
                ps.setString(1, readVehicleRegistrationNumber);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    if(rs.getString("VEHICLE_REG_NUMBER").equalsIgnoreCase(readVehicleRegistrationNumber)){
                        regularUser = true;
                        System.out.println("Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.");
                }
                }
                dataBaseConfig.closeResultSet(rs);
                dataBaseConfig.closePreparedStatement(ps);
            }catch (Exception ex){
                logger.error("Error can't define regular user ",ex);
            }finally {
                dataBaseConfig.closeConnection(con);
            }
            return regularUser;
        }

    public boolean checkAlreadyInParking (String readVehicleRegistrationNumber) {
        boolean alreadyInParking = false;
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.CHECK_ALREADY_IN_PARKING);
            //(ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1, readVehicleRegistrationNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if(!rs.getBoolean("AVAILABLE")){
                    alreadyInParking = true;
                }
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        }catch (Exception ex){
            logger.error("Error can't define if vehicle is already in parking ",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
        return alreadyInParking;
    }


    public boolean updateTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
            ps.setInt(3,ticket.getId());
            ps.execute();
            dataBaseConfig.closePreparedStatement(ps);
            return true;
        }catch (Exception ex){
            logger.error("Error saving ticket info",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }
}
