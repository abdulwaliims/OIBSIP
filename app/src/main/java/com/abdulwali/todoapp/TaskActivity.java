package com.abdulwali.todoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity {

    EditText etNewTask;
    Button btnAddTask, btnLogout;
    ListView listViewTasks;
    TextView tvEmptyState;
    DatabaseHelper dbHelper;
    String currentUserEmail;

    ArrayList<String> taskList;
    ArrayList<Integer> taskIds;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        dbHelper = new DatabaseHelper(this);

        // Get the email of the person who logged in
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserEmail = prefs.getString("USER_EMAIL", null);

        if (currentUserEmail == null) {
            // If no user is logged in, kick them back to the login screen
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        etNewTask = findViewById(R.id.etNewTask);
        btnAddTask = findViewById(R.id.btnAddTask);
        btnLogout = findViewById(R.id.btnLogout);
        listViewTasks = findViewById(R.id.listViewTasks);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        taskList = new ArrayList<>();
        taskIds = new ArrayList<>();

        // Setup the adapter to show text in the list
        adapter = new ArrayAdapter<>(this, R.layout.item_task, R.id.tvTaskText, taskList);
        listViewTasks.setAdapter(adapter);

        loadTasks(); // Load existing tasks when screen opens

        // Add Task Logic
        btnAddTask.setOnClickListener(v -> {
            String taskText = etNewTask.getText().toString().trim();
            if (taskText.isEmpty()) {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.addTask(currentUserEmail, taskText);
            etNewTask.setText(""); // Clear input
            loadTasks(); // Refresh list
        });

        // Delete Task Logic (Click on a task to delete it)
        listViewTasks.setOnItemClickListener((parent, view, position, id) -> {
            int taskIdToDelete = taskIds.get(position);
            dbHelper.deleteTask(taskIdToDelete);
            Toast.makeText(this, "Task Deleted", Toast.LENGTH_SHORT).show();
            loadTasks(); // Refresh list
        });

        // Logout Logic
        btnLogout.setOnClickListener(v -> {
            // Clear the saved email
            prefs.edit().clear().apply();
            Intent intent = new Intent(TaskActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Method to load tasks from Database into the ListView
    private void loadTasks() {
        taskList.clear();
        taskIds.clear();

        Cursor cursor = dbHelper.getTasks(currentUserEmail);
        if (cursor.moveToFirst()) {
            do {
                // Column index 0 is ID, Column index 2 is Task Text (based on our DatabaseHelper)
                int id = cursor.getInt(0);
                String task = cursor.getString(2);
                taskIds.add(id);
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged(); // Tell the list to update

        // Show "Empty State" message if there are no tasks
        if (taskList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
        }
    }
}