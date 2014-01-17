package com.rbinesconsulting.localisation;

import java.util.HashMap;

public class StringResource {

	private String key;
	private String[] locales;

	private HashMap<String, String> stringLocales;
	private String description;

	public StringResource(String key, String description, String[] locales) {
		this.key = key;
		this.description = description;
		this.locales = locales;
		stringLocales = new HashMap<String, String>();
	}

	public String getDescription() {
		return description;
	}

	public String getKey() {
		return key;
	}

	public void addString(String locale, String value) {
		stringLocales.put(locale, value);
	}

	public String getLocaleString(String locale) {
		return stringLocales.get(locale);
	}

	public HashMap<String, String> getStringLocales() {
		return stringLocales;
	}

	public String[] getLocales() {
		return locales;
	}
}
