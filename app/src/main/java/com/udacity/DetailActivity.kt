package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        file_name.text = intent.getStringExtra(Constants.KEY_FILE_NAME)
        status.text = intent.getStringExtra(Constants.KEY_STATUS)

        if (status.text.equals(Constants.Status.SUCCESS.name)) {
            status.setTextColor(resources.getColor(R.color.success, theme))
        } else {
            status.setTextColor(resources.getColor(R.color.fail, theme))
        }

        ok_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
