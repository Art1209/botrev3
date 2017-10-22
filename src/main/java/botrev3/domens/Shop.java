package botrev3.domens;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Shop {

    public static List<Shop> shops = new ArrayList<>();

    public static Shop getShopForId(String givenId){
        for (Shop shop:shops){
            if (shop.getId().equals(givenId))return shop;
        }
        Shop newShop = new Shop(givenId);
        shops.add(newShop);
        return newShop;
    }

    public static Shop getShopForLink(String link) {
        for (Shop shop : shops) {
            if (link.contains(shop.getName())) return shop;
        }
        return null;
    }

    private Shop(String id) {
        this.id = id;
    }

    private Shop() {
    }

    @Getter @Setter
    private String id;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private String vendor_id;

    @Getter @Setter
    private String company_id;

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return getId().equals(obj);
    }

    @Override
    public String toString() {
        return getId().toString();
    }
}
