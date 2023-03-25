package com.mercadona.barcoderestapi.model;

//In the case we want to give some data of the destination we would create an Entity instead of an Enum
public enum DestinationEnum {
    SPAIN, PORTUGAL, WAREHOUSE, OFFICE, HIVE;

    public static DestinationEnum getDestinationFromCode(Integer code){
        //Conditions from briefing the switch case
        code = 0 < code && code <= 5 ?  -1 : code;
        switch(code){
            case -1:
                return SPAIN;
            case 6:
                return PORTUGAL;
            case 8:
                return WAREHOUSE;
            case 9:
                return OFFICE;
            case 0:
                return HIVE;
            default:
                return null;
        }
    }
}
