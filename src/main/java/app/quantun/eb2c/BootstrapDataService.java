package app.quantun.eb2c;


import app.quantun.eb2c.model.entity.bussines.Category;
import app.quantun.eb2c.model.entity.bussines.CategoryRepository;
import app.quantun.eb2c.model.entity.bussines.Product;
import app.quantun.eb2c.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BootstrapDataService implements CommandLineRunner {


    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;


    @Override
    public void run(String... args) {
        // Create roles if they don't exist

        // Create default users


        createCategoriesAndProducts();

    }


    private void createCategoriesAndProducts() {
        // Check if data already exists
        if (categoryRepository.count() > 0 && productRepository.count() > 0) {
            log.info("Categories and products already exist - skipping creation");
            return;
        }

        log.info("Starting to create categories and products...");

        // Create main categories with subcategories
        Map<String, List<String>> categoryStructure = new LinkedHashMap<>();

        // Electronics
        categoryStructure.put("Electronics", Arrays.asList("Smartphones", "Laptops", "Tablets", "Accessories"));

        // Clothing
        categoryStructure.put("Clothing", Arrays.asList("Men's Wear", "Women's Wear", "Kids", "Sportswear"));

        // Home & Kitchen
        categoryStructure.put("Home & Kitchen", Arrays.asList("Appliances", "Furniture", "Decor", "Kitchenware"));

        // Books
        categoryStructure.put("Books", Arrays.asList("Fiction", "Non-Fiction", "Educational", "Comics"));

        // Beauty & Personal Care
        categoryStructure.put("Beauty", Arrays.asList("Skincare", "Haircare", "Makeup", "Fragrances"));

        // Save categories and build category map
        Map<String, Category> allCategories = new HashMap<>();

        // Create and save all categories
        for (Map.Entry<String, List<String>> entry : categoryStructure.entrySet()) {
            String mainCategoryName = entry.getKey();
            List<String> subCategories = entry.getValue();

            Category mainCategory = new Category();
            mainCategory.setName(mainCategoryName);
            mainCategory = categoryRepository.save(mainCategory);
            allCategories.put(mainCategoryName, mainCategory);
            log.info("Created main category: {}", mainCategoryName);

            for (String subCategoryName : subCategories) {
                Category subCategory = new Category();
                subCategory.setName(subCategoryName);
                subCategory.setSubCategory(mainCategory);
                subCategory = categoryRepository.save(subCategory);
                allCategories.put(subCategoryName, subCategory);
                log.info("Created sub-category: {} under {}", subCategoryName, mainCategoryName);
            }
        }

        // Product data
        List<String> productNames = Arrays.asList(
                // Electronics - Smartphones
                "Ultra Phone X", "GalaxyTech Pro", "PixelView 5", "iConnect Plus", "MegaPhone Elite",
                // Electronics - Laptops
                "PowerBook Air", "UltraSlim Pro", "GamerStation X", "BusinessEdge 15", "CreatorPad Ultra",
                // Electronics - Tablets
                "SlateTab 10", "ProPad Mini", "DrawMaster Tab", "ViewTab 12", "SmartSlate 8",
                // Electronics - Accessories
                "SuperCharge Cable", "BlueSound Pro Earbuds", "ClickMaster Mouse", "ErgoComfort Keyboard", "ViewClear Screen Protector",

                // Clothing - Men's Wear
                "Classic Fit Shirt", "Urban Denim Jeans", "Business Casual Blazer", "ComfortFlex Chinos", "Moisture-Wicking Polo",
                // Clothing - Women's Wear
                "Elegance Blouse", "Stretch Fit Jeans", "Summer Maxi Dress", "Office Pencil Skirt", "Casual Knit Cardigan",
                // Clothing - Kids
                "Adventure Dinosaur Tee", "Elastic Waist Jeans", "Princess Party Dress", "School Uniform Polo", "Comfy Sleep Set",
                // Clothing - Sportswear
                "Performance Running Shirt", "Flex Training Shorts", "Quick-Dry Tennis Skirt", "Compression Leggings", "Breathable Basketball Jersey",

                // Home & Kitchen - Appliances
                "QuickBlend Pro Mixer", "SmartBrew Coffee Maker", "PowerVac Ultimate", "FreshKeep Refrigerator", "SpeedDry Dishwasher",
                // Home & Kitchen - Furniture
                "Ergonomic Office Chair", "Memory Foam Mattress", "Convertible Sofa Bed", "Rustic Coffee Table", "Modular Bookshelf System",
                // Home & Kitchen - Decor
                "Ambient LED Lamp", "Botanical Wall Art", "Handwoven Throw Blanket", "Geometric Area Rug", "Scented Soy Candle Set",
                // Home & Kitchen - Kitchenware
                "Non-Stick Cookware Set", "Precision Chef Knife", "Crystal Wine Glass Set", "Bamboo Cutting Board", "Stainless Steel Utensils",

                // Books - Fiction
                "Midnight Shadows Mystery", "Epic of the Forgotten Realm", "Love Beyond Time", "The Last Detective", "Whispers in the Wind",
                // Books - Non-Fiction
                "Habits of Success", "Historical Perspectives", "The Science of Everything", "Memoirs of a Chef", "Financial Freedom Guide",
                // Books - Educational
                "Complete Python Programming", "World History: Complete Edition", "Advanced Calculus Simplified", "Chemistry Essentials", "Language Learning Master",
                // Books - Comics
                "Superhero Chronicles Vol.1", "Manga Adventure Series", "Graphic Novel Masterpiece", "Comic Anthology", "Illustrated Epic Tales",

                // Beauty - Skincare
                "Hydrating Face Serum", "Revitalizing Night Cream", "Gentle Cleansing Foam", "SPF 50 Sunscreen", "Exfoliating Face Scrub",
                // Beauty - Haircare
                "Volumizing Shampoo", "Repair & Restore Conditioner", "Styling Gel Strong Hold", "Argan Oil Hair Treatment", "Anti-Frizz Serum",
                // Beauty - Makeup
                "Long-Wear Foundation", "Volumizing Mascara", "24H Matte Lipstick", "Eyeshadow Palette", "Precision Eyeliner Pen",
                // Beauty - Fragrances
                "Citrus Burst Cologne", "Floral Elegance Perfume", "Woody Essence EDT", "Fresh Ocean Scent", "Vanilla Dreams Fragrance"
        );

        // Product descriptions
        List<String> descriptions = Arrays.asList(
                "Premium quality product designed for everyday use.",
                "Elevate your experience with this high-performance item.",
                "Crafted with care using only the finest materials available.",
                "Innovative design meets exceptional functionality.",
                "The perfect balance of style, comfort, and durability.",
                "Latest generation technology for optimal performance.",
                "Eco-friendly and sustainable choice for conscious consumers.",
                "Versatile option suitable for various needs and occasions.",
                "Enhanced features that simplify your daily routine.",
                "Expertly engineered to exceed industry standards."
        );

        Random random = new Random();

        // Create 100 products
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            // Select random values
            String name = productNames.get(i % productNames.size()) + " " + (i + 1);
            String description = descriptions.get(random.nextInt(descriptions.size()));
            BigDecimal price = BigDecimal.valueOf(10 + random.nextInt(990))
                    .add(BigDecimal.valueOf(random.nextInt(100) / 100.0))
                    .setScale(2, RoundingMode.HALF_UP);
            boolean inStock = random.nextBoolean();
            int stock = inStock ? random.nextInt(100) + 1 : 0;

            // Select random category - for a more realistic approach, we'll match product names with relevant categories
            String categoryName;
            if (i < 20) {
                // Electronics products
                categoryName = getRandomSubcategory(allCategories, "Electronics", random);
            } else if (i < 40) {
                // Clothing products
                categoryName = getRandomSubcategory(allCategories, "Clothing", random);
            } else if (i < 60) {
                // Home & Kitchen products
                categoryName = getRandomSubcategory(allCategories, "Home & Kitchen", random);
            } else if (i < 80) {
                // Books products
                categoryName = getRandomSubcategory(allCategories, "Books", random);
            } else {
                // Beauty products
                categoryName = getRandomSubcategory(allCategories, "Beauty", random);
            }

            Category category = allCategories.get(categoryName);

            // Create product
            Product product = Product.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .inStock(inStock)
                    .stock(stock)
                    .category(category)
                    .build();

            products.add(product);
        }

        // Save all products
        productRepository.saveAll(products);

        log.info("Data creation completed successfully!");
        log.info("Created {} categories", allCategories.size());
        log.info("Created {} products", products.size());
    }

    private String getRandomSubcategory(Map<String, Category> allCategories, String mainCategory, Random random) {
        String[] subcategories;
        switch (mainCategory) {
            case "Electronics":
                subcategories = new String[]{"Smartphones", "Laptops", "Tablets", "Accessories"};
                break;
            case "Clothing":
                subcategories = new String[]{"Men's Wear", "Women's Wear", "Kids", "Sportswear"};
                break;
            case "Home & Kitchen":
                subcategories = new String[]{"Appliances", "Furniture", "Decor", "Kitchenware"};
                break;
            case "Books":
                subcategories = new String[]{"Fiction", "Non-Fiction", "Educational", "Comics"};
                break;
            case "Beauty":
                subcategories = new String[]{"Skincare", "Haircare", "Makeup", "Fragrances"};
                break;
            default:
                return mainCategory;
        }
        return subcategories[random.nextInt(subcategories.length)];
    }

}