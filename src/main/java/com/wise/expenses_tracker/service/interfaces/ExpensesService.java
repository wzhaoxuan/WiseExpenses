package com.wise.expenses_tracker.service.interfaces;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.wise.expenses_tracker.transferObject.ExpensesDTO;

public interface ExpensesService {

    List<ExpensesDTO> getAllExpenses();
    Optional<ExpensesDTO> getExpenseById(Long id);
    Optional<ExpensesDTO> deleteExpense(Long id);
    Optional<ExpensesDTO> updateExpense(Long id, ExpensesDTO expensesDTO);
    ExpensesDTO saveExpense(ExpensesDTO expensesDTO);
    Map<String, Double> getCategoryExpenses();

}
