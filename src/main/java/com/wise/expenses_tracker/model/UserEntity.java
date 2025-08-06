package com.wise.expenses_tracker.model;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor // Lombok will generate a constructor with all fields
@NoArgsConstructor // Lombok will generate a no-args constructor
@Entity
@Data // Lombok will generate getters, setters, toString, equals, and hashCode methods
@Table(name = "Users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    // Assuming a user can have multiple expenses
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<ExpensesEntity> expenses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<BudgetEntity> budgets;

}
