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
        if (obj instanceof Shop) {
            Shop shop2 = (Shop) obj;
            String id2 = shop2.getId();
            String name2 = shop2.getName();
            String company_id2 = shop2.getCompany_id();
            String vendor_id2 = shop2.getVendor_id();
            if ((id != null ? id.equals(id2) : id == id2) &&
                    (name != null ? name.equals(name2) : name == name2) &&
                    (company_id != null ? company_id.equals(company_id2) : company_id == company_id2) &&
                    (vendor_id != null ? vendor_id.equals(vendor_id2) : vendor_id == vendor_id2)) return true;
        }
        return false;
    }
    @Override
    public String toString() {
        return getId().toString();
    }
}
