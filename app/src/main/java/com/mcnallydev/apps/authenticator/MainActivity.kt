package com.mcnallydev.apps.authenticator

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.mcnallydev.apps.authenticator.ui.SetupActivity
import com.tumblr.remember.Remember

import kotlinx.android.synthetic.main.activity_main.*
import com.afollestad.materialdialogs.MaterialDialog
import com.mcnallydev.apps.authenticator.models.ServiceModel
import io.realm.Realm
import android.support.v7.widget.RecyclerView
import com.mcnallydev.apps.authenticator.adapters.ServicesAdapter
import io.realm.RealmResults


class MainActivity : AppCompatActivity() {

    private var salt: String? = null
    private var mRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // instance salt with local salt
        salt = Remember.getString(Config.SALT, "")




        // to call setup screen
        setup(salt as String)



        mRecyclerView = findViewById<RecyclerView>(R.id.my_recycler_view)
        mRecyclerView?.setHasFixedSize(true)
        mRecyclerView?.layoutManager = LinearLayoutManager(this)
        query()



        fab.setOnClickListener { _ ->
            add()
        }
    }

    fun query() {
        val realm = Realm.getDefaultInstance()
        val results = realm.where(ServiceModel::class.java).findAll()
        mRecyclerView?.adapter = ServicesAdapter(this, results)
    }

    fun add() {
        MaterialDialog.Builder(this)
                .title(R.string.dialog_add_title)
                .customView(R.layout.dialog_add, true)
                .cancelable(false)
                .negativeText(R.string.dialog_cancel)
                .positiveText(R.string.dialog_save)
                .onPositive({
                    dialog, _ ->
                    val view = dialog.view
                    val service = view.findViewById<EditText>(R.id.service)
                    val username = view.findViewById<EditText>(R.id.username)

                    if(service.text.toString().isEmpty()) {
                        Toast.makeText(applicationContext, R.string.dialog_add_service_required, Toast.LENGTH_LONG).show()
                    } else if (username.text.toString().isEmpty()) {
                        Toast.makeText(applicationContext, R.string.dialog_add_username_required, Toast.LENGTH_LONG).show()
                    } else {
                        // store on realm database
                        val realm = Realm.getDefaultInstance()
                        realm.beginTransaction()
                        val serviceModel = realm.createObject(ServiceModel::class.java)
                        serviceModel.name = service.text.toString()
                        serviceModel.username = username.text.toString()
                        realm.commitTransaction()
                        realm?.close()
                        query()
                    }

                })
                .show()
    }

    /**
     * To open setup activity
     */
    fun setup(salt: String) {
        if (salt.isEmpty()) {
            // open setup activity
            intent = Intent(applicationContext, SetupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
