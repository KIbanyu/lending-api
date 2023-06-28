package lendingapi.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.jcraft.jsch.*;

import java.io.File;
import java.sql.Timestamp;
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

        if (inputString == null){
            return false;
        }
        return !inputString.isBlank() && !inputString.isEmpty();
    }

    public static boolean isNumberValid(String inputString) {
        return inputString.matches("[0-9]+");
    }

    public static boolean isPhoneNumberValid(String inputString) {
        if (!isNumberValid(inputString.replace("+", ""))){
            return false;
        }

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


    public static void backUpMySql()  throws JSchException, SftpException {
        File file = new File("mysqlBackup");
        File f = new File(file, new Timestamp(System.currentTimeMillis()).getTime() + ".sql");


        //Run the command to ge the sql dumps.
        //On success, create a connection with the remote server
        //Send the file to the server.
         String remoteHost = "HOST_NAME_HERE";
         String username = "USERNAME_HERE";
         String password = "PASSWORD_HERE";
        ChannelSftp channelSftp = setupJsch(username, password, remoteHost);

        channelSftp.connect();
        String localFile = f.getPath();
        String remoteDir = "remote_sftp_test/";
        channelSftp.put(localFile, remoteDir + "jschFile.txt");
        channelSftp.exit();


    }

    private static ChannelSftp setupJsch(String username, String password, String remoteHost) throws JSchException {
        JSch jsch = new JSch();
        jsch.setKnownHosts("yourhosts here");
        Session jschSession = jsch.getSession(username, remoteHost);
        jschSession.setPassword(password);
        jschSession.connect();
        return (ChannelSftp) jschSession.openChannel("sftp");
    }


}
