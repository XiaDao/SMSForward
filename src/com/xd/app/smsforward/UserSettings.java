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

public class UserSettings extends PreferenceActivity {

	public Preference feedback;
	public Preference forwardswitch;
	public Preference forwardstyle;
	public Preference phonenumber;
	public Preference emailaddress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_settings);
		
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
	
}
