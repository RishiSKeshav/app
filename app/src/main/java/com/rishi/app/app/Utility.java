package com.rishi.app.app;

/**
 * Created by RishiS on 1/27/2016.
 */
import android.content.Context;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Class which has Utility methods
 *
 */
public class Utility {
    private static Pattern pattern;
    private static Matcher matcher;

    private ArrayList<AlbumMedia> albummedia= new ArrayList<AlbumMedia>();
    //Email Pattern
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    private static final String PASSWORD_PATTERN ="^.{6,20}";

    /**
     * Validate Email with regular expression
     *
     * @param email
     * @return true for Valid Email and false for Invalid Email
     */

    private Context _context;

    // constructor
    public Utility(Context context) {
        this._context = context;
    }

    public static boolean validateEmail(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean validatePassword(String password) {
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean validateMobile(String mobileNo) {
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(mobileNo);
        return matcher.matches();
    }

    public static boolean checkMatchPassword(String password,String confirmpassword){
        if(password.equals(confirmpassword)){
            return true;
        }
        return false;
    }



    /**
     * Checks for Null String object
     *
     * @param txt
     * @return true for not null and false for null String object
     */
    public static boolean isNotNull(String txt){
        return txt!=null && txt.trim().length()>0 ? true: false;
    }
}
