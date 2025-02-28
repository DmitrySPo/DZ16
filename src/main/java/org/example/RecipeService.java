package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;

@Service
public class RecipeService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RecipeService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void addRecipe(Recipe recipe) {
        //Создаем KeyHolder для хранения сгенерированного ключа
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //Сохраняем рецепт в базу данных
        String insertRecipeSql = "INSERT INTO recipes (name) VALUES (?)";

        // Выполняем вставкуи сохраняем сгенерированный ключв KeyHolder
        jdbcTemplate.update(
                conn -> {
                    PreparedStatement ps =conn.prepareStatement(insertRecipeSql, PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setString(1, recipe.getName());
                    return ps;
                },
                keyHolder
        );

        // Получаем сгенерированный ключ
        long recipeId =keyHolder.getKey().longValue();

        // Сохраняем ингредиенты рецепта
        for (Ingredient ingredient : recipe.getIngredients()) {
            String insertIngredientSql = "INSERT INTO ingredients (name,quantity, recipe_id) VALUES (?, ?, ?)";
            jdbcTemplate.update(insertIngredientSql, ingredient.getName(), ingredient.getQuantity(), recipeId);
        }

    }

    @Transactional(readOnly = true)
    public List<Recipe> findeRecipesByName(String searchTerm) {
        String selectRecipesSql = "SELECT r.id, r.name FROM recipes r WHERE LOWER(r.name) LIKE LOWER(?)";
        List<Recipe> recipes = jdbcTemplate.query(selectRecipesSql, new Object[]{"%" + searchTerm + "%"},
                (resultSet, rowNum) -> {
                    Recipe recipe = new Recipe();
                    recipe.setId(resultSet.getLong("id"));
                    recipe.setName(resultSet.getString("name"));
                    return recipe;
                });

        // Загружаем ингредиенты для каждого рецепта
        for (Recipe recipe : recipes) {
            String selectIngredientsSql = "SELECT i.id, i.name, i.quantity FROM ingredients i WHERE i.recipe_id = ?";
            List<Ingredient> ingredients = jdbcTemplate.query(selectIngredientsSql, new Object[]{recipe.getId()},
                    (resultSet, rowNum) -> {
                        Ingredient ingredient = new Ingredient();
                        ingredient.setId(resultSet.getLong("id"));
                        ingredient.setName(resultSet.getString("name"));
                        ingredient.setQuantity(resultSet.getDouble("quantity"));
                        return ingredient;
                    });
            recipe.setIngredients(ingredients);
        }

        return recipes;
    }

    @Transactional
    public void deleteRecipe(Long id) {
        // Удаляем ингредиенты рецепта
        String deleteIngredientsSql = "DELETE FROM ingredients WHERE recipe_id = ?";
        jdbcTemplate.update(deleteIngredientsSql, id);

        // Удаляем рецепт
        String deleteRecipeSql = "DELETE FROM recipes WHERE id = ?";
        jdbcTemplate.update(deleteRecipeSql, id);
    }

}
