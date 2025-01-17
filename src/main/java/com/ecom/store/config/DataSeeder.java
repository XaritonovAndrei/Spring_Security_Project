//package com.ecom.store.config;
//
//import com.ecom.store.model.Category;
//import com.ecom.store.model.Product;
//import com.ecom.store.repository.CategoryRepository;
//import com.ecom.store.repository.ProductRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//
//@Component
//public class DataSeeder implements CommandLineRunner {
//
//    private final ProductRepository productRepository;
//    private final CategoryRepository categoryRepository;
//
//    public DataSeeder(ProductRepository productRepository,
//                      CategoryRepository categoryRepository) {
//        this.productRepository = productRepository;
//        this.categoryRepository = categoryRepository;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//
//        productRepository.deleteAll();
//        categoryRepository.deleteAll();
//
//        Category electronics = new Category();
//        electronics.setName("Electronics");
//
//        Category clothes = new Category();
//        clothes.setName("Clothes");
//
//        categoryRepository.saveAll(Arrays.asList(electronics, clothes));
//
//        Product smartphone = new Product();
//        smartphone.setName("Yabloko");
//        smartphone.setDescription("Yablochnoe");
//        smartphone.setCategory(electronics);
//        smartphone.setImageUrl("https://placehold.co/600x400");
//        smartphone.setPrice(1000000000.99);
//
//        Product PC = new Product();
//        PC.setName("mac");
//        PC.setDescription("makintosh");
//        PC.setCategory(electronics);
//        PC.setImageUrl("https://placehold.co/600x400");
//        PC.setPrice(100000000000000.99);
//
//        Product kurtka = new Product();
//        kurtka.setName("kurtka");
//        kurtka.setDescription("puxovik");
//        kurtka.setCategory(clothes);
//        kurtka.setImageUrl("https://placehold.co/600x400");
//        kurtka.setPrice(199.99);
//
//        Product kepka = new Product();
//        kepka.setName("kepka");
//        kepka.setDescription("na golovu");
//        kepka.setCategory(clothes);
//        kepka.setImageUrl("https://placehold.co/600x400");
//        kepka.setPrice(29.99);
//
//        productRepository.saveAll(Arrays.asList(smartphone, PC, kurtka, kepka));
//
//    }
//}
