package phone_numbers;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;


//Class to convert any phone number 
//to an standard format used by google according to country code imprinted
//on sim card 
public class To_international {
	
	String countryCode;
	public To_international(Context con) {
		TelephonyManager tm = (TelephonyManager)con.getSystemService(con.TELEPHONY_SERVICE);
        countryCode = tm.getNetworkCountryIso();
	}
	
	public String change_to_international(String ph_no)
	{
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
	    try {
	      PhoneNumber swissNumberProto = phoneUtil.parse(ph_no, countryCode.toUpperCase());
	      ph_no = phoneUtil.format(swissNumberProto, PhoneNumberFormat.E164);
	      return ph_no;
	    } catch (NumberParseException e) {
	    	Log.d("error in phone number", e.getMessage());
	    	return null;	
	    }
	}
}
