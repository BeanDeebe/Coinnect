package edu.northeastern.coinnect.activities.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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
import edu.northeastern.coinnect.activities.transactions.TransactionCardClickListener;
import edu.northeastern.coinnect.activities.transactions.TransactionsActivity;
import edu.northeastern.coinnect.activities.transactions.TransactionsRecyclerViewAdapter;
import edu.northeastern.coinnect.activities.transactions.addTransaction.AddTransactionActivity;
import edu.northeastern.coinnect.databinding.ActivityHomeScreenBinding;
import edu.northeastern.coinnect.models.persistence.FirebaseDBHandler;
import edu.northeastern.coinnect.models.persistence.entities.TransactionEntity;
import edu.northeastern.coinnect.models.transactionModels.AbstractTransactionModel;
import edu.northeastern.coinnect.repositories.TransactionsRepository;
import edu.northeastern.coinnect.repositories.UsersRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

  private final Handler handler = new Handler();
  private static final FirebaseDBHandler firebaseDBHandler = FirebaseDBHandler.getInstance();
  private static final UsersRepository userRepository = UsersRepository.getInstance();

  private RecyclerView recentTransactionsRV;

  private TransactionsRecyclerViewAdapter recentTransactionsRVA;

  private final TransactionsRepository transactionsRepository = TransactionsRepository.getInstance();

  private ProgressBar progressBar;

  private FloatingActionButton addTransactionFAB;
  private TextView greetingTextView;
  private TextView budgetTextView;
  private TextView dateTextView;
  private String currentUserName;
  private String userBudget;
  private String userFirstName;

  private void setupRecyclerView(ActivityHomeScreenBinding binding) {
    this.recentTransactionsRV = binding.rvRecentTransactions;

    this.recentTransactionsRV.setHasFixedSize(true);
    this.recentTransactionsRV.setLayoutManager(new LinearLayoutManager(this));
  }

  private void setupRecyclerViewListenerAndAdapter() {
    // set up a listener for transaction card click -> Edit Transaction
//    TransactionCardClickListener transactionCardClickListener =
//        () -> {
//          Intent intent = new Intent(HomeActivity.this, AddTransactionActivity.class);
//          startActivity(intent);
//        };

//    this.transactionsRVA.setCardClickListener(transactionCardClickListener);
    this.recentTransactionsRV.setAdapter(this.recentTransactionsRVA);
  }

  private void setupAddTransactionFAB() {
    this.addTransactionFAB.setOnClickListener(
        view -> {
          Intent intent = new Intent(getApplicationContext(), AddTransactionActivity.class);
          startActivity(intent);
        });
  }

  private void setupUserGreeting() {
    this.userBudget = userRepository.getMonthlyBudget();
    this.userFirstName = userRepository.getUserFirstName();

    Calendar todayCalendar = Calendar.getInstance();

    String month = String.valueOf(todayCalendar.get(Calendar.MONTH));
    int dayOfMonth = todayCalendar.get(Calendar.DAY_OF_MONTH);
    String datePass = String.join(" ", month, String.valueOf(dayOfMonth));

    if (currentUserName != null) {
      this.greetingTextView.setText(String.format("Hello %s", this.userFirstName));
      this.budgetTextView.setText(String.format("$%s", this.userBudget));
      this.dateTextView.setText(datePass);
    } else {
      this.budgetTextView.setText("$2500");
      this.dateTextView.setText(datePass);
    }
  }

  @SuppressLint({"SetTextI18n", "ResourceAsColor"})
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    edu.northeastern.coinnect.databinding.ActivityHomeScreenBinding binding =
        ActivityHomeScreenBinding.inflate(getLayoutInflater());
    setContentView(R.layout.activity_home_screen);

    this.addTransactionFAB = findViewById(R.id.fab_addTransactionButton);
    greetingTextView = findViewById(R.id.tv_greeting);
    budgetTextView = findViewById(R.id.set_budget);
    dateTextView = findViewById(R.id.tv_today_date);

    this.setupAddTransactionFAB();

    currentUserName = userRepository.getCurrentUserName();

    this.setupUserGreeting();

    BottomNavigationView navView = findViewById(R.id.bottom_nav_home);
    menuBarActions(navView);
    progressBar = findViewById(R.id.homeScreenProgressBar);
    List<AbstractTransactionModel> transactionsList = new ArrayList<>();

    this.setupRecyclerView(binding);

    this.recentTransactionsRVA = new TransactionsRecyclerViewAdapter(transactionsList);
    this.setupRecyclerViewListenerAndAdapter();
  }

  protected void menuBarActions(BottomNavigationView navView) {
    navView.setOnItemSelectedListener(
        item -> {
          if (item.getItemId() == R.id.friends) {
            Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
            System.out.println(currentUserName);
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

  @Override
  protected void onResume() {
    super.onResume();

    runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));

    this.transactionsRepository.getRecentTransactionsList(
        this.handler, this.recentTransactionsRVA, this.progressBar);
  }

  @Override
  protected void onPause() {
    super.onPause();
  }
}
