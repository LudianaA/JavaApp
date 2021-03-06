package com.codesmiths.mongoDB;

import com.codesmiths.priority.Priority;
import com.codesmiths.structures.Email;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;



public class MongoDBConnector {
    // Returns list of emails from MongoDB
    public static List<Email> getHackathonData() {
        // Temporary data pulled from a capture of the endpoint
        ArrayList<Email> emails = new ArrayList<Email>();

        String csvFile = (System.getenv("USERPROFILE") + "\\Desktop\\Hackathon\\JavaApp\\mongo.csv");
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {
                String[] email = line.split(cvsSplitBy);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Email e = null;
                try {
                    e = new Email(email[5], email[1], email[0], email[2], email[4], sdf.parse(email[3]), Priority.LOW);
                } catch (ParseException ex) { }

                emails.add(e);
            }

        } catch (IOException e) { }

        return emails;
    }

    // Tells MongoDB to update the data from the endpoint
    public static void updateHackathonData() {
        try {
            Runtime.getRuntime().exec("python resources/scripts/EmailRetriever.py");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean authenticateUser(String username, String password) {
        // Connect the MongoDB and authenticate user
        System.out.println("Username:" + username + "\nPassword: " + hashPassword(password));
        return true;
    }

    private static String hashPassword(String password) {
        MessageDigest md = null;
        String hashed = "";

        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];

            md = MessageDigest.getInstance("SHA-512");
            random.nextBytes(salt);
            md.update(salt);

            byte[] passwd = md.digest(password.getBytes(StandardCharsets.UTF_8));

            hashed = new String(passwd, "UTF-8");
        } catch (Exception e) { }
        return hashed;
    }
}