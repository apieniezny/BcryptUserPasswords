package org.neric;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringEscapeUtils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Main {

    public static List<String> statements = new ArrayList<>();

    public static void main(String[] args) throws IOException
    {

        Reader in = new FileReader("C:/regentsorder_users.csv");
        String file = "C:/regents_user_sql.txt";
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);

        for (CSVRecord record : records)
        {
            String firstName = record.get("FIRST_NAME");
            String lastName = record.get("LAST_NAME");
            String username = record.get("USERNAME");
            String password = record.get("PASSWORD");
            String districtId = record.get("district_id");

            sqlStatement(firstName, lastName, username, createEncryptedPassword(password), districtId);



        }

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(file)))
        {
            for(String s : statements)
            {
                System.out.println(s.toString());
                writer.write(s.toString() + System.getProperty("line.separator"));
            }
        }
        //System.out.println(statements.toString());
       // statements.forEach(System.out::println);
//        statements.stream().filter(s -> s.contains("Andrew")).forEach(System.out::println);
    }

    private static String createEncryptedPassword(String password)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    public static void sqlStatement(String firstName, String lastName, String username, String password, String districtId)
    {
        String message = String.format("insert into regentsorder.app_user (uuid, first_name, last_name, username, password, locked, visible) values (uuid(), '%s', '%s', '%s', '%s', 0, 1);", StringEscapeUtils.escapeSql(firstName), StringEscapeUtils.escapeSql(lastName), StringEscapeUtils.escapeSql(username), StringEscapeUtils.escapeSql(password));
        statements.add(message);

        String message2 = String.format("INSERT INTO user_district (district_id, user_id) SELECT '%s', u.id FROM regentsorder.app_user as u WHERE u.username = '%s';", districtId, username);
        statements.add(message2);

        String message3 = String.format("INSERT INTO app_user_user_profile (user_id, user_profile_id) SELECT u.id, 2 FROM regentsorder.app_user as u WHERE u.username = '%s';", username);
        statements.add(message3);
    }
}
