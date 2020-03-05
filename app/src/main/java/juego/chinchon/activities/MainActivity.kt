package juego.chinchon.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.leoam.chinchonkotlin.R
import kotlinx.android.synthetic.main.main.*

class MainActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        main_btn_1.setOnClickListener {
            val intent = Intent(this@MainActivity, RegistroJugadoresActivity::class.java)
            startActivity(intent)
        }

        main_btn_2.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            startActivity(intent)
        }
    }
}