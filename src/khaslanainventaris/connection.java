/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package khaslanainventaris;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author rafidhp
 */
public class connection {
    Connection conn;
    
    public connection() {
        String id, password, driver, url;
        
        id = "aizen";
        password = "aizen.exe";
        driver = "com.mysql.cj.jdbc.Driver";
        url = "jdbc:mysql://localhost:3306/khaslana_inventaris?userTimezone=true&server=UTC";
        
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url, id, password);
            
            if (conn == null) {
                System.out.println("Koneksi Gagal");
            } else {
                System.out.println("Koneksi Berhasil");
            }
        } catch (Exception e) {
            System.out.println("error database connection: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        connection dbc = new connection();
    }
}
