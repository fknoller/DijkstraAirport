package view;

import model.Airport;

import java.sql.SQLException;
import java.util.Scanner;

public class main {
    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Do you want to do an airport search? (Y/N)");
        String city, state;
        while(sc.nextLine().equalsIgnoreCase("Y")) {
            //prints states list
            System.out.print("Please select the state from the list above: ");
            state = sc.nextLine();
            //prints cities list
            System.out.print("Now, please select the city from the list above: ");
            city = sc.nextLine();
            //prints airport list

        }
    }
}
