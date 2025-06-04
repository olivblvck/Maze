package com.example.maze;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RulesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules); // Set the layout for the rules screen

        // Display the game rules in the text view
        TextView textView = findViewById(R.id.text_rules);
        textView.setText(
                "OBJECTIVE:\n\n" +
                        "• Your goal is to navigate through the maze from the starting point to the finish point (the green tile).\n" +
                        "• The red dot represents your current position in the maze.\n" +
                        "• The green dot in the bottom-right corner is the finish point – reach it to win!\n\n" +

                        "CONTROLS:\n\n" +
                        "You can move in two ways:\n" +
                        "   • By tapping the on-screen arrow buttons \n \t (↑ ↓ ← →)\n" +
                        "   • By tilting your phone in the desired direction (accelerometer)\n\n" +

                        "MAZE SAVING:\n\n" +
                        "• You can save a generated maze and play it later.\n\n\n\n"
        );

        // Return to the previous screen when button is clicked
        Button backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> finish());
    }
}
