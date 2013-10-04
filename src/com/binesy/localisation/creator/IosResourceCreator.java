package com.binesy.localisation.creator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.rbinesconsulting.localisation.StringResource;

public class IosResourceCreator extends ResourceCreator {

	public IosResourceCreator(ArrayList<StringResource> resources, File outputFile, String replacePattern) {
		super(resources, outputFile, replacePattern);
	}

	@Override
	protected String createResourceString(ArrayList<StringResource> resources, String locale) {
		StringBuilder sb = new StringBuilder();
		sb.append(getAutoGeneratedText());
		sb.append('\n');

		Set<String> keysAdded = new HashSet<String>();

		for (StringResource res : resources) {
			if (res != null) {
				if (!res.getKey().equals("") && !keysAdded.contains(res.getKey())) {
					String key = sanitiseKeyString(res.getKey());

					String replacedString = regexReplaceInString(res.getLocaleString(locale));
					String escapedData = escapeData(replacedString);
					String line = String.format(getStringItemTemplate(), res.getDescription(), key, escapedData);

					sb.append(line);
					sb.append("\n\n");

					keysAdded.add(res.getKey());
				}
			}
		}

		return sb.toString().trim();
	}

	@Override
	protected String sanitiseKeyString(String key) {
		return key.replace("\"", "").trim();
	}

	@Override
	protected String escapeData(String data) {
		data = data.replace("\"", "\\\"");
		return data.replace("\n", "\\n");
	}

	@Override
	protected String getStringItemTemplate() {
		return "/* %1$s */\n\"%2$s\" = \"%3$s\";";
	}

	@Override
	protected String getGeneratedFilePath(String locale) {
		locale = locale.replace(' ', '_').replace('\\', '-');
		return "ios\\" + locale + "\\Localizable.strings";
	}

	@Override
	protected String getAutoGeneratedText() {
		return "/**\n * This file has been generated automatically. Do NOT manually edit.\n */\n";
	}

	@Override
	protected String getRegexReplaceString() {
		return "%@";
	}
}
