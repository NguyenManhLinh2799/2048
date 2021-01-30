package com.example.a2048;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a2048.SimpleGestureFilter.SimpleGestureListener;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SimpleGestureListener, View.OnClickListener {
    private SimpleGestureFilter detector;

    private int[][] table = new int[4][4];
    private LinearLayout layout;
    private boolean tableChanged = true;
    private TextView tvCurrentScore;
    private int currentScore = 0;
    private TextView tvBestScore;
    private int bestScore;
    private Button btnRestart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Detect touched area
        detector = new SimpleGestureFilter(MainActivity.this, this);

        // Find views
        layout = findViewById(R.id.table);
        tvCurrentScore = findViewById(R.id.tvCurrentScore);
        tvBestScore = findViewById(R.id.tvBestScore);
        btnRestart = findViewById(R.id.btnRestart);
        btnRestart.setOnClickListener(this);

        // Load best score
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        bestScore = sharedPreferences.getInt("bestScore2048", 0);
        tvBestScore.setText(String.valueOf(bestScore));

        // Assign 2 to a random cell
        generate();
    }

    @Override
    protected void onDestroy() {
        // Save best score
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("bestScore2048", bestScore);
        editor.commit();

        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onSwipe(int direction) {
        // Detect the swipe gesture

        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT:
                swipeRight();
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                swipeLeft();
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                swipeDown();
                break;
            case SimpleGestureFilter.SWIPE_UP:
                swipeUp();
                break;
        }
    }

    // Toast shown when double tapped in screen
    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "You have Double Tapped.", Toast.LENGTH_SHORT).show();
    }

    private void swipeLeft() {
        for (int row = 0; row < 4; row++) {
            for (int col = 1; col < 4; col++) {
                for (int i = col - 1; i >= 0; i--) {
                    if (table[row][i] == 0) {
                        table[row][i] = table[row][i + 1];
                        updateTableLayout(row, i);
                        table[row][i + 1] = 0;
                        updateTableLayout(row, i + 1);
                    } else if (table[row][i] == table[row][i + 1]) {
                        table[row][i] *= 2;
                        addToScore(table[row][i]);
                        updateTableLayout(row, i);
                        table[row][i + 1] = 0;
                        updateTableLayout(row, i + 1);
                        break;
                    } else {
                        break;
                    }
                }
            }
        }
        // Assign 2 to a random 0-value cell
        generate();
    }

    private void swipeRight() {
        for (int row = 0; row < 4; row++) {
            for (int col = 2; col >= 0; col--) {
                for (int i = col + 1; i < 4; i++) {
                    if (table[row][i] == 0) {
                        table[row][i] = table[row][i - 1];
                        updateTableLayout(row, i);
                        table[row][i - 1] = 0;
                        updateTableLayout(row, i - 1);
                    } else if (table[row][i] == table[row][i - 1]) {
                        table[row][i] *= 2;
                        addToScore(table[row][i]);
                        updateTableLayout(row, i);
                        table[row][i - 1] = 0;
                        updateTableLayout(row, i - 1);
                        break;
                    } else {
                        break;
                    }
                }
            }
        }
        // Assign 2 to a random 0-value cell
        generate();
    }

    private void swipeUp() {
        for (int col = 0; col < 4; col++) {
            for (int row = 1; row < 4; row++) {
                for (int i = row - 1; i >= 0; i--) {
                    if (table[i][col] == 0) {
                        table[i][col] = table[i + 1][col];
                        updateTableLayout(i, col);
                        table[i + 1][col] = 0;
                        updateTableLayout(i + 1, col);
                    } else if (table[i][col] == table[i + 1][col]) {
                        table[i][col] *= 2;
                        addToScore(table[i][col]);
                        updateTableLayout(i, col);
                        table[i + 1][col] = 0;
                        updateTableLayout(i + 1, col);
                        break;
                    } else {
                        break;
                    }
                }
            }
        }
        // Assign 2 to a random 0-value cell
        generate();
    }

    private void swipeDown() {
        for (int col = 0; col < 4; col++) {
            for (int row = 2; row >= 0; row--) {
                for (int i = row + 1; i < 4; i++) {
                    if (table[i][col] == 0) {
                        table[i][col] = table[i - 1][col];
                        updateTableLayout(i, col);
                        table[i - 1][col] = 0;
                        updateTableLayout(i - 1, col);
                    } else if (table[i][col] == table[i - 1][col]) {
                        table[i][col] *= 2;
                        addToScore(table[i][col]);
                        updateTableLayout(i, col);
                        table[i - 1][col] = 0;
                        updateTableLayout(i - 1, col);
                        break;
                    } else {
                        break;
                    }
                }
            }
        }
        // Assign 2 to a random 0-value cell
        generate();
    }

    private void updateTableLayout(int row, int col) {
        LinearLayout rowLayout = (LinearLayout) layout.getChildAt(row);
        TextView tv = (TextView) rowLayout.getChildAt(col);
        if (table[row][col] == 0) {
            tv.setBackgroundResource(R.color.black);
            tv.setText("");
        } else {
            tv.setBackgroundResource(R.color.white);
            tv.setText(String.valueOf(table[row][col]));
            tableChanged = true;
        }
    }

    private boolean hasMove(int row, int col) {
        int currentValue = table[row][col];
        if (row - 1 >= 0 && (table[row - 1][col] == 0 || table[row - 1][col] == currentValue)) {
            return true;
        }
        if (col + 1 < 4 && (table[row][col + 1] == 0 || table[row][col + 1] == currentValue)) {
            return true;
        }
        if (row + 1 < 4 && (table[row + 1][col] == 0 || table[row + 1][col] == currentValue)) {
            return true;
        }
        if (col - 1 >= 0 && (table[row][col - 1] == 0 || table[row][col - 1] == currentValue)) {
            return true;
        }
        return false;
    }

    private void generate() {
        // Find all blank positions
        boolean stillHasMove = false; // Check if there's any move possible
        ArrayList<Integer> blankPos = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            if (table[i / 4][i % 4] == 0) {
                blankPos.add(i);
            } else if (hasMove(i / 4, i % 4)) {
                stillHasMove = true;
            }
        }

        if (blankPos.isEmpty() && !stillHasMove) { // No blank position and no move
            Toast.makeText(this, "No more move. Restart the game.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!tableChanged) {
            return;
        }

        int randomPos = blankPos.get((new Random()).nextInt(blankPos.size()));
        table[randomPos / 4][randomPos % 4] = 2;
        updateTableLayout(randomPos / 4, randomPos % 4);

        tableChanged = false;
    }

    private void addToScore(int amount) {
        currentScore += amount;
        tvCurrentScore.setText(String.valueOf(currentScore));
        if (currentScore > bestScore) {
            bestScore = currentScore;
            tvBestScore.setText(String.valueOf(bestScore));
        }
    }

    @Override
    public void onClick(View v) {
        // Reset current score
        currentScore = 0;
        tvCurrentScore.setText(String.valueOf(currentScore));

        // Reset table;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                table[row][col] = 0;
                updateTableLayout(row, col);
            }
        }

        tableChanged = true;
        generate();
    }
}