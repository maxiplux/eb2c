package app.quantun.eb2c.config;

import app.quantun.eb2c.model.contract.value.OrderStatus;
import app.quantun.eb2c.model.entity.bussines.*;
import app.quantun.eb2c.repository.BranchRepository;
import app.quantun.eb2c.repository.OrderRepository;
import app.quantun.eb2c.repository.OrganizationRepository;
import app.quantun.eb2c.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Configuration class responsible for initializing sample data for business entities.
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {

    private final OrganizationRepository organizationRepository;
    private final BranchRepository branchRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    /**
     * Creates a CommandLineRunner bean that initializes sample data for business entities.
     * The order value of 2 ensures this runs after the BootstrapDataService (which creates categories and products).
     *
     * @return CommandLineRunner instance
     */
    @Bean
    @org.springframework.core.annotation.Order(2)
    public CommandLineRunner initBusinessData() {
        return args -> {
            // Check if data already exists
            if (organizationRepository.count() > 0 && branchRepository.count() > 0 && orderRepository.count() > 0) {
                log.info("Business entities already exist - skipping creation");
                return;
            }

            log.info("Starting to create business entities...");

            // Create organizations
            List<Organization> organizations = createOrganizations();

            // Create branches for each organization
            Map<Organization, List<Branch>> organizationBranches = createBranches(organizations);

            // Create orders for each branch
            createOrders(organizationBranches);

            log.info("Business data initialization completed successfully!");
        };
    }

    /**
     * Creates sample organizations.
     *
     * @return List of created organizations
     */
    private List<Organization> createOrganizations() {
        log.info("Creating organizations...");

        List<Organization> organizations = new ArrayList<>();

        // Sample organization data
        String[] orgNames = {
                "Quantum Enterprises",
                "Stellar Solutions Inc.",
                "Horizon Technologies",
                "Pinnacle Group",
                "Apex Industries"
        };

        String[] orgDescriptions = {
                "A leading provider of innovative business solutions",
                "Specializing in cutting-edge technology services",
                "Transforming businesses through digital innovation",
                "Excellence in enterprise solutions and consulting",
                "Industry leaders in manufacturing and distribution"
        };

        String[] taxIds = {
                "123-45-6789",
                "987-65-4321",
                "456-78-9123",
                "789-12-3456",
                "321-65-4987"
        };

        // Create and save organizations
        for (int i = 0; i < orgNames.length; i++) {
            Organization organization = new Organization();
            organization.setName(orgNames[i]);
            organization.setDescription(orgDescriptions[i]);
            organization.setTaxId(taxIds[i]);

            organizations.add(organizationRepository.save(organization));
            log.info("Created organization: {}", orgNames[i]);
        }

        return organizations;
    }

    /**
     * Creates sample branches for each organization.
     *
     * @param organizations List of organizations to create branches for
     * @return Map of organizations to their branches
     */
    private Map<Organization, List<Branch>> createBranches(List<Organization> organizations) {
        log.info("Creating branches for organizations...");

        Map<Organization, List<Branch>> organizationBranches = new HashMap<>();

        // Sample branch data
        String[] branchTypes = {"Headquarters", "Regional Office", "Sales Office", "Distribution Center", "Support Center"};
        String[] cities = {"New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia", "San Antonio", "San Diego"};

        Random random = new Random();

        // Create branches for each organization
        for (Organization organization : organizations) {
            List<Branch> branches = new ArrayList<>();

            // Number of branches for this organization (2-5)
            int numBranches = random.nextInt(4) + 2;

            for (int i = 0; i < numBranches; i++) {
                Branch branch = new Branch();

                // Set branch name (Organization Name - Branch Type - City)
                String branchType = branchTypes[i % branchTypes.length];
                String city = cities[random.nextInt(cities.length)];
                branch.setName(organization.getName() + " - " + branchType + " - " + city);

                // Set address
                branch.setAddress(random.nextInt(9999) + " Main St, " + city + ", US");

                // Set phone
                branch.setPhone("(" + (random.nextInt(900) + 100) + ") " + (random.nextInt(900) + 100) + "-" + (random.nextInt(9000) + 1000));

                // Set email
                branch.setEmail(branchType.toLowerCase().replace(" ", ".") + "@" +
                        organization.getName().toLowerCase().replace(" ", "") + ".com");

                // Set organization
                branch.setOrganization(organization);

                // Save branch
                branches.add(branchRepository.save(branch));
                log.info("Created branch: {} for organization: {}", branch.getName(), organization.getName());
            }

            organizationBranches.put(organization, branches);
        }

        return organizationBranches;
    }

    /**
     * Creates sample orders for each branch.
     *
     * @param organizationBranches Map of organizations to their branches
     */
    private void createOrders(Map<Organization, List<Branch>> organizationBranches) {
        log.info("Creating orders for branches...");

        // Get all products
        List<Product> allProducts = productRepository.findAll();
        if (allProducts.isEmpty()) {
            log.warn("No products found. Cannot create orders without products.");
            return;
        }

        Random random = new Random();

        // Create orders for each branch
        for (Map.Entry<Organization, List<Branch>> entry : organizationBranches.entrySet()) {
            Organization organization = entry.getKey();
            List<Branch> branches = entry.getValue();

            for (Branch branch : branches) {
                // Number of orders for this branch (3-8)
                int numOrders = random.nextInt(6) + 3;

                for (int i = 0; i < numOrders; i++) {
                    // Create order
                    Order order = new Order();

                    // Set order number (ORD-ORGID-BRANCHID-RANDOM)
                    order.setOrderNumber("ORD-" + organization.getId() + "-" + branch.getId() + "-" + (random.nextInt(9000) + 1000));

                    // Set order date (within the last 30 days)
                    order.setOrderDate(LocalDateTime.now().minusDays(random.nextInt(30)));

                    // Set initial status
                    OrderStatus[] statuses = OrderStatus.values();
                    order.setStatus(statuses[random.nextInt(statuses.length)]);

                    // Set notes (optional)
                    if (random.nextBoolean()) {
                        order.setNotes("Sample order notes for " + order.getOrderNumber());
                    }

                    // Set organization and branch
                    order.setOrganization(organization);
                    order.setBranch(branch);

                    // Initialize total amount (will be recalculated)
                    order.setTotalAmount(BigDecimal.ZERO);

                    // Save order to get ID
                    order = orderRepository.save(order);

                    // Create order items
                    createOrderItems(order, allProducts, random);

                    // Recalculate total and save again
                    order.recalculateTotal();
                    orderRepository.save(order);

                    log.info("Created order: {} for branch: {}", order.getOrderNumber(), branch.getName());
                }
            }
        }
    }

    /**
     * Creates sample order items for an order.
     *
     * @param order       Order to create items for
     * @param allProducts List of all available products
     * @param random      Random number generator
     */
    private void createOrderItems(Order order, List<Product> allProducts, Random random) {
        // Number of items in this order (1-5)
        int numItems = random.nextInt(5) + 1;

        // Keep track of products already added to this order
        Set<Long> addedProductIds = new HashSet<>();

        for (int i = 0; i < numItems; i++) {
            // Select a random product that hasn't been added to this order yet
            Product product;
            do {
                product = allProducts.get(random.nextInt(allProducts.size()));
            } while (addedProductIds.contains(product.getId()));

            addedProductIds.add(product.getId());

            // Create order item
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(random.nextInt(5) + 1)
                    .unitPrice(product.getPrice())
                    .build();

            // Add discount (optional)
            if (random.nextInt(10) < 3) { // 30% chance of discount
                BigDecimal discountAmount = product.getPrice()
                        .multiply(BigDecimal.valueOf(random.nextInt(30) / 100.0)); // 0-29% discount
                orderItem.setDiscount(discountAmount);
            }

            // Add notes (optional)
            if (random.nextBoolean()) {
                orderItem.setNotes("Sample item notes for " + product.getName());
            }

            // Add to order
            order.addOrderItem(orderItem);
        }
    }
}
