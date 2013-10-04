package com.binesy.localisation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.binesy.localisation.creator.AndroidResourceCreator;
import com.binesy.localisation.creator.IosResourceCreator;
import com.binesy.localisation.creator.ResourceCreator;

public class Generator {

	private static final String DEFAULT_KEY_COLUMN_NAME = "key";
	private static final String DEFAULT_DESCRIPTION_COLUMN_NAME = "description";

	private static boolean debugEnabled = false;
	private boolean buildAllPlatforms = false;
	private File outputFile;
	private File inputFile;
	private ArrayList<String> osToBuild;
	private String[] columnsToIgnore;
	private String keyColumnName;
	private String descriptionColumnName;
	private String replacePattern;

	public Generator(String[] args) {
		osToBuild = new ArrayList<String>();
		processParameters(args);
	}

	private void processParameters(String[] args) {
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);

			// Debug
			if (args[i].equals("-D") || args[i].equals("-d")) {
				debugEnabled = true;
			}

			// Output file location
			else if (args[i].contains("-output")) {
				outputFile = new File(getParameterValue(args[i]));
			}

			// Input file location
			else if (args[i].contains("-input")) {
				inputFile = new File(getParameterValue(args[i]));
			}

			// Build all OS versions
			else if (args[i].equals("-all")) {
				buildAllPlatforms = true;
			}

			// OS to build
			else if (args[i].contains("-os")) {
				String data = getParameterValue(args[i]);
				osToBuild = getOsToBuild(data);
			}

			// Key column name
			else if (args[i].contains("-keyColumnName")) {
				keyColumnName = getParameterValue(args[i]);
			}

			// Description column name
			else if (args[i].contains("-descriptionColumnName")) {
				descriptionColumnName = getParameterValue(args[i]);
			}

			// Columns to ignore
			else if (args[i].contains("-ignoreColumns")) {
				columnsToIgnore = setColumnsToIgnore(args[i]);
			}

			// Text to replace
			else if (args[i].contains("-replacePattern")) {
				replacePattern = getParameterValue(args[i]);
			}

