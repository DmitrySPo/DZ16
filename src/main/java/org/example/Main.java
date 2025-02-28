package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class,args);
        RecipeService recipeService = context.getBean(RecipeService.class);

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            System.out.println("\nВыберите действие:");
            System.out.println("1. Найти рецепты по ключевому слову");
            System.out.println("2. Добавить новый рецепт");
            System.out.println("3. Удалить рецепт");
            System.out.println("4. Выход");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    System.out.println("Введите название рецепта или его часть: ");
                    String searchTerm = scanner.nextLine();
                    List<Recipe> recipes = recipeService.findeRecipesByName(searchTerm);
                    if (recipes.isEmpty()) {
                        System.out.println("Рецепты не найдены.");
                    } else {
                        for (Recipe recipe : recipes) {
                            System.out.println("Название рецепта: " + recipe.getName());
                            System.out.println("Ингредиенты:");
                            for (Ingredient ingredient : recipe.getIngredients()) {
                                System.out.printf("%s: %.2f\n", ingredient.getName(), ingredient.getQuantity());
                            }
                            System.out.println(); // Пустая строка для разделения рецептов
                        }
                    }
                    break;
                case 2:
                    System.out.println("Введите название рецепта: ");
                    String recipeName = scanner.nextLine();
                    Recipe recipe = new Recipe();
                    recipe.setName(recipeName);

                    System.out.println("Введите количество ингредиентов: ");
                    int numIngredients = Integer.parseInt(scanner.nextLine());

                    for (int i = 0; i < numIngredients; i++) {
                        System.out.println("Введите название ингредиента: ");
                        String ingredientName = scanner.nextLine();
                        System.out.println("Введите количество: ");
                        double ingredientQuantity = Double.parseDouble(scanner.nextLine());
                        Ingredient ingredient = new Ingredient(null, ingredientName, ingredientQuantity);
                        recipe.addIngredient(ingredient);
                    }
                    recipeService.addRecipe(recipe);
                    System.out.println("Рецепт успешно добавлен");
                    break;
                case 3:
                    System.out.println("Введите ID рецепта для удаления: ");
                    Long recipeId = Long.parseLong(scanner.nextLine());
                    recipeService.deleteRecipe(recipeId);
                    System.out.println("Рецепт успешно удален");
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println("Неверный выбор. Попробуйте еще раз.");
            }
        }
        System.out.println("Программа завершена.");
        context.close();

    }
}