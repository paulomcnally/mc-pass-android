package com.mcnallydev.apps.authenticator.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.google.zxing.integration.android.IntentIntegrator
import com.mcnallydev.apps.authenticator.Config
import com.mcnallydev.apps.authenticator.MainActivity
import com.mcnallydev.apps.authenticator.R
import com.tumblr.remember.Remember
import java.util.*
import android.widget.Toast



class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        val barcode = findViewById<LinearLayout>(R.id.item_barcode)
        barcode.setOnClickListener({
            saltFromBarcode()
        })

        val generate = findViewById<LinearLayout>(R.id.item_generate)
        generate.setOnClickListener({
            saltFromGenerate()
        })
    }

    fun saltFromBarcode() {
        val integrator = IntentIntegrator(this)
        integrator.setPrompt(getString(R.string.activity_setup_item_barcode))
        integrator.initiateScan()
        //done()
    }

    fun saltFromGenerate() {
        val random = UUID.randomUUID().toString()
        Remember.putString(Config.SALT, random, {
            Toast.makeText(this, R.string.generate_message, Toast.LENGTH_LONG).show()
            done()
        })
    }

    fun done() {
        intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, R.string.activity_setup_item_barcode_cancelled, Toast.LENGTH_LONG).show()
            } else {
                Remember.putString(Config.SALT, result.contents, {
                    Toast.makeText(this, R.string.scanner_message, Toast.LENGTH_LONG).show()
                    done()
                })
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