			// Anything else give an error
			else {
				System.out.println("Unknown parameter " + args[i]);
			}
		}

		if (keyColumnName == null) {
			keyColumnName = DEFAULT_KEY_COLUMN_NAME;

			if (debugEnabled) {
				System.out.println("No key column specified. Using default - \"key\"");
			}
		}

		if (descriptionColumnName == null) {
			descriptionColumnName = DEFAULT_DESCRIPTION_COLUMN_NAME;

			if (debugEnabled) {
				System.out.println("No description column specified. Using default - \"description\"");
			}
		}

		if (outputFile == null) {
			System.out.println("Must specify an output location");
		}

		if (inputFile == null) {
			System.out.println("Must specify an input location");
		} else if (!getFileExtension(inputFile.getAbsolutePath()).equals("xlsx")) {
			System.out.println("Localisation generator only supports XLSX files. Please try another file.");
		}

		if (!buildAllPlatforms && osToBuild.size() == 0) {
			System.out.println("You must either specify OSs to build with -os or build all with -all");
		}
	}

	private String[] setColumnsToIgnore(String arg) {
		String data = getParameterValue(arg);
		return data.split(",");
	}

	private String getParameterValue(String arg) {
		String[] keyValue = arg.split("=");
		String value = keyValue[1];

		// Remove quotes around arg data if present
		if (value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
			StringBuilder sb = new StringBuilder(value);
			sb.deleteCharAt(0);
			sb.deleteCharAt(sb.length() - 1);

			return sb.toString();
		}

		return value;
	}

	private ArrayList<String> getOsToBuild(String data) {
		String[] os = data.split(",");
		return new ArrayList<String>(Arrays.asList(os));
	}

	private String getFileExtension(String file) {
		String extension = "";

		int i = file.lastIndexOf('.');
		int p = Math.max(file.lastIndexOf('/'), file.lastIndexOf('\\'));

		if (i > p) {
			extension = file.substring(i + 1);
		}

		return extension;
	}

	public void generateFiles() throws FileNotFoundException {
		ArrayList<StringResource> resources = createStringResources(inputFile);

		if (buildAllPlatforms()) {
			ResourceCreator c = new AndroidResourceCreator(resources, outputFile, replacePattern);
			c.createResources();

			c = new IosResourceCreator(resources, outputFile, replacePattern);
			c.createResources();
		} else {
			ArrayList<String> osToBuild = getOsToBuild();
			ResourceCreator creator = null;

			for (String os : osToBuild) {
				if (os.equals("ios".toLowerCase())) {
					creator = new IosResourceCreator(resources, outputFile, replacePattern);

				} else if (os.equals("android".toLowerCase())) {
					creator = new AndroidResourceCreator(resources, outputFile, replacePattern);

				} else {
					throw new IllegalArgumentException("Unknown OS to build \"" + os
							+ "\". Must be either android or ios");
				}

				creator.createResources();
			}
		}
	}

	private FileInputStream fis;

	private ArrayList<StringResource> createStringResources(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			Sheet sheet = wb.getSheetAt(0);

			ArrayList<StringResource> stringResources = new ArrayList<StringResource>();
			boolean headersRead = false;

			ArrayList<String> headers = new ArrayList<String>();
			ArrayList<String> rows = new ArrayList<String>();

			// Iterate through each rows from first sheet
			// Iterator<Row> rowIterator = sheet.iterator();
			// while (rowIterator.hasNext()) {
			// Row row = rowIterator.next();
			// rows.clear();
			//
			// // For each row, iterate through each columns
			// Iterator<Cell> cellIterator = row.cellIterator();
			//
			// while (cellIterator.hasNext()) {
			// Cell cell = cellIterator.next();
			//
			// if (!headersRead) {
			// headers.add(cell.getStringCellValue());
			// } else {
			// rows.add(cell.getStringCellValue());
			// System.out.println("Printing row data: " +
			// cell.getStringCellValue());
			// }
			// }
			//
			// if (!headersRead) {
			// headersRead = true;
			// } else {
			// StringResource res = createStringResource(headers, rows);
			// stringResources.add(res);
			// }
			// }

			// Get headers
			Row headerRow = sheet.getRow(0);

			// For each row, iterate through each columns
			Iterator<Cell> cellIterator = headerRow.cellIterator();

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				headers.add(cell.getStringCellValue());
			}

			// Iterate through each rows from first sheet
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				rows.clear();

				if (headersRead) {

					// For each row, iterate through each columns
					final int totalColumns = headers.size();
					for (int i = 0; i < totalColumns; i++) {
						Cell cell = row.getCell(i);
						String cellData;

						if (cell != null) {
							cellData = cell.getStringCellValue();
						} else {
							cellData = "";
						}

						rows.add(cellData);
					}

					StringResource res = createStringResource(headers, rows);
					stringResources.add(res);

				} else {
					headersRead = true;
				}
			}

			return stringResources;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	private StringResource createStringResource(ArrayList<String> headers, ArrayList<String> rows) {
		if (rows.size() == 0) {
			return null;
		}

		// Check for blank entries in row and fill out with blanks. This should
		// never happen as all fields should be filled in.
		if (headers.size() > rows.size()) {
			int rowsToAdd = headers.size() - rows.size();
			for (int i = 0; i < rowsToAdd; i++) {
				rows.add("");
			}
		}

		int keyIndex = getColumnIndex(keyColumnName, headers);

		if (keyIndex == -1) {
			keyIndex = 0;
		}

		int descriptionIndex = getColumnIndex(descriptionColumnName, headers);

		if (descriptionIndex == -1) {
			descriptionIndex = 0;
		}

		String key = rows.get(keyIndex);
		String description = rows.get(descriptionIndex);

		final int totalColumnsToIgnore = (columnsToIgnore == null ? 0 : columnsToIgnore.length);
		String[] locales = new String[headers.size() - 2 - totalColumnsToIgnore];

		for (int i = 0; i < locales.length; i++) {
			locales[i] = headers.get(i + 2 + totalColumnsToIgnore);
		}

		StringResource stringResource = new StringResource(key, description, locales);

		for (int i = 0; i < headers.size(); i++) {
			if (!ignoreHeader(headers.get(i))) {
				stringResource.addString(headers.get(i).trim(), rows.get(i).trim());
			}
		}

		return stringResource;
	}

	private boolean ignoreHeader(String header) {
		if (columnsToIgnore != null) {
			for (String column : columnsToIgnore) {
				if (header.equals(column)) {
					return true;
				}
			}
		}

		return false;
	}

	private int getColumnIndex(String columnName, ArrayList<String> headers) {
		final int totalSize = headers.size();
		for (int i = 0; i < totalSize; i++) {
			if (columnName.equals(headers.get(i))) {
				return i;
			}
		}

		return -1;
	}

	public static boolean isDebugEnabled() {
		return debugEnabled;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public File getInputFile() {
		return inputFile;
	}

	public boolean buildAllPlatforms() {
		return buildAllPlatforms;
	}

	public ArrayList<String> getOsToBuild() {
		return osToBuild;
	}
}
