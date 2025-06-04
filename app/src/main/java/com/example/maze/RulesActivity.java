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
                "CEL GRY:\n" +
                        "• Twoim zadaniem jest przejście labiryntu od punktu startowego do mety (zielone pole).\n" +
                        "• Czerwona kropka to twoja pozycja w labiryncie.\n" +
                        "• Zielony punkt w prawym dolnym rogu to meta – kiedy na nią dotrzesz, wygrasz!\n\n" +

                        "STEROWANIE:\n" +
                        "Możesz poruszać się na dwa sposoby:\n" +
                        "   • Strzałkami na ekranie (↑ ↓ ← →)\n" +
                        "   • Przechylając telefon w odpowiednią stronę (akcelerometr)\n\n" +

                        "ZAPIS LABIRYNTU:\n" +
                        "• Wygenerowany labirynt możesz zapisać i zagrać w niego później.\n"
        );

        // Return to the previous screen when button is clicked
        Button backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> finish());
    }
}
