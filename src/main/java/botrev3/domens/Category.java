package botrev3.domens;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Category {


    public static List<Category> categories = new ArrayList<>();

    public static Category getCategoryForId(String givenId){
        for (Category category:categories){
            if (category.getId().equals(givenId))return category;
        }
        Category newCategory = new Category(givenId);
        categories.add(newCategory);
        return new Category(givenId);
    }

    private Category(String id) {
        this.id = id;
    }

    private Category() {
    }

    public static Category getCategoryForPrice(int price){
        for (Category cat:categories){
            if (price>cat.getLowLevelRange()&&price<=cat.getHighLevelRange())return cat;
        }
        return null;

    }

    @Getter @Setter
    private String id;
    @Getter @Setter
    private String sub_id;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String description;
    @Getter @Setter
    private int lowLevelRange;
    @Getter @Setter
    private int highLevelRange;

    @Getter @Setter
    private String range; // lowLevelRange+"-"+highLevelRange;

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

    public void renewRange() {
        int raiser = getRange().indexOf("-");
        setLowLevelRange(Integer.parseInt(range.substring(0,raiser)));
        setHighLevelRange(Integer.parseInt(range.substring(raiser+1, getRange().length())));
    }
}
