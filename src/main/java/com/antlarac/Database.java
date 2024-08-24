package com.antlarac;

import java.sql.*;

import static java.lang.Class.forName;

public class Database {

  public Database() throws SQLException, ClassNotFoundException {
      // uncomment next line for running in IDE, commont for loading in Burp
//       Class.forName("org.sqlite.JDBC");
    System.getProperty("java.class.path");
  String url = "jdbc:sqlite:archaeology.db";
  try {
      Connection conn = DriverManager.getConnection(url);
  Statement stmt = conn.createStatement();

    stmt.execute("CREATE TABLE IF NOT EXISTS history (id INTEGER PRIMARY KEY AUTOINCREMENT,comment TEXT)");

//     stmt.execute("INSERT INTO history VALUES (1, 'San Francisco')");
//     stmt.execute("INSERT INTO history VALUES (2, 'San Jose')");
//     stmt.execute("INSERT INTO history VALUES (3, 'San Diego')");
     stmt.close();
  conn.close();
  } catch (SQLException e) {
      System.out.println("Error connecting to DB");;
      e.printStackTrace();
      }

  }
}
