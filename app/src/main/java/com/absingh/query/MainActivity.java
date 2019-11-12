package com.absingh.query;

/*
  Created by Pratik Butani
  www.pratikbutani.com
 */

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.absingh.query.helper.Util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

	/**
	 * Driver for Oracle
	 */
	private static final String DEFAULT_DRIVER = "oracle.jdbc.driver.OracleDriver";

	/**
	 * URL to connect database
	 */
	private static final String DEFAULT_URL = "jdbc:oracle:thin:@192.168.0.102:1521:oracle"; // Change IP_Address:Database

	private static String DEFAULT_USERNAME;
	private static String DEFAULT_PASSWORD;

	/**
	 * Context
	 */
	Context context;

	/**
	 * Creating Connection
	 *
	 * @param driver   driver object
	 * @param url      url for db
	 * @param username username
	 * @param password password
	 * @return Connection object
	 * @throws ClassNotFoundException throwing exception
	 * @throws SQLException           throwing exception
	 */
	public static Connection createConnection(String driver, String url, String username, String password) throws ClassNotFoundException, SQLException {
		Class.forName(driver);
		return DriverManager.getConnection(url, username, password);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = getApplicationContext();

		/*
		 * To handle NetworkOnMainThreadException
		 */
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		/*
		 * Just for display data in Table
		 */
		TableLayout tableLayout = findViewById(R.id.tlGridTable);
		TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

		try {
			/*
			  Creating Connection
			 */
			Connection connection = createConnection();

			if (connection != null) {
				Toast.makeText(MainActivity.this, "Connected with Database", Toast.LENGTH_SHORT).show();

				/*
				  Creating statement to get for query
				 */
				Statement stmt = connection.createStatement();
				StringBuilder stringBuilder = new StringBuilder();

				/*
				  Executing Query for get Data
				 */
				ResultSet rs = stmt.executeQuery("select * from genpara"); // write your table name here..

				/*
				  Getting MetaData
				 */
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnsNumber = rsmd.getColumnCount();

				/*
				  Creating Table Row for Header Part
				 */
				TableRow tableRow = new TableRow(context);
				tableRow.setLayoutParams(rowParams);

				// print column names
				for (int i = 1; i <= columnsNumber; i++) {
					TextView textView = new TextView(context);
					textView.setText(getString(R.string.tab).concat(rsmd.getColumnName(i)).concat(getString(R.string.tab)));
					textView.setLayoutParams(rowParams);// TableRow is the parent view
					textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
					tableRow.addView(textView);
				}

				tableLayout.addView(tableRow);

				/*
				  Getting each row
				 */
				while (rs.next()) {
					TableRow tableRow1 = new TableRow(context);
					tableRow1.setLayoutParams(rowParams);// TableLayout is the parent view

					/*
					  Getting Columns
					 */
					for (int i = 1; i <= columnsNumber; i++) {
						String columnValue = rs.getString(i);
						stringBuilder.append(columnValue);

						TextView textView = new TextView(context);
						textView.setText(getString(R.string.tab).concat(columnValue).concat(getString(R.string.tab)));
						textView.setLayoutParams(rowParams);// TableRow is the parent view
						tableRow1.addView(textView);
					}
					/* Adding into Table */
					tableLayout.addView(tableRow1);
				}
				/*
				  Closing connection.
				 */
				connection.close();
			} else {
				Toast.makeText(MainActivity.this, "Not Connected : " + connection, Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(MainActivity.this, "" + e, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	public Connection createConnection() throws ClassNotFoundException, SQLException {
		try {
			DEFAULT_USERNAME = Util.getProperty("username", context);
			DEFAULT_PASSWORD = Util.getProperty("password", context);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return createConnection(DEFAULT_DRIVER, DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
	}
}