package edu.northeastern.coinnect.activities.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import edu.northeastern.coinnect.R;
import edu.northeastern.coinnect.activities.friends.FriendsActivity;
import edu.northeastern.coinnect.activities.settings.SettingsActivity;
import edu.northeastern.coinnect.activities.transactions.TransactionsActivity;
import edu.northeastern.coinnect.activities.transactions.addTransaction.AddTransactionActivity;
import edu.northeastern.coinnect.databinding.ActivityHomeScreenBinding;
import edu.northeastern.coinnect.models.persistence.FirebaseDBHandler;
import edu.northeastern.coinnect.models.persistence.entities.TransactionEntity;
import edu.northeastern.coinnect.repositories.TransactionsRepository;
import edu.northeastern.coinnect.repositories.UsersRepository;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

  private final Handler handler = new Handler();
  private static final FirebaseDBHandler firebaseDBHandler = FirebaseDBHandler.getInstance();

  private RecyclerView transactionRecyclerView;

  private RecentTransactionAdapter recentTransactionAdapter;

  private TransactionsRepository transactionsRepository;

  private ProgressBar homeScreenProgressBar;
  private TextView greeting;
  private TextView budget;
  private TextView date;
  private String userName;
  private String userBudget;
  private String dateExtra;
  private final UsersRepository usersRepository = UsersRepository.getInstance();

  @SuppressLint({"SetTextI18n", "ResourceAsColor"})
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    edu.northeastern.coinnect.databinding.ActivityHomeScreenBinding binding =
        ActivityHomeScreenBinding.inflate(getLayoutInflater());
    setContentView(R.layout.activity_home_screen);
    FloatingActionButton addTransaction =
        (FloatingActionButton) findViewById(R.id.addTransactionButton);
    addTransaction.setOnClickListener(
        view -> {
          Intent intent = new Intent(getApplicationContext(), AddTransactionActivity.class);
          startActivity(intent);
        });

    // getting username, budget, date to display greeting
    userName =   this.usersRepository.getCurrentUserName();

    userBudget = getIntent().getStringExtra("BUDGET");
    dateExtra = getIntent().getStringExtra("DATE");
    greeting = findViewById(R.id.greeting);
    budget = findViewById(R.id.set_budget);
    date = findViewById(R.id.today_date);

    if (userName != null) {
      greeting.setText("Hello " + userName);
      budget.setText("$" + userBudget);
      date.setText(dateExtra);
    } else {
      greeting.setText("Free Pass");
      budget.setText("$2500");
      date.setText(dateExtra);
    }

    BottomNavigationView navView = findViewById(R.id.bottom_nav_home);
    menuBarActions(navView);
    homeScreenProgressBar = findViewById(R.id.homeScreenProgressBar);
    List<TransactionEntity> transactionEntityList = new ArrayList<>();

    //    this.setupToolbar(binding);
    this.setupRecyclerView(binding);

    this.recentTransactionAdapter = new RecentTransactionAdapter(transactionEntityList);
    transactionsRepository = TransactionsRepository.getInstance();
    //    homeScreenProgressBar.setVisibility(View.VISIBLE);
  }

  protected void menuBarActions(BottomNavigationView navView) {
    navView.setOnItemSelectedListener(
        item -> {
          if (item.getItemId() == R.id.friends) {
            Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
            System.out.println(userName);
            startActivity(intent);
            overridePendingTransition(0, 0);
          } else if (item.getItemId() == R.id.homeActivity) {
            return true;
          } else if (item.getItemId() == R.id.transactionActivity) {
            Intent intent = new Intent(getApplicationContext(), TransactionsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            return true;
          } else {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            return true;
          }
          return false;
        });
  }

  private void setupRecyclerView(ActivityHomeScreenBinding binding) {
    this.transactionRecyclerView = binding.transactionRecyclerView;
    this.transactionRecyclerView.setHasFixedSize(true);
    this.transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
  }
}
