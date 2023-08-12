package edu.northeastern.coinnect.repositories;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import edu.northeastern.coinnect.models.persistence.FirebaseDBHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransactionsRepository {
  private static final String TAG = "_TransactionsRepository";
  private static final FirebaseDBHandler firebaseDbHandler = FirebaseDBHandler.getInstance();

  private static TransactionsRepository INSTANCE;

  private TransactionsRepository() {}

  public static TransactionsRepository getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new TransactionsRepository();
    }

    return INSTANCE;
  }

  public FirebaseDBHandler getFirebaseDbHandler() {
    return firebaseDbHandler;
  }

  public Task<DataSnapshot> addTransaction(
      Handler handler,
      Context activityContext,
      ProgressBar progressBar,
      Integer year,
      Integer month,
      Integer dayOfMonth,
      Double amount,
      String description) {
    return getFirebaseDbHandler()
        .addTransaction(year, month, dayOfMonth, amount, description)
        .addOnCompleteListener(
            task -> {
              handler.post(
                  () -> {
                    // TODO: use TransactionRecyclerViewAdapter
                    // adapter.addTransaction(transactionModel);
                    progressBar.setVisibility(View.INVISIBLE);
                  });
            });
  }

  public void getTransactionsForMonthList(
      Handler handler,
      Context activityContext,
      ProgressBar progressBar,
      Integer year,
      Integer month) {
    firebaseDbHandler
        .getDbInstance()
        .getReference()
        .child(FirebaseDBHandler.USERS_BUCKET_NAME)
        .child(firebaseDbHandler.getCurrentUserName())
        .child(FirebaseDBHandler.TRANSACTIONS_BUCKET_NAME)
        .child(year.toString())
        .child(month.toString())
        .get()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
              } else {
                List<String> transactionsList = new ArrayList<>();
                HashMap value = (HashMap) task.getResult().getValue();

                for (Object key : value.keySet()) {
                  transactionsList.add(key.toString());
                }

                Log.i(TAG, String.format("Transactions being added to the Recycler View"));
                handler.post(
                    () -> {
                      // TODO: use TransactionRecyclerViewAdapter
                      // adapter.setupList(transactionsList);
                      progressBar.setVisibility(View.INVISIBLE);
                    });
              }
            });
  }

  public void getTransactionsForDayOfMonthList(
      Handler handler,
      Context activityContext,
      ProgressBar progressBar,
      Integer year,
      Integer month,
      Integer dayOfMonth) {
    firebaseDbHandler
        .getDbInstance()
        .getReference()
        .child(FirebaseDBHandler.USERS_BUCKET_NAME)
        .child(firebaseDbHandler.getCurrentUserName())
        .child(FirebaseDBHandler.TRANSACTIONS_BUCKET_NAME)
        .child(year.toString())
        .child(month.toString())
        .child(dayOfMonth.toString())
        .get()
        .addOnCompleteListener(
            task -> {
              if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
              } else {
                List<String> transactionsList = new ArrayList<>();
                HashMap value = (HashMap) task.getResult().getValue();

                for (Object key : value.keySet()) {
                  transactionsList.add(key.toString());
                }

                Log.i(TAG, String.format("Transactions being added to the Recycler View"));
                handler.post(
                    () -> {
                      // TODO: use TransactionRecyclerViewAdapter
                      // adapter.setupList(transactionsList);
                      progressBar.setVisibility(View.INVISIBLE);
                    });
              }
            });
  }
}
