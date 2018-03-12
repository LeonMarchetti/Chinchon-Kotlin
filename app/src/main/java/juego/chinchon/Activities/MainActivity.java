package juego.chinchon.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import com.example.leoam.chinchonandroid.R;

public class MainActivity extends AppCompatActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Botón "Comenzar Partida"
        final Button bt_partida = findViewById(R.id.main_btn_1);
        // Botón "Salir"
        final Button bt_salir = findViewById(R.id.main_btn_2);

        bt_partida.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        RegistroJugadoresActivity.class);
                startActivity(intent);
            }

        });

        bt_salir.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
        });
    }
}
