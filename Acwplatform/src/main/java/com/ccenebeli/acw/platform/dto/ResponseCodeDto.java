/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccenebeli.acw.platform.dto;

/**
 *
 * @author Cendstudios
 */
public class ResponseCodeDto {
  public static String SUCCESSFUL = "00";
    public static String ALL_VALUES_REQUIRED = "02";
    public static String INVALID_SENDER = "03";
    public static String DO_NOT_HONOR = "05";
    public static String DORMANT_ACCOUNT = "06";
    public static String INVALID_ACCOUNT = "07";
    public static String ACCOUNT_NAME_MISMATCH = "08";
    public static String REQUEST_PROCESSING_IN_PROGRESS = "09";
    public static String INVALID_TRANSACTION = "12";
    public static String INVALID_AMOUNT = "13";
    public static String INVALID_BATCH_NUMBER = "14";
    public static String INVALID_SESSION_OR_RECORD_ID = "15";
    public static String UNKNOWN_BANK_CODE = "16";
    public static String INVALID_CHANNEL = "17";
    public static String WRONG_METHOD_CALL = "18";
    public static String NO_ACTION_TAKEN = "21";
    public static String UNABLE_TO_LOCATE_RECORD = "25";
    public static String DUPLICATE_RECORD = "26";
    public static String FORMAT_ERROR = "30";
    public static String SUSPECTED_FRAUD = "34";
    public static String CONTACT_SENDING_BANK = "35";
    public static String NO_SUFFICIENT_FUNDS = "51";
    public static String TRANSACTION_NOT_PERMITTED_TO_SENDER = "57";
    public static String TRANSACTION_NOT_PERMITTED_ON_CHANNEL = "58";
    public static String TRANSFER_LIMIT_EXCEEDED = "61";
    public static String SECURITY_VIOLATION = "63";
    public static String EXCEEDS_WITHDRAWAL_FREQUENCY = "65";
    public static String RESPONSE_RECEIVED_TOO_LATE = "68";
    public static String BENEFICIARY_BANK_NOT_AVAILABLE = "91";
    public static String ROUTING_ERROR = "92";
    public static String DUPLICATE_TRANSACTION = "94";
    public static String SYSTEM_MALFUNCTION = "96";
    public static String TIMEOUT_WAITING_FOR_RESPONSE_FROM_DESTINATION = "97";

    public static String REPEAT_REQUEST = "99";  
}
