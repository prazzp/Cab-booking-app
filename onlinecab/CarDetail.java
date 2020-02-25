package in.co.app.onlinecab;

/**
 * Created by root on 29/4/17.
 * "id": 1,
 "user": {
 "id": 3,
 "name": "basu",
 "contact": 72348927,
 "email": "basu@gmail.com"
 },
 "model": "alto",
 "reg_num": "KA 25 H1234",
 "current_location": "15.3690605,75.1194101",
 "type": "micro",
 "price": 6.0,
 "availability": true,
 "car_status": true
 */

public class CarDetail {

    public int nId;
    public int nUserId;
    public String sDriverName;
    public int nDriverContact;
    public String sDriverEmail;
    public String sModel;
    public String sReg_No;
    public String sCurrentLocation;
    public String sType;
    public double fPrice;
    public boolean isAvailable;
    public boolean isCarStatus;
}
