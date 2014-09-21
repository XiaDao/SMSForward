package com.xd.app.smsforward;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

public class UserSettings extends PreferenceActivity {

	public Preference feedback;
	public Preference forwardswitch;
	public Preference forwardstyle;
	public Preference phonenumber;
	public Preference emailaddress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.user_settings);
		
		initSettings();
	}

	/**
	 * <p>Method Name：initSettings</p>
	 * <p>Method Description：Initialize Settings Panel</p>
	 * @author XiaDao
	 * @since  2014-9-21
	 */
	@SuppressWarnings("deprecation")
	private void initSettings() {
		addPreferencesFromResource(R.xml.preference);
		
		//Enable Forward 
		forwardswitch = (Preference) findPreference("forwardswitch");
		
		//Forward Style
		forwardstyle = (Preference) findPreference("forwardstyle");
		forwardstyle.setOnPreferenceChangeListener(listListener);
		String forwardstyleValue = (String) getPreferenceValue(forwardstyle);
		listListener.onPreferenceChange(forwardstyle, forwardstyleValue);
		
		//Receive Phone Number
		phonenumber = (Preference) findPreference("phonenumber");
		phonenumber.setOnPreferenceChangeListener(listListener);
		listListener.onPreferenceChange(phonenumber, (String) getPreferenceValue(phonenumber));
		if(forwardstyleValue.equals("0")) {
			phonenumber.setEnabled(true);
		}else {
			phonenumber.setEnabled(false);
		}
		
		//Receive E-mail Address
		emailaddress = (Preference) findPreference("emailaddress");
		emailaddress.setOnPreferenceChangeListener(listListener);
		listListener.onPreferenceChange(emailaddress, (String) getPreferenceValue(emailaddress));
		if(forwardstyleValue.equals("1")) {
			emailaddress.setEnabled(true);
		}else {
			emailaddress.setEnabled(false);
		}
		
		//Contact Us
		feedback = (Preference) findPreference("feedback");
		feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				//发送邮件
				sendEmail();
				return false;
			}
		});
	}
	
	/**
	 * Preference Common ChangeListener
	 */
	public Preference.OnPreferenceChangeListener listListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			String stringValue = newValue.toString();
			if (preference instanceof ListPreference) {
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);
				preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
				if(stringValue.equals("0") && phonenumber!=null) {
					phonenumber.setEnabled(true);
					if(emailaddress!=null){
						emailaddress.setEnabled(false);
					}
				}else if(stringValue.equals("1") && emailaddress!=null) {
					emailaddress.setEnabled(true);
					if(phonenumber!=null){
						phonenumber.setEnabled(false);
					}
				}
				return true;
			}else if(preference instanceof EditTextPreference) {
				preference.setSummary(stringValue);
			}
			return true;
		}
	};
	
	/**
	 * <p>Method Name：getPreferenceValue</p>
	 * <p>Method Description：Get Different type Preference's Value</p>
	 * @param preference
	 * @return
	 * @author XiaDao
	 * @since  2014-9-18
	 */
	private Object getPreferenceValue(Preference preference) {
		SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
		if (preference instanceof ListPreference) {
			return manager.getString(preference.getKey(),"");
		}else if (preference instanceof EditTextPreference) {
			return manager.getString(preference.getKey(),"");
		}else if (preference instanceof CheckBoxPreference) {
			return manager.getBoolean(preference.getKey(),true);
		}
		return null;
	}
	
	/**
	 * <p>Method Name：sendEmail</p>
	 * <p>Method Description：Contact Us Send E-mail</p>
	 * @author XiaDao
	 * @since  2014-9-17
	 */
	private void sendEmail(){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"xiadaokfz@126.com"});
		intent.putExtra(Intent.EXTRA_SUBJECT, "MSMForward 问题反馈");
		intent.putExtra(Intent.EXTRA_TEXT, new String[]{});
		intent.setType("plain/text");
		startActivity(Intent.createChooser(intent, "Mail Chooser"));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			if(!isEnableReturn()) {
				Toast.makeText(this, "信息不完整，请补充完整！", Toast.LENGTH_SHORT).show();
				return false;
			}
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(!isEnableReturn()) {
				Toast.makeText(this, "信息不完整，请补充完整！", Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * <p>Method Name：isEnableReturn</p>
	 * <p>Method Description：Is Could Return</p>
	 * @return
	 * @author XiaDao
	 * @since  2014-9-18
	 */
	private boolean isEnableReturn() {
		if((Boolean) getPreferenceValue(forwardswitch)) {
			String forwardstyleValue = (String) getPreferenceValue(forwardstyle);
			if(forwardstyleValue.equals("0")) {
				String photo = (String) getPreferenceValue(phonenumber);
				if(photo==null || photo.length()==0) {
					return false;
				}
			}else if(forwardstyleValue.equals("1")) {
				String email = (String) getPreferenceValue(emailaddress);
				if(email==null || email.length()==0) {
					return false;
				}
			}
		}
		return true;
	}
}
