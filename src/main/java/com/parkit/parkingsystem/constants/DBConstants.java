package com.parkit.parkingsystem.constants;

public final class DBConstants {

    private DBConstants(){};
    public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE = ?";
    public static final String UPDATE_PARKING_SPOT = "update parking set available = ? where PARKING_NUMBER = ?";

    public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) values(?,?,?,?,?)";
    public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=? where ID=?";
    public static final String CHECK_REGULAR_USERS ="SELECT *  from ticket WHERE VEHICLE_REG_NUMBER =?";
    public static final String CHECK_ALREADY_IN_PARKING ="SELECT t.PARKING_NUMBER, p.AVAILABLE from ticket t,parking p where p.parking_number = t.parking_number and p.available = 0 and t.VEHICLE_REG_NUMBER=? order by p.PARKING_NUMBER limit 1 ";
    public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.ID DESC limit 1";
}
