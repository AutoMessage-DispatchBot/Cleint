package database;

import data.Buyer;

import java.util.LinkedList;
import java.util.List;

public class Database {
    private static List<Buyer> buyersList = new LinkedList<>();
    private static List<String> managersList = new LinkedList<>();

    public static synchronized void setBuyersList(List<Buyer> buyersList) {
         Database.buyersList = buyersList;
    }

    public static synchronized void setManagersList(List<String> managersList) {
        Database.managersList = managersList;
    }

    public static synchronized List<Buyer> getBuyersList() {
        return buyersList;
    }

    public static synchronized List<String> getManagersList() {
        return managersList;
    }

    public static synchronized Buyer getBuyer(int id) {
        for(Buyer buyer: Database.buyersList) {
            if(buyer.id() == id)
                return buyer;
        }

        return null;
    }
}
