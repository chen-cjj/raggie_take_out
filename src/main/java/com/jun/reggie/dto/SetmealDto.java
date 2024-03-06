package com.jun.reggie.dto;
import com.jun.reggie.entity.Setmeal;
import com.jun.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
