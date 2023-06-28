package lendingapi.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Itotia Kibanyu on 27 Jun, 2023
 */
public class AppUtil {

    public static final String LINE = "---------------------------------------------\n";

    public static boolean isInputValid(String inputString) {
        return !inputString.isBlank() && !inputString.isEmpty();
    }

    public static boolean isNumberValid(String inputString) {
        return inputString.matches("[0-9]+");
    }

    public static boolean isPhoneNumberValid(String inputString) {

        inputString = parsePhoneNumber(inputString);

        boolean isValid = false;
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(inputString, LendingApiEnums.KE.label);
            isValid = phoneNumberUtil.isValidNumber(number);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return isValid;
    }

    public static String parsePhoneNumber(String phone) {
        phone = phone.replace("+", "");
        String finalPhoneNumber = "";
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phone, LendingApiEnums.KE.label);
            finalPhoneNumber = phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);

        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return finalPhoneNumber.replace(" ", "").replace("+", "");
    }

    public static long returnDays(String dueDate) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
            Date todayDate = sdf.parse(getTodayDate());
            Date loanDueDate = sdf.parse(dueDate);
            long daysDiff =  loanDueDate.getTime() - todayDate.getTime();

           return TimeUnit.DAYS.convert(daysDiff, TimeUnit.MILLISECONDS);

        } catch (ParseException e) {
            return 0;
        }

    }


    public static String getTodayDate() {
        DateFormat outputDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);

        return outputDateFormat.format(calendar.getTime());
    }


}
